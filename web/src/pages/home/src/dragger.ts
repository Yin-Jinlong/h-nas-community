let lastCancelTimeout = 0

function checkCancel() {
    if (lastCancelTimeout) {
        clearTimeout(lastCancelTimeout)
        lastCancelTimeout = 0
    }
}

function accept(e: DragEvent): boolean {
    if (!e.dataTransfer!.types.includes('Files')) {
        e.dataTransfer!.dropEffect = 'none'
        Dragger.infoText.value = '不支持的类型'
        return false
    }
    e.dataTransfer!.dropEffect = 'copy'
    return true
}


export const Dragger = {
    mouseIn: ref(false),
    infoText: ref(''),
    isDragging: ref(false),
    upload: (file: File) => {
    },
    onDragStart(e: DragEvent) {
        if (Dragger.mouseIn.value)
            return
        checkCancel()
        if (!accept(e))
            return
        e.preventDefault()
        Dragger.isDragging.value = true
        Dragger.infoText.value = '松开开始上传'
    },
    onDragOver(e: DragEvent) {
        if (!accept(e) || !Dragger.isDragging.value)
            return
        e.preventDefault()
        checkCancel()
    },
    onDragCancel() {
        checkCancel()
        lastCancelTimeout = setTimeout(() => {
            Dragger.isDragging.value = false
            lastCancelTimeout = 0
        }, 100) as unknown as number
    },
    async onDragEnd(e: DragEvent) {
        if (!accept(e) || !Dragger.isDragging.value)
            return
        e.preventDefault()
        Dragger.isDragging.value = false
        await nextTick()
        let files = e.dataTransfer!.files
        console.log(files)
        for (let i = 0; i < files.length; i++) {
            Dragger.upload(files[i])
        }
    }
}