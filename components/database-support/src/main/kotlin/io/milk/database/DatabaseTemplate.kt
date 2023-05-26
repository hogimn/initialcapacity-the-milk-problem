package io.milk.database

import java.sql.*
import java.time.LocalDate
import javax.sql.DataSource

/**
 * A template class for executing database operations using JDBC.
 *
 * @property dataSource The data source to obtain database connections from.
 */
class DatabaseTemplate(private val dataSource: DataSource) {

    /**
     * Function to perform an insert operation and return the generated ID.
     *
     * @param sql The SQL statement for the insert operation.
     * @param id The function to map the generated ID to a specific type.
     * @param params The parameters for the SQL statement.
     * @return The generated ID mapped to the specified type.
     */
    fun <T> create(sql: String, id: (Long) -> T, vararg params: Any) =
            dataSource.connection.use { connection ->
                create(connection, sql, id, *params)
            }

    /**
     * Function to perform an insert operation and return the generated ID.
     *
     * @param sql The SQL statement for the insert operation.
     * @param id The function to map the generated ID to a specific type.
     * @param params The parameters for the SQL statement.
     * @return The generated ID mapped to the specified type.
     */
    fun <T> create(connection: Connection, sql: String, id: (Long) -> T, vararg params: Any): T {
        return connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use { statement ->
            setParameters(params, statement)
            statement.executeUpdate()
            val keys = statement.generatedKeys
            keys.next()
            id(keys.getLong(1))
        }
    }

    /**
     * Function to fetch all records from the database using a provided mapper function.
     *
     * @param sql The SQL statement for fetching all records.
     * @param mapper The function to map a result set row to a specific type.
     * @return The list of mapped objects.
     */
    fun <T> findAll(sql: String, mapper: (ResultSet) -> T): List<T> {
        return query(sql, {}, mapper)
    }

    /**
     * Function to fetch all records from the database using a provided mapper function and connection.
     *
     * @param connection The database connection to use for the query.
     * @param sql The SQL statement for fetching all records.
     * @param mapper The function to map a result set row to a specific type.
     * @return The list of mapped objects.
     */
    fun <T> findAll(connection: Connection, sql: String, mapper: (ResultSet) -> T): List<T> {
        return query(connection, sql, {}, mapper)
    }

    /**
     * Function to fetch a single record from the database based on ID using a provided mapper function.
     *
     * @param sql The SQL statement for fetching a single record.
     * @param mapper The function to map a result set row to a specific type.
     * @param id The ID of the record to fetch.
     * @return The mapped object, or null if no record is found.
     */
    fun <T> findBy(sql: String, mapper: (ResultSet) -> T, id: Long): T? {
        dataSource.connection.use { connection ->
            return findBy(connection, sql, mapper, id)
        }
    }

    /**
     * Function to fetch a single record from the database based on ID using a provided mapper function and connection.
     *
     * @param connection The database connection to use for the query.
     * @param sql The SQL statement for fetching a single record.
     * @param mapper The function to map a result set row to a specific type.
     * @param id The ID of the record to fetch.
     * @return The mapped object, or null if no record is found.
     */
    fun <T> findBy(connection: Connection, sql: String, mapper: (ResultSet) -> T, id: Long): T? {
        val list = query(connection, sql, { ps -> ps.setLong(1, id) }, mapper)
        return list.firstOrNull()
    }

    /**
     * Function to perform an update operation on the database.
     *
     * @param sql The SQL statement for the update operation.
     * @param params The parameters for the SQL statement.
     */
    fun update(sql: String, vararg params: Any) {
        dataSource.connection.use { connection ->
            update(connection, sql, *params)
        }
    }

    /**
     * Function to perform an update operation on the database with a specified connection.
     *
     * @param connection The database connection to use for the update operation.
     * @param sql The SQL statement for the update operation.
     * @param params The parameters for the SQL statement.
     */
    fun update(connection: Connection, sql: String, vararg params: Any) {
        return connection.prepareStatement(sql).use { statement ->
            setParameters(params, statement)
            statement.executeUpdate()
        }
    }

    /**
     * Function to execute a query on the database and return a list of results using a provided mapper function.
     *
     * @param sql The SQL statement for the query.
     * @param params The function to set parameters in the prepared statement.
     * @param mapper The function to map a result set row to a specific type.
     * @return The list of mapped objects.
     */
    fun <T> query(sql: String, params: (PreparedStatement) -> Unit, mapper: (ResultSet) -> T): List<T> {
        dataSource.connection.use { connection ->
            return query(connection, sql, params, mapper)
        }
    }

    /**
     * Function to execute a query on the database with a specified connection and return a list of results using a provided mapper function.
     *
     * @param connection The database connection to use for the query.
     * @param sql The SQL statement for the query.
     * @param params The function to set parameters in the prepared statement.
     * @param mapper The function to map a result set row to a specific type.
     * @return The list of mapped objects.
     */
    fun <T> query(
            connection: Connection,
            sql: String,
            params: (PreparedStatement) -> Unit,
            mapper: (ResultSet) -> T
    ): List<T> {
        val results = ArrayList<T>()
        connection.prepareStatement(sql).use { statement ->
            params(statement)
            statement.executeQuery().use { rs ->
                while (rs.next()) {
                    results.add(mapper(rs))
                }
            }
        }
        return results
    }

    /**
     * Function to set parameter values in a prepared statement.
     *
     * @param params The parameter values.
     * @param statement The prepared statement to set parameters in.
     */
    private fun setParameters(params: Array<out Any>, statement: PreparedStatement) {
        for (i in params.indices) {
            val param = params[i]
            val parameterIndex = i + 1

            when (param) {
                is String -> statement.setString(parameterIndex, param)
                is Int -> statement.setInt(parameterIndex, param)
                is Long -> statement.setLong(parameterIndex, param)
                is Boolean -> statement.setBoolean(parameterIndex, param)
                is LocalDate -> statement.setDate(parameterIndex, Date.valueOf(param))

            }
        }
    }

    /// USED FOR TESTING

    /**
     * Function to execute a SQL statement for testing purposes.
     *
     * @param sql The SQL statement to execute.
     */
    fun execute(sql: String) {
        dataSource.connection.use { connection ->
            connection.prepareCall(sql).use(CallableStatement::execute)
        }
    }
}
