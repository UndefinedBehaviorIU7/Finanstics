package com.ub.finanstics.presentation.groups

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ub.finanstics.R
import com.ub.finanstics.api.models.GroupWithImage
import com.ub.finanstics.dialogs.ErrorAlertDialog
import com.ub.finanstics.dialogs.ErrorDialogContent
import com.ub.finanstics.presentation.Navigation
import com.ub.finanstics.presentation.converters.bitmapToBase64
import com.ub.finanstics.presentation.preferencesManager.PreferencesManager
import com.ub.finanstics.presentation.templates.Loader
import com.ub.finanstics.ui.theme.icons.CircleIcon
import com.ub.finanstics.ui.theme.icons.PlusCircleIcon

@ExperimentalMaterial3Api
@Suppress("MagicNumber", "LongMethod")
@Composable
fun Groups(navController: NavController, vm: GroupsViewModel = viewModel()) {
    val uiState = vm.uiState.collectAsState().value
    var searchQuery = remember { mutableStateOf("") }
    var isPlusButtonVisible by remember { mutableStateOf(false) }
    val isSearchActive = searchQuery.value.isNotEmpty()
    val shouldShowPlusButton = isPlusButtonVisible && !isSearchActive

    val plusButtonOffsetY by animateDpAsState(
        targetValue = if (shouldShowPlusButton) 0.dp else 100.dp,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "plusButtonAnimation"
    )

    LaunchedEffect(isSearchActive) {
        if (isSearchActive) {
            isPlusButtonVisible = false
        }
    }

    LaunchedEffect(Unit) {
        vm.fetchGroups()
    }

    var wasDialogShown by remember(uiState) { mutableStateOf(false) }
    var showDialog by remember(uiState) { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is GroupsUiState.Error && !wasDialogShown) {
            showDialog = true
            wasDialogShown = true
        }
    }

    if (showDialog && uiState is GroupsUiState.Error) {
        ErrorAlertDialog(
            onDismissRequest = { showDialog = false }
        ) {
            ErrorDialogContent(
                msg = uiState.errorMsg,
                action = { showDialog = false },
                buttonText = stringResource(R.string.ok),
                onClose = { showDialog = false }
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 10.dp,
                    end = 10.dp,
                    top = 10.dp,
                    bottom = 10.dp
                ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                GroupSearchBar(vm, searchQuery)
            }

            when (uiState) {
                is GroupsUiState.Loading -> {
                    BoxWithConstraints {
                        val width = maxWidth
                        Loader(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(width / 3)
                        )
                    }
                }

                is GroupsUiState.All -> {
                    GroupList(
                        navController = navController,
                        groups = uiState.groups,
                        onSwipeDown = { isPlusButtonVisible = true },
                        onSwipeUp = { isPlusButtonVisible = false }
                    )
                }

                is GroupsUiState.Error -> {
                    Spacer(modifier = Modifier.weight(0.2f))

                    Image(
                        painter = painterResource(R.drawable.connection_error),
                        contentDescription = stringResource(R.string.connection_error),
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Text(
                        text = stringResource(R.string.no_internet),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.weight(0.2f))

                    Button(
                        onClick = { vm.fetchGroups() },
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
                }

                is GroupsUiState.Search -> {
                    GroupList(
                        navController = navController,
                        groups = uiState.searchedGroups,
                        onSwipeDown = { isPlusButtonVisible = true },
                        onSwipeUp = { isPlusButtonVisible = false }
                    )
                }

                else -> Unit
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .offset(y = plusButtonOffsetY)
        ) {
            PlusActionButton(
                onClick = { navController.navigate(Navigation.ADD_GROUP.toString()) },
            )
        }
    }
}

@Suppress("MagicNumber")
@Composable
fun GroupList(
    navController: NavController,
    groups: List<GroupWithImage>,
    onSwipeDown: (() -> Unit)? = null,
    onSwipeUp: (() -> Unit)? = null
) {
    val listState = rememberLazyListState()
    var previousFirstVisibleItemIndex by remember { mutableIntStateOf(0) }
    var previousFirstVisibleItemScrollOffset by remember { mutableIntStateOf(0) }

    if (onSwipeDown != null && onSwipeUp != null) {
        LaunchedEffect(listState) {
            snapshotFlow {
                listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
            }.collect { (currentIndex, currentOffset) ->
                val isScrollingDown = if (currentIndex > previousFirstVisibleItemIndex) {
                    true
                } else if (currentIndex < previousFirstVisibleItemIndex) {
                    false
                } else {
                    currentOffset > previousFirstVisibleItemScrollOffset
                }

                if (isScrollingDown) {
                    onSwipeUp.invoke()
                } else {
                    onSwipeDown.invoke()
                }

                previousFirstVisibleItemIndex = currentIndex
                previousFirstVisibleItemScrollOffset = currentOffset
            }
        }
    }

    if (groups.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.no_groups),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 24.sp
            )
        }
    } else {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = if (onSwipeDown != null) 80.dp else 16.dp)
        ) {
            items(groups.size) { index ->
                GroupCard(navController, groups[index])
            }
        }
    }
}

@Suppress("MagicNumber")
@Composable
fun GroupCard(navController: NavController, group: GroupWithImage) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }

    val image = group.image
    val imageStr = bitmapToBase64(image)

    val painter = image?.asImageBitmap()?.let { BitmapPainter(it) }
        ?: painterResource(R.drawable.placeholder)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                preferencesManager.saveData("groupId", group.group.id)
                preferencesManager.saveData("groupName", group.group.name)
                preferencesManager.saveData("groupImage", imageStr)
                navController.navigate(Navigation.GROUP_STATS.toString())
            }
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            Image(
                painter = painter,
                contentDescription = group.group.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
            )
            Text(
                text = group.group.name,
                fontSize = 20.sp,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
    HorizontalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.primaryContainer
    )
}

@Suppress("MagicNumber")
@Composable
fun PlusActionButton(
    onClick: () -> Unit,
) {
    Box {
        Icon(
            imageVector = CircleIcon,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .size(50.dp)
                .clickable {
                    onClick()
                }
        )
        Icon(
            imageVector = PlusCircleIcon,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .size(50.dp)
        )
    }
}

@Suppress("MagicNumber")
@Composable
fun GroupSearchBar(
    vm: GroupsViewModel,
    searchQuery: MutableState<String>
) {
    OutlinedTextField(
        value = searchQuery.value,
        onValueChange = { query ->
            searchQuery.value = query
            if (query.isNotEmpty()) {
                vm.searchGroups(query)
            } else {
                vm.fetchGroups()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = {
            Text(
                text = stringResource(R.string.search),
                color = MaterialTheme.colorScheme.primary
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = stringResource(R.string.search)
            )
        },
        trailingIcon = {
            if (searchQuery.value.isNotEmpty()) {
                IconButton(
                    onClick = {
                        searchQuery.value = ""
                        vm.fetchGroups()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = stringResource(R.string.clear)
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.onBackground,
            unfocusedContainerColor = MaterialTheme.colorScheme.onBackground,
            focusedBorderColor = MaterialTheme.colorScheme.onBackground,
            unfocusedBorderColor = MaterialTheme.colorScheme.onBackground
        )
    )
}
