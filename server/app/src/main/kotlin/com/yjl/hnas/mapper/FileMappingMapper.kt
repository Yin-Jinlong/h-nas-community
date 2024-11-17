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

    @Select("select * from file_mapping where hash = #{hash}")
    fun selectByHash(hash: String): FileMapping?

    //******//
    //  增  //
    //******//

    @Insert("insert into file_mapping(hash,data_path,type,sub_type ) VALUES (#{hash}, #{dataPath}, #{type}, #{subType})")
    fun insert(fileMapping: FileMapping): Int

    //******//
    //  改  //
    //******//

    @Update("update file_mapping set size=#{size} where hash = #{hash}")
    fun updateSize(hash: String, size: Long): Int

    //******//
    //  删  //
    //******//

    @Delete("delete from file_mapping where hash = #{hash}")
    fun deleteById(hash: String): Int
}