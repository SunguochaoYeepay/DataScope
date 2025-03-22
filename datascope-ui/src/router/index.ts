import { createRouter, createWebHistory } from 'vue-router';
import { routes } from './routes';
import { useUserStore } from '@/stores';
import { message } from 'ant-design-vue';

// 创建路由实例
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

// 路由守卫
router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - DataScope` : 'DataScope';

  const userStore = useUserStore();
  const isLoggedIn = userStore.isLoggedIn;
  const token = userStore.token;

  // 登录页面直接放行
  if (to.name === 'login') {
    if (isLoggedIn) {
      next({ name: 'home' });
    } else {
      next();
    }
    return;
  }

  // 验证登录状态
  if (!isLoggedIn) {
    message.warning('请先登录');
    next({ name: 'login', query: { redirect: to.fullPath } });
    return;
  }

  // 验证token
  if (!token) {
    userStore.logout();
    message.warning('登录已过期，请重新登录');
    next({ name: 'login', query: { redirect: to.fullPath } });
    return;
  }

  // 获取用户信息
  if (!userStore.userInfo) {
    try {
      await userStore.fetchUserInfo();
    } catch (error) {
      message.error('获取用户信息失败');
      next({ name: 'login', query: { redirect: to.fullPath } });
      return;
    }
  }

  next();
});

export default router;