<template>
    <div :title="info.name"
         class="box"
         data-flex-center
         @click="onClick">
        <div v-if="info.fileType==='FILE'" class="img" data-flex-center>
            <el-icon v-if="(previewPath?.length??0)<1" size="100%">
                <el-skeleton :loading="previewPath===undefined" animated data-fill-size>
                    <template #template>
                        <el-skeleton-item :variant="loadingVariant()"
                                          style="width: 8em;height: 8em"/>
                    </template>
                    <component :is="fileIcon"/>
                </el-skeleton>
            </el-icon>
            <el-image v-else
                      :alt="info.name"
                      :src="previewPath"
                      :title="info.name"
                      class="img"
                      fit="cover"
                      loading="lazy">
                <template #placeholder>
                    <el-icon size="100%">
                        <el-skeleton animated data-fill-size>
                            <template #template>
                                <el-skeleton-item style="width: 8em;height: 8em"
                                                  variant="image"/>
                            </template>
                        </el-skeleton>
                    </el-icon>
                </template>
            </el-image>
        </div>
        <el-icon v-else size="100%">
            <folder-file/>
        </el-icon>
    </div>

</template>

<style lang="scss" scoped>
.box, :deep(.el-icon) {
  -moz-user-select: none;
  -ms-user-select: none;
  -webkit-user-select: none;
  user-select: none;

  &, & > svg {
    height: 8em;
    width: 8em;
  }

}

.img {
  height: 100%;
  width: 100%;
}
</style>

<script lang="ts" setup>
import API from '@/utils/api'
import {subPath} from '@/utils/path'
import {FolderFile, UnknownFile} from '@/icon'
import {IconMapping} from '@/icon/file'
import FileGridViewPropsDefault, {FileGridViewProps} from './props'

const extra = defineModel<FilePreview>({
    required: true
})
const props = withDefaults(defineProps<FileGridViewProps>(), FileGridViewPropsDefault)
const previewPath = ref<string | undefined>()
const fileIcon = shallowRef<Component>()
const emits = defineEmits({
    'click': (e: MouseEvent, info: FileInfo) => {
    },
    'dblclick': (last: MouseEvent, info: FileInfo) => {
    }
})

let clickTimeout = 0

function loadingVariant() {
    let mt = props.info.mediaType ?? ''
    return /^(image)|(video)\/.*/.test(mt) ? 'image' : 'rect'
}

function onClick(e: MouseEvent) {
    if (clickTimeout) {
        clearTimeout(clickTimeout)
        clickTimeout = 0
        emits('dblclick', e, props.info)
    } else {
        clickTimeout = setTimeout(() => {
            clickTimeout = 0
            emits('click', e, props.info)
        }, props.dbClickInterval) as unknown as number
    }
}

function defaultIcon() {
    fileIcon.value = UnknownFile
}

async function updateIcon() {
    let mt = props.info.mediaType
    if (!mt) {
        return defaultIcon()
    }
    let i = mt.indexOf('/')
    if (i < 1)
        return defaultIcon()
    let type = mt.substring(0, i)
    let subType = mt.substring(i + 1)
    let map = IconMapping[type]
    if (!map)
        return defaultIcon()
    if (typeof map == 'function') {
        fileIcon.value = (await map()).default
    } else {
        let icon = map[subType]
        fileIcon.value = icon ? (await icon()).default : UnknownFile
    }
}

async function getExtra() {
    let info = await API.getPublicFilePreview(subPath(props.info.dir, props.info.name))
    if (!info)
        return
    extra.value.thumbnail = info.thumbnail
    extra.value.preview = info.preview
    return info
}

onMounted(() => {
    updateIcon()
})

watch(extra, (nv) => {
    if (nv.thumbnail === undefined) {
        previewPath.value = ''
        return
    }
    if (nv.thumbnail != '') {
        previewPath.value = API.publicThumbnailURL(nv.thumbnail!)
        return
    }

    async function retry() {
        let info = await getExtra()
        if (!info)
            return
        if (info.thumbnail === undefined && info.preview === undefined) {
            previewPath.value = ''
            return
        }

        if (info.thumbnail !== undefined && info.thumbnail.length != 0) {
            previewPath.value = API.publicThumbnailURL(info.thumbnail)
        }
        if (info.preview == '' || info.thumbnail == '')
            setTimeout(() => {
                retry()
            }, 500 + Math.random() * 500)
    }

    retry()
}, {
    immediate: true
})

</script>
