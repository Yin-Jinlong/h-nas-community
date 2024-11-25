<template>
    <router-view/>
</template>

<script lang="ts" setup>

import API from '@/utils/api'
import {authToken, token, user} from '@/utils/globals'
import {HMessage} from '@yin-jinlong/h-ui'

onMounted(() => {
    let id = user.value?.uid
    if (id)
        API.tryLogin(id.toString()).then(res => {
            if (res) {
                user.value = res
            }
        }).catch(() => {
            user.value = null
        })
})
</script>
