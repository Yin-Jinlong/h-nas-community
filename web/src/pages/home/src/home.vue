<template>
    <top-bar :on-uploaded="onUploaded" @new-folder="newFolder"/>
    <el-scrollbar height="100%">
        <div class="contents"
             data-fill-size
             data-flex-column>
            <div class="breadcrumbs">
                <el-breadcrumb separator="/">
                    <el-breadcrumb-item>
                        <el-dropdown @command="onChangeRoot">
                            <template #default>
                                <div class="breadcrumb" data-flex-center data-pointer>
                                    {{ isPublic ? '公开' : '个人' }}
                                    <el-icon>
                                        <arrow-down/>
                                    </el-icon>
                                </div>
                            </template>
                            <template #dropdown>
                                <el-dropdown-item :command="[true]">
                                    公开
                                </el-dropdown-item>
                                <el-dropdown-item :command="[false]">
                                    个人
                                </el-dropdown-item>
                            </template>
                        </el-dropdown>
                    </el-breadcrumb-item>
                    <el-breadcrumb-item to="/">
                        root
                    </el-breadcrumb-item>
                    <el-breadcrumb-item v-for="(p,i) in nowPaths" class="breadcrumb" @click="toPath(i)">
                        {{ p }}
                    </el-breadcrumb-item>
                </el-breadcrumb>
            </div>
            <div data-relative
                 style="flex: 1"
                 @dragleave="onDragCancel"
                 @mouseout="onDragCancel"
                 @drop.prevent="onDragEnd"
                 @dragenter.prevent="onDragStart"
                 @dragover.prevent="onDragOver">
                <div :class="{'dragger':true,'drag':uploadIsDragging}"
                     data-absolute
                     data-fill-size
                     data-flex-center>
                    <div v-if="uploadIsDragging">
                        {{ uploadInfoText }}
                    </div>
                </div>
                <el-empty v-if="!files.length"/>
                <div class="file-container">
                    <div v-for="f in files"
                         :key="f.name"
                         class="file-box"
                         data-fill-size data-flex-column-center
                         @click="showPreview(f)">
                        <file-grid-view :info="f"
                                        @click="onClick"
                                        @dblclick="onDblClick"/>
                        <div class="file-name">{{ f.name }}</div>
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
                        <h3>{{ activeFile?.name ?? '' }}</h3>
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
        </div>
    </el-scrollbar>
</template>

<style lang="scss" scoped>
@use '@yin-jinlong/h-ui/style/src/tools/fns' as *;
@use '@/vars' as *;

.contents {
  padding-top: $top-bar-height;
}

.breadcrumbs {
  cursor: pointer;
  padding: 0.5em 0.3em;
}

.breadcrumb {
  font-weight: bold;
  text-decoration: underline;
  transition: all 0.2s ease-out;

  &:hover {
    color: get-css(color, primary);
  }

  :deep(.el-breadcrumb__inner) {
    color: inherit;
  }

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

:deep(.el-image).img {
  border-radius: 1em;
  height: 100%;
  width: 100%;
}

.file-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  width: 100%;
}

