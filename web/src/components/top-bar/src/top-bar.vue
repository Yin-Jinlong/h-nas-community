<template>
    <div class="top-bar bg-dot" data-fill-width data-flex>
        <div style="flex: 1">
            <h-tool-tip>
                <h-button v-disabled="!user" type="primary" @click="showNewFolderDialog = true">
                    创建目录
                </h-button>
                <template #tip>
                    {{ user ? '在当前目录下创建目录' : '请先登录' }}
                </template>
            </h-tool-tip>
        </div>
        <div>
            <h-tool-tip>
                <h-button @click="emits('refresh')">
                    <el-icon>
                        <Refresh/>
                    </el-icon>
                </h-button>
                <template #tip>
                    刷新
                </template>
            </h-tool-tip>
            <el-popover width="400px">
                <template #reference>
                    <h-badge :value="UploadTasks.length"
                             style="display: inline-block;font-size: 16px;margin-right: 1em">
                        <h-button>
                            <el-icon>
                                <Sort/>
                            </el-icon>
                        </h-button>
                    </h-badge>
                </template>
                <template #default>
                    <el-empty v-if="!UploadTasks.length">
                        <template #description>
                            空空如也
                        </template>
                    </el-empty>
                    <div>
                        <div v-for="(t,i) in UploadTasks" class="task" data-relative>
                            <div :style="{
                                '--p':t.progress(),
                                background:calcBg(t.status())
                            }"
                                 class="progress">
                            </div>
                            <div>
                                <h4>{{ t.file().name }}</h4>
                            </div>
                            {{ t.statusText() }} {{ (t.progress() * 100).toFixed(1) }}%
                            <el-icon v-if="t.status()>UploadStatus.Uploading" class="remove-btn" @click="removeTask(i)">
                                <Close/>
                            </el-icon>
                        </div>
                    </div>
                </template>
            </el-popover>
            <el-dropdown v-if="user">
                <template #default>
                    <el-button>
                        {{ user?.nick }}
                    </el-button>
                </template>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item @click="user=null">退出</el-dropdown-item>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
            <el-button v-else @click="showLogDialog=true">
                登录/注册
            </el-button>
        </div>
    </div>
    <el-dialog v-model="showNewFolderDialog"
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
    <el-dialog v-model="showLogDialog"
               :close-on-click-modal="!isLogging"
               :close-on-press-escape="!isLogging"
               :show-close="false">
        <template #default>
            <el-tabs>
                <el-tab-pane label="登录">
                    <el-form
                            ref="loginFormEl"
                            :model="logInfo"
                            :rules="loginRules"
                            label-width="100"
                            status-icon>
                        <el-form-item label="用户名" prop="logId">
                            <el-input
                                    v-model="logInfo.logId"
                                    placeholder="用户名/id"/>
                        </el-form-item>
                        <el-form-item label="密码" prop="password">
                            <el-input
                                    v-model="logInfo.password"
                                    maxlength="18"
                                    placeholder="密码"
                                    show-password type="password"/>
                        </el-form-item>
                        <h-button
                                v-disabled="!canLogIn||isLogging"
                                v-loading.inner="isLogging"
                                data-fill-width
                                type="primary"
                                @click.prevent="tryLogin">
                            登录
                        </h-button>
                    </el-form>
                </el-tab-pane>
                <el-tab-pane label="注册">
                    <el-form
                            ref="logonFormEl"
                            :model="logInfo"
                            :rules="logonRules"
                            label-width="100">
                        <el-form-item label="用户名" prop="logId">
                            <el-input v-model="logInfo.logId" placeholder="用户名"/>
                        </el-form-item>
                        <el-form-item label="密码" prop="password">
                            <el-input
                                    v-model="logInfo.password"
                                    maxlength="18"
                                    minlength="8"
                                    placeholder="密码"
                                    show-password
                                    type="password"/>
                        </el-form-item>
                        <el-form-item label="确认" prop="password2">
                            <el-input
                                    v-model="logInfo.password2"
                                    maxlength="18"
                                    placeholder="密码"
                                    type="password"/>
                        </el-form-item>
                        <h-button
                                v-disabled="!canLogOn||isLogging"
                                v-loading.inner="isLogging"
                                data-fill-width type="primary" @click="tryLogon">
                            注册
                        </h-button>
                    </el-form>
                </el-tab-pane>
            </el-tabs>
        </template>
    </el-dialog>
</template>

<style lang="scss" scoped>
@use '@/vars' as *;

.top-bar {
  align-items: center;
  box-shadow: rgb(128, 128, 128, 0.4) 0 1px 2px;
  height: $top-bar-height;
  left: 0;
  position: fixed;
  top: 0;
  z-index: 10;
}

