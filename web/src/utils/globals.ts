import {ref} from 'vue'

function getUser() {
    let text = localStorage.getItem('user')
    return text ? JSON.parse(text) : null
}

function getToken() {
    return localStorage.getItem('token')
}

export const token = ref<string | null>(getToken())

export const user = ref<UserInfo | null>(
    getUser()
)

watch(token, (nv) => {
    if (nv) {
        localStorage.setItem('token', nv)
    } else {
        localStorage.removeItem('token')
    }
})

watch(user, (nv) => {
    if (nv) {
        localStorage.setItem('user', JSON.stringify(nv))
    } else {
        localStorage.removeItem('user')
        token.value = null
    }
})