:deep(.el-scrollbar__view) {
  height: calc(100% - #{$top-bar-height});
}

.dragger {
  border-color: transparent;
  border-style: solid;
  border-width: 0.2rem;
  box-sizing: border-box;
  height: 100%;
  transition: all 0.2s ease-out;

  &.drag {
    background-color: rgb(128, 128, 128, 0.4);
    border-color: get-css(color, primary);
    border-style: dashed;
  }

}

</style>

<script lang="ts" setup>

import {FileGridView, TopBar} from '@/components'
import {token, user} from '@/utils/globals'
import {ArrowDown, MoreFilled} from '@element-plus/icons-vue'
import {toHumanSize} from '@/utils/size-utils'
import {pathGetName} from '@/utils/path-utils'
import axios from 'axios'
import {computed} from 'vue'
import API from '@/utils/api'
import {HMessage} from '@yin-jinlong/h-ui'

const route = useRoute()
const router = useRouter()

const nowIndex = ref(-1)
const isPublic = ref(true)
const uploadIsDragging = ref(false)
const uploadInfoText = ref('')
const nowPaths = reactive<string[]>([])
const files = reactive<FileInfo[]>([])
const showFileInfoDialog = ref(false)
const previewMap = reactive(new Map<FileInfo, number>())
const previewList = computed<string[]>(() => {
    return files.filter(f => f.preview && f.type === 'image').map(f => toImageUrl(f.name))
})
const activeFile = ref<FileInfo>()
const infoTable = computed(() => {
    let f = activeFile.value
    return [
        {
            label: '路径',
            value: getPath(f?.name ?? '')
        },
        {
            label: '文件类型',
            value: f?.fileType === 'FILE' ? '文件' : '目录'
        },
        {
            label: '类型',
            value: `${f?.type}/${f?.subType}`
        },
        {
            label: '创建时间',
            value: new Date(f?.createTime ?? 0).toLocaleString()
        },
        {
            label: '修改时间',
            value: new Date(f?.updateTime ?? 0).toLocaleString()
        },
        {
            label: '大小',
            value: toHumanSize(f?.size ?? 0)
        }
    ]
})

function onChangeRoot(cmdArr: boolean[]) {
    isPublic.value = cmdArr[0]
    updateFiles()
}

function toPath(i: number) {
    router.push({
        path: '/' + nowPaths.slice(0, i + 1).join('/')
    })
}

function enterFolder(name: string) {
    nowPaths.push(name)
    toPath(nowPaths.length - 1)
}

function getPath(name: string) {
    let s = nowPaths.join('/') + '/' + name
    return nowPaths.length ? '/' + s : s
}

function toImageUrl(name: string) {
    return API.publicFileURL(getPath(name))
}

function showPreview(f: FileInfo,) {
    let i = previewMap.get(f)
    if (i !== undefined) {
        nowIndex.value = i
    }
}

function updateFiles() {
    API.getPublicFiles(nowPaths.length ? nowPaths.join('/') : '/').then(data => {
        if (!data)
            return
        files.length = 0
        previewMap.clear()
        let i = 0

        console.log('files', data)
        data.forEach(f => {
            if (f.preview && f.type == 'image') {
                previewMap.set(f, i++)
            }
            files.push(f)
        })
    })
}

function update() {
    updateFiles()
}

function newFolder(name: string, ok: () => void) {
    let uid = user.value?.uid
    if (!uid) {
        HMessage.error('没有登录！')
        return
    }
    API.newPublicFolder(getPath(name), uid).then(res => {
        if (res) {
            HMessage.success('创建成功')
            update()
            ok()
        }
    })
}

function onUploaded() {
    update()
}

function onCommand(args: [string, FileInfo]) {
    let [cmd, f] = args
    switch (cmd) {
        case 'del':
            API.deletePublicFile(getPath(f.name)).then(res => {
                if (res) {
                    HMessage.success('删除成功')
                    update()
                }
            })
            break
        case 'info':
            activeFile.value = f
            showFileInfoDialog.value = true
            break
    }
}

let lastCancelTimeout = 0

function checkCancel() {
    if (lastCancelTimeout) {
        clearTimeout(lastCancelTimeout)
        lastCancelTimeout = 0
    }
}

function accept(e: DragEvent): boolean {
    if (!e.dataTransfer!.types.includes('Files')) {
        e.dataTransfer!.dropEffect = 'none'
        uploadInfoText.value = '不支持的类型'
        return false
    }
    e.dataTransfer!.dropEffect = 'copy'
    return true
}

function onDragStart(e: DragEvent) {
    checkCancel()
    uploadIsDragging.value = true
    if (!accept(e))
        return
    uploadInfoText.value = '松开开始上传'
}

function onDragOver(e: DragEvent) {
    uploadIsDragging.value = true
    checkCancel()
    if (!accept(e))
        return
}

function onDragEnd(e: DragEvent) {
    uploadIsDragging.value = false
    if (!accept(e))
        return
    let files = e.dataTransfer!.files
    console.log(files)
    for (let i = 0; i < files.length; i++) {
        upload(files[i])
    }
}

async function upload(file: File) {
    API.uploadPublic(nowPaths.join('/'), file).then(res => {
        if (res) {
            HMessage.success('上传成功')
            updateFiles()
        }
    })
}

function onDragCancel() {
    checkCancel()
    lastCancelTimeout = setTimeout(() => {
        uploadIsDragging.value = false
        lastCancelTimeout = 0
    }, 100) as unknown as number
}

function onClick(e: MouseEvent) {

}

function onDblClick(e: MouseEvent, info: FileInfo) {
    if (info.fileType == 'FOLDER')
        enterFolder(info.name)
}

onMounted(() => {

})

watch(() => route.params.path as string[] | undefined, async (nv?: string[]) => {
    files.length = 0
    nowPaths.length = 0
    if (nv?.length) {
        if (!nv[0].length) {
            nv.shift()
        }
        nowPaths.push(...nv)
    }
    updateFiles()
}, {
    immediate: true
})

</script>
