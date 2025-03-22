import request from '@/utils/request'
import type { UserInfo } from '@/stores/user'

interface LoginResponse {
  token: string
  userInfo: UserInfo
}

export async function login(username: string, password: string): Promise<LoginResponse> {
  // 临时硬编码验证
  if (username === 'admin' && password === 'admin123') {
    return {
      token: 'mock_token_12345',
      userInfo: {
        id: 1,
        username: 'admin',
        email: 'admin@example.com',
        roles: ['admin'],
        permissions: ['*'],
      },
    }
  }
  throw new Error('用户名或密码错误')
}

export async function logout(): Promise<void> {
  // 模拟退出登录
  return Promise.resolve()
} 