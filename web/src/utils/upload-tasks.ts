import API from '@/utils/api'
import {HMessage} from '@yin-jinlong/h-ui'
import CryptoJs from 'crypto-js'
import {reactive} from 'vue'

export enum UploadStatus {
    Error = -1,
    Pending,
    Hash,
    Uploading,
    Success,
    Failed
}

export interface UploadTask {
    file(): File

    status(): UploadStatus

    statusText(): string

    progress(): number

}

function subRanges(range: FileRange, maxChunk: number): FileRange[] {
    let res: FileRange[] = []
    let start = range.start
    let end = range.end
    while (start < end) {
        let next = Math.min(start + maxChunk, end)
        res.push({
            start,
            end: next
        })
        start = next
    }
    return res
}

export class UploadTaskImpl implements UploadTask {
    #path: string
    #file: File

    #status: Ref<UploadStatus>

    #progress: Ref<number>

    #remain: number

    constructor(path: string, file: File) {
        this.#path = path
        this.#file = file
        this.#status = ref<UploadStatus>(UploadStatus.Pending)
        this.#progress = ref(0)
        this.#remain = file.size
    }

    start(onOk: () => void) {
        new Promise(async (resolve: Function) => {
            resolve()
        }).then(async () => {
            try {
                this.#status.value = UploadStatus.Hash
                let hash = await this.#calcHash()
                this.#progress.value = 0
                this.#status.value = UploadStatus.Uploading
                this.#progress.value = 1 - this.#remain / this.#file.size
                for (let r of (subRanges({
                    start: 0,
                    end: this.#file.size
                }, 20 * 1024 * 1024))) {
                    let res = await this.upload(hash, r)
                    if (res) {
                        this.#progress.value = 1
                        this.#status.value = UploadStatus.Success
                        onOk()
                        return
                    }
                }
                await this.upload(hash, {
                    start: this.#file.size,
                    end: this.#file.size
                })
                this.#status.value = UploadStatus.Success
                onOk()
            } catch (e) {
                this.#progress.value = 1
                this.#status.value = UploadStatus.Error
                throw e
            }
        })
    }

    async upload(hash: string, r: FileRange) {
        let res = await API.uploadPublic(this.#path, hash, this.#file, r)
        this.#remain -= r.end - r.start
        this.#progress.value = 1 - this.#remain / this.#file.size
        return res
    }

    file() {
        return this.#file
    }

    status() {
        return this.#status.value
    }

    progress() {
        return this.#progress.value
    }

    statusText() {
        switch (this.#status.value) {
            case UploadStatus.Error:
                return '错误'
            case UploadStatus.Pending:
                return '等待'
            case UploadStatus.Hash:
                return '计算Hash'
            case UploadStatus.Uploading:
                return '上传中'
            case UploadStatus.Success:
                return '上传成功'
            case UploadStatus.Failed:
                return '上传失败'
            default:
                return '未知'
        }
    }

    async #calcHash(): Promise<string> {
        return await getHash(this.#file, p => {
            this.#progress.value += p
        })
    }

}

async function getHash(file: File, onProgress: (p: number) => void): Promise<string> {
    const sha256 = CryptoJs.algo.SHA256.create()

    const ChunkSize = 4 * 1024 * 1024
    const ReadCount = Math.ceil(file.size / ChunkSize)
    const reader = new FileReader()

    async function readChunk(index: number) {
        return new Promise<string>(resolve => {
            let start = index * ChunkSize
            let end = Math.min(file.size, start + ChunkSize)

            reader.onload = async e => {
                let result = e.target!.result as ArrayBuffer
                let read = result!.byteLength
                sha256.update(CryptoJs.lib.WordArray.create(result))
                onProgress(read / file.size)
                if (index < ReadCount - 1)
                    resolve(await readChunk(index + 1))
                else {
                    resolve(CryptoJs.enc.Base64url.stringify(sha256.finalize()))
                }
            }

            reader.readAsArrayBuffer(file.slice(start, end))
        })

    }

    return new Promise<string>(async (resolve, reject) => {
        reader.onerror = function (e) {
            reject(e)
        }

        resolve(await readChunk(0))
    })
}

export const UploadTasks: UploadTask[] = shallowReactive<UploadTask[]>([])

export function uploadPublicFile(path: string, file: File, ok: () => void) {
    let task = new UploadTaskImpl(path, file)
    UploadTasks.push(task)
    task.start(() => {
        HMessage.success('上传成功')
        ok()
    })
}
