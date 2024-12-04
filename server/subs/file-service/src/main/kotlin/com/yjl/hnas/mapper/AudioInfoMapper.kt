package com.yjl.hnas.mapper

import com.yjl.hnas.entity.AudioInfo
import com.yjl.hnas.entity.Hash
import org.apache.ibatis.annotations.*

/**
 * @author YJL
 */
@Mapper
interface AudioInfoMapper {

    //******//
    //  查  //
    //******//

    @Select(
        """
select fid, title, sub_title, artists, cover, album,duration, year, num, style, bitrate, comment 
from audio_info
where fid = #{fid}
    """
    )
    fun selectByHash(fid: Hash): AudioInfo?

    @Select(
        """
select fid, title, sub_title, artists, cover, album,duration, year, num, style, bitrate, comment 
from audio_info
where fid = #{fid}
for update
    """
    )
    fun selectByHashLock(fid: Hash): AudioInfo?

    //******//
    //  增  //
    //******//

    @Insert(
        """
insert into audio_info(fid, title, sub_title, artists, cover,duration, album, year, num, style, bitrate, comment) 
values (#{fid}, #{title}, #{subTitle}, #{artists}, #{cover},#{duration}, #{album}, #{year}, #{num}, #{style}, #{bitrate}, #{comment})
    """
    )
    fun insert(audioInfo: AudioInfo): Int

    //******//
    //  改  //
    //******//

    @Update(
        """
update audio_info set 
title = #{title}, 
sub_title = #{subTitle}, 
artists = #{artists}, 
cover = #{cover}, 
duration = #{duration},
album = #{album}, 
year = #{year}, 
num = #{num}, 
style = #{style}, 
bitrate = #{bitrate}, 
comment = #{comment} 
where fid = #{fid}
    """
    )
    fun update(audioInfo: AudioInfo): Int

    //******//
    //  删  //
    //******//

    @Delete("delete from audio_info where fid = #{fid}")
    fun deleteById(fid: Hash): Int

}
