package com.yjl.hnas.mapper

import com.yjl.hnas.entity.VFileId
import com.yjl.hnas.entity.view.VirtualFile
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

/**
 * @author YJL
 */
@Mapper
interface VirtualFileMapper {
    @Select("select * from virtual_file where parent = #{fid}")
    fun selectsByParent(fid: VFileId): List<VirtualFile>
}
