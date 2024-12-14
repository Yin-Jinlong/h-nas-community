<template>
    <div class="contents"
         data-fill-size
         data-flex-column>
        <div class="tools" data-flex>
            <div style="flex: 1">
                <h-tool-tip>
                    <h-button v-disabled="!user" type="primary" @click="shows.newFolderDialog = true">
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
                                 style="display: inline-block;font-size: 16px;margin-right: 1em"/>
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
                            <div :style="{
                                '--p':t.progress(),
                                background:calcBg(t.status())
                            }"
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
        </div>
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
        <el-scrollbar data-relative height="100%">
            <div data-relative
                 style="flex: 1"
                 @dragenter="Dragger.onDragStart"
                 @dragleave="Dragger.onDragCancel"
                 @dragover="Dragger.onDragOver"
                 @drop="Dragger.onDragEnd"
                 @mouseenter="Dragger.mouseIn.value=true"
                 @mouseleave="Dragger.mouseIn.value=false"
                 @mouseout="Dragger.onDragCancel">
                <div ref="draggerEle"
                     :class="{'dragger':true,'drag':Dragger.isDragging.value}"
                     data-absolute
                     data-fill-size
                     data-flex-center>
                    <div v-if="Dragger.isDragging.value">
                        {{ Dragger.infoText }}
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
                        <file-grid-view
                                v-model="files[i].preview"
                                :info="f.info"
                                @click="onClick"
                                @dblclick="onDblClick"/>
                        <div class="file-name">{{ f.info.name }}</div>
                        <div class="file-op-menu">
                            <file-grid-options
                                    :dir="f.info.fileType==='FOLDER'"
                                    :media-type="f.info.mediaType"
                                    @command="onCommand($event,f)"/>
                        </div>
                    </div>
                </div>
            </div>
        </el-scrollbar>
        <image-viewer
                v-model="shows.imageViewer"
                :count="images.length"
                :index="nowIndex"
                :on-get="getNow"
                :on-get-raw="getRaw"
                :on-get-raw-size="getRawSize"
                :on-next="getNext"
                :on-prev="getPrev"/>
        <file-info-dialog v-if="activeFile"
                          v-model="shows.fileInfoDialog"
                          v-model:preview="activeFile.preview"
                          :extra-info="activeFile.extraInfo"
                          :info="activeFile?.info"/>
        <count-dialog v-model="shows.countDialog" :path="subPath(nowPaths,activeFile?.info?.name??'')"/>
        <rename-dialog v-model="shows.renameDialog" :name="activeFile?.info?.name ?? ''" @rename-file="renameFile"/>
        <new-folder-dialog v-model="shows.newFolderDialog" @new-folder="newFolder"/>
    </div>
</template>

<style lang="scss" scoped>
@use '@yin-jinlong/h-ui/style/src/tools/fns' as *;

.tools {
  padding: 0.2em;
  width: 100%;
}

