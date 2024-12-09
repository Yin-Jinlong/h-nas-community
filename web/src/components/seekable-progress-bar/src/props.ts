export interface SeekableProgressBarProps {
    current: number
    duration: number
    timeTogether?: boolean
    /**
     * 以分钟显示，而不是小时
     */
    minutes?: boolean
    chapters?: ChapterInfo[]
    progressExtra?: (p: number) => string[]
}