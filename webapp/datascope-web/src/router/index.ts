import { createRouter, createWebHistory } from 'vue-router';
import QueryHistory from '../views/QueryHistory.vue';
import QueryStats from '../views/QueryStats.vue';
import SlowQueries from '../views/SlowQueries.vue';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/query-history'
    },
    {
      path: '/query-history',
      name: 'QueryHistory',
      component: QueryHistory
    },
    {
      path: '/query-stats',
      name: 'QueryStats',
      component: QueryStats
    },
    {
      path: '/slow-queries',
      name: 'SlowQueries',
      component: SlowQueries
    }
  ]
});

export default router;