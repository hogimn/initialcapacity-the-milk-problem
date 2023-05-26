package io.milk.workflow

import org.slf4j.LoggerFactory

/**
 * A worker that performs a no-operation (noop) task.
 *
 * @param name The name of the worker.
 */
class NoopWorker(override val name: String = "noop-worker") : Worker<NoopTask> {

    /**
     * Logger for logging information
     */
    private val logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * Executes the noop task.
     *
     * @param task The noop task to be executed.
     */
    override fun execute(task: NoopTask) {
        // Log the task name and value
        logger.info("doing work. {} {}", task.name, task.value)
    }
}
