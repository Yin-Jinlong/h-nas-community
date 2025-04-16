package com.yjl.hnas.mapper

import com.yjl.hnas.entity.*
import org.apache.ibatis.annotations.*
import java.sql.Timestamp

/**
 * @author YJL
 */
@Mapper
interface VirtualFileMapper {
    //******//
    //  查  //
    //******//

    @Select(
        """
select fid, name, parent, hash, owner,user, create_time, update_time, size 
from virtual_file 
where fid = #{fid}
"""
    )
    fun selectById(id: FileId): VirtualFile?

    @Select(
        """
select update_time
from virtual_file 
where fid = #{fid}
"""
    )
    fun selectUpdateTimeById(id: FileId): Timestamp?

    @Select(
        """
select fid
from virtual_file 
where name = #{name} and parent = #{parent}
"""
    )
    fun selectIdByNameParent(name: String, parent: FileId): FileId?

    @Select(
        """
select fid
from virtual_file 
where user = #{user} and parent = #{parent}
limit 1
"""
    )
    fun selectRootIdByUser(user: Uid, parent: Hash = Hash(IVirtualFile.ID_LENGTH)): FileId?

    @Select(
        """
select fid, name, parent, hash, owner,user, create_time, update_time ,size
from virtual_file 
where fid = #{fid} 
for update
"""
    )
    fun selectByIdLock(id: FileId): VirtualFile?

    @Select(
        """
select fid, name, parent, hash, owner,user, create_time, update_time, size
from virtual_file 
where parent = #{parent} 
order by hash is not null,name
"""
    )
    fun selectsByParent(parent: FileId): List<VirtualFile>

    @Select("select count(*) from virtual_file where hash = #{hash} limit 2")
    fun countHash(hash: Hash): Int

    //******//
    //  增  //
    //******//

    @Insert(
        """
insert into virtual_file(hash, name, parent, owner,user,size) 
VALUES (#{hash}, #{name}, #{parent}, #{owner}, #{user}, #{size})
"""
    )
    fun insert(virtualFile: VirtualFile): Int

    //******//
    //  改  //
    //******//

    @Update("update virtual_file set name = #{name} where fid = #{fid}")
    fun updateName(fid: FileId, name: String): Int

    @Update("update virtual_file set size = #{size} where fid = #{fid}")
    fun updateSize(fid: FileId, size: Long): Int

    @Update("update virtual_file set update_time = #{time} where fid = #{fid}")
    fun updateUpdateTime(fid: FileId, time: Timestamp): Int

    //******//
    //  删  //
    //******//

    @Delete("delete from virtual_file where fid = #{id}")
    fun deleteById(id: FileId): Int
}
