<template>
    <div class="contents"
         data-fill-size
         data-flex-column>
        <tools v-model:list-mode="shows.listMode"
               :update="update"
               @show-new-folder-dialog="shows.newFolderDialog = true"/>
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
        <el-scrollbar class="aaa">
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
                        <div v-for="i in 8" v-if="shows.listMode"
                             class="file-container"
                             data-list>
                            <el-skeleton-item style="width: 4em;height: 4em" variant="rect"/>
                            <div data-fill-width style="display: inline-block">
                                <el-skeleton-item style="width: 25%;height: 45%" variant="text"/>
                                <br>
                                <el-skeleton-item style="width: 75%;height: 35%" variant="text"/>
                            </div>
                        </div>
                        <div v-for="i in 8" v-else class="file-container">
                            <el-skeleton-item style="width: 8em;height: 8em" variant="rect"/>
                        </div>
                    </template>
                </el-skeleton>
                <div :data-list="shows.listMode?'':undefined" class="file-container">
                    <div v-for="(f,i) in files"
                         :key="f.info.name"
                         class="file-box"
                         data-fill-size
                         data-flex-column-center
                         @click="showPreview(f)"
                         @dblclick="onDblClick($event,f.info)">
                        <file-icon
                                v-model="files[i].preview"
                                :info="f.info"
                                :size="shows.listMode?'4em':'8em'"
                                style="flex: 0 0 auto"/>
                        <div data-fill-width style="padding-left: 0.2em">
                            <div class="file-name">{{ f.info.name }}</div>
                            <div v-if="shows.listMode" class="info-text">
                                <span>{{ f.lastTime }}</span>
                                <span>{{ toHumanSize(f.info.size) }}</span>
                            </div>
                        </div>
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
@use '@/vars' as *;

.contents {
  height: calc(100% - $top-bar-height);
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

  &[data-list] {
    display: flex;
    flex-direction: column;
    padding: 0.25em 0.5em;

    & > .file-box {
      display: flex;
      flex-direction: row;
      padding: 0;
      width: 100%;

    }

    .file-name {
      line-height: unset;
      word-break: keep-all;

      &:before {
        display: none;
      }

    }

    .info-text {
      color: gray;
      display: flex;
      opacity: 0.8;

      :nth-child(1) {
        width: 100%;
      }

      :nth-child(2) {
        width: max-content;
      }

    }

    .file-op-menu {
      position: relative;
    }

  }

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

:deep(.el-scrollbar__view) {
  display: flex;
  flex-direction: column;
  height: 100%;
}

</style>
<script lang="ts" setup>

import {FileGridCommand, FileGridOptions, FileIcon, FileInfoDialog, ImageViewer} from '@/components'
import {MiniMusicPlayer} from '@/components/music-mini-player'
import {toHumanSize} from '@/utils/size-utils'
import {FileWrapper} from './type'
import API from '@/utils/api'
import {user} from '@/utils/globals'
import {subPath} from '@/utils/path'
import {uploadPublicFile} from '@/utils/upload-tasks'
import {ArrowDown} from '@element-plus/icons-vue'
import {HMessage} from '@yin-jinlong/h-ui'
import CountDialog from './count-dialog.vue'
import {Dragger} from './dragger'
import NewFolderDialog from './new-folder-dialog.vue'
import RenameDialog from './rename-dialog.vue'
import {onInfoCommand} from './file-info'
import {nowIndex, getNow, getNext, getPrev, getRaw, getRawSize} from './image-viewer-helper'
import {images, files, updateFiles} from './files'
import Tools from './tools.vue'

const route = useRoute()
const router = useRouter()

const shows = reactive({
    countDialog: false,
    imageViewer: false,
    fileInfoDialog: false,
    renameDialog: false,
    newFolderDialog: false,
    listMode: false,
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
        case 'download':
            let link = API.publicFileURL(subPath(f.info.dir, f.info.name), true)
            console.log(link)
            location.href = link
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
