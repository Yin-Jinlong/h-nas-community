package com.yjl.hnas.mapper

import com.yjl.hnas.entity.PublicVFile
import com.yjl.hnas.entity.VFileId
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select

/**
 * @author YJL
 */
@Mapper
interface PublicVFileMapper {
    //******//
    //  查  //
    //******//

    @Select("select * from public_vfile where fid = #{fid}")
    fun selectById(id: VFileId): PublicVFile?

    //******//
    //  增  //
    //******//

    @Insert("insert into public_vfile(fid) VALUES (#{fid})")
    fun insert(vFile: PublicVFile): Int
}