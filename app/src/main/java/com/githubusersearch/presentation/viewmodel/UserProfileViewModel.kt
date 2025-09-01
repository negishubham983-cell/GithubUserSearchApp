package com.githubusersearch.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.githubusersearch.data.repository.GithubRepository
import com.githubusersearch.domain.model.Repository
import com.githubusersearch.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val repository: GithubRepository
) : ViewModel() {

    // Loading & error state for user details
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    // Repositories PagingData
    private var _repos: Flow<PagingData<Repository>>? = null
    fun getUserRepos(username: String): Flow<PagingData<Repository>> {
        if (_repos == null) {
            _repos = repository.getUserRepos(username)
                .cachedIn(viewModelScope)
        }
        return _repos!!
    }

    // Load user details
    fun loadUserProfile(username: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val userDetails = repository.getUserDetails(username)
                _user.value = userDetails
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
