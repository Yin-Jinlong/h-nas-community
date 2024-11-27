package com.yjl.hnas.mapper

import com.yjl.hnas.entity.Uid
import com.yjl.hnas.entity.User
import org.apache.ibatis.annotations.*

/**
 * @author YJL
 */
@Mapper
interface UserMapper {

    //******//
    //  查  //
    //******//

    @Select("select uid, username, nick, password, password_type from user where uid = #{uid}")
    fun selectByUid(uid: Uid): User?

    @Select("select uid, username, nick, password, password_type from user where uid = #{uid} && password = #{password}")
    fun selectByUidPassword(uid: Uid, password: String): User?

    @Select("select uid, username, nick, password, password_type from user where username = #{username}")
    fun selectByUsername(username: String): User?

    @Select("select uid, username, nick, password, password_type from user where username = #{username} && password = #{password}")
    fun selectByUsernamePassword(username: String, password: String): User?

    //******//
    //  增  //
    //******//

    @Options(
        useGeneratedKeys = true,
        keyColumn = "user_id",
        keyProperty = "userId"
    )
    @Insert("insert into user(uid,username, nick, password, password_type) VALUES (default,#{username}, #{nick}, #{password}, #{passwordType})")
    fun insert(user: User): Int

    //******//
    //  改  //
    //******//

    @Update("update user set username = #{username} where uid = #{uid}")
    fun updateUserNameByUid(user: User): Int

    @Update("update user set nick = #{nickName} where uid = #{uid}")
    fun updateNickNameByUid(user: User): Int

    @Update("update user set password = #{password} where uid = #{uid}")
    fun updatePasswordByUid(user: User): Int

    @Update("update user set password_type = #{passwordType} where uid = #{uid}")
    fun updatePasswordTypeByUid(user: User): Int

    //******//
    //  删  //
    //******//

    @Delete("delete from user where uid = #{uid}")
    fun deleteByUid(uid: Uid): Int
}