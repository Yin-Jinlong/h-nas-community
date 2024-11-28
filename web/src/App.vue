<template>
    <top-bar/>
    <router-view v-slot="{ Component }">
        <component :is="Component" class="padding"/>
    </router-view>
</template>

<style lang="scss" scoped>
@use '@/vars' as *;

.padding {
  padding-top: $top-bar-height;
}

</style>

<script lang="ts" setup>

import API from '@/utils/api'
import {user} from '@/utils/globals'

onMounted(() => {
    let id = user.value?.uid
    if (id)
        API.tryLogin(id.toString()).then(res => {
            if (res) {
                user.value = res
            }
        })
})
</script>
