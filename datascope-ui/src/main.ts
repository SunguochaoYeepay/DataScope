import { createApp } from 'vue';
import { store, useAppStore } from '@/stores';
import App from './App.vue';
import router from './router';
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/reset.css';
import '@/styles/global.scss';

// 创建应用实例
const app = createApp(App);

// 注册插件
app.use(store);
app.use(router);
app.use(Antd);

// 挂载应用
app.mount('#app');

// 初始化应用状态
const appStore = useAppStore();
appStore.initApp();