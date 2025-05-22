package io.github.yinjinlong.hnas.mapper

import io.github.yinjinlong.hnas.entity.IUser
import io.github.yinjinlong.hnas.entity.Uid
import io.github.yinjinlong.hnas.entity.User
import org.apache.ibatis.annotations.*

/**
 * @author YJL
 */
@Mapper
interface UserMapper {

    companion object {
        const val TABLE = IUser.TABLE
    }

    //******//
    //  查  //
    //******//

    @Select("select uid, username, nick, password, password_type,role from $TABLE where uid = #{uid}")
    fun selectByUid(uid: Uid): User?

    @Select("select uid, username, nick, password, password_type,role from $TABLE where uid = #{uid} && password = #{password}")
    fun selectByUidPassword(uid: Uid, password: String): User?

    @Select("select uid, username, nick, password, password_type,role from $TABLE where username = #{username}")
    fun selectByUsername(username: String): User?

    @Select("select uid, username, nick, password, password_type,role from $TABLE where username = #{username} && password = #{password}")
    fun selectByUsernamePassword(username: String, password: String): User?

    @Select("select count(*) from $TABLE limit 1")
    fun hasUser(): Boolean

    @Select("select count(*) from $TABLE")
    fun selectUserCount(): Int

    @Select(
        """
select uid, username, nick, password, password_type,role
from $TABLE
where uid >= #{start}
order by uid
limit #{count}
"""
    )
    fun selectUsers(start: Uid, count: Int): List<User>

    //******//
    //  增  //
    //******//

    @Options(
        useGeneratedKeys = true,
        keyColumn = "user_id",
        keyProperty = "userId"
    )
    @Insert("insert into $TABLE(uid,username, nick, password, password_type,role) VALUES (default,#{username}, #{nick}, #{password}, #{passwordType},#{role})")
    fun insert(user: User): Int

    //******//
    //  改  //
    //******//

    @Update("update $TABLE set username = #{username} where uid = #{uid}")
    fun updateUserNameByUid(user: User): Int

    @Update("update $TABLE set nick = #{nick} where uid = #{uid}")
    fun updateNickNameByUid(user: User): Int

    @Update("update $TABLE set password = #{password} where uid = #{uid}")
    fun updatePasswordByUid(user: User): Int

    @Update("update $TABLE set password_type = #{passwordType} where uid = #{uid}")
    fun updatePasswordTypeByUid(user: User): Int

    //******//
    //  删  //
    //******//

    @Delete("delete from $TABLE where uid = #{uid}")
    fun deleteByUid(uid: Uid): Int
}