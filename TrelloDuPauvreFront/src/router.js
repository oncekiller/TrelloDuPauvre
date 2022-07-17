import { createRouter, createWebHistory } from 'vue-router'
import AllProjects from './views/AllProjects.vue'

const router =  createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes: [
      {
        path: "/",
        name: "AllProjects",
        component: AllProjects
      },
      {
        path: "/projectPage",
        name: "projectPage",
        component: () => import( "./views/ProjectPage.vue"),
      }
    ]
  })
  export default router