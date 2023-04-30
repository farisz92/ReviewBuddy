package slack.model

enum class Emoji(val slackCode: String) {
    REVIEW_REQUIRED(":eyes:"),
    COMMENTED(":speech_balloon:"),
    CHANGE_REQUEST(":thinking_face:"),
    APPROVED(":white_check_mark:")
}

data class SlackMessage (
    val emoji: Emoji?,
    val prTitle: String,
    val prUrl: String,
    val mainMessage: String,
    val authorName: String,
    val authorUrl: String,
    val createdTimeStamp: String,
    val reviewState: String? = "",
    val reviewerName: String? = "",
    val reviewerUrl: String? = "",
    val reviewedTimeStamp: String? = ""
) {
    fun getNewMessage() = "${emoji?.slackCode ?: ""} <$prUrl|$prTitle> $mainMessage <$authorUrl|$authorName> on $createdTimeStamp"
    fun getUpdatedMessage() = "${emoji?.slackCode ?: ""} <$prUrl|$prTitle> $mainMessage <$authorUrl|$authorName> on $createdTimeStamp - $reviewState <$reviewerUrl|$reviewerName> on $reviewedTimeStamp"
}
