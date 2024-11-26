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
                         data-fill-size
                         data-flex-column-center
                         @click="showPreview(f)">
                        <file-grid-view v-model="files[i].extra"
                                        :info="f.info"
                                        @click="onClick"
                                        @dblclick="onDblClick"/>
                        <div class="file-name">{{ f.info.name }}</div>
                        <div class="file-op-menu">
                            <file-grid-options @command="onCommand($event,f)"/>
                        </div>
                    </div>
                </div>
                <file-info-dialog v-if="activeFile"
                                  v-model="showFileInfoDialog"
                                  v-model:extra="activeFile.extra"
                                  :info="activeFile?.info"/>
                <el-dialog v-model="showRenameDialog"
                           :close-on-click-modal="!renamePosting"
                           :close-on-press-escape="!renamePosting"
                           :show-close="!renamePosting">
                    <template #header>
                        {{ activeFile?.info?.name }}
                    </template>
                    <template #default>
                        <el-input v-model="newName" placeholder="重命名为"/>
                        <div style="margin-top: 0.5em"/>
                        <h-button v-disabled="!newName.length" data-fill-width type="primary" @click="renameFile">
                            <span>提交</span>
                        </h-button>
                    </template>
                </el-dialog>
                <image-viewer
                        v-model="showImageViewer"
                        :count="images.length"
                        :index="nowIndex"
                        :on-get="getNow"
                        :on-get-raw="getRaw"
                        :on-get-raw-size="getRawSize"
                        :on-next="getNext"
                        :on-prev="getPrev"/>
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

import {FileGridOptions, FileGridView, FileInfoDialog, ImageViewer, TopBar} from '@/components'
import {user} from '@/utils/globals'
import {subPath} from '@/utils/path'
import {uploadPublicFile} from '@/utils/upload-tasks'
import {ArrowDown} from '@element-plus/icons-vue'
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
const showImageViewer = ref(false)
const uploadIsDragging = ref(false)
const uploadInfoText = ref('')
const nowPaths = reactive<string[]>([])
const files = reactive<FileWrapper[]>([])
const showFileInfoDialog = ref(false)
const showRenameDialog = ref(false)
const renamePosting = ref(false)
const images = reactive<FileWrapper[]>([])
const newName = ref('')
const activeFile = ref<FileWrapper>()
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

function showPreview(f: FileWrapper) {
    if (f.previewIndex !== undefined) {
        nowIndex.value = f.previewIndex
        showImageViewer.value = true
    }
}

function getUrl(f: FileWrapper) {
    return API.publicPreviewURL(f.extra.preview!!)
}

function getNow(): string | undefined {
    let i = nowIndex.value
    if (i < 0)
        i = 0
    else if (i >= files.length)
        i = files.length - 1
    let f = images[i]
    nowIndex.value = i
    if (!f)
        return
    return getUrl(f)
}

function getRaw(): string | undefined {
    let i = nowIndex.value
    if (i < 0)
        return
    let f = images[i]
    return API.publicFileURL(subPath(f.info.dir, f.info.name))
}

function getRawSize(): number | undefined {
    let i = nowIndex.value
    if (i < 0)
        return
    let f = images[i]
    return f.info.size
}

function getPrev(): string | undefined {
    let i = nowIndex.value - 1
    if (i < 0)
        i = images.length - 1
    let f = images[i]
    nowIndex.value = i
    if (!f)
        return
    return getUrl(f)
}

function getNext(): string | undefined {
    let i = nowIndex.value + 1
    if (i >= images.length)
        i = 0
    nowIndex.value = i
    let f = images[i]
    if (!f)
        return
    return getUrl(f)
}

function getInfo(file: FileWrapper) {
    API.getPublicFileExtraInfo(subPath(file.info.dir, file.info.name)).then(res => {
        if (!res)
            return
        file.extra = res
        if (res.preview !== undefined && res.type == 'image') {
            images.push(file)
            images.sort((a, b) => {
                return a.index - b.index
            })
            for (let i = 0; i < images.length; i++) {
                let f = images[i]
                f.previewIndex = i
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
            let file: FileWrapper = {
                index: files.length,
                info: f,
                extra: {
                    thumbnail: '',
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

function newFolder(name: string, ok: (close: boolean) => void) {
    let uid = user.value?.uid
    if (!uid) {
        HMessage.error('没有登录！')
        return
    }
    API.newPublicFolder(subPath(nowPaths, name), uid).then(res => {
        if (res) {
            HMessage.success('创建成功')
            update()
            ok(true)
        }
    }).finally(() => {
        ok(false)
    })
}

function onUploaded() {
    update()
}

function onCommand(cmd: string, f: FileWrapper) {
    activeFile.value = f
    switch (cmd) {
        case 'rename':
            newName.value = ''
            showRenameDialog.value = true
            renamePosting.value = false
            break
        case 'del':
            API.deletePublicFile(subPath(nowPaths, f.info.name)).then(res => {
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

async function onDragEnd(e: DragEvent) {
    if (!accept(e) || !uploadIsDragging.value)
        return
    e.preventDefault()
    uploadIsDragging.value = false
    await nextTick()
    let files = e.dataTransfer!.files
    console.log(files)
    for (let i = 0; i < files.length; i++) {
        upload(files[i])
    }
}

function upload(file: File) {
    uploadPublicFile(nowPaths.join('/') + '/' + file.name, file, () => {
        update()
    })
    HMessage.success('已添加任务')
}

function onDragCancel() {
    checkCancel()
    lastCancelTimeout = setTimeout(() => {
        uploadIsDragging.value = false
        lastCancelTimeout = 0
    }, 100) as unknown as number
}

function onClick(e: MouseEvent, info: FileInfo, extra: FileExtraInfo) {

}

function onDblClick(e: MouseEvent, info: FileInfo) {
    if (info.fileType == 'FOLDER')
        enterFolder(info.name)
}

function renameFile() {
    if (renamePosting.value)
        return
    renamePosting.value = true
    API.renamePublic(subPath(nowPaths, activeFile.value!.info.name), newName.value).then(res => {
        if (res) {
            showRenameDialog.value = false
            HMessage.success('重命名成功')

            activeFile.value!.extra.preview = ''
            activeFile.value!.info.name = newName.value
        }
    }).finally(() => {
        renamePosting.value = false
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
