<script setup>
import { ref } from 'vue';

import {
  ChevronDown,
  ChevronUp,
  MoreVertical,
  Pencil,
  Power,
  Wrench
} from 'lucide-vue-next';

import StatusBadge from '../common/StatusBadge.vue';

defineProps({
  plans: {
    type: Array,
    required: true
  },

  canManage: {
    type: Boolean,
    default: false
  }
});


const emit = defineEmits([
  'edit',
  'toggle-status'
]);

const expandedPlanId = ref(null);

function togglePlanDetails(planId) {
  expandedPlanId.value =
    expandedPlanId.value === planId
      ? null
      : planId;
}

const openMenuId = ref(null);

function toggleMenu(planId) {
  openMenuId.value =
    openMenuId.value === planId
      ? null
      : planId;
}

function handleEdit(plan) {
  openMenuId.value = null;

  emit('edit', plan);
}

function handleToggleStatus(plan) {
  openMenuId.value = null;

  emit('toggle-status', plan);
}


</script>

<template>
  <section class="maintenance-plan-list">
    <article v-for="plan in plans" :key="plan.id" class="maintenance-plan-card" :class="[
      `maintenance-plan-card--${plan.color ?? 'green'}`,
      {
        'maintenance-plan-card--inactive': !plan.active,
        'maintenance-plan-card--expanded': expandedPlanId === plan.id
      }
    ]">

      <div class="plan-card-content">
        <div class="plan-card-info">
          <div class="plan-card-title-row">
            <h2>
              {{ plan.name }}
            </h2>
            <StatusBadge :label="plan.active ? 'Ativo' : 'Inativo'" :variant="plan.active ? 'success' : 'neutral'" />
          </div>

          <p>
            {{
              plan.description ||
              'Nenhuma descrição informada para este plano.'
            }}
          </p>
        </div>

        <div v-if="canManage" class="plan-actions">
          <button class="plan-menu-button" type="button" aria-label="Abrir ações do plano" @click="toggleMenu(plan.id)">
            <MoreVertical :size="18" />
          </button>

          <div v-if="openMenuId === plan.id" class="plan-actions-menu">
            <button type="button" @click="handleEdit(plan)">
              <Pencil :size="15" />
              Editar
            </button>

            <div class="plan-actions-divider"></div>

            <button type="button" :class="plan.active
              ? 'plan-action-danger'
              : 'plan-action-success'
              " @click="handleToggleStatus(plan)">
              <Power :size="15" />

              {{ plan.active ? 'Desativar' : 'Ativar' }}
            </button>
          </div>
        </div>
      </div>

      <div class="plan-card-meta">

        <span>
          {{ plan.items.length }}
          {{ plan.items.length === 1 ? 'serviço' : 'serviços' }}
        </span>

      </div>

      <button class="plan-expand-button" type="button" @click="togglePlanDetails(plan.id)">

        <span>
          {{
            expandedPlanId === plan.id
              ? 'Ocultar serviços'
              : 'Ver serviços'
          }}
        </span>

        <ChevronUp v-if="expandedPlanId === plan.id" :size="16" />

        <ChevronDown v-else :size="16" />

      </button>

      <div v-if="expandedPlanId === plan.id" class="plan-services">

        <div v-for="(item, index) in plan.items" :key="item.id ?? `${plan.id}-${index}`" class="plan-service-item">

          <strong>
            {{ item.serviceDescription }}
          </strong>

          <div class="plan-service-details">

            <span>
              A cada
              <strong>{{ item.intervalHours }}h</strong>
            </span>

            <span>
              Alerta
              <strong>{{ item.alertMarginHours }}h antes</strong>
            </span>

          </div>

        </div>

      </div>

    </article>

    <div v-if="plans.length === 0" class="plan-empty-state">

      <div class="plan-empty-icon">
        <Wrench :size="25" stroke-width="1.7" />
      </div>

      <strong>
        Nenhum plano encontrado
      </strong>

      <span>
        Tente alterar os filtros utilizados na busca.
      </span>

    </div>

  </section>
</template>