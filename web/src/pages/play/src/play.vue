<template>
    <div class="player-box">
        <h3>{{ path }}</h3>
        <div ref="videoBoxEle"
             v-loading="nowStreamIndex < -1"
             :data-full-window="fullWindow?'':undefined"
             class="video-box"
             data-flex-center
             data-relative
             h-loading-text="转码中..."
             tabindex="0"
             @keydown="onPlayerKeydown"
             @keyup="onPlayerKeyup">
            <canvas ref="videoCanvasEle"
                    :style="`cursor: ${mouseInControls?'auto':'none'}`"
                    data-fill-size
                    @click="playPause"
                    @dblclick="requestFullscreen(!fullscreen)"
                    @mouseenter="showControls(true)"
                    @mousemove="showControls(true)"
                    @mouseout="delayControlsDismiss"
                    @wheel="onPlayerWheel"/>
            <div :style="`--p: ${videoInfo.progress*100}%;`"
                 class="mini-progress"/>
            <transition name="control-anim">
                <div v-show="mouseInControls"
                     class="bottom-controls"
                     @mouseout="delayControlsDismiss"
                     @mousemove.prevent="showControls(false)">
                    <h-tool-tip>
                        <el-icon class="play-pause" data-pointer @click="playPause">
                            <VideoPause v-if="playing"/>
                            <VideoPlay v-else/>
                        </el-icon>
                        <template #tip>
                            {{ playing ? '暂停' : '播放' }}
                        </template>
                    </h-tool-tip>
                    <seekable-progress-bar
                            :chapters="chapters"
                            :current="videoInfo.cur"
                            :duration="videoInfo.dur"
                            @seek="seekTo"/>
                    <h-tool-tip place="top">
                        <div class="width" style="color: white">
                            {{ getBitrateName(nowStreamIndex) }}
                        </div>
                        <template #tip>
                            <div class="menu">
                                <div v-for="i in streams.length"
                                     :data-now="(i-1)==nowStreamIndex?'':undefined"
                                     class="menu-item"
                                     @click="nowStreamIndex=i-1">
                                    {{ getBitrateName(i - 1) }}({{ streams[i - 1].bitrate }}kbps)
                                </div>
                            </div>
                        </template>
                    </h-tool-tip>
                    <div class="width"
                         data-pointer
                         data-relative
                         @mouseenter="showVolumeBar"
                         @mouseout="hideVolumeBar">
                        <h-tool-tip place="bottom"
                                    @click="player?.muted(!videoInfo.muted)"
                                    @mousemove="showVolumeBar">
                            <el-icon>
                                <VolumeMuted v-if="videoInfo.muted"/>
                                <VolumeZero v-else-if="videoInfo.volume==0"/>
                                <VolumeLow v-else-if="videoInfo.volume<0.33"/>
                                <VolumeMid v-else-if="videoInfo.volume<0.67"/>
                                <VolumeHigth v-else/>
                            </el-icon>
                            <template #tip>
                                <span>{{ videoVolume.toFixed(0) }}%</span>
                            </template>
                        </h-tool-tip>
                        <div v-if="showVolume"
                             class="volume-bar"
                             @mousemove="showVolumeBar"
                             @mouseout="hideVolumeBar">
                            <el-slider
                                    v-model="videoVolume"
                                    :disabled="videoInfo.muted"
                                    :format-tooltip="v=>v+'%'"
                                    placement="left"
                                    vertical/>
                        </div>
                    </div>
                    <div v-if="!fullscreen" class="width" data-pointer>
                        <h-tool-tip>
                            <el-icon>
                                <normal-screen v-if="fullWindow" @click="requestFullWindow(false)"/>
                                <full-window v-else @click="requestFullWindow()"/>
                            </el-icon>
                            <template #tip>
                                <span>{{ fullWindow ? '退出全屏' : '窗口全屏' }}</span>
                            </template>
                        </h-tool-tip>
                    </div>
                    <div v-if="!fullWindow" class="width" data-pointer>
                        <h-tool-tip>
                            <el-icon>
                                <normal-screen v-if="fullscreen" @click="requestFullscreen(false)"/>
                                <full-screen v-else @click="requestFullscreen()"/>
                            </el-icon>
                            <template #tip>
                                <span>{{ fullscreen ? '退出全屏' : '全屏' }}</span>
                            </template>
                        </h-tool-tip>
                    </div>
                </div>
            </transition>
            <div class="msg-box">
                <transition-group name="list">
                    <div v-if="loadingVideo" key="loading">
                        加载中...
                    </div>
                    <div v-if="fastSeeking" key="seek">
                        3X 快进中>>>
                    </div>
                    <div v-for="m in playerMsgs" :key="m.id">
                        {{ m.msg }}{{ m.count > 1 ? ` x${m.count}` : '' }}
                    </div>
                </transition-group>
            </div>
        </div>
    </div>
