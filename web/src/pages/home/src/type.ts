export interface FileWrapper {
    index: number
    info: FileInfo
    extraInfo?: Record<string, string | undefined>
    preview: FilePreview
    previewIndex?: number
}
