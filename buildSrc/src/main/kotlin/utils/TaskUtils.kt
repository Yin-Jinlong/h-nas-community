package utils

import org.gradle.api.Action
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import kotlin.reflect.KClass

/**
 * @author YJL
 */
internal fun <T : Task> TaskContainer.register(name: String, cls: KClass<T>, config: T.() -> Unit) =
    register(name, cls.java, object : Action<T> {
        override fun execute(t: T) {
            t.config()
        }
    })