export declare interface FileInfo {
    path: string
    fileType: 'FOLDER' | 'FILE'
    type: string
    subType: string
    preview?: string
    createTime: number
    updateTime: number
    size: number
}
