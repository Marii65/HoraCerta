<script setup>
import {
  Truck,
  CircleCheck,
  TriangleAlert,
  CircleAlert
} from 'lucide-vue-next';

import StatCard from './StatCard.vue';


const props = defineProps({
  stats: {
    type: Array,
    required: true
  }
});

const icons = {
  equipment: Truck,
  ok: CircleCheck,
  warning: TriangleAlert,
  overdue: CircleAlert
};

function getStatIcon(statId) {
  return icons[statId] ?? Truck;
}
</script>


<template>
  <section class="stats-grid">

    <StatCard
      v-for="stat in props.stats"
      :key="stat.id"
      :value="stat.value"
      :label="stat.label"
      :description="stat.description"
      :icon="getStatIcon(stat.id)"
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