</template>

<style lang="scss">
@import 'video.js/dist/video-js.min.css';
</style>

<style lang="scss" scoped>

@use '@yin-jinlong/h-ui/style/src/tools/fns' as *;

.player-box {
  position: relative;
  user-select: none;
}

.video-box {
  background: black;
  box-sizing: border-box;
  height: 600px;
  width: 100%;
}

[data-full-window] {
  background-color: #000;
  height: 100%;
  left: 0;
  padding: 0;
  position: fixed;
  top: 0;
  width: 100%;
  z-index: 10;

  & > .bottom-controls {
    bottom: 0;
    left: 0;
    width: 100%;
  }

}

.bottom-controls {
  align-items: center;
  background-color: rgb(0, 0, 0, 0.5);
  --progress-bar-background-color: rgb(255, 255, 255, 0.3);
  bottom: 0;
  box-sizing: border-box;
  color: white;
  display: flex;
  left: 0;
  padding: 0.5em 1em;
  position: absolute;
  width: 100%;

  & > * {
    margin: 0 0.25em;
  }

  .width {
    flex: 0 0 auto;
  }

  :deep(.el-icon) {
    height: 1.2em;
    width: 1.2em;

    & > svg {
      height: 100%;
      width: 100%;
    }
  }

}

.play-pause {
  flex: 0 0 auto;
  font-size: 1.5em;
}

.mini-progress {
  background-color: get-css(color, primary);
  bottom: 0;
  box-sizing: border-box;
  height: 1px;
  overflow: hidden;
  pointer-events: none;
  position: absolute;
  transform: scaleX(var(--p, 0));
  transform-origin: left center;
  width: 100%;
}


.volume-bar {
  bottom: calc(100% + 1em);
  height: 100px;
  left: -50%;
  position: absolute;
}

.list-enter-active,
.list-leave-active {
  transition: all 0.2s ease-out;
}

.list-enter-from,
.list-leave-to {
  opacity: 0;
}

.control-anim-enter-active,
.control-anim-leave-active {
  transition: all 0.2s ease-out;
}

.control-anim-enter-from,
.control-anim-leave-to {
  opacity: 0;
}

.msg-box {
  left: 50%;
  margin-top: 1em;
  position: absolute;
  top: 0;
  transform: translateX(-50%);

  & > div {
    align-items: center;
    background: rgb(0, 0, 0, 0.6);
    border-radius: 0.7em;
    color: white;
    display: flex;
    justify-content: center;
    margin: 0.5em auto;
    padding: 0.5em;
    width: max-content;
  }

}

.menu {

}

.menu-item {
  color: white;
  cursor: pointer;
  padding: 0.4em 0;

  &[data-now] {
    color: get-css(color, primary-4);
  }

  &:hover {
    color: get-css(color, primary);
  }

}

</style>

<script lang="ts" setup>
import FullWindow from '@/pages/play/src/full-window.vue'
import NormalScreen from '@/pages/play/src/normal-screen.vue'
import VolumeZero from './volume-zero.vue'
import VolumeHigth from './volume-higth.vue'
import VolumeLow from './volume-low.vue'
import VolumeMid from './volume-mid.vue'
import VolumeMuted from './volume-muted.vue'
import API from '@/utils/api'
import {HMessage, HToolTip} from '@yin-jinlong/h-ui'
import {FullScreen, VideoPause, VideoPlay} from '@element-plus/icons-vue'
import videojs from 'video.js'
import Player from 'video.js/dist/types/player'

interface Msg {
    time: number
    id: string
    msg: string
    count: number
}

let player: Player | undefined
const videoEle = document.createElement('video')
const videoCanvasEle = ref<HTMLCanvasElement>()
const videoBoxEle = ref<HTMLCanvasElement>()
const path = ref<string>()
const route = useRoute()
const playing = ref(false)
const fastSeeking = ref(false)
const videoInfo = reactive({
    cur: 0,
    dur: 0,
    progress: 0,
    volume: 1,
    muted: false
})
const videoVolume = ref(0)
const showVolume = ref(false)
const fullWindow = ref(false)
const fullscreen = ref(false)
const mouseInControls = ref(false)
const loadingVideo = ref(false)
let lastHideVolumeId = 0
let fastSeekTimeout = 0
let mouseInControlsTimeout = 0
let rafId = 0
const playerMsgs = reactive<Msg[]>([])
const streams = reactive<HLSStreamInfo[]>([])
const nowStreamIndex = ref(-1)
const chapters = reactive<ChapterInfo[]>([])

