<template>
  <div class="layout-container" :data-user-type="userType">
    <!-- 顶部导航栏 -->
    <el-header class="top-nav" height="60px">
      <div class="nav-inner">
        <!-- Logo -->
        <div class="nav-logo">
          <i class="el-icon-d-arrow-left" style="font-size:20px;color:#409EFF;margin-right:6px;"></i>
          <span class="logo-text">健身房管理系统</span>
        </div>

        <!-- 横向菜单 - 管理员 -->
        <div class="nav-menu" v-if="userType === 1">
          <el-menu
            :default-active="activeMenu"
            mode="horizontal"
            background-color="transparent"
            text-color="#409EFF"
            active-text-color="#409EFF"
            :router="true"
            menu-trigger="hover"
          >
            <el-menu-item index="/dashboard/index">
              <i class="el-icon-s-home"></i><span>首页</span>
            </el-menu-item>
            <el-submenu index="/admin-member">
              <template slot="title">
                <i class="el-icon-user"></i><span>会员管理</span>
              </template>
              <el-menu-item index="/admin-member/list">会员列表</el-menu-item>
              <el-menu-item index="/admin-member/level">会员等级</el-menu-item>
              <el-menu-item index="/admin-member/card">会员卡管理</el-menu-item>
              <el-menu-item index="/admin-member/signin">会员签到</el-menu-item>
              <el-menu-item index="/admin-member/bodyTest">体测记录</el-menu-item>
              <el-menu-item index="/admin-member/consumption">消费记录</el-menu-item>
            </el-submenu>
            <el-submenu index="/coach">
              <template slot="title">
                <i class="el-icon-user-solid"></i><span>教练管理</span>
              </template>
              <el-menu-item index="/coach/list">教练列表</el-menu-item>
              <el-menu-item index="/coach/certificate">教练证书</el-menu-item>
              <el-menu-item index="/coach/specialty">教练专长</el-menu-item>
              <el-menu-item index="/coach/schedule">教练排班</el-menu-item>
              <el-menu-item index="/coach/shift">教练调班</el-menu-item>
              <el-menu-item index="/coach/salary">教练工资</el-menu-item>
              <el-menu-item index="/coach/performance">教练绩效</el-menu-item>
              <el-menu-item index="/coach/review">教练评价</el-menu-item>
            </el-submenu>
            <el-submenu index="/course">
              <template slot="title">
                <i class="el-icon-reading"></i><span>课程管理</span>
              </template>
              <el-menu-item index="/course/list">课程列表</el-menu-item>
              <el-menu-item index="/course/category">课程分类</el-menu-item>
              <el-menu-item index="/course/schedule">课程排期</el-menu-item>
              <el-menu-item index="/course/my">我的课程</el-menu-item>
              <el-menu-item index="/course/available">可预约课程</el-menu-item>
              <el-menu-item index="/course/booking">课程预约</el-menu-item>
              <el-menu-item index="/course/review">课程评价</el-menu-item>
            </el-submenu>
            <el-submenu index="/equipment">
              <template slot="title">
                <i class="el-icon-tools"></i><span>器材管理</span>
              </template>
              <el-menu-item index="/equipment/list">器材列表</el-menu-item>
            </el-submenu>
            <el-submenu index="/points">
              <template slot="title">
                <i class="el-icon-shopping-bag-2"></i><span>积分商城</span>
              </template>
              <el-menu-item index="/points/goods">商品管理</el-menu-item>
            </el-submenu>
          </el-menu>
        </div>

        <!-- 横向菜单 - 教练 -->
        <div class="nav-menu" v-else-if="userType === 2">
          <el-menu
            :default-active="activeMenu"
            mode="horizontal"
            background-color="transparent"
            text-color="#409EFF"
            active-text-color="#409EFF"
            :router="true"
            menu-trigger="hover"
          >
            <el-menu-item index="/coach-dashboard/index">
              <i class="el-icon-s-home"></i><span>工作台</span>
            </el-menu-item>
            <el-menu-item index="/coach-schedule/list">
              <i class="el-icon-date"></i><span>我的排课</span>
            </el-menu-item>
            <el-menu-item index="/coach-members/list">
              <i class="el-icon-user"></i><span>我的学员</span>
            </el-menu-item>
            <el-menu-item index="/coach-reviews/list">
              <i class="el-icon-chat-dot-round"></i><span>我的评价</span>
            </el-menu-item>
            <el-menu-item index="/coach-bodytest/add">
              <i class="el-icon-data-analysis"></i><span>录入体测</span>
            </el-menu-item>
            <el-menu-item index="/coach-homework/list">
              <i class="el-icon-document-copy"></i><span>布置作业</span>
            </el-menu-item>
          </el-menu>
        </div>

        <!-- 横向菜单 - 会员 -->
        <div class="nav-menu" v-else-if="userType === 3">
          <el-menu
            :default-active="activeMenu"
            mode="horizontal"
            background-color="transparent"
            text-color="#409EFF"
            active-text-color="#409EFF"
            :router="true"
            menu-trigger="hover"
          >
            <el-menu-item index="/member/home">
              <i class="el-icon-s-home"></i><span>会员首页</span>
            </el-menu-item>
            <el-submenu index="/course">
              <template slot="title">
                <i class="el-icon-reading"></i><span>课程预约</span>
              </template>
              <el-menu-item index="/course/available">可预约课程</el-menu-item>
              <el-menu-item index="/course/booking">我的预约</el-menu-item>
            </el-submenu>
            <el-menu-item index="/member/bodytest">
              <i class="el-icon-data-analysis"></i><span>体测记录</span>
            </el-menu-item>
            <el-menu-item index="/member/contactCoach">
              <i class="el-icon-chat-line-round"></i><span>联系教练</span>
            </el-menu-item>
            <el-menu-item index="/member/sportData">
              <i class="el-icon-data-line"></i><span>运动数据</span>
            </el-menu-item>
            <el-menu-item index="/member/points">
              <i class="el-icon-shopping-bag-2"></i><span>积分商城</span>
            </el-menu-item>
            <el-menu-item index="/member/messages">
              <i class="el-icon-bell"></i><span>消息中心</span>
            </el-menu-item>
            <el-menu-item index="/member/homework">
              <i class="el-icon-document-copy"></i><span>我的作业</span>
            </el-menu-item>
          </el-menu>
        </div>

        <!-- 右侧用户信息 -->
        <div class="nav-right">
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="user-avatar">
              <img v-if="avatar" :src="avatar" alt="avatar" class="nav-avatar-img" />
              <i v-else class="el-icon-user-solid" style="font-size:20px;color:#409EFF;"></i>
              <span class="username">{{ username }}</span>
              <i class="el-icon-arrow-down el-icon--right" style="font-size:12px;color:#909399;"></i>
            </div>
            <el-dropdown-menu slot="dropdown" style="min-width: 160px;">
              <el-dropdown-item command="profile" icon="el-icon-user">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout" icon="el-icon-switch-button" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <!-- 主内容区 -->
    <el-main class="main-content">
      <router-view />
    </el-main>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Layout',
  computed: {
    ...mapGetters(['username', 'avatar']),
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
        // 根据角色跳转到对应个人中心页面
        if (this.userType === 3) {
          this.$router.push('/member/profile')
        } else {
          this.$router.push('/profile/index')
        }
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.layout-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: url('D:/gympicture/bjt2.jpg') no-repeat center center fixed;
  background-size: cover;
  background-position: center center;
  background-attachment: fixed;
  background-repeat: no-repeat;
  position: relative;
  z-index: 1;
}

