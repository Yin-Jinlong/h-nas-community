<template>
    <div class="box" data-flex-center>
        <div class="width">
            {{ sec2TimeStr(current) }}{{ timeTogether ? '/' + sec2TimeStr(duration) : '' }}
        </div>
        <div :style="`--p: ${!duration?0:current/duration*100}%;--sp:${seekProgress*100}%`"
             class="time-bar margin"
             data-pointer
             @click="seekTo">
            <div class="progress"
                 @mousemove="onMouseMoveTimeBar"/>
            <div class="float dot"/>
            <h-tool-tip class="float seek-dot">
                <template #tip>
                    {{ sec2TimeStr(duration * seekProgress) }}
                </template>
            </h-tool-tip>
        </div>
        <div v-if="!timeTogether" class="width margin">
            {{ sec2TimeStr(duration) }}
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

}

</style>

<script lang="ts" setup>

import {SeekableProgressBarProps} from './props'
import {sec2TimeStr} from '@/utils/time'
import {HToolTip} from '@yin-jinlong/h-ui'

const seekProgress = ref(0)
const props = defineProps<SeekableProgressBarProps>()
const emits = defineEmits({
    seek: (p: number) => void 0
})

function onMouseMoveTimeBar(e: MouseEvent) {
    seekProgress.value = e.offsetX / (e.target as HTMLElement).offsetWidth
}

function seekTo() {
    emits('seek', seekProgress.value)
}


</script>
