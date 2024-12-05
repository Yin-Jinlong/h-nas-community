import {authToken, token, user} from '@/utils/globals'
import {HMessage} from '@yin-jinlong/h-ui'
import axios, {AxiosError, AxiosRequestConfig, AxiosResponse} from 'axios'
import {Base64} from 'js-base64'
import qs from 'qs'

export declare interface RespData<T> {
    code: number
    msg?: string
    data?: T
}

const AUTH_HEADERS = {
    Authorization: token.value
}

const FORM_HEADER = {
    'Content-Type': 'application/x-www-form-urlencoded'
}

async function get<R>(url: string, params?: object, config?: AxiosRequestConfig<R>) {
    let resp = await axios.get<RespData<R>>(url, {
        params: params,
        ...config,
    })
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
    params?: object,
    config?: AxiosRequestConfig<D>
) {
    let resp = await axios.delete<RespData<R>>(url, {
        params: params,
        ...config
    })
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

async function catchWithAuth(e: AxiosError<any> | RespData<any>) {
    if (e instanceof Error) {
        let resp = e.response
        if (resp && resp.data?.code) {
            let data = resp.data
            let t = authToken.value
            if ((t && data.code == 100) || (!t && data.code == 2003)) {
                reAuth().then((r) => {
                    if (!r)
                        return
                    HMessage.error('请重试')
                })
            }
        }
    }
    catchError(e)
}

async function getPublicFiles(path: string) {
    return get<FileInfo[]>('api/file/public/files', {
        path: path
    })
        .then(resp => resp.data)
        .catch(catchError)
}

async function getPublicFilePreview(path: string) {
    return get<FilePreview>('api/file/public/preview/info', {
        path: path
    })
        .then(resp => resp.data)
        .catch(catchError)
}

async function getDirChildrenCount(dir: string) {
    return get<FolderChildrenCount>('api/file/public/folder/count', {
        path: dir
    }).then(res => res.data)
        .catch(catchError)
}

async function getPublicHLSInfo(path: string) {
    return get<HLSStreamInfo[]>('api/file/public/video/stream/info', {
        path: path
    }).then(resp => resp.data)
        .catch(catchError)
}

async function getPublicVideoChapter(path: string) {
    return get<ChapterInfo[]>('api/file/public/video/chapter', {
        path: path
    }).then(resp => resp.data)
        .catch(catchError)
}

async function getPublicAudioInfo(path: string) {
    return get<AudioFileInfo>('api/file/public/audio/info', {
        path
    }).then(resp => resp.data)
        .catch(catchError)
}

async function deletePublicFile(path: string) {
    return del<void>('api/file/public', {
        path: path
    }, {
        headers: {
            ...FORM_HEADER,
            ...AUTH_HEADERS,
        },
    })
        .then(resp => true)
        .catch(catchWithAuth)
}

async function newPublicFolder(folder: string, uid: number) {
    return post<boolean>('api/file/public/folder', {
        path: folder,
        user: uid,
    }, {
        headers: {
            ...FORM_HEADER,
            ...AUTH_HEADERS,
        },
    })
        .then(resp => true)
        .catch(catchWithAuth)
}

async function reAuth() {
    return post('api/user/auth', {}, {
        headers: {
            'Authorization': authToken.value
        },
    }, resp => {
        let auth = resp.headers['authorization']
        if (auth)
            token.value = auth
    }).catch(catchError)
}

async function login(logId: string, password?: string) {
    return post<UserInfo>('api/user/login', {
        logId: logId,
        password: password
    }, {
        headers: {
            ...FORM_HEADER,
            ...AUTH_HEADERS,
        },
    }, resp => {
        let auth = resp.headers['authorization']
        if (auth) {
            authToken.value = auth
            if (!token.value)
                reAuth()
        }
    }).then(resp => resp.data)
        .catch(catchError)
}

async function tryLogin(logId: string) {
    return post<UserInfo>('api/user/login', {
        logId: logId,
    }, {
        headers: {
            ...FORM_HEADER,
            ...AUTH_HEADERS,
        },
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
    return `api/file/public?${qs.stringify({path})}`
}

function publicThumbnailURL(path: string) {
    return `api/file/public/thumbnail?${qs.stringify({path})}`
}

function publicPreviewURL(path: string) {
    return `api/file/public/preview?${qs.stringify({path})}`
}

function publicHSLURL(path: string) {
    return `api/file/public/video/stream/${path}`
}

function publicAudioCoverURL(path: string) {
    return `api/file/public/audio/cover?${qs.stringify({path})}`
}

async function uploadPublic(path: string, hash: string, file: File, range: FileRange) {
    return new Promise<boolean>(async (resolve, reject) => {
        post<boolean>('api/file/public/upload', file.slice(range.start, range.end), {
            headers: {
                'Authorization': token.value,
                'Content-ID': Base64.encodeURL(path),
                'Hash': hash,
                'Content-Type': 'application/octet-stream',
                'Content-Range': `${range.start}-${range.end}/${file.size}`
            },
        })
            .then(resp => resolve(resp.data ?? false))
            .catch(e => {
                catchWithAuth(e)
                reject(e)
            })
    })
}

async function renamePublic(path: string, name: string) {
    return post<boolean>('api/file/public/rename', {
        path: path,
        name: name
    }, {
        headers: {
            ...FORM_HEADER,
            ...AUTH_HEADERS,
        },
    }).then(resp => true)
        .catch(catchWithAuth)
}

watch(token, (nv) => {
    AUTH_HEADERS.Authorization = nv
}, {
    immediate: true
})

const API = {
    login,
    tryLogin,
    logon,
    getPublicHLSInfo,
    getPublicAudioInfo,
    getPublicVideoChapter,
    getPublicFiles,
    getDirChildrenCount,
    getPublicFilePreview,
    deletePublicFile,
    newPublicFolder,
    uploadPublic,
    publicFileURL,
    publicThumbnailURL,
    publicPreviewURL,
    publicHSLURL,
    publicAudioCoverURL,
    renamePublic
}

export default API