package io.milk.client

/**
 * Represents a purchase task.
 *
 * @property id The ID of the purchase task.
 * @property name The name associated with the purchase task.
 * @property amount The amount associated with the purchase task.
 */
data class PurchaseTask(val id: Long, val name: String, val amount: Int)
