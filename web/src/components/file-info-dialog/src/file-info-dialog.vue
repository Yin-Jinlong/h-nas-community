<template>
    <el-dialog v-model="modelValue"
               style="--el-dialog-width: max-content;max-width: 90%">
        <template #header>
            <h3>{{ info.name ?? '' }}</h3>
        </template>
        <template #default>
            <div data-flex>
                <file-grid-view
                        v-if="modelValue"
                        v-model="extra"
                        :info="info"
                        style="flex: 0 0 auto"/>
                <table style="margin-left: 1em">
                    <tbody>
                    <tr v-for="r in infoTable">
                        <td class="label"
                            data-fill-size
                            data-flex-center>
                            <label>{{ r.label }}：</label>
                        </td>
                        <td style="word-wrap: anywhere">
                            {{ r.value }}
                        </td>
                    </tr>
                    <tr v-for="k in Object.keys(extraInfo)" v-if="extraInfo">
                        <td class="label"
                            data-fill-size
                            data-flex-center>
                            <label>{{ k }}：</label>
                        </td>
                        <td style="word-wrap: anywhere">
                            {{ extraInfo[k] }}
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </template>
    </el-dialog>
</template>

<style lang="scss" scoped>
.label {
  display: flex;
  justify-content: end;
  min-width: 5em;
  padding: 0.25em 0.2em 0.25em 0;
  width: 100%;

  & > label {
    flex: 0 0 auto;
  }
}
</style>

<script lang="ts" setup>
import {FileGridView} from '@/components'
import {subPath} from '@/utils/path'
import {toHumanSize} from '@/utils/size-utils'
import {FileInfoDialogProps} from './props'

const modelValue = defineModel<boolean>()

const extra = defineModel<FilePreview>('preview', {
    required: true
})
const props = defineProps<FileInfoDialogProps>()
const infoTable = computed(() => {
    return [
        {
            label: '路径',
            value: subPath(props.info.dir, props.info.name)
        },
        {
            label: '文件类型',
            value: props.info?.fileType === 'FILE' ? '文件' : '目录'
        },
        {
            label: '类型',
            value: `${props.info.mediaType ?? '目录'}`
        },
        {
            label: '创建时间',
            value: new Date(props.info?.createTime ?? 0).toLocaleString()
        },
        {
            label: '修改时间',
            value: new Date(props.info?.updateTime ?? 0).toLocaleString()
        },
        {
            label: '大小',
            value: toHumanSize(props.info?.size ?? 0)
        }
    ]
})
</script>
