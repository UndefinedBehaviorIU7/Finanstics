package com.ub.finanstics.presentation.settings.groupSettings

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ub.finanstics.R
import com.ub.finanstics.api.models.User

@Suppress("MagicNumber", "LongMethod")
@Composable
fun GroupSettings(navController: NavController, vm: GroupSettingsViewModel = viewModel()) {
    val uiState = vm.uiState.collectAsState().value

    LaunchedEffect(Unit) {
        vm.fetchGroupData()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        when (uiState) {
            is GroupSettingsUiState.Loading -> {
                CircularProgressIndicator()
            }
            is GroupSettingsUiState.Idle -> {
                GroupSettingsColumn(
                    vm = vm,
                    navController = navController,
                    userId = vm.getUserId(),
                    groupName = uiState.groupName,
                    groupData = uiState.groupData,
                    imageUri = uiState.imageUri,
                    imageBitmap = uiState.imageBitmap,
                    owner = uiState.owner,
                    users = uiState.users,
                    admins = uiState.admins,
                    members = uiState.members
                )
            }
            is GroupSettingsUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = 32.dp
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Spacer(modifier = Modifier.weight(0.2f))

                    Image(
                        painter = painterResource(R.drawable.connection_error),
                        contentDescription = stringResource(R.string.connection_error),
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Text(
                        text = stringResource(R.string.no_internet),
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = uiState.errorMsg,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.weight(0.05f))

                    Button(
                        onClick = { vm.fetchGroupData() },
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onBackground,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.retry),
                            fontSize = 22.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.weight(0.15f))
                }
            }
        }
    }
}

@Suppress("MagicNumber")
@Composable
private fun GroupImage(vm: GroupSettingsViewModel, image: Bitmap?) {

    val painter = image?.asImageBitmap()?.let { BitmapPainter(it) }
    ?: painterResource(R.drawable.placeholder)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            vm.updateImage(uri)
        }
    )

    Box(
        modifier = Modifier
            .size(180.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Image(
            painter = painter,
            contentDescription = stringResource(R.string.group_picture),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .clickable(onClick = {
                    launcher.launch("image/*")
                }
            )
        )

        IconButton(
            onClick = {
                launcher.launch("image/*")
            },
            modifier = Modifier
                .padding(8.dp)
                .size(32.dp)
                .zIndex(1f)
                .background(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.edit),
                modifier = Modifier
                    .size(30.dp),
                tint = MaterialTheme.colorScheme.background
            )
        }
    }
}

@Suppress("MagicNumber", "LongMethod", "LongParameterList")
@Composable
private fun GroupSettingsColumn(
    vm: GroupSettingsViewModel,
    navController: NavController,
    userId: Int,
    groupName: String,
    groupData: String?,
    imageUri: Uri?,
    imageBitmap: Bitmap?,
    owner: User,
    users: List<Int>?,
    admins: List<Int>?,
    members: List<User>?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                vertical = 100.dp,
                horizontal = 32.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        GroupImage(vm, imageBitmap)

        EditableTextField(
            currentName = groupName,
            onNameChanged = { },
        )

        EditableTextField(
            currentName = groupData,
            onNameChanged = {},
        )

        ComposeUserList(
            owner = owner,
            currentUserId = userId,
            memberList = members,
            userList = users,
            adminList = admins,
            onRemoveUser = { userId -> vm.removeUser(userId) },
            onToggleAdmin = {}
//            onRemoveUser = { userId ->
//                users = users.filter { it.id != userId }
//                users = users.filter { it != userId }
//                admins = admins.filter { it != userId }
//            },
//            onToggleAdmin = { userId ->
//                adminList = if (adminList.contains(userId)) {
//                    adminList.filter { it != userId }
//                } else {
//                    adminList + userId
//                }
//            }
        )

        val isOwner = userId == owner.id
        val isAdmin = admins?.contains(userId) == true

        if (isAdmin || isOwner) {
            Button(
                onClick = {}
            ) {
                Text(text = stringResource(R.string.add_user))
            }
        }

        LeaveButton(
            vm = vm,
            navController = navController,
            isOwner = isOwner
        )
    }
}

