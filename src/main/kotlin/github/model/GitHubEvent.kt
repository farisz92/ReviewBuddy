package github.model

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue
import com.google.gson.annotations.SerializedName

enum class ActionType {
    @SerializedName("opened") OPENED,
    @SerializedName("closed") CLOSED,
    @SerializedName("review_requested") REVIEW_REQUESTED,
    @SerializedName("assigned") ASSIGNED,
    @SerializedName("submitted") SUBMITTED,
    // This is to handle actions that haven't been considered yet
    UNKNOWN
}

data class GitHubEvent (
    val action: ActionType? = ActionType.UNKNOWN,
    @SerializedName("pull_request") val pullRequest: PullRequest,
    @SerializedName("requested_reviewer") val reviewer: GitHubUser?,
    @SerializedName("review") val review: Review?,
    @SerializedName("assignee") val assignee: GitHubUser?
)
