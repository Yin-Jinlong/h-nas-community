<template>
    <el-dialog v-model="show"
               :close-on-click-modal="!renamePosting"
               :close-on-press-escape="!renamePosting"
               :show-close="!renamePosting">
        <template #header>
            {{ name }}
        </template>
        <template #default>
            <el-input v-model="newName" placeholder="重命名为"/>
            <div style="margin-top: 0.5em"/>
            <h-button v-disabled="!newName.length" data-fill-width type="primary" @click="renameFile">
                <span>提交</span>
            </h-button>
        </template>
    </el-dialog>
</template>

<style lang="scss" scoped>

</style>

<script lang="ts" setup>

import {HButton} from '@yin-jinlong/h-ui'

const show = defineModel({
    default: false
})
const props = defineProps<{
    name: string
}>()
const emits = defineEmits({
    renameFile(name: string, ok: (close: boolean) => void) {
    }
})


const newName = ref('')
const renamePosting = ref(false)

function renameFile() {
    if (renamePosting.value)
        return
    renamePosting.value = true
    emits('renameFile', props.name, (close) => {
        if (close)
            show.value = false
        renamePosting.value = false
    })
}

watch(show, nv => {
    if (nv)
        newName.value = props.name
})

</script>
