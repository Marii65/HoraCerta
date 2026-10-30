<script setup>
import {
  Truck,
  CircleCheck,
  TriangleAlert,
  CircleAlert
} from 'lucide-vue-next';

import StatCard from './StatCard.vue';

const stats = [
  {
    id: 'equipment',
    value: 28,
    label: 'Equipamentos',
    description: 'Total da frota',
    icon: Truck,
    variant: 'default'
  },
  {
    id: 'ok',
    value: 21,
    label: 'Em dia',
    description: '75% da frota',
    icon: CircleCheck,
    variant: 'success'
  },
  {
    id: 'warning',
    value: 4,
    label: 'Em atenção',
    description: 'Necessitam planejamento',
    icon: TriangleAlert,
    variant: 'warning'
  },
  {
    id: 'overdue',
    value: 3,
    label: 'Vencidos',
    description: 'Ação necessária',
    icon: CircleAlert,
    variant: 'danger'
  }
];
</script>

<template>
  <section class="stats-grid">

    <StatCard
      v-for="stat in stats"
      :key="stat.id"
      :value="stat.value"
      :label="stat.label"
      :description="stat.description"
      :icon="stat.icon"
      :variant="stat.variant"
    />

  </section>
</template>

<style scoped>
.stats-grid {
  display: grid;

  grid-template-columns: repeat(4, minmax(0, 1fr));

  gap: 16px;
}


/*RESPONSIVIDADE*/

@media (max-width: 1100px) {
  .stats-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 600px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>