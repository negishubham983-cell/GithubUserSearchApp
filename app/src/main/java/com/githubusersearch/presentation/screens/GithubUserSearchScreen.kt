package com.githubusersearch.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.githubusersearch.domain.model.User
import com.githubusersearch.presentation.ui.ErrorItem
import com.githubusersearch.presentation.ui.LoadingItem
import com.githubusersearch.presentation.viewmodel.GithubUserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GithubUserSearchScreen(
    navController: NavController,
    viewModel: GithubUserViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current

    var query by rememberSaveable { mutableStateOf("") }
    val hasSearched = viewModel.query.collectAsState().value.isNotBlank()
    val users = viewModel.users.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("GitHub User Search") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            SearchBar(
                query = query,
                onQueryChange = { query = it },
                onSearch = {
                    if (query.isNotBlank()) {
                        viewModel.search(query)
                        focusManager.clearFocus() // hide keyboard after search
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    count = users.itemCount,
                    key = users.itemKey { it.username }
                ) { index ->
                    val user = users[index]
                    if (user != null) {
                        UserRow(
                            user = user,
                            onClick = {
                                navController.navigate("profile/${user.username}")
                            }
                        )
                    }
                }

                // Handle loading, errors, and empty state
                users.apply {
                    when {
                        hasSearched && loadState.refresh is LoadState.Loading && itemCount == 0 -> {
                            item { LoadingItem("Loading users...") }
                        }

                        loadState.append is LoadState.Loading -> {
                            item { LoadingItem("Loading more...") }
                        }

                        loadState.refresh is LoadState.Error && itemCount == 0 -> {
                            val e = loadState.refresh as LoadState.Error
                            item {
                                ErrorItem(
                                    message = e.error.message ?: "Unknown Error",
                                    onRetry = { users.retry() }
                                )
                            }
                        }

                        loadState.append is LoadState.Error -> {
                            val e = loadState.append as LoadState.Error
                            item {
                                ErrorItem(
                                    message = e.error.message ?: "Unknown Error",
                                    onRetry = { users.retry() }
                                )
                            }
                        }

                        hasSearched && itemCount == 0 && loadState.refresh !is LoadState.Loading -> {
                            item { Text("No users found", modifier = Modifier.padding(16.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text("Search GitHub Users") },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch() }
        ),
        modifier = modifier.padding(8.dp)
    )
}

@Composable
fun UserRow(user: User, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.avatarUrl,
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = user.username, style = MaterialTheme.typography.bodyLarge)
            user.bio?.let { Text(text = it, style = MaterialTheme.typography.bodySmall) }
        }
    }
}
