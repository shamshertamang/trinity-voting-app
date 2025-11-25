//oldvite.config.js

import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// change target to wherever your Spring app runs locally
const BACKEND = process.env.BACKEND || 'http://localhost:8080';

export default defineConfig({
    plugins: [react()],
    server: {
        proxy: {
            '/api': {
                target: BACKEND,
                changeOrigin: true
            },
            '/config': {
                target: BACKEND,
                changeOrigin: true
            },
            '/fib': {
                target: BACKEND,
                changeOrigin: true },
        },
    },
});