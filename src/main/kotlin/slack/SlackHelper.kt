package slack

import com.slack.api.Slack
import com.slack.api.methods.SlackApiException
import com.slack.api.methods.request.conversations.ConversationsHistoryRequest
import com.slack.api.methods.response.conversations.ConversationsHistoryResponse
import com.slack.api.model.Message
import slack.SlackConstants.BOT_TOKEN
import slack.model.Emoji
import slack.model.SlackMessage
import java.io.IOException

class SlackHelper {

    private val slack = Slack.getInstance()

    fun sendMessage(message: SlackMessage) {
        message.reviewingTeams?.forEach { teamInfo ->
            if (!findChannelIdAndMessageTs(message, false, teamInfo)?.second.isNullOrEmpty()) return@forEach
            val sendMessageResponse = slack.methods(BOT_TOKEN).chatPostMessage{ it.channel(teamInfo.channel).text(message.getNewMessage()) }
            if (sendMessageResponse.isOk) {
                println("Message sent: ${sendMessageResponse.message.ts}")
            } else {
                println("Error: ${sendMessageResponse.error}")
            }
        }
    }

    fun updateMessage(slackMessage: SlackMessage) {
        getBotMessages(slackMessage.prTitle)?.let { result ->
            result.forEach { channelIdAndTimeStamp ->
                val updateResponse = slack.methods(BOT_TOKEN).chatUpdate {
                    it.channel(channelIdAndTimeStamp.first)
                        .text(slackMessage.getUpdatedMessage())
                        .ts(channelIdAndTimeStamp.second)
                }

                if (updateResponse.isOk) {
                    println("Message updated: ${updateResponse.message.ts}")
                } else {
                    println("Error: ${updateResponse.error}")
                }
            }
        }
    }

    fun deleteMessage(slackMessage: SlackMessage, forSpecificTeamInfo: Boolean? = false) {
        getBotMessages(slackMessage.prTitle)?.let { result ->
            if (forSpecificTeamInfo == true) {
                slackMessage.teamRemoved?.let {
                    findChannelIdAndMessageTs(slackMessage, false, slackMessage.teamRemoved)?.let { toRemove ->
                        val deleteMessageResponse = slack.methods(BOT_TOKEN)
                            .chatDelete { it.channel(toRemove.first).ts(toRemove.second) }

                        if (deleteMessageResponse.isOk) {
                            println("Message deleted: ${deleteMessageResponse.ts}")
                        } else {
                            println("Error: ${deleteMessageResponse.error}")
                        }
                    }
                }
            } else {
                result.forEach { channelIdAndTimeStamp ->
                    val deleteMessageResponse = slack.methods(BOT_TOKEN)
                        .chatDelete { it.channel(channelIdAndTimeStamp.first).ts(channelIdAndTimeStamp.second) }

                    if (deleteMessageResponse.isOk) {
                        println("Message deleted: ${deleteMessageResponse.ts}")
                    } else {
                        println("Error: ${deleteMessageResponse.error}")
                    }
                }
            }
        }
    }

    private fun findChannelId(teamInfo: TeamInfo) : String? {
        // Find channel Id for prChannel
        val channelList = slack.methods(BOT_TOKEN).conversationsList { it.excludeArchived(true) }
        val myChannel = channelList.channels.find { it.name == teamInfo.channel }
        return myChannel?.id
    }

    private fun findChannelIdAndMessageTs(slackMessage: SlackMessage, deleteMessage: Boolean? = false, teamInfo: TeamInfo) : Pair<String?, String?>? {
        val channelId = findChannelId(teamInfo)

        // Get conversations by bot in this channel
        val conversationHistory = slack.methods(BOT_TOKEN).conversationsHistory {
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

    private fun getBotMessages(message: String) : List<Pair<String?, String?>>? {
        val client = slack.methods(BOT_TOKEN)
        val channels = client.conversationsList { it.token(BOT_TOKEN) }.channels.filter { it.isMember }

        val messages = mutableListOf<Message>()
        for (channel in channels) {
            val request = ConversationsHistoryRequest.builder()
                .channel(channel.id)
                .token(BOT_TOKEN)
                .build()
            try {
                val response: ConversationsHistoryResponse = client.conversationsHistory(request)
                val messagesToAdd = response.messages.filter { it.botId != null  && it.text.contains(message)}
                messagesToAdd.forEach { it.channel = channel.id }
                messages.addAll(messagesToAdd)
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: SlackApiException) {
                e.printStackTrace()
            }
        }

        val botMessagesWithInfo = messages.map { message ->
            Pair(message.channel, message.ts)
        }

        return botMessagesWithInfo
    }
}