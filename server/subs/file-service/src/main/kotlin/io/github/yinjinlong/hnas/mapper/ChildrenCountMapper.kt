package io.github.yinjinlong.hnas.mapper

import io.github.yinjinlong.hnas.entity.ChildrenCount
import io.github.yinjinlong.hnas.entity.FileId
import io.github.yinjinlong.hnas.entity.IChildrenCount
import org.apache.ibatis.annotations.*

/**
 * @author YJL
 */
@Mapper
interface ChildrenCountMapper {

    companion object {
        const val TABLE = IChildrenCount.TABLE
    }

    //******//
    //  查  //
    //******//

    @Select("select fid, sub_count, subs_count from $TABLE where fid = #{fid}")
    fun selectByFid(fid: FileId): ChildrenCount?

    @Select("select fid, sub_count, subs_count from $TABLE where fid = #{fid} for update")
    fun selectByFidLock(fid: FileId): ChildrenCount?

    //******//
    //  增  //
    //******//

    @Insert("insert into $TABLE(fid,sub_count,subs_count) VALUES (#{fid},0,0)")
    fun insert(fid: FileId): Int

    //******//
    //  改  //
    //******//

    @Update("update $TABLE set sub_count = #{subCount}, subs_count = #{subsCount} where fid = #{fid}")
    fun updateCount(fid: FileId, subCount: Int, subsCount: Int): Int

    //******//
    //  删  //
    //******//

    @Delete("delete from $TABLE where fid = #{fid}")
    fun deleteById(fid: FileId): Int

}
