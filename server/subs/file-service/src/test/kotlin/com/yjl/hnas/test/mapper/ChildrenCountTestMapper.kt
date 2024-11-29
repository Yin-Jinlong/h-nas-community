package com.yjl.hnas.test.mapper

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

/**
 * @author YJL
 */
@Mapper
interface ChildrenCountTestMapper {

    @Select("select count(*) from children_count")
    fun count(): Int

}