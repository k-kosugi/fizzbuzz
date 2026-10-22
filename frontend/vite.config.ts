import react from '@vitejs/plugin-react'
import { defineConfig, loadEnv } from 'vite'

// 開発サーバーでは /api を backend に中継し、CORS の設定を不要にする
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')
  return {
    plugins: [react()],
    server: {
      proxy: {
        '/api': env.BACKEND_URL ?? 'http://localhost:8080',
      },
    },
  }
})
