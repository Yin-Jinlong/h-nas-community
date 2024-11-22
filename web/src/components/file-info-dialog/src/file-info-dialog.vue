<template>
    <el-dialog v-model="modelValue">
        <template #header>
            <h3>{{ info.name ?? '' }}</h3>
        </template>
        <template #default>
            <div data-flex>
                <file-grid-view v-if="modelValue"
                                v-model="extra"
                                :info="info"/>
                <table style="margin-left: 1em">
                    <tbody>
                    <tr v-for="r in infoTable">
                        <td>
                            <div data-fill-size
                                 data-flex-center
                                 style="justify-content: end;padding: 0.25em 0.2em 0.25em 0;">
                                <label>{{ r.label }}：</label>
                            </div>
                        </td>
                        <td>
                            <span>{{ r.value }}</span>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </template>
    </el-dialog>
</template>

<style lang="scss" scoped>

</style>

<script lang="ts" setup>
import {FileGridView} from '@/components'
import {toHumanSize} from '@/utils/size-utils'
import {FileInfoDialogProps} from './props'

const modelValue = defineModel<boolean>()

const extra = defineModel<FileExtraInfo>('extra', {
    required: true
})
const props = defineProps<FileInfoDialogProps>()
const infoTable = computed(() => {
    let dir = props.info.dir
    if (dir == '/')
        dir = ''
    return [
        {
            label: '路径',
            value: dir + '/' + props.info.name
        },
        {
            label: '文件类型',
            value: props.info?.fileType === 'FILE' ? '文件' : '目录'
        },
        {
            label: '类型',
            value: `${extra.value.type}/${extra.value.subType}`
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
