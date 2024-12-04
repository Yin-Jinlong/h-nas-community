<template>
    <div class="floating-bar" data-flex-center>
        <div class="width cover-box">
            <div :class="{cover:1,playing:MiniMusicPlayer.status.playing}">
                <el-image :src="getCover()">
                    <template #error>
                        <el-icon size="2em">
                            <MusicFile/>
                        </el-icon>
                    </template>
                </el-image>
            </div>
        </div>
        <div class="width">
            <el-icon data-pointer @click="MiniMusicPlayer.nextMode()">
                <play-normal v-if="MiniMusicPlayer.status.playMode==PlayMode.Normal"/>
                <play-repeat-all v-else-if="MiniMusicPlayer.status.playMode==PlayMode.RepeatAll"/>
                <play-repeat-this v-else-if="MiniMusicPlayer.status.playMode==PlayMode.RepeatThis"/>
                <play-random v-else-if="MiniMusicPlayer.status.playMode==PlayMode.Random"/>
            </el-icon>
        </div>
        <div class="width">
            <el-icon data-pointer @click="MiniMusicPlayer.playPrev()">
                <play-prev/>
            </el-icon>
        </div>
        <div class="width">
            <el-icon data-pointer size="20px" @click="playPause">
                <VideoPause v-if="MiniMusicPlayer.status.playing"/>
                <VideoPlay v-else/>
            </el-icon>
        </div>
        <div class="width">
            <el-icon data-pointer @click="MiniMusicPlayer.playNext()">
                <play-next/>
            </el-icon>
        </div>
        <div class="width">
            <div class="title marquee">
                <div>
                    <span>{{ getTitle() }}</span>
                    <span>{{ getTitle() }}</span>
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
                    <div v-for="i in MiniMusicPlayer.size" class="music-item" data-fill-width>
                        <h-button
                                :color="(i-1)==MiniMusicPlayer.now()?'primary':'info'"
                                style="padding: 0.5em 0"
                                type="link"
                                @click="MiniMusicPlayer.play(i-1)">
                            {{ i }}. {{ MiniMusicPlayer.get(i - 1).title }}
                        </h-button>
                        <el-icon class="remove-item-btn" data-pointer @click="MiniMusicPlayer.remove(i-1)">
                            <Close/>
                        </el-icon>
                    </div>
                    <h-button color="danger" data-fill-width type="link" @click="MiniMusicPlayer.close()">
                        清空
                    </h-button>
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

.cover-box {
  height: 2em;
  overflow: visible;
  width: 3em;
}

.cover {
  align-items: center;
  background: white;
  border-radius: 2em;
  bottom: 0;
  box-shadow: gray 0 0 4px;
  cursor: pointer;
  display: flex;
  height: 4em;
  justify-content: center;
  overflow: hidden;
  position: absolute;
  transform-origin: bottom center;
  transition: all 0.2s ease-out;
  width: 4em;

  & > div {
    animation: cover 15s linear infinite;
    animation-play-state: paused;
  }

  &:hover {
    scale: 3;
    z-index: 1;
  }

  &.playing > div {
    animation-play-state: running;
    box-shadow: gray 0 0 8px;
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

.music-item {
  padding-right: 1em;
  position: relative;
}

.remove-item-btn {
  position: absolute;
  right: 5px;
  top: 50%;
  transform: translateY(-50%);
}

@keyframes cover {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
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
import PlayNext from '@/components/music-mini-player/src/play-next.vue'
import PlayPrev from '@/components/music-mini-player/src/play-prev.vue'
import VolumeHigth from '@/pages/play/src/volume-higth.vue'
import VolumeLow from '@/pages/play/src/volume-low.vue'
import VolumeMid from '@/pages/play/src/volume-mid.vue'
import VolumeMuted from '@/pages/play/src/volume-muted.vue'
import VolumeZero from '@/pages/play/src/volume-zero.vue'
import API from '@/utils/api'
import {subPath} from '@/utils/path'
import {Close, CloseBold, VideoPause, VideoPlay} from '@element-plus/icons-vue'
import {HButton, HToolTip} from '@yin-jinlong/h-ui'
import {MiniMusicPlayer, PlayMode} from './mini-music-player'
import PlayList from './play-list.vue'

const showVolume = ref(false)
const audioVolume = ref(0)
let lastHideVolumeId = 0

function getCover() {
    let info = MiniMusicPlayer.info
    if (info.cover)
        return API.publicAudioCoverURL(info.cover)
    return ''
}

function getTitle() {
    let info = MiniMusicPlayer.info
    if (info.path) {
        if (info.title) {
            let r = info.title
            if (info.album)
                r += ` - ${info.album}`
            if (info.artists)
                r += ` - ${info.artists}`
            return r
        }
    }
    return MiniMusicPlayer.status.item?.title
}

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

watch(() => MiniMusicPlayer.status.volume, () => {
    audioVolume.value = MiniMusicPlayer.status.volume * 100
}, {
    immediate: true
})

watch(audioVolume, nv => {
    MiniMusicPlayer.volume(nv / 100)
})

</script>
