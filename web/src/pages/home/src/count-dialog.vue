<template>
    <el-dialog v-model="show" width="max-content">
        <template #header>
            {{ path }}
        </template>
        <table v-loading="loadingChildrenCount" data-relative>
            <tr>
                <td><b>当前目录下：</b></td>
                <td>
                    {{ counts?.subCount ?? '?' }}
                </td>
            </tr>
            <tr>
                <td><b>包含子目录：</b></td>
                <td>
                    {{ counts?.subsCount ?? '?' }}
                </td>
            </tr>
        </table>
    </el-dialog>
</template>

<style lang="scss" scoped>

</style>

<script lang="ts" setup>

import API from '@/utils/api'

const show = defineModel({
    default: false
})
const props = defineProps<{
    path: string
}>()


const counts = ref<FolderChildrenCount>()
const loadingChildrenCount = ref<boolean>(false)

watch(show, nv => {
    if (nv) {
        loadingChildrenCount.value = true
        counts.value = undefined
        API.getDirChildrenCount(props.path).then(res => {
            if (!res)
                return
            counts.value = res
            loadingChildrenCount.value = false
        })
    }
})

</script>
