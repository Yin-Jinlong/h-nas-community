package io.github.yinjinlong.hnas.fe

import org.apache.tika.mime.MediaType

/**
 * @author YJL
 */
abstract class AbstractFileExtraReader(
    vararg types: MediaType
) : FileExtraReader {

    override val types: Set<MediaType> = HashSet<MediaType>().apply {
        addAll(types)
    }

}
