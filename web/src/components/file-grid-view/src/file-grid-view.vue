<template>
    <el-image
            v-if="info.fileType==='FILE' &&info.preview"
            :alt="info.path"
            :src="info.preview"
            :title="info.path"
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
    <el-icon v-else size="8em">
        <folder v-if="info.fileType=='FOLDER'"/>
        <unknown-file v-else/>
    </el-icon>
</template>

<style lang="scss" scoped>

</style>

<script lang="ts" setup>
import Folder from '@/components/file-grid-view/src/folder.vue'
import UnknownFile from './unknown-file.vue'
import {FileGridViewProps} from './props'

const props = defineProps<FileGridViewProps>()

</script>
