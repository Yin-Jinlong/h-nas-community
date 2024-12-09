export interface SeekableProgressBarProps {
    current: number
    duration: number
    timeTogether?: boolean
    chapters?: ChapterInfo[]
    progressExtra?: (p: number) => string[]
}