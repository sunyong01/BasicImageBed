import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/init',
    name: 'Init',
    component: () => import('../views/init/Index.vue'),
    meta: { public: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/auth/Login.vue'),
    meta: { public: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/auth/Register.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    component: () => import('../layouts/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('../views/dashboard/Index.vue'),
        alias: '/dashboard'
      },
      
      {
        path: 'images',
        name: 'ImageManagement',
        component: () => import('../views/image/Index.vue')
      },
      {
        path: 'gallery',
        name: 'Gallery',
        component: () => import('../views/gallery/Index.vue'),
        meta: {
          title: '图片广场',
          icon: 'Picture',
          requiresAuth: true
        }
      },
      {
        path: 'albums',
        name: 'AlbumManagement',
        component: () => import('../views/album/Index.vue')
      },
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('../views/user/Index.vue')
      },
      {
        path: 'settings',
        name: 'SystemSettings',
        component: () => import('../views/system/Index.vue')
      },
      {
        path: 'storage',
        name: 'Storage',
        component: () => import('@/views/storage/Index.vue'),
        meta: {
          title: '存储策略',
          icon: 'Storage',
          requiresAuth: true
        }
      },
      {
        path: 'stats',
        name: 'Stats',
        component: () => import('@/views/stats/Index.vue'),
        meta: {
          title: '访问统计',
          icon: 'DataLine',
          requiresAuth: true
        }
      },
      {
        path: 'open',
        name: 'ApiDocs',
        component: () => import('@/views/api/Index.vue'),
        meta: {
          title: 'API文档',
          icon: 'Document',
          requiresAuth: true
        }
      },
      {
        path: 'profile',
        name: 'UserProfile',
        component: () => import('../views/profile/Index.vue'),
        meta: {
          title: '个人信息',
          requiresAuth: true
        }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const frontConfig = JSON.parse(localStorage.getItem('frontConfig') || '{}')
  
  // 检查系统是否已初始化
  if (!frontConfig.data?.systemInitialized && to.path !== '/init') {
    next('/init')
    return
  }
  
  // 如果系统已初始化，但访问 /init 页面，重定向到首页或登录页
  if (frontConfig.data?.systemInitialized && to.path === '/init') {
    next(token ? '/' : '/login')
    return
  }
  
  // 检查是否是图片广场路由且不允许游客访问
  if (to.name === 'Gallery' && !frontConfig.data?.allowGuest && !token) {
    next('/login')
    return
  }
  
  if (to.meta.requiresAuth) {
    if (!token) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else {
      // 检查是否需要管理员权限
      if (to.meta.requiresAdmin && !hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')) {
        next('/403')
        return
      }
      // 检查是否需要超级管理员权限
      if (to.meta.requiresSuperAdmin && !hasRole('ROLE_SUPER_ADMIN')) {
        next('/403')
        return
      }
      next()
    }
  } else {
    if (token && to.path === '/login') {
      next('/')
    } else {
      next()
    }
  }
})

export default router 