<template>
    <div class="floating-bar" data-flex-center>
        <div class="width">
            <el-icon data-pointer size="20px" @click="playPause">
                <VideoPause v-if="MiniMusicPlayer.status.playing"/>
                <VideoPlay v-else/>
            </el-icon>
        </div>
        <div class="width">
            <div class="title marquee">
                <div>
                    <span>{{ MiniMusicPlayer.status.item?.title }}</span>
                    <span>{{ MiniMusicPlayer.status.item?.title }}</span>
                </div>
            </div>
        </div>
        <seekable-progress-bar
                :current="MiniMusicPlayer.status.current"
                :duration="MiniMusicPlayer.status.duration"
                time-together
                @seek="seekTo"/>
        <div class="width"
             data-pointer
             data-relative
             @mouseenter="showVolumeBar"
             @mouseout="hideVolumeBar">
            <h-tool-tip place="bottom"
                        @click="MiniMusicPlayer.muted(!MiniMusicPlayer.status.muted)"
                        @mousemove="showVolumeBar">
                <el-icon>
                    <VolumeMuted v-if="MiniMusicPlayer.status.muted"/>
                    <VolumeZero v-else-if="MiniMusicPlayer.status.volume==0"/>
                    <VolumeLow v-else-if="MiniMusicPlayer.status.volume<0.33"/>
                    <VolumeMid v-else-if="MiniMusicPlayer.status.volume<0.67"/>
                    <VolumeHigth v-else/>
                </el-icon>
                <template #tip>
                    <span>{{ (MiniMusicPlayer.status.volume * 100).toFixed(0) }}%</span>
                </template>
            </h-tool-tip>
            <div v-if="showVolume"
                 class="volume-bar"
                 @mousemove="showVolumeBar"
                 @mouseout="hideVolumeBar">
                <el-slider
                        v-model="audioVolume"
                        :disabled="MiniMusicPlayer.status.muted"
                        :format-tooltip="v=>v+'%'"
                        placement="left"
                        vertical/>
            </div>
        </div>
        <div class="width">
            <el-popover width="max-content">
                <template #reference>
                    <el-icon data-pointer>
                        <play-list/>
                    </el-icon>
                </template>
                <div>
                    <div v-for="i in MiniMusicPlayer.size" data-fill-width>
                        <h-button
                                :color="(i-1)==MiniMusicPlayer.now()?'primary':'info'"
                                style="padding: 0.5em 0"
                                type="link"
                                @click="MiniMusicPlayer.play(i-1)">
                            {{ i }}. {{ MiniMusicPlayer.get(i - 1).title }}
                        </h-button>
                    </div>
                </div>
            </el-popover>
        </div>
        <div class="width">
            <el-icon data-pointer @click="closePlayer">
                <CloseBold/>
            </el-icon>
        </div>
    </div>
</template>

<style lang="scss" scoped>
.floating-bar {
  background-color: #fafafa;
  border-radius: 1em;
  bottom: 1em;
  box-shadow: #989898 0 0 5px;
  left: 5%;
  padding: 0.5em;
  position: fixed;
  width: 90%;

  & > div {
    margin: 0 0.5em;
  }

  & > .width {
    align-items: center;
    display: flex;
    flex: 0 0 auto;
    justify-content: center;
  }

}

.title {
  max-width: 8em;
  position: relative;
}

.volume-bar {
  bottom: calc(100% + 1em);
  height: 100px;
  left: -50%;
  position: absolute;
}

.marquee {
  overflow: hidden;
  width: 100%;

  & > div {
    animation: marquee 10s linear infinite;
    display: inline-block;
    hyphens: none;
    width: max-content;

    & > span:nth-child(2) {
      padding-left: 2em;
    }

  }

}

@keyframes marquee {
  0% {
    transform: translateX(0);
  }
  75%, 100% {
    transform: translateX(calc(-50% - 1em));
  }
}

</style>

<script lang="ts" setup>
import PlayList from './play-list.vue'
import {MiniMusicPlayer} from './mini-music-player'
import VolumeHigth from '@/pages/play/src/volume-higth.vue'
import VolumeLow from '@/pages/play/src/volume-low.vue'
import VolumeMid from '@/pages/play/src/volume-mid.vue'
import VolumeMuted from '@/pages/play/src/volume-muted.vue'
import VolumeZero from '@/pages/play/src/volume-zero.vue'
import {CloseBold, VideoPause, VideoPlay} from '@element-plus/icons-vue'
import {HToolTip, HButton} from '@yin-jinlong/h-ui'

const showVolume = ref(false)
const audioVolume = ref(0)
let lastHideVolumeId = 0

function playPause() {
    if (MiniMusicPlayer.status.playing) {
        MiniMusicPlayer.pause()
    } else {
        MiniMusicPlayer.play()
    }
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

function seekTo(p: number) {
    MiniMusicPlayer.seekP(p)
}

function closePlayer() {
    MiniMusicPlayer.close()
}

onMounted(() => {
    audioVolume.value = MiniMusicPlayer.status.volume * 100
})

watch(audioVolume, nv => {
    MiniMusicPlayer.volume(nv / 100)
})

</script>
