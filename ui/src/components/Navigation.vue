<template>
  <el-menu :default-active="activeMenu" class="el-menu-vertical" :collapse="isCollapse" background-color="#304156"
    text-color="#bfcbd9" active-text-color="#409EFF" router>
    <el-menu-item index="/">
      <el-icon>
        <Odometer />
      </el-icon>
      <template #title>仪表盘</template>
    </el-menu-item>

    <el-menu-item index="/images">
      <el-icon>
        <Picture />
      </el-icon>
      <template #title>图片管理</template>
    </el-menu-item>

    <el-menu-item index="/albums">
      <el-icon>
        <Collection />
      </el-icon>
      <template #title>相册管理</template>
    </el-menu-item>

    <el-menu-item index="/open">
      <el-icon>
        <Document />
      </el-icon>
      <template #title>API文档</template>
    </el-menu-item>

    <el-menu-item v-if="frontConfig?.data?.allowGuest" index="/gallery">
      <el-icon>
        <Picture />
      </el-icon>
      <template #title>图片广场</template>
    </el-menu-item>

    <template v-if="hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')">
      <el-menu-item index="/users">
        <el-icon>
          <User />
        </el-icon>
        <template #title>用户管理</template>
      </el-menu-item>

      <el-menu-item index="/storage">
        <el-icon>
          <FolderOpened />
        </el-icon>
        <template #title>存储策略</template>
      </el-menu-item>

      <el-menu-item index="/stats">
        <el-icon>
          <DataLine />
        </el-icon>
        <template #title>访问统计</template>
      </el-menu-item>
    </template>

    <template v-if="hasAnyRole('ROLE_SUPER_ADMIN')">
      <el-menu-item index="/settings">
        <el-icon>
          <Setting />
        </el-icon>
        <template #title>系统设置</template>
      </el-menu-item>
    </template>
  </el-menu>
</template>

<script>
import { ref, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import {
  Odometer,
  Picture,
  Collection,
  User,
  Setting,
  FolderOpened,
  DataLine,
  Document
} from '@element-plus/icons-vue'
import { hasRole, hasAnyRole } from '@/api'

export default {
  name: 'Navigation',
  components: {
    Odometer,
    Picture,
    Collection,
    User,
    Setting,
    FolderOpened,
    DataLine,
    Document
  },
  setup() {
    const route = useRoute()
    const isCollapse = ref(false)

    const activeMenu = computed(() => {
      return route.path
    })

    // 获取前端配置
    const frontConfig = computed(() => {
      try {
        const config = localStorage.getItem('frontConfig')
        return config ? JSON.parse(config) : null
      } catch (e) {
        console.error('Parse frontConfig error:', e)
        return null
      }
    })

    // 添加调试日志
    watch(frontConfig, (newVal) => {
      console.log('Current frontConfig:', newVal)
    }, { immediate: true })

    return {
      isCollapse,
      activeMenu,
      hasRole,
      hasAnyRole,
      frontConfig
    }
  }
}
</script>

<style scoped>
.el-menu-vertical {
  border-right: none;
}

.el-menu-vertical:not(.el-menu--collapse) {
  width: 240px;
}
</style>