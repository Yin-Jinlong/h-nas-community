<template>
    <top-bar :on-uploaded="onUploaded"/>
    <el-scrollbar>
        <div class="contents" data-fill-size>
            <el-empty v-if="!files.length"/>
            <div class="file-container">
                <div
                        v-for="f in folders"
                        class="file-box" data-fill-size
                        data-flex-column-center>
                    <file-grid-view :info="f"/>
                    <div class="file-name">{{ f }}</div>
                </div>
                <div v-for="f in files"
                     :key="f.path"
                     class="file-box"
                     data-fill-size data-flex-column-center
                     @click="showPreview(f)">
                    <file-grid-view :info="f"/>
                    <div class="file-name">{{ getName(f.path) }}</div>
                    <div class="file-op-menu">
                        <el-dropdown @command="onCommand">
                            <template #default>
                                <el-icon color="var(--h-color-gray-4)" size="20">
                                    <MoreFilled/>
                                </el-icon>
                            </template>
                            <template #dropdown>
                                <el-dropdown-menu>
                                    <el-dropdown-item :command="['del',f]">
                                        删除
                                    </el-dropdown-item>
                                    <el-dropdown-item :command="['info',f]">
                                        信息
                                    </el-dropdown-item>
                                </el-dropdown-menu>
                            </template>
                        </el-dropdown>
                    </div>
                </div>
            </div>
            <el-dialog v-model="showFileInfoDialog">
                <template #header>
                    <h3>{{ pathGetName(activeFile?.path ?? '') }}</h3>
                </template>
                <template #default>
                    <div data-flex>
                        <file-grid-view v-if="activeFile" :info="activeFile"/>
                        <table style="margin-left: 1em">
                            <tbody>
                            <tr v-for="r in infoTable">
                                <td>
                                    <div data-fill-size
                                         data-flex-center
                                         style="justify-content: end;padding: 0.25em 0.2em 0.25em 0;">
                                        <label>{{ r.label }}：</label>
                                    </div>
                                </td>
                                <td>
                                    <span>{{ r.value }}</span>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </template>
            </el-dialog>
            <el-image-viewer
                    v-if="nowIndex>=0"
                    :initial-index="nowIndex"
                    :url-list="previewList"
                    @close="nowIndex=-1"/>
        </div>
    </el-scrollbar>
</template>

<style lang="scss" scoped>
@use '@yin-jinlong/h-ui/style/src/tools/fns' as *;
@use '@/vars' as *;

.contents {
  padding-top: $top-bar-height;
}


.file-container {
  align-content: center;
  display: grid;
  gap: 5px;
  grid-template-columns: repeat(auto-fill, minmax(9em, 1fr));
  justify-content: center;
  padding: 1em;
}

.file-box {
  border: transparent solid 2px;
  border-radius: 0.5em;
  box-sizing: border-box;
  cursor: pointer;
  overflow: hidden;
  padding: 1em;
  position: relative;
  transition: all 0.2s ease-out;

  &:hover {
    border-color: get-css(color, gray-5);
  }

  &:active {
    border-color: get-css(color, primary);
  }

}

.file-op-menu {
  position: absolute;
  right: 0;
  top: 0;

}

.img {
  border-radius: 1em;
  height: 8em;
  width: 8em;
}

.file-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  width: 100%;
}

</style>

<script lang="ts" setup>

import {FileGridView, TopBar} from '@/components'
import {MoreFilled} from '@element-plus/icons-vue'
import {toHumanSize} from '@/utils/size-utils'
import {pathGetName} from '@/utils/path-utils'
import {computed} from 'vue'
import {FileInfo} from '@/types/file-info'
import {deleteFile, getFiles} from '@/utils/api'
import {HMessage} from '@yin-jinlong/h-ui'

const nowIndex = ref(-1)
const files = reactive<FileInfo[]>([])
const folders = reactive<string[]>([])
const showFileInfoDialog = ref(false)
const previewMap = reactive(new Map<FileInfo, number>())
const previewList = computed<string[]>(() => {
    return files.filter(f => f.preview).map(f => toImageUrl(f.reqPath))
})
const activeFile = ref<FileInfo>()
const infoTable = computed(() => {
    return [
        {
            label: '路径',
            value: activeFile.value?.path ?? ''
        },
        {
            label: '修改时间',
            value: new Date(activeFile.value?.updateTime ?? 0).toLocaleString()
        },
        {
            label: '大小',
            value: toHumanSize(activeFile.value?.size ?? 0)
        }
    ]
})

function getName(path: string) {
    return path.split('/').pop()
}

function toImageUrl(path: string) {
    return `api/file/get/${path}`
}

function showPreview(f: FileInfo,) {
    let i = previewMap.get(f)
    if (i !== undefined) {
        nowIndex.value = i
    }
}

function updateFiles() {
    getFiles().then(data => {
        files.length = 0
        previewMap.clear()
        let i = 0

        console.log('files', data)
        data?.forEach(f => {
            if (f.preview) {
                previewMap.set(f, i++)
            }
            files.push(f)
        })
    }).catch(e => {
        console.log(e)
        HMessage.error(e.message ?? '未知错误')
    })
}

function update() {
    updateFiles()
}

function onUploaded() {
    update()
}

function onCommand(args: any) {
    switch (args[0]) {
        case 'del':
            deleteFile(args[1].path).then(res => {
                if (res.code === 0) {
                    HMessage.success('删除成功')
                    update()
                } else {
                    HMessage.error(res.msg ?? '未知错误')
                }
            }).catch(e => {
                HMessage.error(e.message)
            })
            break
        case 'info':
            activeFile.value = args[1]
            showFileInfoDialog.value = true
            break
    }
}

onMounted(() => {
    update()
})
</script>
