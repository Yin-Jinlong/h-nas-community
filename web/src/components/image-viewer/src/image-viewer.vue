<template>
    <teleport to="body">
        <transition name="fade">
            <div v-show="show&&url!==undefined" ref="boxEle" :style="{
            zIndex: zIndex
        }" class="viewer-box"
                 data-absolute
                 data-fill-size
                 data-flex-center
                 @mousemove="onMove"
                 @mouseout="onUp"
                 @mouseup="onUp"
                 @wheel="onWheel"
                 @mousedown.prevent="onDown">
                <div ref="imgBoxEle" v-loading="url==''" :style="orV('100%')" class="img-box"
                     data-flex-center>
                    <img :key="url"
                         ref="imgEle"
                         :src="url"
                         :style="orV('105%')"
                         loading="lazy"
                         @load="loadImg"
                         @loadstart="loaded=false"/>
                </div>
                <h-tool-tip class="close-btn">
                    <h-button
                            color="info"
                            round
                            type="primary"
                            @click="close">
                        <el-icon>
                            <CloseBold/>
                        </el-icon>
                    </h-button>
                    <template #tip>
                        关闭
                    </template>
                </h-tool-tip>
                <div class="bottom-controllers">
                    <h-button v-if="rawSize" class="show-raw" color="#888" type="primary" @click="showRaw">
                        <span>查看原图 {{ toHumanSize(rawSize) }}</span>
                    </h-button>
                    <transition name="fade">
                        <div v-if="showBtns" class="btns" data-flex-center data-transition-fast>
                            <h-tool-tip>
                                <h-button color="warning" round type="primary" @click="mirror">
                                    <el-icon>
                                        <Switch/>
                                    </el-icon>
                                </h-button>
                                <template #tip>
                                    左右翻转
                                </template>
                            </h-tool-tip>
                            <h-tool-tip>
                                <h-button color="success" round type="primary" @click="prev">
                                    <el-icon>
                                        <ArrowLeftBold/>
                                    </el-icon>
                                </h-button>
                                <template #tip>
                                    上一个
                                </template>
                            </h-tool-tip>
                            <h-tool-tip>
                                <h-button round type="primary" @click="rotate(-90)">
                                    <el-icon>
                                        <RefreshLeft/>
                                    </el-icon>
                                </h-button>
                                <template #tip>
                                    逆时针90°
                                </template>
                            </h-tool-tip>
                            <h-button color="info" type="primary">
                                {{ index + 1 }}/{{ count }}
                            </h-button>
                            <h-tool-tip>
                                <h-button round type="primary" @click="rotate(90)">
                                    <el-icon>
                                        <RefreshRight/>
                                    </el-icon>
                                </h-button>
                                <template #tip>
                                    顺时针90°
                                </template>
                            </h-tool-tip>
                            <h-tool-tip>
                                <h-button color="success" round type="primary" @click="next">
                                    <el-icon>
                                        <ArrowRightBold/>
                                    </el-icon>
                                </h-button>
                                <template #tip>
                                    下一个
                                </template>
                            </h-tool-tip>
                            <h-tool-tip>
                                <h-button color="danger" round type="primary" @click="reset">
                                    <el-icon>
                                        <Refresh/>
                                    </el-icon>
                                </h-button>
                                <template #tip>
                                    重置
                                </template>
                            </h-tool-tip>
                        </div>
                    </transition>
                </div>
            </div>
        </transition>
    </teleport>
</template>

<style lang="scss" scoped>
.viewer-box {
  background-color: rgb(0, 0, 0, 0.5);
}

.img-box {
  transition: transform 0s ease-out;

  & > img {
    object-fit: contain;
    transition: transform 0.16s ease-out;
  }
}

.close-btn {
  opacity: 0.2;
  position: fixed;
  right: 1em;
  top: 1em;
  transition: all 0.2s linear;

  &:hover {
    opacity: 0.4;
  }

}

