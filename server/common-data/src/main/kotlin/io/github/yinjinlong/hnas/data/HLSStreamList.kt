package io.github.yinjinlong.hnas.data

/**
 * @author YJL
 */
data class HLSStreamList(
    val codec: String,
    val streams: List<HLSStream>
)