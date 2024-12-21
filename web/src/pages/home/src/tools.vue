<template>
    <div class="tools" data-flex>
        <div style="flex: 1">
            <h-tool-tip>
                <h-button v-disabled="!user" type="primary" @click="$emit('showNewFolderDialog')">
                    创建目录
                </h-button>
                <template #tip>
                    {{ user ? '在当前目录下创建目录' : '请先登录' }}
                </template>
            </h-tool-tip>
        </div>
        <h-tool-tip>
            <h-button @click="update">
                <el-icon>
                    <Refresh/>
                </el-icon>
            </h-button>
            <template #tip>
                刷新
            </template>
        </h-tool-tip>
        <el-popover width="400px">
            <template #reference>
                <h-button>
                    <el-icon>
                        <Sort/>
                    </el-icon>
                    <h-badge :value="UploadTasks.length"
                             style="display: inline-block;font-size: 10px;margin-right: 1em"/>
                </h-button>
            </template>
            <template #default>
                <el-empty v-if="!UploadTasks.length">
                    <template #description>
                        空空如也
                    </template>
                </el-empty>
                <div>
                    <div v-for="(t,i) in UploadTasks" class="task" data-relative>
                        <div :style="{ '--p':t.progress(),  background:calcBg(t.status())}"
                             class="progress">
                        </div>
                        <div>
                            <h4>{{ t.file().name }}</h4>
                        </div>
                        {{ t.statusText() }} {{ (t.progress() * 100).toFixed(1) }}%
                        <el-icon v-if="t.status()>UploadStatus.Uploading" class="remove-btn"
                                 @click="removeTask(i)">
                            <Close/>
                        </el-icon>
                    </div>
                </div>
            </template>
        </el-popover>
        <el-icon data-pointer size="1em" style="padding: 0 0.5em;flex: 0 0 auto" @click="listMode=!listMode">
            <ListMode v-if="listMode"/>
            <GridMode v-else/>
        </el-icon>
    </div>
</template>

<style lang="scss" scoped>
.tools {
  align-items: center;
  padding: 0.2em;
  user-select: none;
  width: 100%;
}

.task {
  border: gray solid 1px;
  position: relative;
}

.progress {
  --p: 0;
  height: 100%;
  left: 0;
  position: absolute;
  scale: var(--p, 0) 1;
  top: 0;
  transform-origin: left center;
  transition: all 0.1s ease-out;
  width: 100%;
  z-index: -1;
}

.remove-btn {
  cursor: pointer;
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
}


</style>

<script lang="ts" setup>
import {ListMode, GridMode} from '@/icon'
import {user} from '@/utils/globals'
import {UploadStatus, UploadTasks} from '@/utils/upload-tasks'
import {Close, Refresh, Sort} from '@element-plus/icons-vue'
import {convertColor, HBadge, HButton, HToolTip} from '@yin-jinlong/h-ui'

const listMode = defineModel<boolean>('listMode')

defineProps<{
    update: () => void
}>()

defineEmits({
    showNewFolderDialog() {
    }
})

function calcBg(status: UploadStatus) {
    return status == UploadStatus.Error ?
        convertColor('danger', '2') :
        convertColor('success', '2')
}

function removeTask(i: number) {
    UploadTasks.splice(i, 1)
}

</script>
