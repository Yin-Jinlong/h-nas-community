<template>
    <div :title="info.name"
         class="box"
         data-flex-center
         @click="onClick">
        <el-image
                v-if="info.fileType==='FILE'"
                :alt="info.name"
                :src="previewPath??''"
                :title="info.name"
                class="img"
                fit="cover"
                loading="lazy">
            <template #placeholder>
                <el-icon size="100%">
                    <el-skeleton animated data-fill-size>
                        <template #template>
                            <el-skeleton-item style="width: 8em;height: 8em" variant="image"/>
                        </template>
                    </el-skeleton>
                </el-icon>
            </template>
            <template #error>
                <el-icon size="100%">
                    <component :is="fileIcon" v-if="fileIcon"/>
                    <el-skeleton v-else animated data-fill-size>
                        <template #template>
                            <el-skeleton-item style="width: 8em;height: 8em" variant="image"/>
                        </template>
                    </el-skeleton>
                </el-icon>
            </template>
        </el-image>
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
</style>

<script lang="ts" setup>
import Folder from '@/components/file-grid-view/src/folder.vue'
import {IconMapping} from '@/components/file-grid-view/src/icon-mapping'
import API from '@/utils/api'
import FileGridViewPropsDefault, {FileGridViewProps} from './props'
import UnknownFile from './unknown-file.vue'

const extra = defineModel<FileExtraInfo>({
    required: true
})
const props = withDefaults(defineProps<FileGridViewProps>(), FileGridViewPropsDefault)
const previewPath = ref<string | null>()
const fileIcon = shallowRef<Component>()
const emits = defineEmits({
    'click': (e: MouseEvent, info: FileInfo) => {
    },
    'dblclick': (last: MouseEvent, info: FileInfo) => {
    }
})

let clickTimeout = 0

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

async function updateIcon(extra: FileExtraInfo) {
    let map = IconMapping[extra.type]
    if (!map)
        return
    let icon = map[extra.subType]
    fileIcon.value = icon ? (await icon()).default : UnknownFile
}

async function getExtra() {
    let info = await API.getPublicFileExtraInfo(props.info.dir + '/' + props.info.name)
    if (!info)
        return
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
})

watch(extra, (nv) => {
    if (nv.preview === undefined) {
        return
    }
    if (nv.preview?.length) {
        previewPath.value = API.publicPreviewURL(nv.preview!)
        return
    }

    async function retry() {
        let info = await getExtra()
        if (!info || info.type == 'folder')
            return
        if (info.preview?.length) {
            previewPath.value = API.publicPreviewURL(info.preview)
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
