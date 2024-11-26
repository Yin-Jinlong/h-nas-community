<template>
    <el-dropdown @command="onCommand">
        <template #default>
            <el-icon color="var(--h-color-gray-4)" size="20">
                <MoreFilled/>
            </el-icon>
        </template>
        <template #dropdown>
            <el-dropdown-menu>
                <el-dropdown-item :command="['rename']" :icon="Edit">
                    重命名
                </el-dropdown-item>
                <el-dropdown-item :command="['del']" :icon="Delete">
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
import {Delete, Edit, InfoFilled, MoreFilled} from '@element-plus/icons-vue'

const props = defineProps<FileGridOptionsProps>()

const emits = defineEmits({
    'command': (cmd: FileGridCommand) => void {}
})

function onCommand(args: [FileGridCommand]) {
    emits('command', args[0])
}
</script>
