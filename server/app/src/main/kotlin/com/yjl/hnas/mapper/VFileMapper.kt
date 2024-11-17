package com.yjl.hnas.mapper

import com.yjl.hnas.entity.VFile
import com.yjl.hnas.entity.VFileId
import org.apache.ibatis.annotations.Delete
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

    @Select("select count(1) from vfile where hash = #{hash}")
    fun countHash(hash: String): Int

    //******//
    //  增  //
    //******//

    @Insert("insert into vfile(fid,hash, name, parent, owner,create_time,update_time) VALUES (#{fid},#{hash}, #{name}, #{parent}, #{owner},#{createTime}, #{updateTime})")
    fun insert(vFile: VFile): Int

    //******//
    //  删  //
    //******//

    @Delete("delete from vfile where fid = #{id}")
    fun deleteById(id: VFileId): Int
}
