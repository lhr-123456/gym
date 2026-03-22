const getters = {
  token: state => state.user.token,
  username: state => state.user.username,
  userId: state => state.user.userId,
  userType: state => state.user.userType,
  userInfo: state => state.user,
  role: state => state.user.role,
  permissions: state => state.user.permissions,
  routes: state => state.permission.routes,
  addRoutes: state => state.permission.addRoutes,
  /** 当前登录会员的 memberId（userType=3 时有效） */
  memberId: state => state.user.memberId,
  /** 当前登录教练的 coachId（userType=2 时有效） */
  coachId: state => state.user.coachId
}

export default getters
