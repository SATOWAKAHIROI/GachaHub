import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0', // Docker環境で外部からアクセス可能にする
    port: 3000,
    watch: {
      usePolling: true, // WSL/Docker環境でファイル変更を検知
    },
    hmr: {
      host: 'localhost', // HMRのホスト設定
    },
  },
})
