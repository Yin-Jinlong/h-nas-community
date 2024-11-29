export type FileGridCommand = 'play' | 'rename' | 'del' | 'info' | 'count'

export declare interface FileGridOptionsProps {
    dir: boolean
    mediaType?: string
}