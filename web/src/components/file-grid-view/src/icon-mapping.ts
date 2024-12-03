import {Component} from 'vue'

type ModuleComponent = {
    default: Component
}

type MappingAll = () => Promise<ModuleComponent>

declare interface MappingMap {
    [key: string]: (() => Promise<ModuleComponent>) | undefined
}

type Mapping = MappingAll | MappingMap

export const IconMapping: Record<string, Mapping | undefined> = {
    'application': {
        'x-font-ttf': async () => import('./font.vue')
    },
    'audio': async () => import('./music-file.vue')
}
