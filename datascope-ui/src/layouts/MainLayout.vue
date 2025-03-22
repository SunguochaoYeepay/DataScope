<template>
  <a-layout class="site-layout">
    <!-- 侧边栏 -->
    <a-layout-sider
      v-model:collapsed="sidebarCollapsed"
      :trigger="null"
      collapsible
      class="site-layout-sider"
      theme="dark"
      width="256"
    >
      <div class="logo">
        <img src="@/assets/logo.svg" alt="logo" />
        <h1 v-show="!sidebarCollapsed">DataScope</h1>
      </div>
      <a-menu
        v-model:selectedKeys="selectedKeys"
        v-model:openKeys="openKeys"
        mode="inline"
        theme="dark"
      >
        <template v-for="item in menuItems" :key="item.key">
          <template v-if="!item.children">
            <a-menu-item :key="item.key">
              <template #icon>
                <component :is="item.icon" />
              </template>
              <router-link :to="item.path">{{ item.title }}</router-link>
            </a-menu-item>
          </template>
          <template v-else>
            <a-sub-menu :key="item.key">
              <template #icon>
                <component :is="item.icon" />
              </template>
              <template #title>{{ item.title }}</template>
              <a-menu-item v-for="child in item.children" :key="child.key">
                <router-link :to="child.path">{{ child.title }}</router-link>
              </a-menu-item>
            </a-sub-menu>
          </template>
        </template>
      </a-menu>
    </a-layout-sider>

    <a-layout>
      <!-- 头部 -->
      <a-layout-header class="site-layout-header">
        <div class="header-left">
          <menu-unfold-outlined
            v-if="sidebarCollapsed"
            class="trigger"
            @click="() => (sidebarCollapsed = !sidebarCollapsed)"
          />
          <menu-fold-outlined
            v-else
            class="trigger"
            @click="() => (sidebarCollapsed = !sidebarCollapsed)"
          />
          <a-breadcrumb class="breadcrumb">
            <a-breadcrumb-item v-for="item in breadcrumbItems" :key="item.path">
              {{ item.title }}
            </a-breadcrumb-item>
          </a-breadcrumb>
        </div>
        <div class="header-right">
          <a-dropdown>
            <a class="ant-dropdown-link" @click.prevent>
              {{ username }}
              <down-outlined />
            </a>
            <template #overlay>
              <a-menu>
                <a-menu-item key="profile">
                  <user-outlined />
                  个人信息
                </a-menu-item>
                <a-menu-item key="settings">
                  <setting-outlined />
                  系统设置
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item key="logout" @click="handleLogout">
                  <logout-outlined />
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </a-layout-header>

      <!-- 内容区域 -->
      <a-layout-content class="site-layout-content">
        <router-view v-slot="{ Component }">
          <component :is="Component" />
        </router-view>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, computed, type Component } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  UserOutlined,
  SettingOutlined,
  LogoutOutlined,
  DownOutlined,
  DashboardOutlined,
  DatabaseOutlined,
  SearchOutlined,
} from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const appStore = useAppStore()
const userStore = useUserStore()

const sidebarCollapsed = computed({
  get: () => appStore.sidebarCollapsed,
  set: (value) => appStore.setSidebarCollapsed(value),
})

const username = computed(() => userStore.username)

const selectedKeys = ref<string[]>([route.name as string])
const openKeys = ref<string[]>([])

interface MenuItem {
  key: string
  title: string
  path: string
  icon: Component
  children?: MenuItem[]
}

const menuItems: MenuItem[] = [
  {
    key: 'dashboard',
    title: '仪表盘',
    path: '/dashboard',
    icon: DashboardOutlined,
  },
  {
    key: 'datasource',
    title: '数据源管理',
    path: '/datasource',
    icon: DatabaseOutlined,
  },
  {
    key: 'query',
    title: '查询管理',
    path: '/query',
    icon: SearchOutlined,
  },
]

const breadcrumbItems = computed(() => {
  const matched = route.matched.filter((item) => item.meta && item.meta.title)
  return matched.map((item) => ({
    title: item.meta.title,
    path: item.path,
  }))
})

const handleLogout = async () => {
  try {
    await userStore.logout()
    router.push('/login')
  } catch (error) {
    console.error('Logout failed:', error)
  }
}
</script>

<style lang="scss" scoped>
.site-layout {
  min-height: 100vh;

  .site-layout-sider {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 10;
    box-shadow: 2px 0 8px 0 rgba(29, 35, 41, 0.05);

    .logo {
      height: 64px;
      padding: 16px;
      display: flex;
      align-items: center;
      background: #002140;

      img {
        width: 32px;
        height: 32px;
      }

      h1 {
        margin: 0 0 0 12px;
        color: white;
        font-weight: 600;
        font-size: 18px;
        line-height: 32px;
        white-space: nowrap;
      }
    }
  }

  .site-layout-header {
    position: sticky;
    top: 0;
    z-index: 9;
    padding: 0 24px;
    background: #fff;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
    display: flex;
    align-items: center;
    justify-content: space-between;

    .header-left {
      display: flex;
      align-items: center;

      .trigger {
        padding: 0 24px;
        font-size: 18px;
        cursor: pointer;
        transition: color 0.3s;

        &:hover {
          color: #1890ff;
        }
      }

      .breadcrumb {
        margin-left: 16px;
      }
    }

    .header-right {
      .ant-dropdown-link {
        color: rgba(0, 0, 0, 0.85);
        cursor: pointer;

        &:hover {
          color: #1890ff;
        }
      }
    }
  }

  .site-layout-content {
    margin: 24px;
    padding: 24px;
    background: #fff;
    min-height: 280px;
    border-radius: 2px;
  }
}

// 根据侧边栏状态调整布局
:deep(.ant-layout) {
  margin-left: v-bind('sidebarCollapsed ? "80px" : "256px"');
  transition: margin-left 0.2s;
}
</style>