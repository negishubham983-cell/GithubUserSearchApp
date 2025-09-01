import com.githubusersearch.domain.model.User
import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("login") val login: String,
    @SerializedName("avatar_url") val avatarUrl: String,
    @SerializedName("bio") val bio: String?,
    @SerializedName("followers") val followers: Int,
    @SerializedName("public_repos") val repoCount: Int
) {
    fun toDomain() = User(
        id = id,
        username = login,
        avatarUrl = avatarUrl,
        bio = bio,
        followers = followers,
        repoCount = repoCount
    )
}