@Suppress("MagicNumber")
@Composable
fun EditableTextField(
    currentName: String?,
    onNameChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(
        fontSize = 28.sp,
        textAlign = TextAlign.Center
    )
) {
    var editableName by remember { mutableStateOf(currentName) }
    var isEditing by remember { mutableStateOf(false) }

    LaunchedEffect(currentName) {
        if (!isEditing) {
            editableName = currentName
        }
    }

    if (isEditing) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            OutlinedTextField(
                value = editableName ?: "",
                onValueChange = { editableName = it },
                singleLine = true,
                modifier = Modifier.weight(1f),
                textStyle = textStyle
            )

            IconButton(
                onClick = {
                    isEditing = false
                    editableName?.let { onNameChanged(it) }
                }
            ) {
                Icon(Icons.Default.Check, contentDescription = stringResource(R.string.save))
            }
        }
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            Text(
                text = editableName ?: "",
                modifier = Modifier.weight(1f),
                style = textStyle
            )

            IconButton(
                onClick = { isEditing = true }
            ) {
                Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit))
            }
        }
    }
}

@Suppress("LongParameterList", "LongMethod", "MagicNumber", "ComplexCondition")
@Composable
fun ComposeUserList(
    owner: User,
    currentUserId: Int,
    memberList: List<User>?,
    userList: List<Int>?,
    adminList: List<Int>?,
    onRemoveUser: (Int) -> Unit,
    onToggleAdmin: (Int) -> Unit
) {
    val sortedUsers = remember(memberList, adminList, userList) {
        val safeMemberList = memberList ?: emptyList()
        val safeAdminList = adminList ?: emptyList()
        val safeUserList = userList ?: emptyList()

        safeMemberList.filter { user -> safeAdminList.contains(user.id) } +
        safeMemberList.filter { user ->
            safeUserList.contains(user.id) && !safeAdminList.contains(user.id)
        }
    }

    Surface(
        modifier = Modifier
            .border(1.dp, Color.Gray)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        ) {
            sortedUsers.forEach { user ->
                var showMenu by remember { mutableStateOf(false) }
                val isOwner = user.id == owner.id
                val isAdmin = adminList?.contains(user.id) == true && !isOwner
                val isCurrentUserAdmin = adminList?.contains(currentUserId) == true

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = user.username.toString(),
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (isOwner) {
                        Text(
                            text = stringResource(R.string.owner),
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }

                    if (isAdmin) {
                        Text(
                            text = stringResource(R.string.admin),
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }

                    if (!isOwner && (currentUserId == owner.id ||
                                (isCurrentUserAdmin && !isAdmin))) {
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(
                                    Icons.Default.MoreVert,
                                    contentDescription = stringResource(R.string.actions)
                                )
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                if (!isAdmin) {
                                    DropdownMenuItem(
                                        text = { Text(text = stringResource(R.string.promote)) },
                                        onClick = {
                                            onToggleAdmin(user.id)
                                            showMenu = false
                                        }
                                    )
                                } else {
                                    DropdownMenuItem(
                                        text = { Text(text = stringResource(R.string.demote)) },
                                        onClick = {
                                            onToggleAdmin(user.id)
                                            showMenu = false
                                        }
                                    )
                                }

                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = stringResource(R.string.delete),
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    },
                                    onClick = {
                                        onRemoveUser(user.id)
                                        showMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Suppress("MagicNumber")
@Composable
fun LeaveButton(
    vm: GroupSettingsViewModel,
    navController: NavController,
    isOwner: Boolean
) {
    Button(
        onClick = { vm.showConfirmation(isOwner) },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isOwner) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onBackground
        )
    ) {
        Text(if (isOwner) stringResource(R.string.delete_group)
        else stringResource(R.string.leave_group))
    }

    if (vm.showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { vm.dismissConfirmation() },
            title = { Text(stringResource(R.string.confirmation)) },
            text = {
                Text(if (vm.isOwnerAction)
                    stringResource(R.string.delete_group_msg)
                else stringResource(R.string.leave_group_msg)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { vm.confirmAction(navController) }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { vm.dismissConfirmation() }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
