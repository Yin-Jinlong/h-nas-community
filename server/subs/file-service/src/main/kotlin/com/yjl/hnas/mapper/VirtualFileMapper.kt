package com.yjl.hnas.mapper

import com.yjl.hnas.entity.VirtualFile
import com.yjl.hnas.entity.VFileId
import org.apache.ibatis.annotations.*

/**
 * @author YJL
 */
@Mapper
interface VirtualFileMapper {
    //******//
    //  查  //
    //******//

    @Select("select * from virtual_file where fid = #{fid}")
    fun selectById(id: VFileId): VirtualFile?

    @Select("select * from virtual_file where parent = #{parent}")
    fun selectsByParent(parent: VFileId): List<VirtualFile>

    @Select("select count(*) from virtual_file where hash = #{hash} limit 2")
    fun countHash(hash: String): Int

    @Select("select count(*) from virtual_file where parent = #{fid} limit 1")
    fun hasChildren(fid: VFileId): Boolean

    //******//
    //  增  //
    //******//

    @Insert("insert into virtual_file(fid,hash, name, parent, owner,create_time,update_time,size) VALUES (#{fid},#{hash}, #{name}, #{parent}, #{owner},#{createTime}, #{updateTime}, #{size})")
    fun insert(virtualFile: VirtualFile): Int

    //******//
    //  改  //
    //******//

    @Update("update virtual_file set size = #{size} where fid = #{fid}")
    fun updateSize(fid: VFileId, size: Long): Int

    //******//
    //  删  //
    //******//

    @Delete("delete from virtual_file where fid = #{id}")
    fun deleteById(id: VFileId): Int
}
