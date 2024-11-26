<template>
    <div :title="info.name"
         class="box"
         data-flex-center
         @click="onClick">
        <div v-if="info.fileType==='FILE'" class="img" data-flex-center>
            <el-icon v-if="(previewPath?.length??0)<1" size="100%">
                <el-skeleton :loading="previewPath===undefined" animated data-fill-size>
                    <template #template>
                        <el-skeleton-item :variant="modelValue.type=='image'?'image':'rect'"
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
            <folder/>
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
import Folder from './folder.vue'
import {IconMapping} from './icon-mapping'
import API from '@/utils/api'
import {subPath} from '@/utils/path'
import FileGridViewPropsDefault, {FileGridViewProps} from './props'
import UnknownFile from './unknown-file.vue'

const extra = defineModel<FileExtraInfo>({
    required: true
})
const props = withDefaults(defineProps<FileGridViewProps>(), FileGridViewPropsDefault)
const previewPath = ref<string | undefined>()
const fileIcon = shallowRef<Component>()
const emits = defineEmits({
    'click': (e: MouseEvent, info: FileInfo, extra: FileExtraInfo) => {
    },
    'dblclick': (last: MouseEvent, info: FileInfo, extra: FileExtraInfo) => {
    }
})

let clickTimeout = 0

function onClick(e: MouseEvent) {
    if (clickTimeout) {
        clearTimeout(clickTimeout)
        clickTimeout = 0
        emits('dblclick', e, props.info, extra.value)
    } else {
        clickTimeout = setTimeout(() => {
            clickTimeout = 0
            emits('click', e, props.info, extra.value)
        }, props.dbClickInterval) as unknown as number
    }
}

async function updateIcon(extra: FileExtraInfo) {
    let map = IconMapping[extra.type]
    if (!map)
        return
    let icon = map[extra.subType]
    fileIcon.value = icon ? (await icon()).default : UnknownFile
}

async function getExtra() {
    let info = await API.getPublicFileExtraInfo(subPath(props.info.dir, props.info.name))
    if (!info)
        return
    extra.value.thumbnail = info.thumbnail
    extra.value.preview = info.preview
    extra.value.type = info.type
    extra.value.subType = info.subType
    return info
}

onMounted(async () => {
    if (!extra.value.type) {
        let info = await getExtra()
        if (info)
            await updateIcon(info)
        return
    }
    await updateIcon(extra.value)
    if (extra.value.thumbnail === undefined)
        previewPath.value = ''
})

watch(extra, (nv) => {
    if (nv.thumbnail === undefined) {
        return
    }
    if (nv.thumbnail != '') {
        previewPath.value = API.publicThumbnailURL(nv.thumbnail!)
        return
    }

    async function retry() {
        let info = await getExtra()
        if (!info || info.type == 'folder')
            return
        await updateIcon(info)
        if (info.thumbnail === undefined) {
            previewPath.value = ''
            return
        }

        if (info.thumbnail.length != 0) {
            previewPath.value = API.publicThumbnailURL(info.thumbnail)
            return
        }
        setTimeout(() => {
            retry()
        }, 500 + Math.random() * 500)
    }

    retry()
}, {
    immediate: true
})

</script>
