<template>
    <div class="box" data-flex-center>
        <div class="width">
            {{ timeFn(current) }}{{ timeTogether ? '/' + timeFn(duration) : '' }}
        </div>
        <div :style="`--p: ${!duration?0:current/duration*100}%;--sp:${seekProgress*100}%`"
             class="time-bar margin"
             data-pointer
             @click="seekTo">
            <div class="progress"
                 @mousemove="onMouseMoveTimeBar"/>
            <div class="float dot"/>
            <div v-for="c in chapters"
                 :key="c.start"
                 :style="`--p:${c.start/duration*100}%`"
                 class="chapter-dot"/>
            <h-tool-tip class="float seek-dot">
                <template #tip>
                    <div data-flex-column-center>
                        <p v-for="e in extras">{{ e }}</p>
                        <div v-if="chapters?.length">{{ chapterTitle() }}</div>
                        <span>{{ timeFn(duration * seekProgress) }}</span>
                    </div>
                </template>
            </h-tool-tip>
        </div>
        <div v-if="!timeTogether" class="width margin">
            <p>{{ timeFn(duration) }}</p>
        </div>
    </div>
</template>

<style lang="scss" scoped>
@use '@yin-jinlong/h-ui/style/src/tools/fns' as *;

.box {
  flex: 1;

  .margin {
    margin-left: 0.5em;
  }
}

.width {
  flex: 0 0 auto;
}

.time-bar {
  align-items: center;
  border-radius: 1em;
  display: flex;
  flex: 1;
  height: 0.5em;
  padding: 0.2em 0;
  position: relative;

  &:hover {
    --hover: 1;
  }

  .progress {
    background: var(--progress-bar-background-color, rgb(128, 128, 128, 0.3));
    border-radius: 1em;
    box-sizing: border-box;
    height: 0.5em;
    overflow: hidden;
    position: absolute;
    width: 100%;

    &::before {
      background-color: get-css(color, primary);
      content: '';
      display: block;
      height: 100%;
      transform-origin: left center;
      width: var(--p, 0);
    }

  }

  .float {
    border-radius: 50%;
    opacity: var(--hover, 0);
    position: absolute;
    transform: translate(-50%, 0) scale(var(--hover, 0));
    transform-origin: center;
    z-index: 10;
  }

  .dot {
    background: get-css(color, primary-1);
    height: 0.8em;
    left: var(--p, 0);
    transition: all 0.2s ease-out;
    width: 0.8em;
  }

  .seek-dot {
    background: get-css(color, white--2);
    border-radius: 0;
    height: 0.5em;
    left: var(--sp, 0);
    transition: all 0.1s ease-out;
    width: 2px;
  }

  .chapter-dot {
    background: white;
    border-radius: 1em;
    height: 0.3em;
    left: var(--p);
    pointer-events: none;
    position: absolute;
    transform: translateX(-50%);
    width: 0.3em;
  }
}

</style>

<script lang="ts" setup>

import {SeekableProgressBarProps} from './props'
import {sec2MinuteStr, sec2TimeStr} from '@/utils/time'
import {HToolTip} from '@yin-jinlong/h-ui'

const seekProgress = ref(0)
const extras = reactive<string[]>([])
const props = defineProps<SeekableProgressBarProps>()
const timeFn = ref(sec2TimeStr)
const emits = defineEmits({
    seek: (p: number) => void 0
})

function onMouseMoveTimeBar(e: MouseEvent) {
    let p = e.offsetX / (e.target as HTMLElement).offsetWidth
    seekProgress.value = p
    let es = props.progressExtra?.(p) ?? []
    extras.length = 0
    extras.push(...es)
}

function seekTo() {
    emits('seek', seekProgress.value)
}

function chapterTitle() {
    if (!props.chapters)
        return
    let i = props.chapters.findIndex(c => c.start / props.duration >= seekProgress.value)
    if (i < 0)
        i = props.chapters.length
    return props.chapters[i - 1]?.title
}

watch(() => props.minutes, (nv) => {
    timeFn.value = nv ? sec2MinuteStr : sec2TimeStr
}, {
    immediate: true
})

</script>
