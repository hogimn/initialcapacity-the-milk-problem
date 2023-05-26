package io.milk.workflow

import org.slf4j.LoggerFactory

/**
 * Implementation of a WorkFinder that finds and marks completed NoopTasks.
 */
class NoopWorkFinder : WorkFinder<NoopTask> {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * Finds requested NoopTasks.
     *
     * @param name The name of the requested task.
     * @return A list of NoopTasks.
     */
    override fun findRequested(name: String): List<NoopTask> {
        logger.info("finding work.")

        return mutableListOf(NoopTask("task-name", "task-value"))
    }

    /**
     * Marks a NoopTask as completed.
     *
     * @param info The NoopTask to mark as completed.
     */
    override fun markCompleted(info: NoopTask) {
        logger.info("marking work complete.")
    }
}
