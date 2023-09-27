import { createRouter, createWebHistory } from 'vue-router'

const routes = [

  {
    path: '/login',
    component: ()=>import('../views/LoginView.vue')
  }
  ,
  {
    // path什么都不加就是默认访问的地址
    path: '/',
    component: ()=>import('../views/main.vue')
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