.contents {
  position: relative;
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
  height: max-content;
  line-height: 1em;
  max-height: 2em;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 100%;
  word-break: break-all;

  &::before {
    background: linear-gradient(to right, transparent 25%, #fff);
    bottom: 1em;
    content: '';
    height: 1em;
    position: absolute;
    right: 0;
    width: 100%;
  }

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


.uploader {
  border-radius: 6px;
  cursor: pointer;
  height: 200px;
  overflow: hidden;
  position: relative;
  transition: var(--el-transition-duration-fast);
  width: 100%;

}

:deep(.el-upload--text) {
  height: 100%;
  width: 100%;

  & > .el-upload-dragger {
    height: 100%;
    width: 100%;
  }
}


.upload-icon {
  color: #8c939d;
  font-size: 28px;
  height: 100%;
  text-align: center;
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

:deep(.el-scrollbar__view) {
  display: flex;
  flex-direction: column;
  height: 100%;
}

</style>
<script lang="ts" setup>

import {FileGridCommand, FileGridOptions, FileGridView, FileInfoDialog, ImageViewer} from '@/components'
import {MiniMusicPlayer} from '@/components/music-mini-player/src/mini-music-player'
import {FileWrapper} from './type'
import API from '@/utils/api'
import {user} from '@/utils/globals'
import {subPath} from '@/utils/path'
import {uploadPublicFile, UploadStatus, UploadTasks} from '@/utils/upload-tasks'
import {ArrowDown, Close, Refresh, Sort} from '@element-plus/icons-vue'
import {convertColor, HBadge, HButton, HMessage, HToolTip} from '@yin-jinlong/h-ui'
import CountDialog from './count-dialog.vue'
import {Dragger} from './dragger'
import NewFolderDialog from './new-folder-dialog.vue'
import RenameDialog from './rename-dialog.vue'
import {onInfoCommand} from './file-info'
import {nowIndex, getNow, getNext, getPrev, getRaw, getRawSize} from './image-viewer-helper'
import {images, files, updateFiles} from './files'

const route = useRoute()
const router = useRouter()

const shows = reactive({
    countDialog: false,
    imageViewer: false,
    fileInfoDialog: false,
    renameDialog: false,
    newFolderDialog: false,
})

const activeFile = ref<FileWrapper>()
const draggerEle = ref<HTMLDivElement>()
const isPublic = ref(true)
const nowPaths = reactive<string[]>([])

Dragger.upload = upload

function onChangeRoot(cmdArr: boolean[]) {
    isPublic.value = cmdArr[0]
    update()
}

function calcBg(status: UploadStatus) {
    return status == UploadStatus.Error ?
        convertColor('danger', '2') :
        convertColor('success', '2')
}

function removeTask(i: number) {
    UploadTasks.splice(i, 1)
}

function toPath(i: number) {
    router.push({
        path: '/files/' + nowPaths.slice(0, i + 1).join('/')
    })
}

function enterFolder(name: string) {
    nowPaths.push(name)
    toPath(nowPaths.length - 1)
}

function showPreview(f: FileWrapper) {
    if (f.previewIndex !== undefined) {
        nowIndex.value = f.previewIndex
        shows.imageViewer = true
    }
}

function update() {
    nowIndex.value = -2
    files.length = 0
    images.length = 0
    setTimeout(() => {
        updateFiles(nowPaths.length ? nowPaths.join('/') : '/')
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

function isAudio(f: FileWrapper) {
    return /^(audio)\/.*/.test(f.info.mediaType ?? '')
}

function addToPlayList(f: FileWrapper) {
    let info = f.info
    let lrcName = info.name.replace(/\.[^.]+$/, '.lrc')
    let lrcFile = files.find(i => i.info.name == lrcName)

    return MiniMusicPlayer.add({
        title: info.name,
        async lrc() {
            if (!lrcFile)
                return
            let r = await fetch(API.publicFileURL(subPath(lrcFile.info.dir, lrcFile.info.name)))
            return await r.text()
        },
        src: API.publicFileURL(subPath(info.dir, info.name)),
        async info() {
            return await API.getPublicAudioInfo(subPath(info.dir, info.name))
        }
    })
}

function onCommand(cmd: FileGridCommand, f: FileWrapper) {
    activeFile.value = f
    switch (cmd) {
        case 'play':
            if (f.info.mediaType?.startsWith('video')) {
                router.push({
                    path: '/play',
                    query: {
                        path: subPath(f.info.dir, f.info.name)
                    }
                })
                return
            }
            MiniMusicPlayer.play(addToPlayList(f))
            break
        case 'add-to-play-list':
            if (isAudio(f))
                addToPlayList(f)
            break
        case 'add-all-to-play-list':
            files.forEach(f => {
                if (isAudio(f))
                    addToPlayList(f)
            })
            break
        case 'rename':
            shows.renameDialog = true
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
            onInfoCommand(f)
            shows.fileInfoDialog = true
            break
        case 'count':
            shows.countDialog = true
            break
    }
}


function upload(file: File, isFile: boolean) {
    if (isFile) {
        uploadPublicFile(nowPaths.join('/') + '/' + file.name, file, () => {
            update()
        })
        HMessage.success('已添加任务')
    } else {
        newFolder(file.name, () => {
        })
    }
}


function onClick(e: MouseEvent, info: FileInfo) {

}

function onDblClick(e: MouseEvent, info: FileInfo) {
    if (info.fileType == 'FOLDER')
        enterFolder(info.name)
}

function renameFile(name: string, ok: (close: boolean) => void) {
    let success = false
    API.renamePublic(subPath(nowPaths, activeFile.value!.info.name), name).then(res => {
        if (res) {
            shows.renameDialog = false
            HMessage.success('重命名成功')
            activeFile.value!.info.name = name
            success = true
        }
    }).finally(() => {
        ok(success)
    })
}

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
