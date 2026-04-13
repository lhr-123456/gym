<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style lang="scss">
/* 海绵宝宝动漫风格 - 应用根样式 */

#app {
  width: 100%;
  min-height: 100vh;
  position: relative;
}

body {
  margin: 0;
  padding: 0;
  font-family: 'Comic Sans MS', 'Microsoft YaHei', cursive, sans-serif;
  overflow-x: hidden;
}
</style>
