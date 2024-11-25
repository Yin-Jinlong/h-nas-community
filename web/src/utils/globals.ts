import {ref} from 'vue'

const KEY_USER = 'user'
const KEY_TOKEN = 'token'
const KEY_AUTH_TOKEN = 'auth-token'

function getUser() {
    let text = localStorage.getItem(KEY_USER)
    return text ? JSON.parse(text) : null
}

function getToken() {
    return localStorage.getItem(KEY_TOKEN)
}

function getAuthToken() {
    return localStorage.getItem(KEY_AUTH_TOKEN)
}

export const token = ref<string | null>(getToken())
export const authToken = ref<string | null>(getAuthToken())

export const user = ref<UserInfo | null>(
    getUser()
)

watch(token, (nv) => {
    if (nv) {
        localStorage.setItem(KEY_TOKEN, nv)
    } else {
        localStorage.removeItem(KEY_TOKEN)
    }
})

watch(authToken, (nv) => {
    if (nv) {
        localStorage.setItem(KEY_AUTH_TOKEN, nv)
    } else {
        localStorage.removeItem(KEY_AUTH_TOKEN)
    }
})

watch(user, (nv) => {
    if (nv) {
        localStorage.setItem(KEY_USER, JSON.stringify(nv))
    } else {
        localStorage.removeItem(KEY_USER)
        token.value = null
    }
})