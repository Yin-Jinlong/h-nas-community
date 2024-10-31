import {createApp} from 'vue'
import {createRouter, createWebHashHistory} from 'vue-router'

import App from './App.vue'
import './style.scss'

let router = createRouter({
    history: createWebHashHistory(),
    routes: [
        {
            path: '/',
            component: () => import('@/pages/home')
        }
    ]
})

const app = createApp(App)
app.use(router)
app.mount(document.body)
