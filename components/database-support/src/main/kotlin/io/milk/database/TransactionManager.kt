package io.milk.database

import java.sql.Connection
import javax.sql.DataSource

/**
 * Class responsible for managing transactions.
 *
 * @property dataSource The data source to obtain connections from.
 */
class TransactionManager(private val dataSource: DataSource) {
    /**
     * Executes the provided function within a transaction.
     *
     * @param function The function to be executed within the transaction, which takes a `Connection` as a parameter and returns a result of type `T`.
     * @return The result of executing the function.
     */
    fun <T> withTransaction(function: (Connection) -> T): T {
        // Obtain a connection from the data source and auto-commit is set to false
        dataSource.connection.use { connection ->
            connection.autoCommit = false
            // Execute the provided function within the transaction
            val results = function(connection)
            // Commit the transaction
            connection.commit()
            // Reset auto-commit to true for subsequent operations
            connection.autoCommit = true
            // Return the results from the function
            return results
        }
    }
}
