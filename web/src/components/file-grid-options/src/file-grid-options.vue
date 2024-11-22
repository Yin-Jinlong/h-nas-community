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
            </el-dropdown-menu>
        </template>
    </el-dropdown>
</template>

<style lang="scss" scoped>

</style>

<script lang="ts" setup>
import {Delete, Edit, InfoFilled, MoreFilled} from '@element-plus/icons-vue'

type Command = 'rename' | 'del' | 'info'

const emits = defineEmits({
    'command': (cmd: Command) => void {}
})

function onCommand(args: [Command]) {
    emits('command', args[0])
}
</script>
