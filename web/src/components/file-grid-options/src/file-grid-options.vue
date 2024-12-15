<template>
    <el-dropdown @command="onCommand">
        <template #default>
            <el-icon color="var(--h-color-gray-4)" size="20">
                <MoreFilled/>
            </el-icon>
        </template>
        <template #dropdown>
            <el-dropdown-menu>
                <el-dropdown-item v-if="canPlay" :command="['play']" :icon="VideoPlay">
                    播放
                </el-dropdown-item>
                <el-dropdown-item v-if="isAudio" :command="['add-to-play-list']" :icon="Plus">
                    添加到播放列表
                </el-dropdown-item>
                <el-dropdown-item v-if="isAudio" :command="['add-all-to-play-list']" :icon="Plus">
                    添加全部到播放列表
                </el-dropdown-item>
                <el-dropdown-item :command="['rename']" :icon="Edit">
                    重命名
                </el-dropdown-item>
                <el-dropdown-item :command="['download']" :icon="Download">
                    下载{{ dir ? ' tar.gz' : '' }}
                </el-dropdown-item>
                <el-dropdown-item :command="['del']" :icon="Delete" divided>
                    删除
                </el-dropdown-item>
                <el-dropdown-item :command="['info']" :icon="InfoFilled" divided>
                    信息
                </el-dropdown-item>
                <el-dropdown-item :command="['count']" :disabled="!dir" :icon="InfoFilled">
                    子文件数量
                </el-dropdown-item>
            </el-dropdown-menu>
        </template>
    </el-dropdown>
</template>

<style lang="scss" scoped>

</style>

<script lang="ts" setup>
import {FileGridCommand, FileGridOptionsProps} from './props'
import {Delete, Download, Edit, InfoFilled, MoreFilled, Plus, VideoPlay} from '@element-plus/icons-vue'

const props = defineProps<FileGridOptionsProps>()
const canPlay = computed(() => /^(video|audio)\/.*/.test(props.mediaType ?? ''))
const isAudio = computed(() => /^(audio)\/.*/.test(props.mediaType ?? ''))
const emits = defineEmits({
    'command': (cmd: FileGridCommand) => void {}
})

function onCommand(args: [FileGridCommand]) {
    emits('command', args[0])
}
</script>
