package com.githubusersearch.data.remote.dto

import UserDto
import com.google.gson.annotations.SerializedName

data class GithubResponseDto(
    @SerializedName("items")
    val items: List<UserDto>
)
