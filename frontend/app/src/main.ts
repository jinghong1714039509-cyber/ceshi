import { createApp } from 'vue'
import '@fontsource-variable/outfit'
import '@fontsource/noto-sans-sc/chinese-simplified-400.css'
import '@fontsource/noto-sans-sc/chinese-simplified-500.css'
import '@fontsource/noto-sans-sc/chinese-simplified-700.css'
import App from './App.vue'
import { createPinia } from 'pinia'
import router from './router'
import { permissionDirective } from './directives/permission'
import './style.css'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.directive('permission', permissionDirective)
app.mount('#app')
