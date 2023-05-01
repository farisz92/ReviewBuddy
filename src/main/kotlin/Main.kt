import com.google.gson.Gson
import com.slack.api.bolt.App
import com.slack.api.bolt.jetty.SlackAppServer
import github.GitHubLogicHandler
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import github.model.ActionType
import github.model.GitHubEvent
import java.awt.Event

private val gitHubLogicHandler = GitHubLogicHandler()
private val gson = Gson()

var lastMessageTimeStamp = String()
var lastEventType: ActionType? = null

fun main(args: Array<String>) {
    embeddedServer(Netty, port = 3001) {
        routing {
            post("/webhook") {
                val payload = call.receiveText()
                println(payload)
                val event = gson.fromJson(payload, GitHubEvent::class.java)
                if ((lastMessageTimeStamp != event.pullRequest.updatedTimeStamp && lastEventType != event.action)
                    || (event.action == ActionType.REVIEW_REQUEST_REMOVED && lastEventType != ActionType.REVIEW_REQUEST_REMOVED)
                    || event.action != ActionType.REVIEW_REQUESTED) {
                    println("Handling Github Action")
                    lastMessageTimeStamp = event.pullRequest.updatedTimeStamp
                    lastEventType = event.action
                    gitHubLogicHandler.handleGitHubEvent(event)
                }
                call.respondText("Received payload")
            }
        }
    }.start(wait = true)
}