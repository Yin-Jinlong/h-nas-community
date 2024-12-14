import {FileWrapper} from '@/pages/home/src/type'
import API from '@/utils/api'
import {subPath} from '@/utils/path'
import {files, images} from './files'

export const nowIndex = ref(-2)

function getUrl(f: FileWrapper) {
    if (f.preview.preview == '')
        return ''
    return API.publicPreviewURL(f.preview.preview!!)
}

export function getNow(): string | undefined {
    let i = nowIndex.value
    if (i < 0)
        i = 0
    else if (i >= files.length)
        i = files.length - 1
    let f = images[i]
    nowIndex.value = i
    if (!f)
        return
    return getUrl(f)
}

export function getRaw(): string | undefined {
    let i = nowIndex.value
    if (i < 0)
        return
    let f = images[i]
    return API.publicFileURL(subPath(f.info.dir, f.info.name))
}

export function getRawSize(): number | undefined {
    let i = nowIndex.value
    if (i < 0)
        return
    let f = images[i]
    return f.info.size
}

export function getPrev(): string | undefined {
    let i = nowIndex.value - 1
    if (i < 0)
        i = images.length - 1
    let f = images[i]
    nowIndex.value = i
    if (!f)
        return
    return getUrl(f)
}

export function getNext(): string | undefined {
    let i = nowIndex.value + 1
    if (i >= images.length)
        i = 0
    nowIndex.value = i
    let f = images[i]
    if (!f)
        return
    return getUrl(f)
}