export type FileGridCommand = 'play' | 'rename' | 'del' | 'info' | 'count' | 'add-to-play-list' | 'add-all-to-play-list'

export declare interface FileGridOptionsProps {
    dir: boolean
    mediaType?: string
}