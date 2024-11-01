package com.yjl.hnas.mapper

import com.yjl.hnas.entity.VFile
import com.yjl.hnas.entity.VFileId
import org.apache.ibatis.annotations.Insert
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

    @Select("select * from vfile where fid = #{fid}")
    fun selectById(id: VFileId): VFile?

    @Select("select * from vfile where parent = #{parent}")
    fun selectsByParent(parent: VFileId): List<VFile>

    //******//
    //  增  //
    //******//

    @Insert("insert into vfile(fid, name, parent, owner,create_time,update_time, type) VALUES (#{fid}, #{name}, #{parent}, #{owner},#{createTime}, #{updateTime}, #{type})")
    fun insert(vFile: VFile): Int
}