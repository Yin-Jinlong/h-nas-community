package io.github.yinjinlong.hnas.service

import io.github.yinjinlong.hnas.utils.dbRecordNotFound

/**
 * @author YJL
 */
interface IService {

    val table: String

    fun notfound(with: Any?): Nothing = dbRecordNotFound(table, with)

}
