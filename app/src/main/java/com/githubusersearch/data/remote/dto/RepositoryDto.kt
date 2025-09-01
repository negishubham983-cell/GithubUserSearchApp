package com.githubusersearch.data.remote.dto

import com.githubusersearch.domain.model.Repository
import com.google.gson.annotations.SerializedName

data class RepositoryDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("stargazers_count") val stars: Int,
    @SerializedName("forks_count") val forks: Int,
) {
    fun toDomain() = Repository(
        id = id,
        name = name,
        description = description,
        stars = stars,
        forks = forks
    )
}
