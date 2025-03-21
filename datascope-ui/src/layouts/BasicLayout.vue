<template>
  <a-layout class="layout">
    <a-layout-sider
      v-model:collapsed="collapsed"
      :trigger="null"
      collapsible
      class="sider"
    >
      <div class="logo">
        <img src="@/assets/logo.svg" alt="logo" />
        <h1 v-show="!collapsed">DataScope</h1>
      </div>
      <a-menu
        v-model:selectedKeys="selectedKeys"
        v-model:openKeys="openKeys"
        mode="inline"
        theme="dark"
      >
        <a-menu-item key="datasource" @click="navigateTo('/datasource')">
          <template #icon>
            <database-outlined />
          </template>
          <span>数据源管理</span>
        </a-menu-item>

        <a-menu-item key="sql-editor" @click="navigateTo('/sql-editor')">
          <template #icon>
            <code-outlined />
          </template>
          <span>SQL编辑器</span>
        </a-menu-item>

        <a-sub-menu key="query">
          <template #icon>
            <history-outlined />
          </template>
          <template #title>查询管理</template>
          <a-menu-item key="query-history" @click="navigateTo('/query-history')">
            查询历史
          </a-menu-item>
          <a-menu-item key="slow-queries" @click="navigateTo('/slow-queries')">
            慢查询分析
          </a-menu-item>
        </a-sub-menu>
      </a-menu>
    </a-layout-sider>

    <a-layout>
      <a-layout-header class="header">
        <menu-fold-outlined
          v-if="!collapsed"
          class="trigger"
          @click="() => (collapsed = true)"
        />
        <menu-unfold-outlined
          v-else
          class="trigger"
          @click="() => (collapsed = false)"
        />
        <div class="header-right">
          <a-space>
            <a-tooltip title="文档">
              <question-circle-outlined class="action-icon" />
            </a-tooltip>
            <a-dropdown>
              <a-avatar>
                <template #icon><user-outlined /></template>
              </a-avatar>
              <template #overlay>
                <a-menu>
                  <a-menu-item key="profile">
                    <user-outlined />
                    个人中心
                  </a-menu-item>
                  <a-menu-item key="settings">
                    <setting-outlined />
                    设置
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item key="logout">
                    <logout-outlined />
                    退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </a-space>
        </div>
      </a-layout-header>

      <a-layout-content class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </a-layout-content>

      <a-layout-footer class="footer">
        DataScope ©2024 Created by Your Company
      </a-layout-footer>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  DatabaseOutlined,
  CodeOutlined,
  HistoryOutlined,
  QuestionCircleOutlined,
  UserOutlined,
  SettingOutlined,
  LogoutOutlined,
} from '@ant-design/icons-vue';

const router = useRouter();
const route = useRoute();

const collapsed = ref(false);
const selectedKeys = ref<string[]>([]);
const openKeys = ref<string[]>(['query']);

// 根据当前路由更新选中的菜单项
watch(
  () => route.path,
  (path) => {
    const key = path.split('/')[1];
    selectedKeys.value = [key];
    if (key === 'query-history' || key === 'slow-queries') {
      openKeys.value = ['query'];
    }
  },
  { immediate: true }
);

const navigateTo = (path: string) => {
  router.push(path);
};
</script>

<style lang="scss" scoped>
.layout {
  min-height: 100vh;
}

.sider {
  position: fixed;
  left: 0;
  height: 100vh;
  z-index: 10;
}

.logo {
  height: 64px;
  padding: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.05);

  img {
    width: 32px;
    height: 32px;
    margin-right: 8px;
  }

  h1 {
    color: white;
    font-size: 18px;
    margin: 0;
    white-space: nowrap;
  }
}

.header {
  background: #fff;
  padding: 0;
  position: fixed;
  width: calc(100% - #{v-bind('collapsed ? "80px" : "200px"')});
  right: 0;
  z-index: 9;
  transition: width 0.2s;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.trigger {
  padding: 0 24px;
  font-size: 18px;
  cursor: pointer;
  transition: color 0.3s;

  &:hover {
    color: #1890ff;
  }
}

.header-right {
  padding-right: 24px;
}

.action-icon {
  font-size: 18px;
  cursor: pointer;
  padding: 0 12px;

  &:hover {
    color: #1890ff;
  }
}

.content {
  margin: 64px 0 0 #{v-bind('collapsed ? "80px" : "200px"')};
  transition: margin-left 0.2s;
  min-height: calc(100vh - 64px - 70px);
  padding: 24px;
}

.footer {
  margin-left: #{v-bind('collapsed ? "80px" : "200px"')};
  transition: margin-left 0.2s;
  text-align: center;
  background: #fff;
  padding: 24px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>