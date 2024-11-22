<template>
    <top-bar :on-uploaded="onUploaded" @refresh="update" @new-folder="newFolder"/>
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
                 @dragenter="onDragStart"
                 @dragleave="onDragCancel"
                 @dragover="onDragOver"
                 @drop="onDragEnd"
                 @mouseenter="mouseIn=true"
                 @mouseleave="mouseIn=false"
                 @mouseout="onDragCancel">
                <div ref="draggerEle"
                     :class="{'dragger':true,'drag':uploadIsDragging}"
                     data-absolute
                     data-fill-size
                     data-flex-center>
                    <div v-if="uploadIsDragging">
                        {{ uploadInfoText }}
                    </div>
                </div>
                <el-empty v-if="nowIndex>-2&&!files.length"/>
                <el-skeleton v-if="nowIndex==-2" animated>
                    <template #template>
                        <div class="file-container">
                            <el-skeleton-item v-for="i in 8" style="width: 8em;height: 8em" variant="rect"/>
                        </div>
                    </template>
                </el-skeleton>
                <div class="file-container">
                    <div v-for="(f,i) in files"
                         :key="f.info.name"
                         class="file-box"
                         data-fill-size data-flex-column-center
                         @click="showPreview(f)">
                        <file-grid-view v-model="files[i].extra"
                                        :info="f.info"
                                        @click="onClick"
                                        @dblclick="onDblClick"/>
                        <div class="file-name">{{ f.info.name }}</div>
                        <div class="file-op-menu">
                            <el-dropdown @command="onCommand">
                                <template #default>
                                    <el-icon color="var(--h-color-gray-4)" size="20">
                                        <MoreFilled/>
                                    </el-icon>
                                </template>
                                <template #dropdown>
                                    <el-dropdown-menu>
                                        <el-dropdown-item :command="['rename',f]" :icon="Edit">
                                            重命名
                                        </el-dropdown-item>
                                        <el-dropdown-item :command="['del',f]" :icon="Delete">
                                            删除
                                        </el-dropdown-item>
                                        <el-dropdown-item :command="['info',f]" :icon="InfoFilled" divided>
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
                        <h3>{{ activeFile?.info?.name ?? '' }}</h3>
                    </template>
                    <template #default>
                        <div data-flex>
                            <file-grid-view v-if="activeFile" v-model="activeFile.extra" :info="activeFile.info"/>
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
                <el-dialog v-model="showRenameDialog">
                    <template #header>
                        {{ activeFile?.info?.name }}
                    </template>
                    <template #default>
                        <el-input v-model="newName" placeholder="重命名为"/>
                        <div style="margin-top: 0.5em"/>
                        <h-button data-fill-width type="primary" @click="renameFile">提交</h-button>
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
import {user} from '@/utils/globals'
import {ArrowDown, Delete, Edit, InfoFilled, MoreFilled} from '@element-plus/icons-vue'
import {toHumanSize} from '@/utils/size-utils'
import {computed} from 'vue'
import API from '@/utils/api'
import {HMessage, HButton} from '@yin-jinlong/h-ui'

interface FileWrapper {
    index: number
    info: FileInfo
    extra: FileExtraInfo
    previewIndex?: number
}

const route = useRoute()
const router = useRouter()

const nowIndex = ref(-2)
const isPublic = ref(true)
const uploadIsDragging = ref(false)
const uploadInfoText = ref('')
const nowPaths = reactive<string[]>([])
const files = reactive<FileWrapper[]>([])
const showFileInfoDialog = ref(false)
const showRenameDialog = ref(false)
const images = reactive<FileWrapper[]>([])
const previewList = computed<string[]>(() => {
    return images.map(f => toImageUrl(f.info.name))
})
const newName = ref('')
const activeFile = ref<FileWrapper>()
const infoTable = computed(() => {
    let f = activeFile.value
    return [
        {
            label: '路径',
            value: getPath(f?.info?.name ?? '')
        },
        {
            label: '文件类型',
            value: f?.info?.fileType === 'FILE' ? '文件' : '目录'
        },
        {
            label: '类型',
            value: `${f?.extra?.type}/${f?.extra?.subType}`
        },
        {
            label: '创建时间',
            value: new Date(f?.info?.createTime ?? 0).toLocaleString()
        },
        {
            label: '修改时间',
            value: new Date(f?.info?.updateTime ?? 0).toLocaleString()
        },
        {
            label: '大小',
            value: toHumanSize(f?.info?.size ?? 0)
        }
    ]
})
const draggerEle = ref<HTMLDivElement>()
const mouseIn = ref(false)

function onChangeRoot(cmdArr: boolean[]) {
    isPublic.value = cmdArr[0]
    update()
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

function showPreview(f: FileWrapper) {
    if (f.previewIndex !== undefined) {
        nowIndex.value = f.previewIndex
    }
}

function getInfo(file: FileWrapper) {
    API.getPublicFileExtraInfo(file.info.dir + '/' + file.info.name).then(res => {
        if (!res)
            return
        file.extra = res
        if (res.preview && res.type == 'image') {
            images.push(file)
            images.sort((a, b) => {
                return a.index - b.index
            })
            for (let i = 0; i < images.length; i++) {
                images[i].previewIndex = i
            }
        }
    })
}

function updateFiles() {
    API.getPublicFiles(nowPaths.length ? nowPaths.join('/') : '/').then(data => {
        if (!data)
            return

        console.log('files', data)
        nowIndex.value = -1
        data.forEach(f => {
            let file = {
                index: files.length,
                info: f,
                extra: {
                    preview: '',
                    subType: '?',
                    type: '?'
                }
            }
            files.push(file)
            getInfo(file)
        })
    })
}

function update() {
    nowIndex.value = -2
    files.length = 0
    images.length = 0
    setTimeout(() => {
        updateFiles()
    }, 400)
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

function onCommand(args: [string, FileWrapper]) {
    let [cmd, f] = args
    activeFile.value = f
    switch (cmd) {
        case 'rename':
            newName.value = ''
            showRenameDialog.value = true
            break
        case 'del':
            API.deletePublicFile(getPath(f.info.name)).then(res => {
                if (res) {
                    HMessage.success('删除成功')
                    update()
                }
            })
            break
        case 'info':
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
    if (mouseIn.value)
        return
    checkCancel()
    if (!accept(e))
        return
    e.preventDefault()
    uploadIsDragging.value = true
    uploadInfoText.value = '松开开始上传'
}

function onDragOver(e: DragEvent) {
    if (!accept(e) || !uploadIsDragging.value)
        return
    e.preventDefault()
    checkCancel()
}

function onDragEnd(e: DragEvent) {
    if (!accept(e) || !uploadIsDragging.value)
        return
    e.preventDefault()
    uploadIsDragging.value = false
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
            update()
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

function renameFile() {
    API.renamePublic(getPath(activeFile.value!.info.name), newName.value).then(res => {
        if (res) {
            showRenameDialog.value = false
            HMessage.success('重命名成功')

            activeFile.value!.extra.preview = ''
            activeFile.value!.info.name = newName.value
        }
    })
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
    update()
}, {
    immediate: true
})

</script>
