package io.milk.start

import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.freemarker.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.jetty.*
import io.milk.database.createDatasource
import io.milk.products.ProductDataGateway
import io.milk.products.ProductService
import io.milk.products.PurchaseInfo
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Configures the application module with the specified JDBC URL, username, and password.
 *
 * @param jdbcUrl the JDBC database URL
 * @param username the username for the database connection
 * @param password the password for the database connection
 */
fun Application.module(jdbcUrl: String, username: String, password: String) {
    // Create a logger instance for the module
    val logger = LoggerFactory.getLogger(this.javaClass)
    // Create a data source using HikariCP with the provided JDBC URL, username, and password
    val dataSource = createDatasource(jdbcUrl, username, password)
    // Create a product service with a product data gateway using the data source
    val productService = ProductService(ProductDataGateway(dataSource))

    // Install DefaultHeaders feature
    // DefaultHeaders allows you to set default response headers
    // for all outgoing responses in your application.
    install(DefaultHeaders)
    // Install CallLogging feature
    // It intercepts each incoming request and outgoing response,
    // and logs relevant information about them
    install(CallLogging)
    // Install FreeMarker feature
    // FreeMarker is a popular template engine for generating dynamic content,
    // such as HTML pages, based on templates and data models.
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
    // Install ContentNegotiation feature with Jackson as the JSON serializer
    // ContentNegotiation allows your application to support multiple content types (e.g., JSON, XML)
    // and automatically serialize and deserialize data based on the requested content type.
    install(ContentNegotiation) {
        jackson()
    }
    // Install routing
    install(Routing) {
        // Define GET route for root URL ("/")
        get("/") {
            // Retrieve all products
            val products = productService.findAll()
            // Respond with FreeMarker template "index.ftl" and pass "products" as a parameter
            call.respond(FreeMarkerContent("index.ftl", mapOf("products" to products)))
        }
        // Define POST route for "/api/v1/products"
        post("/api/v1/products") {
            // Receive purchase information from the request body
            val purchase = call.receive<PurchaseInfo>()

            // Find the current inventory for the purchased product
            val currentInventory = productService.findBy(purchase.id)
            logger.info(
                    "current inventory {}, quantity={}, product_id={}",
                    currentInventory.name,
                    currentInventory.quantity,
                    currentInventory.id
            )

            logger.info(
                    "received purchase for {}, quantity={}, product_id={}",
                    purchase.name,
                    purchase.amount,
                    purchase.id
            )

            // Update the product's inventory by decrementing the quantity
            productService.decrementBy(purchase) // TODO - Replace with decrementBy. Why is using update problematic?

            // Respond with HTTP status code 201 (Created)
            call.respond(HttpStatusCode.Created)
        }
        // Serve static files from the "images" directory
        static("images") { resources("images") }
        // Serve static files from the "style" directory
        static("style") { resources("style") }
    }
}

/**
 * Entry point of the application
 */
fun main() {
    // Set the default time zone to UTC
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    // Retrieve the port number from the environment variable "PORT" or use the default port 8081
    val port = System.getenv("PORT")?.toInt() ?: 8081
    // Retrieve JDBC database URL, username, and password from environment variables
    val jdbcUrl = System.getenv("JDBC_DATABASE_URL")
    val username = System.getenv("JDBC_DATABASE_USERNAME")
    val password = System.getenv("JDBC_DATABASE_USERNAME")

    // Start the embedded Jetty server on the specified port and with the defined module
    embeddedServer(Jetty, port, module = { module(jdbcUrl, username, password) }).start()
}
