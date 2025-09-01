package com.githubusersearch.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.githubusersearch.data.repository.GithubRepository
import com.githubusersearch.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class GithubUserViewModel @Inject constructor(
    private val repository: GithubRepository
) : ViewModel() {

    // Holds the current query
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val users: Flow<PagingData<User>> = _query
        .debounce(300) // wait for typing pause
        .filter { it.isNotBlank() } // ignore empty searches
        .distinctUntilChanged()
        .flatMapLatest { query ->
            repository.searchUsers(query)
        }
        .cachedIn(viewModelScope)

    // Called from UI when user hits Search
    fun search(newQuery: String) {
        _query.value = newQuery
    }
}