/* 导航栏样式 - 透明背景 + 蓝色字体 */
.top-nav {
  background-color: transparent; /* 完全透明背景 */
  padding: 0;
  position: sticky;
  top: 0;
  z-index: 1000;
  box-shadow: none; /* 移除阴影 */
  backdrop-filter: none; /* 移除毛玻璃效果 */
}

.nav-inner {
  display: flex;
  align-items: stretch;
  height: 60px;
  max-width: 100%;
  background: transparent;
}

.nav-logo {
  display: flex;
  align-items: center;
  padding: 0 24px;
  min-width: 220px;
  background-color: transparent; /* 完全透明背景 */
  flex-shrink: 0;

  .logo-text {
    color: #409EFF !important; /* 蓝色字体 */
    font-size: 16px;
    font-weight: bold;
    white-space: nowrap;
  }
}

.nav-menu {
  flex: 1;
  overflow: hidden;

  ::v-deep .el-menu {
    background-color: transparent !important;
    border-bottom: none;
    height: 60px;

    .el-menu-item,
    ::v-deep .el-submenu__title {
      height: 60px;
      line-height: 60px;
      color: #409EFF; /* 字体改为蓝色 */
      font-size: 14px;
      padding: 0 16px;
      border-bottom: 3px solid transparent;
      transition: all 0.2s;
      background-color: transparent !important;

      i { 
        margin-right: 5px;
        font-size: 16px;
        color: #409EFF; /* 图标改为蓝色 */
      }

      span { 
        vertical-align: middle;
        color: #409EFF !important; /* 确保字体蓝色 */
      }

      &:hover {
        background-color: rgba(38, 52, 69, 0.8) !important;
        color: #fff !important;
      }

      &.is-active {
        background-color: rgba(38, 52, 69, 0.8) !important;
        color: #409EFF !important;
        border-bottom-color: #409EFF !important;
      }
    }

    ::v-deep .el-submenu {
      .el-submenu__title {
        border-bottom: 3px solid transparent;
      }

      .el-menu {
        background-color: rgba(48, 65, 86, 0.95); /* 半透明背景 */
        border-radius: 0 0 8px 8px;
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15); /* 更柔和的阴影 */

        .el-menu-item {
          height: 42px;
          line-height: 42px;
          padding: 0 20px;
          font-size: 13px;
          border-bottom: none;

          &:hover {
            background-color: #409EFF !important;
            color: #fff;
          }

          &.is-active {
            background-color: #409EFF !important;
            color: #fff;
          }
        }
      }
    }
  }
}