.bottom-controllers {
  bottom: 0;
  display: flex;
  justify-content: center;
  padding-bottom: 1em;
  position: fixed;
  width: 100%;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease-out;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.btns {
  opacity: 0.05;

  & > * {
    margin: 0 6px;
  }

  &:hover {
    opacity: 0.9;
  }
}

.show-raw {
  bottom: 1em;
  font-size: 0.7em;
  left: 1em;
  opacity: 0.8;
  position: absolute;
}

</style>

<script lang="ts" setup>

import {
    ArrowLeftBold,
    ArrowRightBold,
    CloseBold,
    Refresh,
    RefreshLeft,
    RefreshRight,
    Switch
} from '@element-plus/icons-vue'
import {HButton, HToolTip} from '@yin-jinlong/h-ui'
import {toHumanSize} from '@/utils/size-utils'
import Default, {ImageViewerProps} from './props'

interface Options {
    mirrorX: boolean
    mirrorY: boolean
    rotate: number
    scale: number
    offX: number
    offY: number
}

const props = withDefaults(defineProps<ImageViewerProps>(), Default)
const show = defineModel<boolean>()
const url = ref<string>()
const raw = ref<string>()
const rawSize = ref<number>()
const boxEle = ref<HTMLElement>()
const imgEle = ref<HTMLImageElement>()
const imgBoxEle = ref<HTMLDivElement>()
const options = reactive<Options>({
    mirrorX: false,
    mirrorY: false,
    rotate: 0,
    scale: 1,
    offX: 0,
    offY: 0
})
const showBtns = ref(false)
const loaded = ref(false)
let last = {
    x: 0,
    y: 0
}

const RawUrls = new Map<string, string>()

let down = false

function orV(v: string) {
    let s = loaded.value ? undefined : v
    return {
        width: s,
        height: s
    }
}

function updateInfo(u: string | undefined) {
    if (u == '') {
        url.value = ''
        raw.value = undefined
        return
    }
    if (u?.length) {
        let rawCache = RawUrls.get(u)
        url.value = rawCache ? rawCache : u
        let r = props.onGetRaw()
        raw.value = r
        rawSize.value = r ? props.onGetRawSize() : undefined
        if (rawCache)
            rawSize.value = undefined
        if (!r)
            RawUrls.delete(u)
    } else {
        url.value = undefined
        raw.value = undefined
        rawSize.value = undefined
    }
}

function mirror() {
    if (options.rotate % 180 == 0) {
        options.mirrorX = !options.mirrorX
    } else {
        options.mirrorY = !options.mirrorY
    }
    update()
}

function rotate(d: number) {
    options.rotate += d
    update()
}

function prev() {
    updateInfo(props.onPrev())
}

function next() {
    updateInfo(props.onNext())
}

function close() {
    show.value = false
}

function showRaw() {
    if (!raw.value)
        return
    RawUrls.set(url.value!, raw.value!)
    url.value = raw.value
    rawSize.value = undefined
}

function onKeyDown(e: KeyboardEvent) {
    switch (e.key) {
        case 'Home':
            reset()
            break
        case 'Escape':
            close()
            break
        case 'ArrowLeft':
            prev()
            break
        case 'ArrowRight':
            next()
            break
    }
}

function onWheel(e: WheelEvent) {
    options.scale *= e.deltaY > 0 ? 0.9 : 1.1
    update()
}

function calcScale() {
    let img = imgEle.value!
    let winAspectRatio = window.innerWidth / window.innerHeight
    let aspectRatio = img.naturalWidth / img.naturalHeight
    return winAspectRatio < aspectRatio ?
        window.innerWidth / img.naturalWidth : window.innerHeight / img.naturalHeight
}

function reset(u: boolean = true) {
    options.scale = calcScale()
    options.mirrorX = false
    options.mirrorY = false
    options.offX = 0
    options.offY = 0
    options.rotate = 0
    if (u)
        update()
}

function pack(v: number, min: number, max: number) {
    return v < min ? min : v > max ? max : v
}

function nextFrame(next: () => void) {
    requestAnimationFrame(() => {
        requestAnimationFrame(next)
    })
}

function update() {
    let img = imgEle.value
    let imgBox = imgBoxEle.value
    if (!img || !imgBox)
        return
    let s = pack(options.scale, props.minScale, props.maxScale)
    const mr = props.minShowRate
    const iw = img.naturalWidth * s
    const ih = img.naturalHeight * s
    const ww = window.innerWidth
    const wh = window.innerHeight

    let ow = ww / 2 + iw / 2 - Math.min(ww, iw) * mr
    let oh = wh / 2 + ih / 2 - Math.min(wh, ih) * mr
    options.offX = pack(options.offX, -ow, ow)
    options.offY = pack(options.offY, -oh, oh)
    options.scale = s

    let r = options.rotate
    let x = options.offX
    let y = options.offY
    let sx = options.mirrorX ? -s : s
    let sy = options.mirrorY ? -s : s

    imgBox.style.transform = `translate(${x}px, ${y}px)`
    img.style.transform = `rotate(${r}deg) scale(${sx},${sy}) translateZ(0)`

    if (Math.abs(r) >= 360) {
        setTimeout(async () => {
            let r = options.rotate % 360
            img.style.transitionDuration = '0s'
            img.style.rotate = `${r}deg`
            options.rotate = r
            await nextTick()
            update()
            nextFrame(() => {
                img.style.transitionDuration = '0.16s'
            })
        }, 160)
    }
}

function onDown(e: MouseEvent) {
    if (e.button != 0)
        return
    last.x = e.clientX
    last.y = e.clientY
    down = true
    boxEle.value!.style.cursor = 'grabbing'
}

function onMove(e: MouseEvent) {
    if (!down)
        return
    options.offX += e.clientX - last.x
    options.offY += e.clientY - last.y
    last.x = e.clientX
    last.y = e.clientY
    update()
}

function onUp(e: MouseEvent) {
    down = false
    boxEle.value!.style.cursor = 'default'
}

async function loadImg() {
    let img = imgEle.value!!
    loaded.value = true

    img.style.transitionDuration = '0s'
    reset(false)
    options.scale = calcScale() + 0.05
    update()

    nextFrame(() => {
        img.style.transitionDuration = '0.16s'
        reset()
    })
}

function onResize() {
    showBtns.value = window.innerWidth >= 800
    update()
}

let id: number | undefined

function waitUrl() {
    if (id)
        clearInterval(id)
    id = setInterval(() => {
        let v = props.onGet()
        if (v != '') {
            url.value = v
            clearInterval(id)
            id = undefined
        }
    }, 200) as unknown as number
}

onMounted(() => {
    addEventListener('keydown', onKeyDown)
    addEventListener('resize', onResize)
    onResize()
})

onUnmounted(() => {
    removeEventListener('keydown', onKeyDown)
    removeEventListener('resize', onResize)
})

watch(url, (nv) => {
    if (nv == '') {
        waitUrl()
    }
})

watch(show, nv => {
    if (nv) {
        updateInfo(props.onGet())
    }
})

</script>
