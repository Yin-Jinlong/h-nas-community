export declare interface ImageViewerProps {
    zIndex?: number
    index: number
    count: number
    minScale?: number
    maxScale?: number
    onGet: () => string | undefined
    onNext: () => string | undefined
    onPrev: () => string | undefined
}

const Default = {
    zIndex: 10000,
    minScale: 0.05,
    maxScale: 5
}

export default Default