package io.milk.products

import io.milk.database.DatabaseTemplate
import io.milk.database.TransactionManager
import org.slf4j.LoggerFactory
import javax.sql.DataSource

/**
 * The data gateway class for accessing and manipulating product data in the database.
 *
 * @param dataSource The DataSource instance for connecting to the database.
 */
class ProductDataGateway(private val dataSource: DataSource) {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val template = DatabaseTemplate(dataSource)

    /**
     * Creates a new product with the given name and quantity.
     *
     * @param name The name of the product.
     * @param quantity The quantity of the product.
     * @return The created ProductRecord instance.
     */
    fun create(name: String, quantity: Int): ProductRecord {
        return template.create(
                "insert into products (name, quantity) values (?, ?)", { id ->
            ProductRecord(id, name, quantity)
        }, name, quantity
        )
    }

    /**
     * Retrieves all products from the database.
     *
     * @return The list of ProductRecord instances representing all products.
     */
    fun findAll(): List<ProductRecord> {
        return template.findAll("select id, name, quantity from products order by id") { rs ->
            ProductRecord(rs.getLong(1), rs.getString(2), rs.getInt(3))
        }
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id The ID of the product.
     * @return The ProductRecord instance representing the retrieved product, or null if not found.
     */
    fun findBy(id: Long): ProductRecord? {
        return template.findBy(
                "select id, name, quantity from products where id = ?", { rs ->
            ProductRecord(rs.getLong(1), rs.getString(2), rs.getInt(3))
        }, id
        )
    }

    /**
     * Updates a product in the database.
     *
     * @param product The updated ProductRecord instance.
     * @return The updated ProductRecord instance.
     */
    fun update(product: ProductRecord): ProductRecord {
        template.update(
                "update products set name = ?, quantity = ? where id = ?",
                product.name, product.quantity, product.id
        )
        return product
    }

    /**
     * Decrements the quantity of a product by the specified amount within a transaction.
     *
     * @param purchase The PurchaseInfo object containing the product ID and amount to decrement.
     */
    fun decrementBy(purchase: PurchaseInfo) {
        return TransactionManager(dataSource).withTransaction {
            // The query "select ... for update" (or read by lock) is to be thread-safe.
            val found = template.findBy(
                    it,
                    "select id, name, quantity from products where id = ? for update", { rs ->
                ProductRecord(rs.getLong(1), rs.getString(2), rs.getInt(3))
            }, purchase.id
            )
            template.update(
                    it,
                    "update products set quantity = ? where id = ?",
                    (found!!.quantity - purchase.amount), purchase.id
            )
        }
    }

    /**
     * Faster version of decrementing the quantity of a product by the specified amount within a transaction.
     *
     * @param purchase The PurchaseInfo object containing the product ID and amount to decrement.
     */
    fun fasterDecrementBy(purchase: PurchaseInfo) {
        logger.info(
                "decrementing the {} quantity by {} for product_id={}",
                purchase.name,
                purchase.amount,
                purchase.id
        )

        return TransactionManager(dataSource).withTransaction {
            template.update(
                    it,
                    "update products set quantity = (quantity - ?) where id = ?",
                    purchase.amount, purchase.id
            )
        }
    }
}
