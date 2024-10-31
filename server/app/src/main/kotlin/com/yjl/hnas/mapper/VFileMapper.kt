package com.yjl.hnas.mapper

import com.yjl.hnas.entity.VFile
import com.yjl.hnas.entity.VFileId
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

/**
 * @author YJL
 */
@Mapper
interface VFileMapper {
    //******//
    //  查  //
    //******//

    @Select("select * from vfile where fid = #{id}")
    fun selectById(id: VFileId): VFile?

    @Select("select * from vfile where parent = #{parent}")
    fun selectsByParent(parent: VFileId): List<VFile>

}