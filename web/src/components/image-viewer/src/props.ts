export declare interface ImageViewerProps {
    zIndex?: number
    index: number
    count: number
    minScale?: number
    maxScale?: number
    /**
     * 显示在窗口中的最小占比
     */
    minShowRate?: number
    onGet: () => string | undefined
    onNext: () => string | undefined
    onPrev: () => string | undefined
}

const Default = {
    zIndex: 10000,
    minScale: 0.05,
    maxScale: 5,
    minShowRate: 0.25
}

export default Default