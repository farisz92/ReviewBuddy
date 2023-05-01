package github.model

import com.google.gson.annotations.SerializedName

data class GitHubTeam(
    val name: String,
    @SerializedName("html_url") val url: String
)
