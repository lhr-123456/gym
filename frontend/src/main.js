import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import './styles/index.scss'

Vue.config.productionTip = false

Vue.use(ElementUI)

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')

// 屏蔽 Element UI $confirm 点击"取消"时抛出的未捕获 Promise rejection
window.addEventListener('unhandledrejection', event => {
  if (event.reason && event.reason === 'cancel') {
    event.preventDefault()
  }
})
