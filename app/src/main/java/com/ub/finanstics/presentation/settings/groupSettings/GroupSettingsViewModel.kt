package com.ub.finanstics.presentation.settings.groupSettings

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.ub.finanstics.presentation.Navigation
import com.ub.finanstics.presentation.preferencesManager.EncryptedPreferencesManager
import com.ub.finanstics.presentation.preferencesManager.PreferencesManager
import com.ub.finanstics.ui.theme.TIME_INIT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class GroupSettingsViewModel (application: Application) : AndroidViewModel(application) {
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
                    val success = repository.updateGroup(
                        groupId = current.groupId,
                        name = current.groupName,
                        data = current.groupData.toString(),
                        users = current.users,
                        admins = current.admins,
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

    fun removeUser(userId: Int) {
        when (val current = _uiState.value) {
            is GroupSettingsUiState.Idle -> {
                if (current.imageUri == null) return

                val imagePart = createMultipartBodyPart(current.imageUri) ?: run {
                    _uiState.value = GroupSettingsUiState.Error("Не удалось подготовить файл")
                    return
                }

                val newUsers = current.users?.filter { it != userId } ?: emptyList()
                viewModelScope.launch {
                    val success = repository.updateGroup(
                        groupId = current.groupId,
                        name = current.groupName,
                        data = current.groupData.toString(),
                        users = current.users,
                        admins = current.admins,
                        image = imagePart
                    )
                    if (success) {
//                        _uiState.value = current.copy(
//                            users = newUsers,
//                            members = current.members?.filter { it.id != userId }
//                        )
                        fetchGroupData()
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