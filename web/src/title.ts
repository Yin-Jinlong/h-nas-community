import {MiniMusicPlayer, MusicItem} from '@/components'

const title = ref()
const musicItem = ref<MusicItem>()

export function setTitle(t?: string) {
    title.value = t ? t : 'H-NAS'
}

export function updateTitle() {
    let t = title.value
    let i = musicItem.value
    if (i && MiniMusicPlayer.status.playing) {
        t = '正在播放 - ' + MiniMusicPlayer.shortTitle(false)
    }
    document.title = t
}

watch(title, async (nv) => {
    await nextTick()
    updateTitle()
})

watch(() => MiniMusicPlayer.status.playing, async (nv) => {
    await nextTick()
    updateTitle()
})

watch(() => MiniMusicPlayer.status.item, async (nv) => {
    musicItem.value = nv
    await nextTick()
    updateTitle()
})
