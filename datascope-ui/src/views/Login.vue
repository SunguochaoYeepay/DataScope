<template>
  <div class="login-container">
    <div class="login-content">
      <div class="login-header">
        <h1 class="title">DataScope</h1>
      </div>
      <a-form
        :model="formState"
        @finish="handleSubmit"
        class="login-form"
      >
        <a-form-item
          name="username"
          :rules="[{ required: true, message: '请输入用户名' }]"
        >
          <a-input
            v-model:value="formState.username"
            placeholder="用户名"
            size="large"
          >
            <template #prefix>
              <UserOutlined />
            </template>
          </a-input>
        </a-form-item>
        <a-form-item
          name="password"
          :rules="[{ required: true, message: '请输入密码' }]"
        >
          <a-input-password
            v-model:value="formState.password"
            placeholder="密码"
            size="large"
          >
            <template #prefix>
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>
        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            size="large"
            :loading="loading"
            block
          >
            登录
          </a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useUserStore } from '@/stores';
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();
const loading = ref(false);

// 表单数据
const formState = reactive({
  username: 'admin',
  password: 'admin123',
});

// 提交表单
const handleSubmit = async () => {
  loading.value = true;
  try {
    await userStore.login(formState.username, formState.password);
    message.success('登录成功');
    const redirect = route.query.redirect as string;
    router.push(redirect || '/');
  } catch (error: any) {
    message.error(error.message || '登录失败');
  } finally {
    loading.value = false;
  }
};
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f0f2f5;
}

.login-content {
  width: 100%;
  max-width: 400px;
  padding: 32px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;

  .title {
    font-size: 28px;
    font-weight: bold;
    color: #1890ff;
    margin: 0;
  }
}

.login-form {
  :deep(.ant-form-item:last-child) {
    margin-bottom: 0;
  }
}
</style>