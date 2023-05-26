package io.milk.database

import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

/**
 * Creates a data source using HikariCP.
 *
 * @param jdbcUrl The JDBC URL of the database.
 * @param username The username for the database connection.
 * @param password The password for the database connection.
 * @return The created data source.
 */
fun createDatasource(
    jdbcUrl: String, username: String, password: String,
): DataSource = HikariDataSource().apply {
    // Set the JDBC URL for the data source
    setJdbcUrl(jdbcUrl)
    // Set the username for the data source
    setUsername(username)
    // Set the password for the data source
    setPassword(password)
}
