package slack

import com.slack.api.Slack
import slack.model.Emoji
import slack.model.SlackMessage

class SlackHelper {

    private val slack = Slack.getInstance()

    fun sendMessage(message: SlackMessage) {
        val sendMessageResponse = slack.methods(SlackConstants.BOT_TOKEN).chatPostMessage{ it.channel(SlackConstants.HASH_TAG_CHANNEL_ID).text(message.getNewMessage()) }

        if (sendMessageResponse.isOk) {
            println("Message sent: ${sendMessageResponse.message.ts}")
        } else {
            println("Error: ${sendMessageResponse.error}")
        }
    }

    fun updateMessage(slackMessage: SlackMessage) {
        findChannelIdAndMessageTs(slackMessage)?.let { result ->
            // Update the old message
            val updateResponse = slack.methods(SlackConstants.BOT_TOKEN).chatUpdate {
                it.channel(result.first)
                    .text(slackMessage.getUpdatedMessage())
                    .ts(result.second)
            }

            if (updateResponse.isOk) {
                println("Message updated: ${updateResponse.message.ts}")
            } else {
                println("Error: ${updateResponse.error}")
            }
        }
    }

    fun deleteMessage(slackMessage: SlackMessage) {
        findChannelIdAndMessageTs(slackMessage, true)?.let { result ->
            val deleteMessageResponse = slack.methods(SlackConstants.BOT_TOKEN)
                .chatDelete { it.channel(result.first).ts(result.second) }

            if (deleteMessageResponse.isOk) {
                println("Message deleted: ${deleteMessageResponse.ts}")
            } else {
                println("Error: ${deleteMessageResponse.error}")
            }
        }
    }

    private fun findChannelId() : String? {
        // Find channel Id for prChannel
        val channelList = slack.methods(SlackConstants.BOT_TOKEN).conversationsList { it.excludeArchived(true) }
        val myChannel = channelList.channels.find { it.name == SlackConstants.CHANNEL_ID }
        return myChannel?.id
    }

    private fun findChannelIdAndMessageTs(slackMessage: SlackMessage, deleteMessage: Boolean? = false) : Pair<String?, String?>? {
        val channelId = findChannelId()

        // Get conversations by bot in this channel
        val conversationHistory = slack.methods(SlackConstants.BOT_TOKEN).conversationsHistory {
            it.channel(channelId)
        }
        val messages = conversationHistory.messages

        // Find specific message to update
        val myMessage = messages.find { it.text.contains(slackMessage.prTitle)}

        return if (myMessage?.text?.contains(Emoji.APPROVED.slackCode) == true && deleteMessage == false) {
            null
        } else {
            Pair(channelId, myMessage?.ts)
        }
    }

}