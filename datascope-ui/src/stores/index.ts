import { createPinia } from 'pinia';

// 导出 store 实例
export const store = createPinia();

// 导出所有 store
export * from './app';
export * from './user';
export * from './datasource';
export * from './query';

// 导出默认实例
export default store;