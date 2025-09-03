package com.githubusersearch.domain.model

data class User(
    val id: Int,
    val username: String,
    val avatarUrl: String,
    val bio: String?,
    val followers: Int,
    val repoCount: Int,
    val profileUrl: String? = null,
    val reposUrl: String? = null
)