.nav-right {
  display: flex;
  align-items: center;
  padding: 0 20px;
  flex-shrink: 0;
  border-left: none; /* 移除边框 */
  background: transparent;

  .user-avatar {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    padding: 6px 12px;
    border-radius: 4px;
    transition: all 0.2s;
    background: transparent;

    &:hover {
      background-color: rgba(38, 52, 69, 0.8);
    }

    .nav-avatar-img {
      width: 30px;
      height: 30px;
      border-radius: 50%;
      object-fit: cover;
      object-position: center;
      flex-shrink: 0;
    }

    .username {
      color: #409EFF !important; /* 蓝色字体 */
      font-size: 14px;
      max-width: 120px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  ::v-deep .el-dropdown-menu {
    background-color: rgba(48, 65, 86, 0.95); /* 半透明背景 */
    border: 1px solid rgba(61, 81, 102, 0.5); /* 半透明边框 */
    border-radius: 8px;

    .el-dropdown-menu__item {
      color: #bfcbd9;
      font-size: 14px;
      padding: 8px 20px;

      &:hover {
        background-color: #409EFF;
        color: #fff;
      }

      &.is-divided {
        border-top-color: rgba(61, 81, 102, 0.5); /* 半透明分隔线 */
      }
    }
  }
}

.main-content {
  flex: 1;
  background-color: transparent; /* 完全透明背景 */
  padding: 20px;
  min-height: calc(100vh - 60px);
  border-radius: 12px;
  margin: 20px;
  box-shadow: none; /* 移除阴影 */
}
</style>
