import {token} from '@/utils/globals'
import {HMessage} from '@yin-jinlong/h-ui'
import axios, {AxiosError, AxiosRequestConfig, AxiosResponse} from 'axios'

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

function catchError(e: AxiosError<any> | RespData<any>): undefined {
    let data: RespData<any>
    if (e instanceof Error) {
        if (e.response) {
            data = e.response.data
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

async function getFiles(path: string) {
    return get<FileInfo[]>('api/files', {
        params: {
            path: path
        }
    })
        .then(resp => resp.data)
        .catch(catchError)
}

async function deleteFile(path: string): Promise<RespData<any>> {
    let resp = await axios.delete('api/file/' + path)
    return resp.data
}

async function newFolder(folder: string, uid: number, isPublic: boolean) {
    return post<boolean>('api/folder', {
        path: folder,
        user: uid,
        'public': isPublic
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

const API = {
    login,
    tryLogin,
    logon,
    getFiles,
    deleteFile,
    newFolder
}

export default API