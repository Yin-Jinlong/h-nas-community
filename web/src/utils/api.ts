import {token} from '@/utils/globals'
import {HMessage} from '@yin-jinlong/h-ui'
import axios, {AxiosError, AxiosRequestConfig, AxiosResponse} from 'axios'
import {Base64} from 'js-base64'
import CryptoJs from 'crypto-js'

export declare interface RespData<T> {
    code: number
    msg?: string
    data?: T
}


const FORM_HEADER = {
    'Content-Type': 'application/x-www-form-urlencoded',
    'Authorization': token.value
}

async function get<R>(url: string, config?: AxiosRequestConfig<R>) {
    let resp = await axios.get<RespData<R>>(url, config)
    if (resp.data && resp.data.code === 0)
        return resp.data
    throw resp.data
}

async function post<R, D = any>(
    url: string,
    data: D,
    config?: AxiosRequestConfig<R>,
    onResp?: (resp: AxiosResponse<RespData<R>>) => void
) {
    let resp = await axios.post<RespData<R>>(url, data, config)
    if (resp.data && resp.data.code === 0) {
        onResp?.(resp)
        return resp.data
    }
    throw resp.data
}

async function del<R = any, D = any>(
    url: string,
    config?: AxiosRequestConfig<D>
) {
    let resp = await axios.delete<RespData<R>>(url, config)
    if (resp.data && resp.data.code === 0)
        return resp.data
    throw resp.data
}

function catchError(e: AxiosError<any> | RespData<any>): undefined {
    let data: RespData<any>
    if (e instanceof Error) {
        let resp = e.response
        if (resp && resp.data?.code) {
            data = resp.data
        } else {
            HMessage.error(e.message)
            throw e
        }
    } else {
        data = e
    }
    if (!data.msg) {
        HMessage.error('未知错误：' + data.code)
    } else {
        HMessage.error(data.msg + (data.data ? '：' + data.data : ''))
    }
}

async function getPublicFiles(path: string) {
    return get<FileInfo[]>('api/file/public/files', {
        params: {
            path: path
        }
    })
        .then(resp => resp.data)
        .catch(catchError)
}

async function getPublicFileExtraInfo(path: string) {
    return get<FileExtraInfo>('api/file/public/info', {
        params: {
            path: path
        }
    })
        .then(resp => resp.data)
        .catch(catchError)
}

async function deletePublicFile(path: string) {
    return del<void>('api/file/public', {
        headers: FORM_HEADER,
        params: {
            path: path
        }
    })
        .then(resp => true)
        .catch(catchError)
}

async function newPublicFolder(folder: string, uid: number) {
    return post<boolean>('api/file/public/folder', {
        path: folder,
        user: uid,
    }, {
        headers: FORM_HEADER
    })
        .then(resp => true)
        .catch(catchError)
}

async function login(logId: string, password?: string) {
    return post<UserInfo>('api/user/login', {
        logId: logId,
        password: password
    }, {
        headers: FORM_HEADER
    }, resp => {
        let auth = resp.headers['authorization']
        if (auth) {
            token.value = auth
            FORM_HEADER.Authorization = auth
        }
    }).then(resp => resp.data)
        .catch(catchError)
}

async function tryLogin(logId: string) {
    return post<UserInfo>('api/user/login', {
        logId: logId,
    }, {
        headers: FORM_HEADER
    }).then(resp => resp.data)
}

async function logon(userName: string, password: string) {
    return post<boolean>('api/user/logon', {
        username: userName,
        password: password
    }, {
        headers: FORM_HEADER
    }).then(resp => true)
        .catch(catchError)
}

function publicFileURL(path: string) {
    return `api/file/public?path=${path}`
}

function publicPreviewURL(path: string) {
    return `api/file/public/preview?path=${path}`
}

async function getHash(file: File): Promise<string> {
    const sha256 = CryptoJs.algo.SHA256.create()

    const ChunkSize = 4 * 1024 * 1024
    const ReadCount = Math.ceil(file.size / ChunkSize)
    const reader = new FileReader()

    async function readChunk(index: number) {
        return new Promise<string>(resolve => {
            let start = index * ChunkSize
            let end = Math.min(file.size, start + ChunkSize)

            reader.onload = async e => {
                sha256.update(CryptoJs.lib.WordArray.create(e.target!.result as ArrayBuffer))

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

async function uploadPublic(path: string, file: File) {
    return new Promise(async resolve => {
        let hash = await getHash(file)
        post<boolean>('api/file/public/upload', file, {
            headers: {
                'Authorization': token.value,
                'Content-ID': Base64.encodeURL(path + '/' + file.name),
                'Hash': hash,
                'Content-Type': 'application/octet-stream'
            }
        })
            .then(resp => resolve(true))
            .catch(catchError)
    })
}

async function renamePublic(path: string, name: string) {
    return post<boolean>('api/file/public/rename', {
        path: path,
        name: name
    }, {
        headers: FORM_HEADER
    }).then(resp => true)
        .catch(catchError)
}

const API = {
    login,
    tryLogin,
    logon,
    getPublicFiles,
    getPublicFileExtraInfo,
    deletePublicFile,
    newPublicFolder,
    uploadPublic,
    publicFileURL,
    publicPreviewURL,
    renamePublic
}

export default API