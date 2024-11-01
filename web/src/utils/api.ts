import {FileInfo} from '@/types/file-info'
import {UserInfo} from '@/types/user'
import {HMessage} from '@yin-jinlong/h-ui'
import axios, {AxiosError, AxiosRequestConfig} from 'axios'

export declare interface RespData<T> {
    code: number
    msg?: string
    data?: T
}

const FORM_HEADER = {
    'Content-Type': 'application/x-www-form-urlencoded'
}

async function get<R>(url: string, config?: AxiosRequestConfig<R>) {
    let resp = await axios.get<RespData<R>>(url, config)
    if (resp.data && resp.data.code === 0)
        return resp.data
    throw resp.data
}

async function post<R, D = any>(url: string, data: D, config?: AxiosRequestConfig<R>) {
    let resp = await axios.post<RespData<R>>(url, data, config)
    if (resp.data && resp.data.code === 0)
        return resp.data
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

export async function getFiles() {
    return get<FileInfo[]>('api/files', {
        params: {
            path: '/'
        }
    })
        .then(resp => resp.data)
        .catch(catchError)
}

export async function deleteFile(path: string): Promise<RespData<any>> {
    let resp = await axios.delete('api/file/' + path)
    return resp.data
}

export async function newFolder(folder: string, uid: number) {
    return post<boolean>('api/folder', {
        path: folder,
        uid: uid
    }, {
        headers: FORM_HEADER
    })
        .then(resp => true)
        .catch(catchError)
}

export async function login(logId: string, password?: string) {
    return post<UserInfo>('api/user/login', {
        logId: logId,
        password: password
    }, {
        headers: FORM_HEADER
    }).then(resp => resp.data)
        .catch(catchError)
}

export async function logon(userName: string, password: string) {
    return post<boolean>('api/user/logon', {
        username: userName,
        password: password
    }, {
        headers: FORM_HEADER
    }).then(resp => true)
        .catch(catchError)
}
