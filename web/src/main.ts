import {vDisabled} from '@yin-jinlong/h-ui'
import {createApp, ObjectDirective} from 'vue'
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
app.directive('disabled', vDisabled as ObjectDirective)
app.mount(document.body)
