package github.model

import com.google.gson.annotations.SerializedName

enum class State {
    @SerializedName("open") OPEN,
    @SerializedName("closed") CLOSED,
    @SerializedName("merged") MERGED
}

// don't know if this is even visible in the response API
// might need to request specific permissions from the Github App
enum class MergeStateStatus {
    BEHIND, BLOCKED, CLEAN, DIRTY, DRAFT, HAS_HOOKS, UNKNOWN, UNSTABLE
}

enum class MergeableState {
    @SerializedName("conflicting") CONFLICTING,
    @SerializedName("mergeable") MERGEABLE,
    @SerializedName("unknown") UNKNOWN
}

data class PullRequest(
    val id: Long,
    @SerializedName("html_url") val url: String,
    val state: State,
    val title: String,
    @SerializedName("user") val author: GitHubUser,
    @SerializedName("created_at") val createdTimeStamp: String,
    @SerializedName("updated_at") val updatedTimeStamp: String,
    val merged: Boolean,
    @SerializedName("mergeable_state") val mergeableState: MergeableState
)
