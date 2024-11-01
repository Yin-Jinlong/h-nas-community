<template>
    <div class="top-bar bg-dot" data-fill-width data-flex>
        <div style="flex: 1">
            <h-button v-disabled="!user" type="primary" @click="showNewFolderDialog = true">
                创建目录
            </h-button>
        </div>
        <div>
            <el-button @click="">
                <el-icon>
                    <Upload/>
                </el-icon>
            </el-button>
        </div>
        <div>
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
            <el-button v-else @click="showLoDialog=true">
                登录/注册
            </el-button>
        </div>
    </div>
    <el-dialog v-model="showNewFolderDialog">
        <template #header>
            <h3>创建目录</h3>
        </template>
        <el-form :model="newFolderData">
            <el-form-item label="目录名">
                <el-input v-model="newFolderData.name" placeholder="目录名"/>
            </el-form-item>
        </el-form>
        <h-button :disabled="!newFolderData.name.length" type="primary" @click="createFolder">
            创建
        </h-button>
    </el-dialog>
    <el-dialog v-model="showLoDialog" :show-close="false">
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
                                v-disabled="!canLogIn"
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
                                v-disabled="!canLogOn"
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

</style>

<script lang="ts" setup>
import API from '@/utils/api'
import {user} from '@/utils/globals'
import {Upload} from '@element-plus/icons-vue'
import {HMessage, HButton} from '@yin-jinlong/h-ui'
import {FormInstance, FormRules} from 'element-plus'
import {computed} from 'vue'
import {TopBarProps} from './props'

const showLoDialog = ref(false)
const showNewFolderDialog = ref(false)
const loginFormEl = ref<FormInstance>()
const logonFormEl = ref<FormInstance>()
const props = defineProps<TopBarProps>()
const emits = defineEmits({
    newFolder: (name: string, ok: () => void) => void {}
})


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

function createFolder() {
    emits('newFolder', newFolderData.name, () => {
        showNewFolderDialog.value = false
    })
}

function tryLogin() {
    loginFormEl.value?.validate((valid, fields) => {
        if (valid) {
            API.login(logInfo.logId, logInfo.password).then(res => {
                console.log(res)
                if (res) {
                    HMessage.success('登录成功')
                    showLoDialog.value = false
                    user.value = res
                }
            })
        }
    })
}

function tryLogon() {
    logonFormEl.value?.validate((valid, fields) => {
        if (valid) {
            API.logon(logInfo.logId, logInfo.password).then(res => {
                if (res) {
                    HMessage.success('注册成功')
                    showLoDialog.value = false
                }
            })
        }
    })
}

</script>
