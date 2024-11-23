import {ElementPlusResolver} from 'unplugin-vue-components/resolvers'
import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
    resolve: {
        alias: {
            '@': path.resolve(__dirname, 'src'),
            '@/pages': path.resolve(__dirname, 'src/pages'),
            '@/components': path.resolve(__dirname, 'src/components'),
            '@/types': path.resolve(__dirname, 'src/types'),
        }
    },
    server: {
        port: 3344,
        proxy: {
            '/api': {
                target: 'http://localhost:8888',
                changeOrigin: true,
            }
        }
    },
    build: {
        minify: 'terser'
    },
    plugins: [
        vue(),
        AutoImport({
            resolvers: [ElementPlusResolver({
                importStyle: false,
                directives: false
            })],
            imports: ['vue', 'vue-router'],
        }),
        Components({
            resolvers: [ElementPlusResolver({
                importStyle: false,
                directives: false,
            })],
        }),
    ]
})