.uploader {
  border-radius: 6px;
  cursor: pointer;
  height: 200px;
  overflow: hidden;
  position: relative;
  transition: var(--el-transition-duration-fast);
  width: 100%;

}

:deep(.el-upload--text) {
  height: 100%;
  width: 100%;

  & > .el-upload-dragger {
    height: 100%;
    width: 100%;
  }
}


.upload-icon {
  color: #8c939d;
  font-size: 28px;
  height: 100%;
  text-align: center;
  width: 100%;
}

.task {
  border: gray solid 1px;
  position: relative;
}

.progress {
  --p: 0;
  height: 100%;
  left: 0;
  position: absolute;
  scale: var(--p, 0) 1;
  top: 0;
  transform-origin: left center;
  transition: all 0.1s ease-out;
  width: 100%;
  z-index: -1;
}

.remove-btn {
  cursor: pointer;
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
}

</style>

<script lang="ts" setup>
import API from '@/utils/api'
import {user} from '@/utils/globals'
import {UploadStatus, UploadTasks} from '@/utils/upload-tasks'
import {Close, Refresh, Sort} from '@element-plus/icons-vue'
import {convertColor, HBadge, HButton, HMessage, HToolTip} from '@yin-jinlong/h-ui'
import {FormInstance, FormRules} from 'element-plus'
import {computed} from 'vue'
import {TopBarProps} from './props'

const showLogDialog = ref(false)
const showNewFolderDialog = ref(false)
const newFolderPosting = ref(false)
const loginFormEl = ref<FormInstance>()
const logonFormEl = ref<FormInstance>()
const props = defineProps<TopBarProps>()
const emits = defineEmits({
    newFolder: (name: string, ok: (close: boolean) => void) => void {},
    refresh: () => void {},
})
const isLogging = ref(false)

interface LogInfo {
    logId: string,
    password: string,
    password2: string
}

const rules = reactive<FormRules<LogInfo>>({
    logId: [
        {
            required: true,
            message: '请输入id',
            trigger: ['blur', 'change']
        }
    ],
    password: [
        {
            required: true,
            message: '请输入密码',
            trigger: ['blur', 'change']
        }, {
            min: 8,
            max: 18,
            message: '密码长度必须大于8小于18'
        }
    ]
} as FormRules<LogInfo>)

const loginRules = reactive<FormRules<LogInfo>>({
    ...rules
})

const logonRules = reactive<FormRules<LogInfo>>({
    ...rules,
    password2: [
        {
            required: true,
            message: '请输入密码',
            trigger: ['blur', 'change']
        },
        {
            message: '密码不一致',
            validator: (r, v) => v == logInfo.password,
            trigger: ['blur', 'change']
        }
    ]
} as FormRules<LogInfo>)

const newFolderData = reactive({
    name: '',
})

const logInfo = reactive<LogInfo>({
    logId: '',
    password: '',
    password2: '',
})
const canLogIn = computed(() => {
    return logInfo.logId.length && logInfo.password.length >= 8
})
const canLogOn = computed(() => {
    return logInfo.logId.length &&
        logInfo.password.length >= 8 &&
        logInfo.password == logInfo.password2
})

function calcBg(status: UploadStatus) {
    return status == UploadStatus.Error ?
        convertColor('danger', '2') :
        convertColor('success', '2')
}

function createFolder() {
    if (newFolderPosting.value)
        return
    newFolderPosting.value = true
    emits('newFolder', newFolderData.name, (close) => {
        if (close)
            showNewFolderDialog.value = false
        newFolderPosting.value = false
    })
}

function tryLogin() {
    if (isLogging.value)
        return
    loginFormEl.value?.validate((valid, fields) => {
        if (valid) {
            isLogging.value = true
            API.login(logInfo.logId, logInfo.password).then(res => {
                console.log(res)
                if (res) {
                    HMessage.success('登录成功')
                    showLogDialog.value = false
                    user.value = res
                }
            }).finally(() => {
                isLogging.value = false
            })
        }
    })
}

function tryLogon() {
    logonFormEl.value?.validate((valid, fields) => {
        if (valid) {
            isLogging.value = true
            API.logon(logInfo.logId, logInfo.password).then(res => {
                if (res) {
                    HMessage.success('注册成功')
                    showLogDialog.value = false
                }
            }).finally(() => {
                isLogging.value = false
            })
        }
    })
}

function removeTask(i: number) {
    UploadTasks.splice(i, 1)
}

watch(showLogDialog, nv => {
    if (nv) {
        isLogging.value = false
    }
})

</script>
