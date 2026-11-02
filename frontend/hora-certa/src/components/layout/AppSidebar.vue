<script setup>
import { onMounted, ref, computed } from 'vue';

import {
  LayoutDashboard,
  Truck,
  ClipboardList,
  Gauge,
  Settings,
  Clock3
} from 'lucide-vue-next';

import { getEquipments } from '../../services/equipmentService';

import horaCertaLogo from '../../assets/images/logo/horacerta-logo.png';

const menuItems = [
  {
    label: 'Dashboard',
    route: '/app',
    icon: LayoutDashboard
  },
  {
    label: 'Equipamentos',
    route: '/app/equipamentos',
    icon: Truck
  },
  {
    label: 'Planos de Manutenção',
    route: '/app/planos-manutencao',
    icon: ClipboardList
  },
  {
    label: 'Leituras de Horímetro',
    route: '/app/leituras',
    icon: Gauge
  },
  {
    label: 'Ordens de Serviço',
    route: '/app/ordens-servico',
    icon: Settings
  }
];

const equipments = ref([]);

const totalEquipments = computed(() =>
  equipments.value.length
);

const activeEquipments = computed(() =>
  equipments.value.filter(
    equipment => equipment.status === 'ACTIVE'
  ).length
);

const activePercentage = computed(() => {
  if (totalEquipments.value === 0) {
    return 0;
  }

  return Math.round(
    (activeEquipments.value / totalEquipments.value) * 100
  );
});

async function loadFleetStatus() {
  try {
    equipments.value = await getEquipments();
  } catch (error) {
    console.error(
      'Erro ao carregar status da frota:',
      error
    );
  }
}

onMounted(loadFleetStatus);
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
          <strong>{{ activeEquipments }}</strong>
          <span>/ {{ totalEquipments }}</span>
        </div>

        <div class="fleet-progress">
          <div class="fleet-progress-bar" :style="{ width: `${activePercentage}%` }"></div>
        </div>

        <span class="fleet-percentage">
          {{ activePercentage }}% da frota
        </span>
      </div>
    </div>
  </aside>
</template>