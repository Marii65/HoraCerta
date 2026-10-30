<script setup>
import {
  Gauge,
  Wrench,
  ClipboardPlus,
  Truck
} from 'lucide-vue-next';

const actions = [
  {
    id: 'reading',
    label: 'Registrar Leitura',
    route: '/leituras',
    icon: Gauge,
    variant: 'green'
  },
  {
    id: 'work-order',
    label: 'Nova O.S.',
    route: '/ordens-servico',
    icon: Wrench,
    variant: 'blue'
  },
  {
    id: 'maintenance-plan',
    label: 'Novo Plano',
    route: '/planos-manutencao',
    icon: ClipboardPlus,
    variant: 'yellow'
  },
  {
    id: 'equipment',
    label: 'Novo Equipamento',
    route: '/equipamentos',
    icon: Truck,
    variant: 'purple'
  }
];
</script>

<template>
  <section class="quick-actions-card">

    <div class="quick-actions-header">
      <h2>Ações rápidas</h2>
    </div>

    <div class="quick-actions-grid">

      <RouterLink
        v-for="action in actions"
        :key="action.id"
        :to="action.route"
        class="quick-action"
        :class="`quick-action--${action.variant}`"
      >
        <div class="quick-action-icon">
          <component
            :is="action.icon"
            :size="21"
            stroke-width="1.8"
          />
        </div>

        <span>
          {{ action.label }}
        </span>
      </RouterLink>

    </div>

  </section>
</template>

<style scoped>
.quick-actions-card {
  padding: 18px;

  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);

  background-color: var(--color-surface);

  box-shadow: var(--shadow-sm);
}

.quick-actions-header {
  margin-bottom: 14px;
}

.quick-actions-header h2 {
  color: var(--color-text-primary);

  font-size: 16px;
  font-weight: 700;
}


/*GRID*/

.quick-actions-grid {
  display: grid;

  grid-template-columns: repeat(2, minmax(0, 1fr));

  gap: 10px;
}


/*BOTÃO*/

.quick-action {
  display: flex;
  align-items: center;

  gap: 10px;

  min-height: 54px;

  padding: 10px 12px;

  border: 1px solid transparent;
  border-radius: var(--radius-md);

  font-size: 15px;
  font-weight: 600;

  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
}

.quick-action:hover {
  transform: translateY(-2px);

  box-shadow: var(--shadow-sm);
}

.quick-action-icon {
  display: flex;
  align-items: center;
  justify-content: center;

  flex-shrink: 0;
}


/*CORES*/

.quick-action--green {
  background-color: #e8f6ef;
  color: #187a54;
}

.quick-action--blue {
  background-color: #eaf2ff;
  color: #2767c8;
}

.quick-action--yellow {
  background-color: #fff5dd;
  color: #b9780c;
}

.quick-action--purple {
  background-color: #f0edff;
  color: #6553c7;
}


/*RESPONSIVIDADE*/

@media (max-width: 1150px) {
  .quick-actions-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 1000px) {
  .quick-actions-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 550px) {
  .quick-actions-grid {
    grid-template-columns: 1fr;
  }
}
</style>