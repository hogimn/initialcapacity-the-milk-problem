package io.milk.products

/**
 * Data class representing information about a purchase.
 *
 * @property id The ID of the product.
 * @property name The name of the product.
 * @property amount The amount of the product being purchased.
 */
data class PurchaseInfo(val id: Long, val name: String, val amount: Int)
