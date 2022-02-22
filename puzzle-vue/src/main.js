import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
//import store from './store'

import ElementPlus from 'element-plus';
import 'element-plus/theme-chalk/index.css';
const app = createApp(App)

import axios from 'axios'

app.config.globalProperties.$axios=axios

app.use(router).use(ElementPlus).mount('#app')
//app.use(store).use(router).use(ElementPlus).mount('#app')