function render() {
    let canvas = videoCanvasEle.value?.getContext('2d') as CanvasRenderingContext2D | undefined
    if (!canvas)
        return
    let vw = videoEle.videoWidth
    let vh = videoEle.videoHeight
    let w = videoCanvasEle.value!!.width
    let h = videoCanvasEle.value!!.height

    let rate = vw / vh
    let canvasRate = w / h

    let dw: number, dh: number
    if (rate < canvasRate) {
        dh = h
        dw = dh * rate
    } else {
        dw = w
        dh = dw / rate
    }

    canvas.drawImage(videoEle, (w - dw) / 2, (h - dh) / 2, dw, dh)

    if (!videoEle.paused) {
        cancelAnimationFrame(rafId)
        rafId = requestAnimationFrame(render)
    }
}

function getBitrateName(index: number) {
    const NAMES = [{
        name: '流畅',
        bitrate: 1000,
    }, {
        name: '高清',
        bitrate: 2000,
    }, {
        name: '超清',
        bitrate: 5000,
    }, {
        name: '蓝光',
        bitrate: 10000,
    }]
    if (!streams[index]?.bitrate)
        return '?'
    let bitrate = streams[index].bitrate
    if (index == 0)
        return `原画`
    let i = NAMES.findIndex(n => n.bitrate >= bitrate)
    if (i < 0)
        i = NAMES.length - 1
    return `${NAMES[i].name}`
}

function setVolume(dv: number) {
    let v = Math.fround(Math.abs(dv * 100))
    let volume = videoInfo.volume + dv
    if (volume > 1)
        volume = 1
    if (volume < 0)
        volume = 0
    addMessage('音量', `音量${dv > 0 ? '+' : '-'}${v} ${(100 * volume).toFixed(0)}%`)
    player?.volume(volume)
}

function seek(dt: number) {
    if (dt > 0)
        addMessage('快进', `快进：${dt}s`)
    else
        addMessage('快退', `快退：${-dt}s`)
    let t = (player?.currentTime() ?? 0) + dt
    if (t < 0)
        t = 0
    else if (t > (player?.duration() ?? 0))
        t = player?.duration() ?? 0
    player?.currentTime(t)
}

function playPause() {
    if (player?.paused()) {
        player?.play()
        addMessage('播放', '播放')
    } else {
        player?.pause()
        addMessage('播放', '暂停')
    }
}

function onVideoCanPlay() {
    loadingVideo.value = false
    videoInfo.cur = player!!.currentTime()!!
    videoInfo.dur = player!!.duration()!!
    videoInfo.volume = player!!.volume() ?? 0
    videoInfo.muted = player!!.muted() ?? false
    videoVolume.value = videoInfo.volume * 100
    render()
}

function onVideoError() {
    HMessage.error('播放出错！')
    playing.value = false
    loadingVideo.value = false
}

function onVideoPlay() {
    playing.value = true
    render()
}

function onVideoPlaying() {
    playing.value = true
    render()
}

function onVideoTime() {
    let t = player!!.currentTime()!!
    let d = player!!.duration()!!
    videoInfo.cur = t
    videoInfo.dur = d
    videoInfo.progress = t / d
}

function onVideoPause() {
    playing.value = false
}

function onVideoVolume() {
    videoInfo.volume = player?.volume() ?? 0
    videoInfo.muted = player?.muted() ?? false
    videoVolume.value = videoInfo.volume * 100
}

function onVideoLoading() {
    loadingVideo.value = true
}

function onPlayerWheel(e: WheelEvent) {
    setVolume(e.deltaY < 0 ? 0.05 : -0.05)
}

function seekTo(p: number) {
    player?.currentTime(player.duration()!! * p)
}

function showVolumeBar() {
    if (lastHideVolumeId) {
        clearTimeout(lastHideVolumeId)
        lastHideVolumeId = 0
    }
    showVolume.value = true
}

function hideVolumeBar() {
    if (lastHideVolumeId)
        return
    lastHideVolumeId = setTimeout(() => {
        lastHideVolumeId = 0
        showVolume.value = false
    }, 500) as unknown as number
}

function requestFullscreen(full: boolean = true) {
    let ele = videoBoxEle.value
    if (!ele)
        return
    if (full) {
        ele.requestFullscreen()
    } else {
        document.exitFullscreen()
    }
}

function requestFullWindow(full: boolean = true) {
    fullWindow.value = full
}

function onFullScreenChange() {
    fullscreen.value = document.fullscreenEnabled && document.fullscreenElement == videoBoxEle.value
}

function resize() {
    let ele = videoCanvasEle.value
    if (!ele)
        return
    ele.width = ele.offsetWidth
    ele.height = ele.offsetHeight
    render()
}

function onPlayerKeydown(e: KeyboardEvent) {
    switch (e.code) {
        case 'Escape':
            if (fullWindow.value) {
                requestFullWindow(false)
            }
            break
        case 'ArrowUp':
            setVolume(0.05)
            break
        case 'ArrowDown':
            setVolume(-0.05)
            break
        case 'ArrowRight':
            if (!fastSeekTimeout) {
                fastSeekTimeout = setTimeout(() => {
                    fastSeeking.value = true
                }, 500) as unknown as number
            }
            break
    }
}

