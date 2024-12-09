export interface MusicPlayerStatus {
    item?: MusicItem
    volume: number
    playing: boolean
    muted: boolean
    current: number
    duration: number
    playMode: PlayMode
    nowLrsc: string[]
}

export interface MusicItem {
    title: string
    src: string
    lrc: () => Promise<string | undefined>
    info: () => Promise<AudioFileInfo | undefined>
}

interface LrcLine {
    time: number
    text: string
}

export enum PlayMode {
    Normal,
    RepeatAll,
    RepeatThis,
    Random
}

class MiniMusicPlayer {

    #playList: MusicItem[]
    #nowIndex: Ref<number>
    #ele: HTMLAudioElement
    #status: MusicPlayerStatus
    #info: AudioFileInfo
    #audioAnalyser: AnalyserNode
    #fftBuf: Uint8Array
    #audioContext: AudioContext
    #lrcs: LrcLine[] = []

    constructor() {
        this.#playList = reactive([])
        this.#info = reactive({
            path: '',
            duration: 0,
            bitrate: 0
        })
        this.#nowIndex = ref(-1)
        this.#ele = document.createElement('audio')
        this.#status = reactive({
            volume: 0.5,
            playing: false,
            muted: false,
            current: 0,
            duration: 0,
            playMode: PlayMode.Normal,
            nowLrsc: []
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
            let ct = this.#ele.currentTime
            this.#status.current = ct
            if (!this.#lrcs.length)
                return
            this.#updateLrc(ct)
        })
        this.#ele.addEventListener('error', () => {
            this.#status.playing = false
        })
        this.#ele.addEventListener('ended', () => {
            this.#status.playing = false
            switch (this.#status.playMode) {
                case PlayMode.RepeatThis:
                    this.play()
                    break
                // @ts-ignore
                case PlayMode.Normal:
                    if (this.#nowIndex.value == this.#playList.length - 1)
                        break
                case PlayMode.RepeatAll:
                    let i = this.#nowIndex.value + 1
                    if (i >= this.#playList.length)
                        i = 0
                    this.play(i)
                    break
                case PlayMode.Random:
                    this.play(Math.floor(Math.random() * this.#playList.length))
                    break
            }
        })
        this.#ele.volume = 0.5
        let audioContext = new AudioContext()
        this.#audioAnalyser = audioContext.createAnalyser()
        this.#audioAnalyser.fftSize = 512
        this.#fftBuf = new Uint8Array(this.#audioAnalyser.frequencyBinCount)
        let source = audioContext.createMediaElementSource(this.#ele)
        source.connect(this.#audioAnalyser)
        this.#audioAnalyser.connect(audioContext.destination)
        this.#audioContext = audioContext
    }

    get info() {
        return this.#info
    }

    get status() {
        return this.#status
    }

    get size() {
        return this.#playList.length
    }

    get fft() {
        this.#audioAnalyser.getByteTimeDomainData(this.#fftBuf)
        return this.#fftBuf
    }

    get fftSize() {
        return this.#audioAnalyser.frequencyBinCount
    }

    shortTitle(withAlbum: boolean = true) {
        let info = this.#info
        if (info.path) {
            if (info.title) {
                let r = info.title
                if (info.album && withAlbum)
                    r += ` - ${info.album}`
                if (info.artists)
                    r += ` - ${info.artists}`
                return r
            }
        }
        return this.#status.item?.title
    }

    now() {
        return this.#nowIndex.value
    }

    get(i: number) {
        return this.#playList[i]
    }

    add(item: MusicItem) {
        let i = this.#playList.findIndex(o => o.title == item.title)
        if (i >= 0) {
            this.#playList[i] = item
            return i
        }
        this.#playList.push(item)
        return this.#playList.length - 1
    }

    remove(i: number) {
        this.#playList.splice(i, 1)
    }

    removeItem(title: string) {
        let i = this.#playList.findIndex(o => o.title == title)
        if (i >= 0)
            this.#playList.splice(i, 1)
    }

    play(i?: number) {
        this.#play(i ?? this.#nowIndex.value)
    }

    playPrev() {
        let i = this.#status.playMode == PlayMode.Random ?
            Math.floor(Math.random() * this.#playList.length) :
            this.#nowIndex.value - 1
        if (i < 0)
            i = this.#playList.length - 1
        this.play(i)
    }

    playNext() {
        let i = this.#status.playMode == PlayMode.Random ?
            Math.floor(Math.random() * this.#playList.length) :
            this.#nowIndex.value + 1
        if (i >= this.#playList.length)
            i = 0
        this.play(i)
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
        this.#nowIndex.value = -1
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

    nextMode() {
        let i = this.#status.playMode + 1
        if (i > PlayMode.Random)
            i = PlayMode.Normal
        this.#status.playMode = i
    }

    lrcAtP(p: number): string[] {
        return this.lrcAt(this.#ele.duration * p)
    }

    lrcAt(time: number): string[] {
        if (!this.#lrcs.length)
            return []
        let i = this.#lrcs.findIndex(v => v.time > time)
        if (i < 0)
            i = this.#lrcs.length
        i--
        let r: string[] = []
        let t = this.#lrcs[i].time
        while (i >= 0 && this.#lrcs[i].time == t) {
            r.unshift(this.#lrcs[i].text)
            i--
        }
        return r
    }

    #updateLrc(ct: number) {
        this.#status.nowLrsc.length = 0
        this.lrcAt(ct).forEach(v => {
            this.#status.nowLrsc.push(v)
        })
    }

    #play(index: number) {
        if (!this.#playList.length) {
            return
        }
        if (index < 0)
            index = 0
        if (this.#nowIndex.value == index) {
            this.#ele.play()
            return
        }
        this.#lrcs.length = 0
        this.#status.nowLrsc.length = 0
        this.#nowIndex.value = index
        let item = this.#playList[index]
        this.#ele.src = item.src
        this.#ele.play().then(() => {
            this.#status.item = this.#playList[index]
            if (this.#audioContext.state === 'suspended')
                this.#audioContext.resume()
        }).catch(() => {
            this.#status.playing = false
        })
        item.lrc().then(lrc => {
            if (lrc)
                this.#parseLrc(lrc)
        })
        item.info().then(info => {
            if (!info)
                return
            this.#info = info
        })

    }

    #parseLrc(text: string) {
        let lines = text.split(/\r?\n/)
        for (let line of lines) {
            if (!line.length)
                continue
            let mr = /\[(\d+):(\d+).(\d+)](.*)/.exec(line)
            if (!mr)
                continue
            let min = parseInt(mr[1])
            let sec = parseInt(mr[2])
            let ms = parseInt(mr[3])
            let str = mr[4]
            let t = min * 60 + sec + ms / 1000
            this.#lrcs.push({
                time: t,
                text: str
            })
        }
        this.#lrcs.sort((a, b) => a.time - b.time)
    }

}

const Player = new MiniMusicPlayer()

export {Player as MiniMusicPlayer}
