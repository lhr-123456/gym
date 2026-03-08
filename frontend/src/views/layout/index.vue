<template>
  <div class="layout-container">
    <el-container>
      <el-aside :width="isCollapse ? '64px' : '200px'" class="sidebar">
        <div class="logo-container">
          <span v-show="!isCollapse" class="logo-title">健身房管理系统</span>
          <span v-show="isCollapse" class="logo-title-short">健身</span>
        </div>
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          :router="true"
        >
          <!-- 管理员菜单 -->
          <template v-if="userType === 1">
            <el-menu-item index="/dashboard/index">
              <i class="el-icon-s-home"></i>
              <span slot="title">首页</span>
            </el-menu-item>
            <el-submenu index="/member">
              <template slot="title">
                <i class="el-icon-user"></i>
                <span slot="title">会员管理</span>
              </template>
              <el-menu-item index="/member/list">
                <i class="el-icon-list"></i>
                <span slot="title">会员列表</span>
              </el-menu-item>
            </el-submenu>
            <el-submenu index="/coach">
              <template slot="title">
                <i class="el-icon-user-solid"></i>
                <span slot="title">教练管理</span>
              </template>
              <el-menu-item index="/coach/list">
                <i class="el-icon-list"></i>
                <span slot="title">教练列表</span>
              </el-menu-item>
            </el-submenu>
            <el-submenu index="/course">
              <template slot="title">
                <i class="el-icon-reading"></i>
                <span slot="title">课程管理</span>
              </template>
              <el-menu-item index="/course/list">
                <i class="el-icon-list"></i>
                <span slot="title">课程列表</span>
              </el-menu-item>
            </el-submenu>
            <el-submenu index="/equipment">
              <template slot="title">
                <i class="el-icon-tools"></i>
                <span slot="title">器材管理</span>
              </template>
              <el-menu-item index="/equipment/list">
                <i class="el-icon-list"></i>
                <span slot="title">器材列表</span>
              </el-menu-item>
            </el-submenu>
          </template>

          <!-- 教练菜单 -->
          <template v-else-if="userType === 2">
            <el-menu-item index="/dashboard/index">
              <i class="el-icon-s-home"></i>
              <span slot="title">首页</span>
            </el-menu-item>
            <el-submenu index="/course">
              <template slot="title">
                <i class="el-icon-reading"></i>
                <span slot="title">课程管理</span>
              </template>
              <el-menu-item index="/course/my">
                <i class="el-icon-list"></i>
                <span slot="title">我的课程</span>
              </el-menu-item>
            </el-submenu>
          </template>

          <!-- 会员菜单 -->
          <template v-else-if="userType === 3">
            <el-menu-item index="/dashboard/index">
              <i class="el-icon-s-home"></i>
              <span slot="title">首页</span>
            </el-menu-item>
            <el-submenu index="/course">
              <template slot="title">
                <i class="el-icon-reading"></i>
                <span slot="title">课程预约</span>
              </template>
              <el-menu-item index="/course/available">
                <i class="el-icon-list"></i>
                <span slot="title">可预约课程</span>
              </el-menu-item>
            </el-submenu>
          </template>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header class="header">
          <div class="header-left">
            <i class="el-icon-s-fold" @click="isCollapse = !isCollapse"></i>
          </div>
          <div class="header-right">
            <span class="username">{{ username }}</span>
            <el-dropdown @command="handleCommand">
              <i class="el-icon-setting"></i>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </el-header>

        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Layout',
  data() {
    return {
      isCollapse: false
    }
  },
  computed: {
    ...mapGetters(['username']),
    userType() {
      return this.$store.getters.userType || 1
    },
    activeMenu() {
      return this.$route.path
    }
  },
  methods: {
    handleCommand(command) {
      if (command === 'logout') {
        this.$confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$store.dispatch('user/logout').then(() => {
            this.$router.push('/login')
          })
        })
      } else if (command === 'profile') {
        this.$router.push('/profile')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;
  display: flex;

  .el-container {
    flex: 1;
    height: 100%;
  }

  .sidebar {
    background-color: #304156;
    transition: width 0.28s;
    height: 100%;

    .logo-container {
      height: 60px;
      display: flex;
      align-items: center;
      justify-content: center;
      background-color: #2b3a4b;

      .logo-title {
        color: #fff;
        font-size: 18px;
        font-weight: bold;
      }

      .logo-title-short {
        color: #fff;
        font-size: 18px;
        font-weight: bold;
      }
    }

    .el-menu {
      border-right: none;
    }
  }

  .header {
    background-color: #fff;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;

    .header-left {
      i {
        font-size: 20px;
        cursor: pointer;
      }
    }

    .header-right {
      display: flex;
      align-items: center;

      .username {
        margin-right: 15px;
        font-size: 14px;
      }

      i {
        font-size: 20px;
        cursor: pointer;
      }
    }
  }

  .main-content {
    background-color: #f0f2f5;
    padding: 20px;
    overflow-y: auto;
    height: calc(100vh - 60px);
  }
}
</style>
