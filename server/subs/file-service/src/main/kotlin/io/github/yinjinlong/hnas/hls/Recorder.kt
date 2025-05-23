package io.github.yinjinlong.hnas.hls

import org.bytedeco.javacv.Frame

/**
 * @author YJL
 */
interface Recorder : AutoCloseable {

    fun start()

    fun record(frame: Frame)

    fun finish()

}