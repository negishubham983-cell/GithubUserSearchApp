package com.githubusersearch.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.githubusersearch.data.paging.GithubPagingSource
import com.githubusersearch.data.paging.UserReposPagingSource
import com.githubusersearch.data.remote.api.GithubApi
import com.githubusersearch.domain.model.Repository
import com.githubusersearch.domain.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GithubRepositoryImpl @Inject constructor(
    private val api: GithubApi
) : GithubRepository {
    override fun searchUsers(query: String): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { GithubPagingSource(api, query) }
        ).flow
    }

    override suspend fun getUserDetails(username: String): User {
        return api.getUserDetails(username).toDomain()
    }

    override fun getUserRepos(username: String): Flow<PagingData<Repository>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UserReposPagingSource(api, username) }
        ).flow
    }
}