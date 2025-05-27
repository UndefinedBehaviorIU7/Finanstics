package com.ub.finanstics.presentation.settings.groupSettings

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.ub.finanstics.presentation.Navigation
import com.ub.finanstics.presentation.preferencesManager.PreferencesManager
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class GroupSettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GroupSettingsRepository(application.applicationContext)

    private val _uiState = MutableStateFlow<GroupSettingsUiState>(GroupSettingsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    var showDeleteConfirm by mutableStateOf(false)
        private set

    var isOwnerAction by mutableStateOf(false)
        private set

    fun showConfirmation(isOwner: Boolean) {
        isOwnerAction = isOwner
        showDeleteConfirm = true
    }

    fun dismissConfirmation() {
        showDeleteConfirm = false
    }

    fun confirmAction(navController: NavController) {
        when (val current = _uiState.value) {
            is GroupSettingsUiState.Idle -> {
                viewModelScope.launch {
                    if (isOwnerAction) {
                        repository.deleteGroup(current.groupId)
                    } else {
                        repository.leaveGroup(current.groupId)
                    }
                    navController.navigate(Navigation.GROUPS.toString())
                }
            }
            else -> Unit
        }
    }

    fun changeGroupName(groupName: String) {
        when (val current = _uiState.value) {
            is GroupSettingsUiState.Idle -> {
                if (groupName == "") {
                    _uiState.value = GroupSettingsUiState.Error("Имя группы не должно быть пустым")
                } else {
                    viewModelScope.launch {
                        val success = repository.updateGroupInfo(
                            groupId = current.groupId,
                            name = groupName,
                            groupData = current.groupData,
                            users = current.users,
                            admins = current.admins ?: emptyList<Int>()
                        )
                        if (success) {
                            _uiState.value = current.copy(
                                groupData = groupName
                            )
                        } else {
                            _uiState.value = GroupSettingsUiState.Error("Сервер вернул ошибку")
                        }
                    }
                }
            }
            else -> Unit
        }
    }

    fun onDataChange(newData: String) {
        when (val current = _uiState.value) {
            is GroupSettingsUiState.Idle -> {
                _uiState.value = current.copy(groupData = newData)
            }

            else -> Unit
        }
    }

    fun changeGroupData(groupData: String?) {
        when (val current = _uiState.value) {
            is GroupSettingsUiState.Idle -> {
                viewModelScope.launch {
                    val success = repository.updateGroupInfo(
                        groupId = current.groupId,
                        name = current.groupName,
                        groupData = groupData,
                        users = current.users,
                        admins = current.admins ?: emptyList<Int>()
                    )
                    if (success) {
                        _uiState.value = current.copy(
                            groupData = groupData
                        )
                    } else {
                        _uiState.value = GroupSettingsUiState.Error("Сервер вернул ошибку")
                    }
                }
            }
            else -> Unit
        }
    }

    fun promoteUser(userId: Int) {
        when (val current = _uiState.value) {
            is GroupSettingsUiState.Idle -> {
                viewModelScope.launch {
                    val updatedAdmins = (current.admins ?: emptyList()).toMutableList().apply {
                        if (!contains(userId)) add(userId)
                    }

                    val success = repository.updateGroupInfo(
                        groupId = current.groupId,
                        name = current.groupName,
                        groupData = current.groupData,
                        users = current.users,
                        admins = updatedAdmins
                    )

                    if (success) {
                        _uiState.value = current.copy(
                            admins = updatedAdmins
                        )
                    } else {
                        _uiState.value = GroupSettingsUiState.Error("Сервер вернул ошибку")
                    }
                }
            }
            else -> Unit
        }
    }

    fun demoteUser(userId: Int) {
        when (val current = _uiState.value) {
            is GroupSettingsUiState.Idle -> {
                viewModelScope.launch {
                    val updatedAdmins = (current.admins ?: emptyList()).minus(userId)
                    val success = repository.updateGroupInfo(
                        groupId = current.groupId,
                        name = current.groupName,
                        groupData = current.groupData,
                        users = current.users,
                        admins = updatedAdmins
                    )
                    if (success) {
                        _uiState.value = current.copy(
                            admins = updatedAdmins
                        )
                    } else {
                        _uiState.value = GroupSettingsUiState.Error("Сервер вернул ошибку")
                    }
                }
            }
            else -> Unit
        }
    }

    fun removeUser(userId: Int) {
        when (val current = _uiState.value) {
            is GroupSettingsUiState.Idle -> {
                viewModelScope.launch {
                    val updatedUsers = current.users.minus(userId)
                    val updatedAdmins = current.admins?.minus(userId)

                    val success = repository.updateGroupInfo(
                        groupId = current.groupId,
                        name = current.groupName,
                        groupData = current.groupData,
                        users = updatedUsers,
                        admins = updatedAdmins ?: emptyList<Int>()
                    )

                    if (success) {
                        _uiState.value = current.copy(
                            users = updatedUsers,
                            admins = updatedAdmins
                        )
                    } else {
                        _uiState.value = GroupSettingsUiState.Error("Ошибка")
                    }
                }
            }
            else -> Unit
        }
    }

    fun addUserByTag(tag: String) {
        if (tag.isBlank()) {
            _uiState.value = GroupSettingsUiState.Error("Тег пользователя не может быть пустым")
            return
        }

        when (val current = _uiState.value) {
            is GroupSettingsUiState.Idle -> {
                viewModelScope.launch {
                    val userId = repository.getUserByTag(tag) ?: run {
                        _uiState.value = GroupSettingsUiState.Error("Пользователь не найден")
                        return@launch
                    }

                    if (current.users.contains(userId)) {
                        _uiState.value = GroupSettingsUiState.Error("Пользователь уже в группе")
                        return@launch
                    }

                    val user = repository.getUserById(userId) ?: run {
                        _uiState.value = GroupSettingsUiState.Error("Ошибка")
                        return@launch
                    }

                    val updatedUsers = current.users + userId
                    val updatedMembers = current.members + user

                    val success = repository.updateGroupInfo(
                        groupId = current.groupId,
                        name = current.groupName,
                        groupData = current.groupData,
                        users = updatedUsers,
                        admins = current.admins ?: emptyList<Int>()
                    )

                    if (success) {
                        _uiState.value = current.copy(
                            users = updatedUsers,
                            members = updatedMembers
                        )
                    } else {
                        _uiState.value = GroupSettingsUiState.Error("Ошибка добавления")
                    }
                }
            }
            else -> Unit
        }
    }

    fun getUserId(): Int {
        val prefManager = PreferencesManager(application)
        return prefManager.getInt("id", -1)
    }

    fun fetchGroupData() {
        _uiState.value = GroupSettingsUiState.Loading
        viewModelScope.launch {
            val sharedPrefs = PreferencesManager(getApplication())
            _uiState.value = repository.getGroup(sharedPrefs.getInt("groupId", -1))
        }
    }

    fun updateImage(uri: Uri?) {
        when (val current = _uiState.value) {
            is GroupSettingsUiState.Idle -> {
                if (uri == null) return

                val imagePart = createMultipartBodyPart(uri) ?: run {
                    _uiState.value = GroupSettingsUiState.Error("Не удалось подготовить файл")
                    return
                }

                val newBitmap = uriToBitmap(getApplication(), uri)
                viewModelScope.launch {
                    val success = repository.updateGroupImage(
                        groupId = current.groupId,
                        image = imagePart
                    )
                    if (success) {
                        _uiState.value = current.copy(
                            imageUri = uri,
                            imageBitmap = newBitmap
                        )
                    } else {
                        _uiState.value = GroupSettingsUiState.Error("Сервер вернул ошибку")
                    }
                }
            }
            else -> Unit
        }
    }

    private fun createMultipartBodyPart(uri: Uri): MultipartBody.Part? {
        val context = getApplication<Application>().applicationContext
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null

        val file = File(context.cacheDir, "temp_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { output ->
            inputStream.copyTo(output)
        }

        val requestFile = file
            .asRequestBody("image/jpeg".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData(
            name = "image",
            filename = file.name,
            body = requestFile
        )
    }

    @Suppress("MagicNumber")
    private fun uriToBitmap(context: Context, uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } else {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }
    }
}
