import {nowIndex} from '@/pages/home/src/image-viewer-helper'
import {FileWrapper} from '@/pages/home/src/type'
import API from '@/utils/api'

export const files = reactive<FileWrapper[]>([])
export const images = reactive<FileWrapper[]>([])

export function updateFiles(path: string) {
    API.getPublicFiles(path).then(data => {
        if (!data)
            return

        console.log('files', data)
        nowIndex.value = -1
        files.length = 0
        images.length = 0
        data.forEach(f => {
            let file: FileWrapper = {
                index: files.length,
                info: f,
                preview: {
                    thumbnail: '',
                    preview: '',
                },
                lastTime: new Date(f.updateTime).toLocaleString()
            }
            files.push(file)
            if (f.mediaType?.startsWith('image/'))
                images.push(file)
        })
        images.sort((a, b) => {
            return a.index - b.index
        })
        for (let i = 0; i < images.length; i++) {
            let f = images[i]
            f.previewIndex = i
        }
    })
}
