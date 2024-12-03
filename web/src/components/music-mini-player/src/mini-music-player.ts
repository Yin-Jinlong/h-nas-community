export interface MusicPlayerStatus {
    item?: MusicItem
    volume: number
    playing: boolean
    muted: boolean
    current: number
    duration: number
}

export interface MusicItem {
    title: string
    src: string
}

class MiniMusicPlayer {

    #playList: MusicItem[]
    #nowIndex: Ref<number>
    #ele: HTMLAudioElement
    #status: MusicPlayerStatus

    constructor() {
        this.#playList = reactive([])
        this.#nowIndex = ref(0)
        this.#ele = document.createElement('audio')
        this.#status = reactive({
            volume: 0.5,
            playing: false,
            muted: false,
            current: 0,
            duration: 0,
        })
        this.#ele.addEventListener('canplay', () => {
            this.#status.duration = this.#ele.duration
        })
        this.#ele.addEventListener('play', () => {
            this.#status.playing = true
        })
        this.#ele.addEventListener('pause', () => {
            this.#status.playing = false
        })
        this.#ele.addEventListener('volumechange', () => {
            this.#status.volume = this.#ele.volume
            this.#status.muted = this.#ele.muted
        })
        this.#ele.addEventListener('timeupdate', () => {
            this.#status.current = this.#ele.currentTime
        })
        this.#ele.addEventListener('error', () => {
            this.#status.playing = false
        })
        this.#ele.addEventListener('ended', () => {
            this.#status.playing = false
        })
        this.#ele.volume = 0.5
    }

    get status() {
        return this.#status
    }

    get size() {
        return this.#playList.length
    }

    now() {
        return this.#nowIndex.value
    }

    get(i: number) {
        return this.#playList[i]
    }

    add(item: MusicItem) {
        if (this.#playList[this.#playList.length - 1] == item)
            return
        this.#playList.push(item)
        return this.#playList.length - 1
    }

    play(i?: number) {
        if (!this.#playList.length) {
            return
        }
        let index = i ?? this.#nowIndex.value
        this.#nowIndex.value = index
        let src = this.#playList[index].src
        if (!this.#ele.src.endsWith(src)) {
            this.#ele.src = src
        }
        this.#ele.play().then(() => {
            this.#status.item = this.#playList[index]
        }).catch(() => {
            this.#status.playing = false
        })
    }

    pause() {
        this.#ele.pause()
    }

    close() {
        this.#ele.src = ''
        this.#playList.length = 0
        this.#status.playing = false
        this.#status.item = undefined
        this.#status.current = 0
        this.#status.duration = 0
    }

    muted(m?: boolean) {
        if (m !== undefined) {
            this.#ele.muted = m
        }
        return this.#ele.muted
    }

    volume(v?: number) {
        if (v !== undefined) {
            this.#ele.volume = v
        }
        return this.#ele.volume
    }

    seekP(p: number) {
        this.#ele.currentTime = this.#ele.duration * p
        if (this.#ele.paused) {
            this.#ele.play().then(() => {
                this.#ele.pause()
            })
        }
    }

}

const Player = new MiniMusicPlayer()

export {Player as MiniMusicPlayer}
