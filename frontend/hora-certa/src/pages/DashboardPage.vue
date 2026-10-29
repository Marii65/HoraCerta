<script setup>
import WelcomeBanner from '../components/dashboard/WelcomeBanner.vue';
</script>

<template>
  <div class="dashboard-page">

    <WelcomeBanner />

  </div>
</template>

<style scoped>
.dashboard-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}
</style>