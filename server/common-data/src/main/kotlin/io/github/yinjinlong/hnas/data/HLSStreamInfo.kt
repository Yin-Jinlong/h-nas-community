package io.github.yinjinlong.hnas.data

/**
 * @author YJL
 */
data class HLSStreamInfo(
    val codec: String,
    val streams: List<HLSStream>
)