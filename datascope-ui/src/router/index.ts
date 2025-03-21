import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: {
      title: '首页',
      icon: 'mdi-home'
    }
  },
  {
    path: '/datasource',
    name: 'Datasource',
    component: () => import('@/views/datasource/DatasourceList.vue'),
    meta: {
      title: '数据源管理',
      icon: 'mdi-database'
    }
  },
  {
    path: '/dataset',
    name: 'Dataset',
    component: () => import('@/views/dataset/DatasetList.vue'),
    meta: {
      title: '数据集管理',
      icon: 'mdi-table'
    }
  },
  {
    path: '/visualization',
    name: 'Visualization',
    component: () => import('@/views/visualization/VisualizationList.vue'),
    meta: {
      title: '可视化管理',
      icon: 'mdi-chart-box'
    }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/dashboard/DashboardList.vue'),
    meta: {
      title: '仪表板',
      icon: 'mdi-monitor-dashboard'
    }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('@/views/settings/Settings.vue'),
    meta: {
      title: '系统设置',
      icon: 'mdi-cog'
    }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 路由守卫：设置页面标题
router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title} - DataScope` || 'DataScope'
  next()
})

export default router