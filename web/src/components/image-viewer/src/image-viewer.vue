<template>
    <teleport to="body">
        <transition name="fade">
            <div v-show="show&&url" ref="boxEle" :style="{
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
                <img :key="url"
                     ref="imgEle"
                     :src="url"
                     :style="{
                         width:loaded?undefined:'100%',
                         height:loaded?undefined:'100%',
                         'object-fit':
                         'contain'}"
                     loading="lazy"
                     @load="loadImg"/>
                <h-tool-tip class="close-btn">
                    <h-button
                            color="info"
                            round
                            style="opacity: 0.5;"
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
                    <h-tool-tip class="btns">
                        <h-button round type="primary" @click="prev">
                            <el-icon>
                                <ArrowLeftBold/>
                            </el-icon>
                        </h-button>
                        <template #tip>
                            上一个
                        </template>
                    </h-tool-tip>
                    <h-button color="info" type="primary">
                        {{ index + 1 }}/{{ count }}
                    </h-button>
                    <h-tool-tip class="btns">
                        <h-button round type="primary" @click="next">
                            <el-icon>
                                <ArrowRightBold/>
                            </el-icon>
                        </h-button>
                        <template #tip>
                            下一个
                        </template>
                    </h-tool-tip>
                </div>
            </div>
        </transition>
    </teleport>
</template>

<style lang="scss" scoped>
.viewer-box {
  background-color: rgb(0, 0, 0, 0.5);
}

.close-btn {
  position: fixed;
  right: 1em;
  top: 1em;
}

.bottom-controllers {
  bottom: 0;
  display: flex;
  justify-content: center;
  padding-bottom: 1em;
  position: fixed;
  width: 100%;
}

.btns {
  margin: 0 12px;
}

</style>

<script lang="ts" setup>

import {ArrowLeftBold, ArrowRightBold, CloseBold} from '@element-plus/icons-vue'
import {HButton, HToolTip, vLoading} from '@yin-jinlong/h-ui'
import Default, {ImageViewerProps} from './props'

interface Options {
    scale: number
    offX: number
    offY: number
}

const props = withDefaults(defineProps<ImageViewerProps>(), Default)
const show = defineModel<boolean>()
const url = ref<string>()
const boxEle = ref<HTMLElement>()
const imgEle = ref<HTMLImageElement>()
const options = reactive<Options>({
    scale: 1,
    offX: 0,
    offY: 0
})
const loaded = ref(false)
let last = {
    x: 0,
    y: 0
}

let down = false

function prev() {
    let r = props.onPrev()
    if (r)
        url.value = r
}

function next() {
    let r = props.onNext()
    if (r)
        url.value = r
}

function close() {
    show.value = false
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


function reset() {
    let img = imgEle.value!
    let winAspectRatio = window.innerWidth / window.innerHeight
    let aspectRatio = img.naturalWidth / img.naturalHeight
    options.scale = winAspectRatio < aspectRatio ?
        window.innerWidth / img.naturalWidth : window.innerHeight / img.naturalHeight
    options.offX = 0
    options.offY = 0
    update()
}

function pack(v: number, min: number, max: number) {
    return v < min ? min : v > max ? max : v
}

function update() {
    let img = imgEle.value
    if (!img)
        return
    let s = options.scale
    const mr = props.minShowRate
    const iw = img.naturalWidth * s
    const ih = img.naturalHeight * s
    const ww = window.innerWidth
    const wh = window.innerHeight

    options.scale = pack(options.scale, props.minScale, props.maxScale)
    let ow = ww / 2 + iw / 2 - Math.min(ww, iw) * mr
    let oh = wh / 2 + ih / 2 - Math.min(wh, ih) * mr
    options.offX = pack(options.offX, -ow, ow)
    options.offY = pack(options.offY, -oh, oh)

    let x = options.offX
    let y = options.offY
    img.style.transform = `translate(${x}px, ${y}px) scale(${s}) `
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
    loaded.value = true
    await nextTick()
    reset()
}

onMounted(() => {
    addEventListener('keydown', onKeyDown)
})

onUnmounted(() => {
    removeEventListener('keydown', onKeyDown)
})

watch(url, () => {
    loaded.value = false
})

watch(show, nv => {
    if (nv) {
        url.value = props.onGet()
    }
})

</script>
