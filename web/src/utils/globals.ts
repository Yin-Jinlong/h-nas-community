import {UserInfo} from '@/types/user'
import {ref} from 'vue'

function getUser() {
    let text = localStorage.getItem('user')
    return text ? JSON.parse(text) : null
}

export const user = ref<UserInfo | null>(
    getUser()
)

watch(user, (nv) => {
    if (nv) {
        localStorage.setItem('user', JSON.stringify(nv))
    } else {
        localStorage.removeItem('user')
    }
})