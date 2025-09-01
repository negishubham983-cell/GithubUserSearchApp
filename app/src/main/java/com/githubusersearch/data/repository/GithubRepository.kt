package com.githubusersearch.data.repository

import androidx.paging.PagingData
import com.githubusersearch.domain.model.Repository
import com.githubusersearch.domain.model.User
import kotlinx.coroutines.flow.Flow

interface GithubRepository {

    fun searchUsers(query: String): Flow<PagingData<User>>
    suspend fun getUserDetails(username: String): User
    fun getUserRepos(username: String): Flow<PagingData<Repository>>
}