package io.milk.workflow

/**
 * An interface representing a work finder for tasks of type T.
 *
 * @param T The type of tasks the work finder deals with.
 */
interface WorkFinder<T> {
    /**
     * Finds and retrieves the requested tasks based on the given name.
     *
     * @param name The name associated with the tasks to be found.
     * @return A list of found tasks.
     */
    fun findRequested(name: String): List<T>

    /**
     * Marks the specified task as completed.
     *
     * @param info The task to be marked as completed.
     */
    fun markCompleted(info: T)
}
