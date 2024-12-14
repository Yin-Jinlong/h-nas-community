import {FileWrapper} from './type'
import API from '@/utils/api'
import {subPath} from '@/utils/path'
import {sec2MinuteStr} from '@/utils/time'

export function onInfoCommand(f: FileWrapper) {
    if (f.info.mediaType?.startsWith('audio/')) {
        API.getPublicAudioInfo(subPath(f.info.dir, f.info.name)).then((res?: AudioFileInfo) => {
            if (!res)
                return
            f.extraInfo = {
                '标题': res.title,
                '子标题': res.subTitle,
                '艺术家': res.artists,
                '时长': sec2MinuteStr(res.duration),
                '专辑': res.album,
                '年份': res.year,
                '序号': res.num?.toString(),
                '风格': res.style,
                '比特率': `${res.bitrate} kbps`,
                '备注': res.comment,
            }
        })
    }
}