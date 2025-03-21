// Vuetify
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import { md3 } from 'vuetify/blueprints'
import { aliases, mdi } from 'vuetify/iconsets/mdi'
import '@mdi/font/css/materialdesignicons.css'

// 自定义主题颜色
const lightTheme = {
  dark: false,
  colors: {
    primary: '#006494',      // 主色调：深蓝色
    'primary-darken-1': '#004B6B',
    secondary: '#03A9F4',    // 次要色：浅蓝色
    'secondary-darken-1': '#0288D1',
    accent: '#82B1FF',       // 强调色
    error: '#FF5252',        // 错误色
    info: '#2196F3',         // 信息色
    success: '#4CAF50',      // 成功色
    warning: '#FFC107',      // 警告色
    background: '#F5F5F5',   // 背景色
    surface: '#FFFFFF',      // 表面色
  },
}

const darkTheme = {
  dark: true,
  colors: {
    primary: '#0288D1',
    'primary-darken-1': '#01579B',
    secondary: '#29B6F6',
    'secondary-darken-1': '#0288D1',
    accent: '#82B1FF',
    error: '#FF5252',
    info: '#2196F3',
    success: '#4CAF50',
    warning: '#FFC107',
    background: '#121212',
    surface: '#212121',
  },
}

export default createVuetify({
  blueprint: md3,            // 使用 Material Design 3
  theme: {
    defaultTheme: 'light',
    themes: {
      light: lightTheme,
      dark: darkTheme,
    },
  },
  defaults: {
    VBtn: {
      variant: 'elevated',   // 默认使用浮起按钮样式
      rounded: true,         // 圆角按钮
    },
    VCard: {
      rounded: 'lg',         // 大圆角卡片
      elevation: 2,          // 默认阴影
    },
    VTextField: {
      variant: 'outlined',   // 描边输入框
      density: 'comfortable', // 舒适的密度
    },
    VTable: {
      hover: true,           // 鼠标悬停效果
    },
  },
  icons: {
    defaultSet: 'mdi',
    aliases,
    sets: {
      mdi,
    },
  },
})