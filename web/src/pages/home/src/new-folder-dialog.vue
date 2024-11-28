<template>
    <el-dialog v-model="show"
               :close-on-click-modal="!newFolderPosting"
               :close-on-press-escape="!newFolderPosting"
               :show-close="!newFolderPosting">
        <template #header>
            <h3>创建目录</h3>
        </template>
        <el-form :model="newFolderData">
            <el-form-item label="目录名">
                <el-input v-model="newFolderData.name" placeholder="目录名"/>
            </el-form-item>
        </el-form>
        <h-button v-disabled="!newFolderData.name.length||newFolderPosting"
                  v-loading.inner="newFolderPosting"
                  type="primary"
                  @click="createFolder">
            创建
        </h-button>
    </el-dialog>
</template>

<style lang="scss" scoped>

</style>

<script lang="ts" setup>
import {HButton} from '@yin-jinlong/h-ui'

const show = defineModel<boolean>({
    default: false
})

const newFolderPosting = ref(false)
const newFolderData = reactive({
    name: '',
})

const emits = defineEmits({
    newFolder(name: string, ok: (close: boolean) => void) {
    }
})

function createFolder() {
    if (newFolderPosting.value)
        return
    newFolderPosting.value = true
    emits('newFolder', newFolderData.name, (close) => {
        if (close)
            show.value = false
        newFolderPosting.value = false
    })
}

</script>
