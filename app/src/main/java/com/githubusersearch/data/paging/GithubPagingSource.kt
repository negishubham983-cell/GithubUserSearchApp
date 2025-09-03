package com.githubusersearch.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.githubusersearch.data.remote.api.GithubApi
import com.githubusersearch.domain.model.User
import retrofit2.HttpException
import java.io.IOException

class GithubPagingSource(
    private val api: GithubApi,
    private val query: String
) : PagingSource<Int, User>() {

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        if (query.isBlank()) {
            return LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)
        }

        return try {
            val page = params.key ?: 1
            val response = api.searchUsers(query, page, params.loadSize)
            val users = response.items.map { it.toDomain() }

            LoadResult.Page(
                data = users,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (users.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}
