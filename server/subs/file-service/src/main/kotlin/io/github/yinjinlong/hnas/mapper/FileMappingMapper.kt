package io.github.yinjinlong.hnas.mapper

import io.github.yinjinlong.hnas.entity.FileMapping
import io.github.yinjinlong.hnas.entity.Hash
import io.github.yinjinlong.hnas.entity.IFileMapping
import org.apache.ibatis.annotations.*

/**
 * @author YJL
 */
@Mapper
interface FileMappingMapper {

    companion object {
        const val TABLE = IFileMapping.TABLE
    }

    //******//
    //  查  //
    //******//

    @Select("select hash, data_path, type, sub_type, preview, size from $TABLE where hash = #{hash}")
    fun selectByHash(hash: Hash): FileMapping?

    @Select("select concat(type,'/',sub_type) from $TABLE where hash = #{hash}")
    fun selectMediaTypeByHash(hash: Hash): String?

    @Select("select hash, data_path, type, sub_type, preview, size from $TABLE where hash = #{hash} for update")
    fun selectByHashLock(hash: Hash): FileMapping?

    //******//
    //  增  //
    //******//

    @Insert("insert into $TABLE(hash,data_path,type,sub_type ,preview,size) VALUES (#{hash}, #{dataPath}, #{type}, #{subType},#{preview}, #{size})")
    fun insert(fileMapping: FileMapping): Int

    //******//
    //  改  //
    //******//

    @Update("update $TABLE set size=#{size} where hash = #{hash}")
    fun updateSize(hash: Hash, size: Long): Int

    @Update("update $TABLE set preview=#{preview} where hash = #{hash}")
    fun updatePreview(hash: Hash, preview: Boolean): Int

    //******//
    //  删  //
    //******//

    @Delete("delete from $TABLE where hash = #{hash}")
    fun deleteById(hash: Hash): Int
}