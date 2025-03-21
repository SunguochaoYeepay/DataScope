<template>
  <v-container fluid>
    <v-row>
      <!-- 左侧导航菜单 -->
      <v-col cols="3">
        <v-card>
          <v-list>
            <v-list-item
              v-for="(item, index) in settingsMenu"
              :key="index"
              :value="item"
              :active="activeSection === item.value"
              @click="activeSection = item.value"
            >
              <template v-slot:prepend>
                <v-icon :icon="item.icon"></v-icon>
              </template>
              <v-list-item-title>{{ item.title }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-card>
      </v-col>

      <!-- 右侧设置内容 -->
      <v-col cols="9">
        <v-card>
          <!-- 个人信息设置 -->
          <v-card-text v-if="activeSection === 'profile'">
            <h2 class="text-h6 mb-4">个人信息</h2>
            <v-form v-model="profileValid" @submit.prevent="saveProfile">
              <v-row>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="profile.name"
                    label="用户名"
                    required
                    :rules="[v => !!v || '用户名不能为空']"
                  ></v-text-field>
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="profile.email"
                    label="邮箱"
                    type="email"
                    required
                    :rules="[
                      v => !!v || '邮箱不能为空',
                      v => /.+@.+\..+/.test(v) || '请输入有效的邮箱地址'
                    ]"
                  ></v-text-field>
                </v-col>
                <v-col cols="12">
                  <v-textarea
                    v-model="profile.bio"
                    label="个人简介"
                    rows="3"
                  ></v-textarea>
                </v-col>
              </v-row>
              <v-btn
                color="primary"
                type="submit"
                :loading="saving"
                :disabled="!profileValid"
              >
                保存更改
              </v-btn>
            </v-form>
          </v-card-text>

          <!-- 安全设置 -->
          <v-card-text v-else-if="activeSection === 'security'">
            <h2 class="text-h6 mb-4">安全设置</h2>
            <v-form v-model="passwordValid" @submit.prevent="changePassword">
              <v-row>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="password.current"
                    label="当前密码"
                    type="password"
                    required
                    :rules="[v => !!v || '请输入当前密码']"
                  ></v-text-field>
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="password.new"
                    label="新密码"
                    type="password"
                    required
                    :rules="[
                      v => !!v || '请输入新密码',
                      v => v.length >= 8 || '密码长度至少为8位',
                      v => /[A-Z]/.test(v) || '密码必须包含大写字母',
                      v => /[a-z]/.test(v) || '密码必须包含小写字母',
                      v => /[0-9]/.test(v) || '密码必须包含数字',
                      v => /[^A-Za-z0-9]/.test(v) || '密码必须包含特殊字符'
                    ]"
                  ></v-text-field>
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="password.confirm"
                    label="确认新密码"
                    type="password"
                    required
                    :rules="[
                      v => !!v || '请确认新密码',
                      v => v === password.new || '两次输入的密码不一致'
                    ]"
                  ></v-text-field>
                </v-col>
              </v-row>
              <v-btn
                color="primary"
                type="submit"
                :loading="saving"
                :disabled="!passwordValid"
              >
                修改密码
              </v-btn>
            </v-form>
          </v-card-text>

          <!-- 通知设置 -->
          <v-card-text v-else-if="activeSection === 'notifications'">
            <h2 class="text-h6 mb-4">通知设置</h2>
            <v-list>
              <v-list-item>
                <template v-slot:prepend>
                  <v-checkbox
                    v-model="notifications.email"
                    label="邮件通知"
                  ></v-checkbox>
                </template>
                <v-list-item-subtitle>
                  接收重要更新和提醒的邮件通知
                </v-list-item-subtitle>
              </v-list-item>
              <v-list-item>
                <template v-slot:prepend>
                  <v-checkbox
                    v-model="notifications.browser"
                    label="浏览器通知"
                  ></v-checkbox>
                </template>
                <v-list-item-subtitle>
                  接收实时的浏览器推送通知
                </v-list-item-subtitle>
              </v-list-item>
            </v-list>
            <v-btn
              color="primary"
              class="mt-4"
              :loading="saving"
              @click="saveNotifications"
            >
              保存设置
            </v-btn>
          </v-card-text>

          <!-- 主题设置 -->
          <v-card-text v-else-if="activeSection === 'appearance'">
            <h2 class="text-h6 mb-4">主题设置</h2>
            <v-radio-group v-model="appearance.theme" class="mb-4">
              <v-radio label="浅色主题" value="light"></v-radio>
              <v-radio label="深色主题" value="dark"></v-radio>
              <v-radio label="跟随系统" value="system"></v-radio>
            </v-radio-group>
            <v-select
              v-model="appearance.density"
              label="界面密度"
              :items="[
                { title: '紧凑', value: 'compact' },
                { title: '舒适', value: 'comfortable' },
                { title: '默认', value: 'default' }
              ]"
              class="mb-4"
            ></v-select>
            <v-btn
              color="primary"
              :loading="saving"
              @click="saveAppearance"
            >
              保存设置
            </v-btn>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useSnackbar } from '@/composables/useSnackbar'

const { showSuccess, showError } = useSnackbar()

// 设置菜单
const settingsMenu = [
  { title: '个人信息', value: 'profile', icon: 'mdi-account' },
  { title: '安全设置', value: 'security', icon: 'mdi-shield-lock' },
  { title: '通知设置', value: 'notifications', icon: 'mdi-bell' },
  { title: '主题设置', value: 'appearance', icon: 'mdi-palette' }
]

// 当前激活的设置项
const activeSection = ref('profile')

// 保存状态
const saving = ref(false)

// 个人信息表单
const profileValid = ref(false)
const profile = ref({
  name: '',
  email: '',
  bio: ''
})

// 密码修改表单
const passwordValid = ref(false)
const password = ref({
  current: '',
  new: '',
  confirm: ''
})

// 通知设置
const notifications = ref({
  email: true,
  browser: true
})

// 主题设置
const appearance = ref({
  theme: 'system',
  density: 'default'
})

// 保存个人信息
const saveProfile = async () => {
  if (!profileValid.value) return

  saving.value = true
  try {
    // TODO: 调用保存 API
    await new Promise(resolve => setTimeout(resolve, 1000))
    showSuccess('个人信息已更新')
  } catch (error) {
    showError('保存失败')
  } finally {
    saving.value = false
  }
}

// 修改密码
const changePassword = async () => {
  if (!passwordValid.value) return

  saving.value = true
  try {
    // TODO: 调用修改密码 API
    await new Promise(resolve => setTimeout(resolve, 1000))
    showSuccess('密码已修改')
    password.value = {
      current: '',
      new: '',
      confirm: ''
    }
  } catch (error) {
    showError('修改失败')
  } finally {
    saving.value = false
  }
}

// 保存通知设置
const saveNotifications = async () => {
  saving.value = true
  try {
    // TODO: 调用保存 API
    await new Promise(resolve => setTimeout(resolve, 1000))
    showSuccess('通知设置已更新')
  } catch (error) {
    showError('保存失败')
  } finally {
    saving.value = false
  }
}

// 保存主题设置
const saveAppearance = async () => {
  saving.value = true
  try {
    // TODO: 调用保存 API
    await new Promise(resolve => setTimeout(resolve, 1000))
    showSuccess('主题设置已更新')
  } catch (error) {
    showError('保存失败')
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.v-card {
  border-radius: 8px;
}

.v-list-item--active {
  background-color: rgb(var(--v-theme-primary), 0.1);
}
</style>