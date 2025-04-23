package io.github.yinjinlong.hnas.mapper

import io.github.yinjinlong.hnas.entity.AudioInfo
import io.github.yinjinlong.hnas.entity.Hash
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
select hash, title, sub_title, artists, cover, album,duration, year, num, style, bitrate, comment, lrc
from audio_info
where hash = #{hash}
    """
    )
    fun selectByHash(hash: Hash): AudioInfo?

    @Select(
        """
select hash, title, sub_title, artists, cover, album,duration, year, num, style, bitrate, comment 
from audio_info
where hash = #{fid}
for update
    """
    )
    fun selectByHashLock(hash: Hash): AudioInfo?

    //******//
    //  增  //
    //******//

    @Insert(
        """
insert into audio_info(hash, title, sub_title, artists, cover,duration, album, year, num, style, bitrate, comment,lrc) 
values (#{hash}, #{title}, #{subTitle}, #{artists}, #{cover},#{duration}, #{album}, #{year}, #{num}, #{style}, #{bitrate}, #{comment}, #{lrc})
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
where hash = #{fid}
    """
    )
    fun update(audioInfo: AudioInfo): Int

    //******//
    //  删  //
    //******//

    @Delete("delete from audio_info where hash = #{fid}")
    fun deleteById(fid: Hash): Int

}
