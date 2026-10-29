<script setup>
import { RouterView } from 'vue-router';

import AppSidebar from '../components/layout/AppSidebar.vue';
import AppHeader from '../components/layout/AppHeader.vue';
</script>

<template>
  <div class="app-layout">
    <AppSidebar />

    <div class="app-container">
      <AppHeader />

      <main class="app-content">
        <RouterView />
      </main>
    </div>
  </div>
</template>