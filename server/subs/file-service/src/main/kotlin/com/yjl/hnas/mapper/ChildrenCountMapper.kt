package com.yjl.hnas.mapper

import com.yjl.hnas.entity.ChildrenCount
import com.yjl.hnas.entity.FileId
import org.apache.ibatis.annotations.Delete
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update

/**
 * @author YJL
 */
@Mapper
interface ChildrenCountMapper {

    //******//
    //  查  //
    //******//

    @Select("select fid, sub_count, subs_count from children_count where fid = #{fid}")
    fun selectByFid(fid: FileId): ChildrenCount?

    @Select("select * from children_count where fid = #{fid} for update")
    fun selectByFidLock(fid: FileId): ChildrenCount?

    //******//
    //  增  //
    //******//

    @Insert("insert into children_count(fid,sub_count,subs_count) VALUES (#{fid},0,0)")
    fun insert(fid: FileId): Int

    //******//
    //  改  //
    //******//

    @Update("update children_count set sub_count = #{subCount}, subs_count = #{subsCount} where fid = #{fid}")
    fun updateCount(fid: FileId, subCount: Int, subsCount: Int): Int

    //******//
    //  删  //
    //******//

    @Delete("delete from children_count where fid = #{fid}")
    fun deleteById(fid: FileId): Int

}
