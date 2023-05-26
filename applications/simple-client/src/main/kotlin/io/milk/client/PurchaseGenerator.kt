package io.milk.client

import io.milk.workflow.WorkFinder
import org.slf4j.LoggerFactory

/**
 * Represents a purchase generator that generates purchase tasks for milk.
 */
class PurchaseGenerator : WorkFinder<PurchaseTask> {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * Generates a list of requested purchase tasks for milk.
     *
     * @param name The name associated with the request (not used in this implementation).
     * @return The list of generated purchase tasks.
     */
    override fun findRequested(name: String): List<PurchaseTask> {
        // Generate a random quantity of milk to be purchased
        val random = (1..4).random()

        // Log a message indicating that someone purchased some milk
        logger.info("someone purchased some milk!")

        // Create a single PurchaseTask object with the generated random quantity and return it as a list
        return mutableListOf(PurchaseTask(105442, "milk", random))
    }

    /**
     * Marks a purchase task as completed.
     *
     * @param info The purchase task to mark as completed.
     */
    override fun markCompleted(info: PurchaseTask) {
        // Log a message indicating that the purchase has been completed
        logger.info("completed purchase")
    }
}
