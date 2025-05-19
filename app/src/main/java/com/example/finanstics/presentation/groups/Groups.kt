package com.example.finanstics.presentation.groups

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.finanstics.R
import com.example.finanstics.api.models.Group
import com.example.finanstics.presentation.Navigation
import com.example.finanstics.presentation.preferencesManager.PreferencesManager
import com.example.finanstics.ui.theme.icons.CircleIcon
import com.example.finanstics.ui.theme.icons.PlusCircleIcon

@ExperimentalMaterial3Api
@Suppress("MagicNumber", "LongMethod")
@Composable
fun Groups(navController: NavController, vm: GroupsViewModel = viewModel()) {
    val uiState = vm.uiState.collectAsState().value
    var searchQuery = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        vm.fetchGroups()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize().padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth().height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = { navController.navigateUp() },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                GroupSearchBar(vm, navController, uiState, searchQuery)
            }

            when (uiState) {
                is GroupsUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is GroupsUiState.All -> {
                    GroupList(navController, groups = uiState.groups)
                }

                is GroupsUiState.Error -> {
                    Text(
                        text = uiState.errorMsg,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                            .clickable { vm.fetchGroups() }
                    )
                }

                else -> Unit
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                PlusActionButton(
                    onClick = {},
                    offsetX = 0.dp
                )
            }
        }
    }
}

@Suppress("MagicNumber")
@Composable
fun GroupList(navController: NavController, groups: List<Group>) {
    LazyColumn(
        modifier = Modifier
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(groups.size) { index ->
            GroupCard(navController, groups[index])
        }
    }
}

@Suppress("MagicNumber")
@Composable
fun GroupCard(navController: NavController, group: Group) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                preferencesManager.saveData("groupId", group.id)
                navController.navigate(Navigation.GROUP_STATS.toString())
            }
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.placeholder),
                contentDescription = group.name,
                modifier = Modifier
                    .size(70.dp)
            )
            Text(
                text = group.name,
                fontSize = 20.sp,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
    HorizontalDivider(
        thickness = 2.dp,
        color = MaterialTheme.colorScheme.tertiary
    )
}

@Suppress("MagicNumber")
@Composable
fun PlusActionButton(
    onClick: () -> Unit,
    offsetX: Dp
) {
    Box(
        modifier = Modifier.offset(x = offsetX)
    ) {
        Icon(
            imageVector = CircleIcon,
            contentDescription = "Add",
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .size(50.dp)
                .clickable { onClick }
        )
        Icon(
            imageVector = PlusCircleIcon,
            contentDescription = "Add",
            tint = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .size(50.dp)
        )
    }
}

@ExperimentalMaterial3Api
@Suppress("MagicNumber")
@Composable
fun GroupSearchBar(
    vm: GroupsViewModel,
    navController: NavController,
    uiState: GroupsUiState,
    searchQuery: MutableState<String>
) {
    SearchBar(
        query = searchQuery.value,
        onQueryChange = { query ->
            searchQuery.value = query
            if (query.isNotEmpty()) {
                vm.searchGroups(query)
            } else {
                vm.fetchGroups()
            }
        },
        placeholder = { Text("Search Groups") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon"
            )
        },
        onSearch = {
            if (searchQuery.value.isNotEmpty()) {
                vm.searchGroups(searchQuery.value)
            }
        },
        active = searchQuery.value.isNotEmpty(),
        onActiveChange = { active ->
            if (!active) {
                vm.fetchGroups()
            }
        },
        content = {
            if (uiState is GroupsUiState.Search) {
                if (uiState.searchedGroups.isNotEmpty()) {
                    GroupList(navController, groups = uiState.searchedGroups)
                }
            }
        },
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.secondary,
        ),
    )
}
