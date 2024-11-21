import {Component} from 'vue'

type ModuleComponent = {
    default: Component
}

declare interface Mapping {
    [key: string]: (() => Promise<ModuleComponent>) | undefined
}

export const IconMapping: Record<string, Mapping | undefined> = {
    'application': {
        'x-font-ttf': async () => import('./font.vue')
    }
}
