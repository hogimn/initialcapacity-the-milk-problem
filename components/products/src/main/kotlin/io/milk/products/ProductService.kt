package io.milk.products

/**
 * A service class for managing products.
 *
 * @property dataGateway The data gateway for accessing and manipulating product records.
 */
class ProductService(private val dataGateway: ProductDataGateway) {
    /**
     * Retrieve all products and map them to `ProductInfo` objects.
     *
     * @return A list of `ProductInfo` representing all products.
     */
    fun findAll(): List<ProductInfo> {
        return dataGateway.findAll().map { ProductInfo(it.id, it.name, it.quantity) }
    }

    /**
     * Retrieve all products and map them to `ProductInfo` objects.
     *
     * @return A list of `ProductInfo` representing all products.
     */
    fun findBy(id: Long): ProductInfo {
        val record = dataGateway.findBy(id)!!
        return ProductInfo(record.id, record.name, record.quantity)
    }

    /**
     * Update the quantity of a product based on a purchase and return the updated `ProductInfo`.
     *
     * @param purchase The purchase information containing the product ID and amount.
     * @return The updated `ProductInfo` object representing the product after the update.
     * @throws NoSuchElementException if the product with the specified ID does not exist.
     */
    fun update(purchase: PurchaseInfo): ProductInfo {
        val record = dataGateway.findBy(purchase.id)!!
        record.quantity -= purchase.amount
        dataGateway.update(record)
        return findBy(record.id)
    }

    /**
     * Decrement the quantity of a product based on a purchase.
     *
     * @param purchase The purchase information containing the product ID and amount.
     */
    // TODO - Implement the function to decrement the quantity of a product based on a purchase
    fun decrementBy(purchase: PurchaseInfo) {
        // TODO - Implement the function.
        return dataGateway.decrementBy(purchase)
    }
}