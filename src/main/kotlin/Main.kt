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

private val gitHubLogicHandler = GitHubLogicHandler()
private val gson = Gson()

fun main(args: Array<String>) {
    embeddedServer(Netty, port = 3001) {
        routing {
            post("/webhook") {
                val payload = call.receiveText()
                val event = gson.fromJson(payload, GitHubEvent::class.java)
                println(event)
                gitHubLogicHandler.handleGitHubEvent(event)
                call.respondText("Received payload")
            }
        }
    }.start(wait = true)
}