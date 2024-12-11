import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api/v1',
  timeout: 5000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 每次请求都从 localStorage 获取最新的 token
    const token = localStorage.getItem('token')
    if (token) {
      // 确保 headers 对象存在
      config.headers = config.headers || {}
      // 设置 Authorization header
      config.headers.Authorization = `Bearer ${token}`
    } else {
      // 如果没有 token，确保清除 Authorization header
      if (config.headers) {
        delete config.headers.Authorization
      }
    }
    return config
  },
  error => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.status) {
      return {
        data: res,
        success: res.status
      }
    } else {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  error => {
    if (error.response) {
      if (error.response.status === 401) {
        // 清除所有认证相关的本地存储
        localStorage.removeItem('token')
        localStorage.removeItem('tokenExpireTime')
        localStorage.removeItem('userRoles')
        
        router.push('/login')
        ElMessage.error('登录已过期，请重新登录')
      } else {
        ElMessage.error(error.response.data?.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误，请稍后重试')
    }
    return Promise.reject(error)
  }
)

// 认证相关的 API
export const auth = {
  login(data) {
    return request.post('/auth/login', data).then(res => {
      // 打印完整的响应数据，用于调试
      console.log('Login response:', res)
      
      if (res.success && res.data.data) {  // 注意这里改为 res.data.data
        // 保存 JWT
        localStorage.setItem('token', res.data.data.jwt)
        // 打印日志，用于调试
        console.log('Token saved:', res.data.data.jwt)
        
        // 保存过期时间
        localStorage.setItem('tokenExpireTime', res.data.data.expireTime)
        // 保存用户角色信息
        localStorage.setItem('userRoles', JSON.stringify(res.data.data.role))
      }
      return res
    })
  },

  async logout() {
    try {
      // 先调用后端登出接口
      await request.post('/auth/logout')
    } finally {
      // 无论后端请求是否成功，都清除本地存储
      localStorage.removeItem('token')
      localStorage.removeItem('tokenExpireTime')
      localStorage.removeItem('userRoles')
    }
  },

  // 添加注册方法
  register(data) {
    return request.post('/auth/register', data)
  }
}

// 用户信息相关的 API
export const user = {
  getProfile() {
    return request.get('/profile')
  },

  updateProfile(data) {
    return request.put('/profile', data)
  },

  updateEmail(email) {
    return request.put('/profile/email', null, { params: { email } })
  },

  updateAvatar(avatarUrl) {
    return request.put('/profile/avatar', null, { params: { avatarUrl } })
  },

  getCapacity() {
    return request.get('/profile/capacity')
  },

  // 修改密码方法，使用请求体而不是URL参数
  updatePassword(oldPassword, newPassword) {
    return request.put('/profile/password', {
      oldPassword,
      newPassword
    })
  }
}

// 图片相关的 API
export const image = {
  upload(file, isPublic = false, albumId = null) {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('isPublic', isPublic);
    if (albumId) {
      formData.append('albumId', albumId);
    }

    return request.post('/images/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
  },

  search(condition, page = 1, pageSize = 12, order = null) {
    return request.post('/images/search', condition, {
      params: {
        page,
        page_size: pageSize,
        order
      }
    })
  },

  // 添加更改公开状态的方法
  updatePublicStatus(key, isPublic) {
    return request.put(`/images/open/${key}`, null, {
      params: { isPublic }
    })
  },

  // 添加获取公开图片的方法
  getPublicImages(condition, page = 1, pageSize = 12, order = null) {
    return request.post('/images/public', condition, {
      params: {
        page,
        page_size: pageSize,
        order
      }
    })
  }
}

// 相册管理相关接口
export const album = {
  getList(page = 1, perPage = 20) {
    return request.get('/albums', {
      params: {
        page,
        per_page: perPage
      }
    })
  },

  create(data) {
    return request.post('/albums', data)
  },

  update(id, data) {
    return request.put(`/albums/${id}`, data)
  },

  delete(id) {
    return request.delete(`/albums/${id}`)
  },

  getDetail(id) {
    return request.get(`/albums/${id}`)
  },

  linkImage(albumId, key) {
    return request.post(`/albums/${albumId}/images`, null, {
      params: { key }
    })
  },

  getById(id) {
    return request.get(`/albums/${id}`)
  },

  search(condition, page = 1, pageSize = 20, order = null) {
    return request.post('/albums/search', condition, {
      params: {
        page,
        page_size: pageSize,
        order
      }
    })
  }
}

// 用户管理相关的 API
export const userManage = {
  getList(params) {
    return request.get('/users', { params })
  },

  create(data) {
    return request.post('/users', data)
  },

  updateStatus(userId, enabled) {
    return request.put(`/users/${userId}/status`, null, {
      params: { enabled }
    })
  },

  resetPassword(userId, newPassword) {
    return request.put(`/users/${userId}/password`, null, {
      params: { newPassword }
    })
  },

  delete(userId) {
    return request.delete(`/users/${userId}`)
  },

  setUserAsAdmin(userId) {
    return request.put(`/users/${userId}/role/admin`)
  },

  // 修改更新容量的方法
  updateCapacity(userId, capacity) {
    return request.put(`/users/${userId}/capacity`, null, {
      params: { capacity }
    })
  }
}

// 修改角色检查相关的工具函数
export const hasRole = (roleCode) => {
  const rolesStr = localStorage.getItem('userRoles')
  if (!rolesStr) return false
  
  try {
    const roles = JSON.parse(rolesStr)
    return roles.some(role => role.roleCode === roleCode)
  } catch (e) {
    return false
  }
}

// 添加 hasAnyRole 函数，检查是否拥有任意一个指定角色
export const hasAnyRole = (...roleCodes) => {
  const rolesStr = localStorage.getItem('userRoles')
  if (!rolesStr) return false
  
  try {
    const roles = JSON.parse(rolesStr)
    return roles.some(role => roleCodes.includes(role.roleCode))
  } catch (e) {
    return false
  }
}

// 添加一个工具函数来检查token是否过期
export const isTokenExpired = () => {
  const expireTime = localStorage.getItem('tokenExpireTime')
  if (!expireTime) return true
  
  try {
    return new Date(expireTime) <= new Date()
  } catch (e) {
    return true
  }
}

// 添加系统初始化相关的 API
export const init = {
  // 初始化系统
  initSystem(data) {
    return request.post('/init', data)
  },

  // 获取前端配置
  getFrontConfig() {
    return request.get('/init/front-config')
  }
}

// 搜索用户
export function searchUsers(params) {
  return request({
    url: '/api/v1/users/search',
    method: 'post',
    data: params
  })
}

// 修改统计相关的 API
export const stats = {
  // 仪表板统计（需要 days 参数）
  getStats(days = 7) {  // 默认获取7天的数据
    return request.get('/stats', {
      params: { days }
    })
  },

  // 管理员统计（不需要参数）
  getAdminStats() {
    return request.get('/stats/admin')
  }
}

// 个人信息相关的 API
export const profile = {
  // 获取个人信息
  getProfile() {
    return request.get('/profile')
  },

  // 更新个人信息
  updateProfile(data) {
    // 确保不发送 username 字段
    const { username, ...updateData } = data
    return request.put('/profile', updateData)
  },

  // 更新头像
  updateAvatar(avatarUrl) {
    return request.put('/profile/avatar', null, {
      params: { avatarUrl }
    })
  }
}

// 添加存储策略相关的 API
export const strategy = {
  // 获取策略列表
  getList() {
    return request.get('/strategies')
  },

  // 创建策略
  create(data) {
    return request.post('/strategies', data)
  },

  // 更新策略
  update(id, data) {
    return request.put(`/strategies/${id}`, data)
  },

  // 删除策略
  delete(id) {
    return request.delete(`/strategies/${id}`)
  },

  // 测试策略
  test(id) {
    return request.get(`/strategies/${id}/test`)
  }
}

// 系统配置相关的 API
export const system = {
  // 获取系统配置
  getConfig() {
    return request.get('/system/config')
  },

  // 更新系统配置
  updateConfig(config) {
    return request.put('/system/config', config)
  },

  // 添加重启系统方法
  restart() {
    return request.post('/system/restart')
  }
}

// 导出 request 实例供其他模块使用
export default request
