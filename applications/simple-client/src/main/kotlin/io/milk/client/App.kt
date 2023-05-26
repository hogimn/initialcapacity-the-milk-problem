package io.milk.client

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.jetty.*
import io.milk.workflow.WorkScheduler
import okhttp3.OkHttpClient
import java.util.*

/**
 * Function to configure the Ktor application
 */
fun Application.module() {
    // Install the routing feature
    install(Routing) {
        // Define a GET route for the root path "/"
        get("/") {
            // Respond with the text "ok!"
            call.respondText { "ok!" }
        }
    }

    // Get the URL for the products server from the environment variables, defaulting to a local URL if not found
    val urlString: String = System.getenv("PRODUCTS_SERVER") ?: "http://localhost:8081/api/v1/products"

    // Create an instance of OkHttpClient
    val httpClient = OkHttpClient().newBuilder().build()

    // Create a list of PurchaseRecorder workers, based on the number 1 to 4
    val workers = (1..4).map {
        PurchaseRecorder(httpClient, urlString)
    }

    // Create an instance of WorkScheduler with a PurchaseGenerator, the workers, and a maximum queue size of 10
    val scheduler = WorkScheduler(PurchaseGenerator(), workers, 10)

    // Start the work scheduler
    scheduler.start()
}

/**
 * Entry point of the application
 */
fun main() {
    // Set the default time zone to UTC
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

    // Get the port number from the environment variable, defaulting to 8082 if not found
    val port = System.getenv("PORT")?.toInt() ?: 8082

    // Start the Ktor server using the Jetty engine and the configured application module
    embeddedServer(Jetty, port, module = Application::module).start()
}
