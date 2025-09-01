package com.githubusersearch.domain.model

data class Repository(
    val id: Int,
    val name: String,
    val stars: Int,
    val description: String?,
    val forks: Int
)