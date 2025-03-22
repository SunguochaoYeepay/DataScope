/**
 * 用户信息
 */
export interface UserInfo {
  id: string;
  username: string;
  email: string;
  avatar?: string;
  roles: string[];
  permissions: string[];
}

/**
 * 用户设置
 */
export interface UserSettings {
  theme: 'light' | 'dark';
  language: string;
  tableSize: 'small' | 'medium' | 'large';
  showWelcome: boolean;
}

/**
 * 用户状态
 */
export interface UserState {
  userInfo: UserInfo | null;
  settings: UserSettings;
  token: string | null;
}