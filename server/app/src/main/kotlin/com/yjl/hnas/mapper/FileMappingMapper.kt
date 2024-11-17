package com.yjl.hnas.mapper

import com.yjl.hnas.entity.FileMapping
import com.yjl.hnas.entity.VFileId
import org.apache.ibatis.annotations.*

/**
 * @author YJL
 */
@Mapper
interface FileMappingMapper {

    //******//
    //  查  //
    //******//

    @Select("select * from file_mapping where fid = #{fid}")
    fun selectById(id: VFileId): FileMapping?

    @Select("select COUNT(*) from file_mapping where hash=#{hash} limit 2")
    fun countHash(hash: String): Int

    //******//
    //  增  //
    //******//

    @Insert("insert into file_mapping(fid,data_path,hash,type,sub_type ) VALUES (#{fid}, #{dataPath}, #{hash}, #{type}, #{subType})")
    fun insert(fileMapping: FileMapping): Int

    //******//
    //  改  //
    //******//

    @Update("update file_mapping set data_path = #{dataPath}, hash = #{hash}, type = #{type}, sub_type = #{subType} where fid = #{fid}")
    fun updateById(fileMapping: FileMapping): Int

    //******//
    //  删  //
    //******//

    @Delete("delete from file_mapping where fid = #{id}")
    fun deleteById(id: VFileId): Int
}