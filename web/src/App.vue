<template>
    <top-bar/>
    <router-view v-slot="{ Component }">
        <component :is="Component" class="padding"/>
    </router-view>
    <transition name="music-player">
        <music-mini-player v-if="MiniMusicPlayer.size"/>
    </transition>
</template>

<style lang="scss" scoped>
@use '@/vars' as *;

.padding {
  padding-top: $top-bar-height;
}

.music-player-enter-active,
.music-player-leave-active {
  transition: all 0.2s ease-in;
}

.music-player-enter-from,
.music-player-leave-to {
  opacity: 0.3;
  transform: translateY(100%);
}

</style>

<script lang="ts" setup>

import {MiniMusicPlayer} from '@/components'
import API from '@/utils/api'
import {user} from '@/utils/globals'
import {setTitle, updateTitle} from './title'

const router = useRouter()

onMounted(() => {
    let id = user.value?.uid
    if (id)
        API.tryLogin(id.toString()).then(res => {
            if (res) {
                user.value = res
            }
        })
    setTitle()
    router.afterEach(() => {
        updateTitle()
    })
})
</script>
