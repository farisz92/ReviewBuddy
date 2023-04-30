package github.model

import com.google.gson.annotations.SerializedName

data class GitHubUser (
    val id: Long,
    val login: String,
    @SerializedName("html_url") val url: String
)
