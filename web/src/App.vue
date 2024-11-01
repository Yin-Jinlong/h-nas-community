<template>
    <router-view/>
</template>

<script lang="ts" setup>

import {login, tryLogin} from '@/utils/api'
import {user} from '@/utils/globals'
import {HMessage} from '@yin-jinlong/h-ui'

onMounted(() => {
    HMessage
    let id = user.value?.uid
    if (id)
        tryLogin(id.toString()).then(res => {
            if (res) {
                user.value = res
            }
        }).catch(() => {
            user.value = null
        })
})
</script>