function onPlayerKeyup(e: KeyboardEvent) {
    switch (e.code) {
        case 'ArrowLeft':
            seek(-5)
            break
        case 'ArrowRight':
            if (fastSeekTimeout) {
                clearTimeout(fastSeekTimeout)
                fastSeekTimeout = 0
            }
            if (!fastSeeking.value) {
                seek(5)
            }
            fastSeeking.value = false
            break
        case 'Space':
            playPause()
            break
    }
}

function showControls(close: boolean = true) {
    if (mouseInControlsTimeout) {
        clearTimeout(mouseInControlsTimeout)
        mouseInControlsTimeout = 0
    }
    mouseInControls.value = true
    if (close)
        delayControlsDismiss()
}

function delayControlsDismiss() {
    if (mouseInControlsTimeout) {
        clearTimeout(mouseInControlsTimeout)
    }
    mouseInControlsTimeout = setTimeout(() => {
        mouseInControlsTimeout = 0
        mouseInControls.value = false
    }, 2000) as unknown as number
}

function addMessage(id: string, msg: string) {
    let i = playerMsgs.findIndex(item => item.id == id)
    if (i >= 0) {
        let m = playerMsgs[i]
        m.count++
        m.msg = msg
        m.time = Date.now()
    } else {
        playerMsgs.push({
            msg,
            id,
            count: 1,
            time: Date.now()
        })
    }
    playerMsgs.sort((a, b) => a.time - b.time)
    msgLoop()
}

function msgLoop() {
    if (!playerMsgs.length)
        return
    let now = Date.now()
    let msg = playerMsgs[0]
    while (msg) {
        if (now - msg.time > 600) {
            playerMsgs.shift()
            msg = playerMsgs[0]
        } else {
            break
        }
    }
    if (playerMsgs.length)
        requestAnimationFrame(msgLoop)
}

function getVideoInfo() {
    let p = path.value
    if (p === undefined)
        return
    API.getPublicHLSInfo(p).then(info => {
        if (!info)
            return
        streams.length = 0
        let max = -2
        info.forEach((item, i) => {
            streams.unshift(item)
            if (item.bitrate > (streams[max]?.bitrate ?? -1))
                max = i
        })
        if (!info.length) {
            loadingVideo.value = false
            setTimeout(getVideoInfo, 5000)
        }
        nowStreamIndex.value = max
    })
}

onMounted(() => {
    videoEle.id = 'player'
    player = videojs(videoEle, {
        controls: false
    })
    player.on('waiting', onVideoLoading)
    player.on('canplaythrough', onVideoCanPlay)
    player.on('error', onVideoError)
    videoEle.addEventListener('play', onVideoPlay)
    videoEle.addEventListener('pause', onVideoPause)
    videoEle.addEventListener('playing', onVideoPlaying)
    videoEle.addEventListener('timeupdate', onVideoTime)
    videoEle.addEventListener('volumechange', onVideoVolume)

    document.addEventListener('fullscreenchange', onFullScreenChange)
    addEventListener('resize', resize)

    player.volume((+(localStorage.getItem('videoVolume') ?? '50')) / 100)

    resize()
    path.value = route.query.path as string
})

onUnmounted(() => {
    player?.dispose()
    videoEle.removeEventListener('pause', onVideoPause)
    videoEle.removeEventListener('playing', onVideoPlaying)
    videoEle.removeEventListener('timeupdate', onVideoTime)
    videoEle.removeEventListener('volumechange', onVideoVolume)

    document.removeEventListener('fullscreenchange', onFullScreenChange)
})

watch(fullWindow, async (nv) => {
    await nextTick()
    resize()
})

watch(videoVolume, (nv) => {
    player?.volume(nv / 100)
    localStorage.setItem('videoVolume', nv.toString())
})

watch(fastSeeking, nv => {
    player?.playbackRate(nv ? 3 : 1)
})

watch(path, (nv) => {
    if (!nv)
        return
    nextTick(getVideoInfo)
    chapters.length = 0
    API.getPublicVideoChapter(nv).then(res => {
        if (!res)
            return
        chapters.length = 0
        for (let c of res)
            chapters.push(c)
    })
    loadingVideo.value = true
})

watch(nowStreamIndex, nv => {
    if (nv >= 0) {
        let time = player?.currentTime()
        if (!streams[nv]) {
            player?.src('')
            return
        }
        player?.src(API.publicHSLURL(streams[nv].path))
        if (time) {
            player?.currentTime(time)
            if (playing.value)
                player?.play()
        }
    } else {
        player?.src('')
    }
})

watch(() => route.query.path, (nv) => {
    path.value = nv as string
})

</script>
