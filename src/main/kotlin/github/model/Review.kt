package github.model

import com.google.gson.annotations.SerializedName

enum class ReviewState {
    @SerializedName("approved") APPROVED,
    @SerializedName("changes_requested") CHANGES_REQUEST,
    @SerializedName("commented") COMMENTED,
}
data class Review (
    @SerializedName("user") val reviewer : GitHubUser,
    val state: ReviewState,
    @SerializedName("submitted_at") val reviewedAt: String
)
