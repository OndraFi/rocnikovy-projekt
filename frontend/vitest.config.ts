import { defineConfig } from 'vitest/config'
import { defineVitestProject } from '@nuxt/test-utils/config'
import { fileURLToPath } from 'node:url'
import { dirname, resolve } from 'node:path'

const rootDir = dirname(fileURLToPath(import.meta.url))
const appDir = resolve(rootDir, 'app')

export default defineConfig({
    resolve: {
        alias: {
            '~': appDir,
            '@': appDir,
            '~~': rootDir,
            '@@': rootDir,
        },
    },
    test: {
        projects: [
            {
                test: {
                    name: 'unit',
                    include: ['test/{e2e,unit}/*.{test,spec}.ts'],
                    environment: 'node',
                },
            },
            await defineVitestProject({
                test: {
                    name: 'nuxt',
                    include: ['test/nuxt/*.{test,spec}.ts'],
                    environment: 'nuxt',
                },
            }),
        ],
    },
})
