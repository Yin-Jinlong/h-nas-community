<template>
    <div class="player-box">
        <h3>{{ path }}</h3>
        <div class="video-box" data-flex-center>
            <video id="player" ref="videoEle" class="video-js">
            </video>
        </div>
    </div>
</template>

<style lang="scss">
@import 'video.js/dist/video-js.min.css';
</style>

<style lang="scss" scoped>

.player-box {

}

.video-box {
  box-sizing: border-box;
  height: 600px;
  padding: 1em;
  width: 100%;
}

#player {
  height: 100%;
  width: 100%;
}

</style>

<script lang="ts" setup>
import API from '@/utils/api'
import videojs from 'video.js'
import Player from 'video.js/dist/types/player'

let player: Player
const videoEle = ref<HTMLVideoElement>()
const path = ref<string>()
const route = useRoute()

onMounted(() => {
    player = videojs('player', {
        controls: true
    })
    path.value = route.query.path as string
})

watch(path, (nv) => {
    player.src(API.publicFileURL(nv ?? ''))
})

onUnmounted(() => {
    player.dispose()
})

watch(() => route.query.path, (nv) => {
    path.value = nv as string
})

</script>
