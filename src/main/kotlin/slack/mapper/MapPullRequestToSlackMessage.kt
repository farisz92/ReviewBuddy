package slack.mapper

import github.model.ActionType
import github.model.GitHubEvent
import github.model.PullRequest
import github.model.ReviewState
import slack.model.Emoji
import slack.model.SlackMessage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MapPullRequestToSlackMessage : Mapper<GitHubEvent, SlackMessage> {
    override fun map(from: GitHubEvent): SlackMessage {
        return SlackMessage(
            emoji = mapEmoji(from),
            prTitle = from.pullRequest.title,
            prUrl = from.pullRequest.url,
            mainMessage = " - created by ",
            authorName = from.pullRequest.author.login,
            authorUrl = from.pullRequest.author.url,
            createdTimeStamp = mapIsoTime(from.pullRequest.createdTimeStamp),
            reviewState = mapReviewState(from.review?.state),
            reviewerName = from.review?.reviewer?.login,
            reviewerUrl = from.review?.reviewer?.url,
            reviewedTimeStamp = from.review?.reviewedAt?.let { mapIsoTime(it) }
        )
    }

    private fun mapIsoTime(isoTime: String) : String {
        val inputFormat = DateTimeFormatter.ISO_DATE_TIME

        val outputFormat = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss")

        val dateTime = LocalDateTime.parse(isoTime, inputFormat)
        return dateTime.format(outputFormat)
    }

    private fun mapReviewState(reviewState: ReviewState?) : String {
        return when (reviewState) {
            ReviewState.APPROVED -> "approved by"
            ReviewState.CHANGES_REQUEST -> "changes requested by"
            ReviewState.COMMENTED -> "comment left by"
            else -> ""
        }
    }

    private fun mapEmoji(event: GitHubEvent) : Emoji? {
        return when (event.action) {
            ActionType.REVIEW_REQUESTED -> Emoji.REVIEW_REQUIRED
            ActionType.ASSIGNED -> Emoji.REVIEW_REQUIRED
            ActionType.SUBMITTED -> {
                when (event.review?.state) {
                    ReviewState.APPROVED -> Emoji.APPROVED
                    ReviewState.CHANGES_REQUEST -> Emoji.CHANGE_REQUEST
                    ReviewState.COMMENTED -> Emoji.COMMENTED
                    else -> null
                }
            }
            else -> null
        }
    }
}