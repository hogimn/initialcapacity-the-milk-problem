package io.milk.products

/**
 * A data class representing product information.
 *
 * @property id The ID of the product.
 * @property name The name of the product.
 * @property quantity The quantity of the product.
 */
data class ProductInfo(val id: Long, val name: String, var quantity: Int) {
    /**
     * Increments the quantity of the product by the specified amount.
     *
     * @param amount The amount to increment the quantity by.
     */
    fun incrementBy(amount: Int) {
        quantity += amount
    }

    /**
     * Decrements the quantity of the product by the specified amount.
     *
     * @param amount The amount to decrement the quantity by.
     */
    fun decrementBy(amount: Int) {
        quantity -= amount
    }
}
