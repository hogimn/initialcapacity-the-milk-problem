package io.milk.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.milk.workflow.Worker
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.LoggerFactory

/**
 * Represents a purchase recorder that executes purchase tasks by sending HTTP requests.
 *
 * @property httpClient The OkHttpClient used to send HTTP requests.
 * @property urlString The URL string where the purchase tasks will be sent.
 * @property name The name of the purchase recorder worker (default: "sales-worker").
 */
class PurchaseRecorder(
        private val httpClient: OkHttpClient,
        private val urlString: String,
        override val name: String = "sales-worker"
) : Worker<PurchaseTask> {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val mapper: ObjectMapper = ObjectMapper().registerKotlinModule()
    private val media = "application/json; charset=utf-8".toMediaType()

    /**
     * Executes a purchase task by sending an HTTP request.
     *
     * @param task The purchase task to be executed.
     */
    override fun execute(task: PurchaseTask) {
        try {
            // Convert the task object to JSON string
            val json = mapper.writeValueAsString(task)

            // Create a request body with the JSON string and media type
            val body = json.toRequestBody(media)

            // Build an OkHttp request with the URL and POST method
            val ok = okhttp3.Request.Builder().url(urlString).post(body).build()

            // Log the information about the task being executed
            logger.info("decrementing the {} quantity by {} for product_id={}", task.name, task.amount, task.id)

            // Execute the request and close the response
            httpClient.newCall(ok).execute().close()
        } catch (e: Exception) {
            // Log an error message if an exception occurs during execution
            logger.error(
                    "shoot, failed to decrement the {} quantity by {} for product_id={}",
                    task.name,
                    task.amount,
                    task.id
            )
            e.printStackTrace()
        }
    }
}
