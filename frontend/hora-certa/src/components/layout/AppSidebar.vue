<script setup>
import {
  LayoutDashboard,
  Truck,
  ClipboardList,
  Gauge,
  Settings,
  Clock3
} from 'lucide-vue-next';

import horaCertaLogo from '../../assets/images/logo/horacerta-logo.png';

const menuItems = [
  {
    label: 'Dashboard',
    route: '/',
    icon: LayoutDashboard
  },
  {
    label: 'Equipamentos',
    route: '/equipamentos',
    icon: Truck
  },
  {
    label: 'Planos de Manutenção',
    route: '/planos-manutencao',
    icon: ClipboardList
  },
  {
    label: 'Leituras de Horímetro',
    route: '/leituras',
    icon: Gauge
  },
  {
    label: 'Ordens de Serviço',
    route: '/ordens-servico',
    icon: Settings
  }
];
</script>

<template>
  <aside class="sidebar">

    <div class="sidebar-brand">
      <RouterLink to="/" class="sidebar-brand-link">
        <img :src="horaCertaLogo" alt="HoraCerta" class="sidebar-logo" />
      </RouterLink>
    </div>

    <nav class="sidebar-nav">

      <RouterLink v-for="item in menuItems" :key="item.route" :to="item.route" class="sidebar-link">
        <component :is="item.icon" :size="20" stroke-width="1.8" />

        <span>{{ item.label }}</span>
      </RouterLink>

    </nav>

    <div class="sidebar-footer">

      <div class="fleet-status">

        <div class="fleet-status-title">
          <Clock3 :size="18" />
          <span>Frota em operação</span>
        </div>

        <div class="fleet-status-number">
          <strong>21</strong>
          <span>/ 28</span>
        </div>

        <div class="fleet-progress">
          <div class="fleet-progress-bar"></div>
        </div>

        <span class="fleet-percentage">
          75% da frota
        </span>

      </div>

    </div>

  </aside>
</template>