package github

import github.model.ActionType
import github.model.GitHubEvent
import slack.SlackHelper
import slack.mapper.MapPullRequestToSlackMessage

class GitHubLogicHandler {

    private val slackHelper = SlackHelper()
    private val mapPullRequestToSlackMessage = MapPullRequestToSlackMessage()

    fun handleGitHubEvent(event: GitHubEvent) {
        val mappedMessage = mapPullRequestToSlackMessage.map(event)
        when(event.action) {
            ActionType.CLOSED -> slackHelper.deleteMessage(mappedMessage)
            ActionType.REVIEW_REQUESTED -> slackHelper.sendMessage(mappedMessage)
            ActionType.ASSIGNED -> slackHelper.sendMessage(mappedMessage)
            ActionType.SUBMITTED ->  slackHelper.updateMessage(mappedMessage)
            else -> {}
        }
    }

}