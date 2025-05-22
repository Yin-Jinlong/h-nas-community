package io.github.yinjinlong.hnas.mapper

import io.github.yinjinlong.hnas.entity.*
import org.apache.ibatis.annotations.*
import java.sql.Timestamp

/**
 * @author YJL
 */
@Mapper
interface VirtualFileMapper {

    companion object {
        const val TABLE = IVirtualFile.TABLE
    }

    //******//
    //  查  //
    //******//

    @Select(
        """
select fid, name, parent, hash, owner, user, create_time, update_time, size, extra
from $TABLE 
where fid = #{fid}
"""
    )
    fun selectById(id: FileId): VirtualFile?

    @Select(
        """
select update_time
from $TABLE 
where fid = #{fid}
"""
    )
    fun selectUpdateTimeById(id: FileId): Timestamp?

    @Select(" select fid from $TABLE where name = #{name} and parent = #{parent}")
    fun selectIdByNameParent(name: String, parent: FileId): FileId?

    @Select(
        """
select fid
from $TABLE 
where user = #{user} and parent = #{parent}
limit 1
"""
    )
    fun selectRootIdByUser(user: Uid, parent: Hash = Hash(IVirtualFile.ID_LENGTH)): FileId?

    @Select(
        """
select fid, name, parent, hash, owner, user, create_time, update_time, size, extra
from $TABLE 
where fid = #{fid} 
for update
"""
    )
    fun selectByIdLock(id: FileId): VirtualFile?

    @Select(
        """
select fid, name, parent, hash, owner, user, create_time, update_time, size, extra
from $TABLE 
where parent = #{parent} 
order by hash is not null,name
"""
    )
    fun selectListByParent(parent: FileId): List<VirtualFile>

    @Select(
        """
select fid, name, parent, hash, owner, user, create_time, update_time, size, extra
from $TABLE
where user=#{uid} and fid>#{last} and match(name) against(#{name})
order by fid
limit #{limit}
    """
    )
    fun selectListByName(uid: Uid, name: String, last: FileId, limit: Int): List<VirtualFile>

    @Select("select count(*) from virtual_file where hash = #{hash} limit 2")
    fun countHash(hash: Hash): Int

    @Select("select sum(size) from virtual_file where parent =convert('\\0\\0\\0\\0\\0\\0\\0\\0\\0\\0\\0\\0\\0\\0\\0\\0',binary ) and user!=0")
    fun countUserStorageUsage(): Long

    //******//
    //  增  //
    //******//

    @Insert(
        """
insert into $TABLE(hash, name, parent, owner,user,size) 
VALUES (#{hash}, #{name}, #{parent}, #{owner}, #{user}, #{size})
"""
    )
    fun insert(virtualFile: VirtualFile): Int

    //******//
    //  改  //
    //******//

    @Update("update $TABLE set name = #{name} where fid = #{fid}")
    fun updateName(fid: FileId, name: String): Int

    @Update("update $TABLE set size = #{size} where fid = #{fid}")
    fun updateSize(fid: FileId, size: Long): Int

    @Update("update $TABLE set update_time = #{time} where fid = #{fid}")
    fun updateUpdateTime(fid: FileId, time: Timestamp): Int

    @Update(
        """
update $TABLE set extra = #{extra} where fid = #{fid}
"""
    )
    fun updateExtra(fid: FileId, extra: String)

    //******//
    //  删  //
    //******//

    @Delete("delete from $TABLE where fid = #{id}")
    fun deleteById(id: FileId): Int
}
