<template>
    <div :title="info.name"
         class="box"
         data-flex-center
         @click="onClick">
        <el-image
                v-if="info.fileType==='FILE' &&info.preview"
                :alt="info.name"
                :src="previewPath"
                :title="info.name"
                class="img"
                fit="cover"
                loading="lazy">
            <template #placeholder>
                <el-skeleton animated data-fill-size>
                    <template #template>
                        <el-skeleton-item style="width: 8em;height: 8em" variant="image"/>
                    </template>
                </el-skeleton>
            </template>
        </el-image>
        <el-icon v-else size="100%">
            <folder v-if="info.fileType=='FOLDER'"/>
            <unknown-file v-else/>
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
import API from '@/utils/api'
import FileGridViewPropsDefault, {FileGridViewProps} from './props'
import UnknownFile from './unknown-file.vue'

const props = withDefaults(defineProps<FileGridViewProps>(), FileGridViewPropsDefault)
const path = computed(() => {
    let info = props.info
    return info.dir == '/' ? '/' + info.name : props.info.dir + '/' + info.name
})
const previewPath = computed(() => {
    if (props.info.preview) {
        return API.publicPreviewURL(path.value)
    }
})
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

</script>
