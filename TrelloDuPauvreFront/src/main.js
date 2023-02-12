import { createApp } from 'vue'
import App from './App.vue'
import vuetify from './plugins/vuetify'
import store from './store/index'
import router from './router'
import { loadFonts } from './plugins/webfontloader'
import PrimeVue from 'primevue/config';
import '@/assets/style/main.css'
import globalLogging from './services/logging/globalLogging'
loadFonts()
globalLogging.initIndexDBLogging()

createApp(App)
  .use(vuetify)
  .use(router)
  .use(store)
  .use(PrimeVue)
  .mount('#app')
