package com.githubusersearch.data.remote.api

import com.githubusersearch.data.remote.dto.GithubResponseDto
import com.githubusersearch.data.remote.dto.RepositoryDto
import com.githubusersearch.data.remote.dto.UserDetailDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApi {

    @GET("search/users")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): GithubResponseDto

    @GET("users/{username}")
    suspend fun getUserDetails(
        @Path("username") username: String
    ): UserDetailDto

    @GET("users/{username}/repos")
    suspend fun getUserRepos(
        @Path("username") username: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<RepositoryDto>
}