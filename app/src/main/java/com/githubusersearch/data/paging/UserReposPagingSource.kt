package com.githubusersearch.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.githubusersearch.data.remote.api.GithubApi
import com.githubusersearch.domain.model.Repository

class UserReposPagingSource(
    private val api: GithubApi,
    private val username: String
) : PagingSource<Int, Repository>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repository> {
        val page = params.key ?: 1
        return try {
            val response =
                api.getUserRepos(username, page, 20) // API should support pagination
            val repos = response.map { it.toDomain() }
            LoadResult.Page(
                data = repos,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (repos.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Repository>): Int? {
        return state.anchorPosition?.let { anchor ->
            val anchorPage = state.closestPageToPosition(anchor)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)

        }
    }
}
