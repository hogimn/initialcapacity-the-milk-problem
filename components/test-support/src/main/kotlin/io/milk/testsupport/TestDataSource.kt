package io.milk.testsupport

import io.milk.database.createDatasource
import javax.sql.DataSource

/**
 * The JDBC URL for the test database.
 */
const val testJdbcUrl = "jdbc:postgresql://localhost:5432/milk_test"

/**
 * The username for the test database.
 */
const val testDbUsername = "milk"

/**
 * The password for the test database.
 */
const val testDbPassword = "milk"

/**
 * Creates and returns a DataSource for the test database.
 * It uses the specified JDBC URL, username, and password to establish a connection to the database.
 *
 * @return The DataSource instance for the test database.
 */
fun testDataSource(): DataSource {
    return createDatasource(
        jdbcUrl = testJdbcUrl,
        username = testDbUsername,
        password = testDbPassword
    )
}
