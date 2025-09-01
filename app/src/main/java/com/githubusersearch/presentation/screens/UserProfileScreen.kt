package com.githubusersearch.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.githubusersearch.domain.model.Repository
import com.githubusersearch.presentation.ui.ErrorItem
import com.githubusersearch.presentation.ui.LoadingItem
import com.githubusersearch.presentation.viewmodel.UserProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    username: String,
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Paging repositories
    val reposPagingItems: LazyPagingItems<Repository> =
        viewModel.getUserRepos(username).collectAsLazyPagingItems()

    // Load user profile when screen launches
    LaunchedEffect(username) {
        viewModel.loadUserProfile(username)
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                error != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = error ?: "Unknown error", color = Color.Red)
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { viewModel.loadUserProfile(username) }) {
                                Text("Retry")
                            }
                        }
                    }
                }

                user != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // User info
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AsyncImage(
                                model = user?.avatarUrl ?: "",
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    user?.username ?: "User",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                if (user?.bio?.isNotEmpty() == true) {
                                    Text(user?.bio ?: "No bio available")
                                }
                                Text("Followers: ${user?.followers ?: "NA"}")
                                Text("Repositories: ${user?.repoCount ?: "NA"}")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Repositories", style = MaterialTheme.typography.titleLarge)

                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            // Paging items
                            items(
                                count = reposPagingItems.itemCount,
                                key = reposPagingItems.itemKey { it.id }
                            ) { index ->
                                val repo = reposPagingItems[index]
                                repo?.let {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    ) {
                                        Text(it.name, style = MaterialTheme.typography.titleMedium)
                                        Text(it.description ?: "No description")
                                        Text("★ ${it.stars} | 🍴 ${it.forks}")
                                    }
                                }
                            }

                            // Loading/Error/Empty
                            reposPagingItems.apply {
                                when {
                                    loadState.refresh is LoadState.Loading && itemCount == 0 -> {
                                        item { LoadingItem("Loading repositories...") }
                                    }

                                    loadState.append is LoadState.Loading -> {
                                        item { LoadingItem("Loading more...") }
                                    }

                                    loadState.refresh is LoadState.Error && itemCount == 0 -> {
                                        val e = loadState.refresh as LoadState.Error
                                        item {
                                            ErrorItem(
                                                e.error.message ?: "Unknown Error"
                                            ) { retry() }
                                        }
                                    }

                                    loadState.append is LoadState.Error -> {
                                        val e = loadState.append as LoadState.Error
                                        item {
                                            ErrorItem(
                                                e.error.message ?: "Unknown Error"
                                            ) { retry() }
                                        }
                                    }

                                    itemCount == 0 && loadState.refresh !is LoadState.Loading -> {
                                        item {
                                            Text(
                                                "No repositories found",
                                                color = Color.Gray,
                                                modifier = Modifier.padding(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}