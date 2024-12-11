<template>
  <div class="auth-layout">
    <div class="auth-container">
      <div class="auth-box">
        <div class="logo">
          <el-icon size="64" color="#409EFF"><Picture /></el-icon>
          <h1>图床管理系统</h1>
        </div>
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Picture } from '@element-plus/icons-vue'
</script>

<style scoped>
.auth-layout {
  min-height: 100vh;
  background-color: #f5f7fa;
}
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
}
.auth-box {
  width: 100%;
  max-width: 400px;
}
.logo {
  text-align: center;
  margin-bottom: 30px;
}
.logo h1 {
  margin-top: 10px;
  font-size: 24px;
  color: #303133;
}
</style> 