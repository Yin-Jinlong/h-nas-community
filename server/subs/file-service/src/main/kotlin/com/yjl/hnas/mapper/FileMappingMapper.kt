package com.yjl.hnas.mapper

import com.yjl.hnas.entity.FileMapping
import com.yjl.hnas.entity.Hash
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
    fun selectByHash(hash: Hash): FileMapping?

    //******//
    //  增  //
    //******//

    @Insert("insert into file_mapping(hash,data_path,type,sub_type ,preview,size) VALUES (#{hash}, #{dataPath}, #{type}, #{subType},#{preview}, #{size})")
    fun insert(fileMapping: FileMapping): Int

    //******//
    //  改  //
    //******//

    @Update("update file_mapping set size=#{size} where hash = #{hash}")
    fun updateSize(hash: Hash, size: Long): Int

    @Update("update file_mapping set preview=#{preview} where hash = #{hash}")
    fun updatePreview(hash: Hash, preview: Boolean): Int

    //******//
    //  删  //
    //******//

    @Delete("delete from file_mapping where hash = #{hash}")
    fun deleteById(hash: Hash): Int
}