<script setup>
import {
  CalendarDays,
  Bell,
  ChevronDown
} from 'lucide-vue-next';
</script>

<template>
  <header class="app-header">

    <div class="header-actions">

      <!-- Data -->
      <div class="header-date">
        <CalendarDays :size="19" />

        <div>
          <strong>26 de Outubro de 2026</strong>
          <span>Segunda-feira</span>
        </div>
      </div>

      <button
        class="notification-button"
        type="button"
        aria-label="Notificações"
      >
        <Bell :size="20" />

        <span class="notification-badge">
          3
        </span>
      </button>

      <button
        class="user-profile"
        type="button"
      >
        <div class="user-avatar">
          ML
        </div>

        <div class="user-info">
          <strong>Maria Luiza</strong>
          <span>Administradora</span>
        </div>

        <ChevronDown :size="17" />
      </button>

    </div>

  </header>
</template>