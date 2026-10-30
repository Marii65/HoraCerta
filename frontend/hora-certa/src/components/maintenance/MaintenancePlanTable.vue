<script setup>
import { ref } from 'vue';

import {
  ChevronDown,
  ChevronUp,
  Clock3,
  MoreVertical,
  Pencil,
  Power,
  Settings2,
  Wrench
} from 'lucide-vue-next';


defineProps({
  plans: {
    type: Array,
    required: true
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


function getBaseInterval(plan) {
  if (!plan.items?.length) {
    return 0;
  }

  return Math.min(
    ...plan.items.map(
      (item) => Number(item.intervalHours)
    )
  );
}
</script>


<template>
  <section class="maintenance-plan-list">

    <article
      v-for="plan in plans"
      :key="plan.id"
      class="maintenance-plan-card"
      :class="{
        'maintenance-plan-card--inactive': !plan.active
      }"
    >

      <div class="plan-card-content">

        <div class="plan-card-identity">

          <div class="plan-card-icon">
            <Settings2
              :size="23"
              stroke-width="1.8"
            />
          </div>


          <div class="plan-card-info">

            <div class="plan-card-title-row">

              <h2>
                {{ plan.name }}
              </h2>


              <span
                class="plan-status"
                :class="
                  plan.active
                    ? 'plan-status--active'
                    : 'plan-status--inactive'
                "
              >
                <span class="plan-status-dot"></span>

                {{ plan.active ? 'Ativo' : 'Inativo' }}
              </span>

            </div>


            <p>
              {{
                plan.description ||
                'Nenhuma descrição informada para este plano.'
              }}
            </p>

          </div>

        </div>

        <div class="plan-actions">

          <button
            class="plan-menu-button"
            type="button"
            aria-label="Abrir ações do plano"
            @click="toggleMenu(plan.id)"
          >
            <MoreVertical :size="19" />
          </button>


          <div
            v-if="openMenuId === plan.id"
            class="plan-actions-menu"
          >

            <button
              type="button"
              @click="handleEdit(plan)"
            >
              <Pencil :size="15" />

              Editar
            </button>


            <div class="plan-actions-divider"></div>


            <button
              type="button"
              :class="
                plan.active
                  ? 'plan-action-danger'
                  : 'plan-action-success'
              "
              @click="handleToggleStatus(plan)"
            >
              <Power :size="15" />

              {{ plan.active ? 'Desativar' : 'Ativar' }}
            </button>

          </div>

        </div>

      </div>

      <div class="plan-card-summary">

        <div class="plan-summary-item">

          <div class="plan-summary-icon">
            <Wrench :size="17" />
          </div>


          <div>
            <span>Serviços</span>

            <strong>
              {{ plan.items.length }}
              {{ plan.items.length === 1 ? 'serviço' : 'serviços' }}
            </strong>
          </div>

        </div>


        <div class="plan-summary-divider"></div>


        <div class="plan-summary-item">

          <div class="plan-summary-icon">
            <Clock3 :size="17" />
          </div>


          <div>
            <span>Intervalo base</span>

            <strong>
              {{ getBaseInterval(plan) }}h
            </strong>
          </div>

        </div>

      </div>

      <button
        class="plan-expand-button"
        type="button"
        @click="togglePlanDetails(plan.id)"
      >

        <span>
          {{
            expandedPlanId === plan.id
              ? 'Ocultar serviços'
              : 'Ver serviços do plano'
          }}
        </span>


        <ChevronUp
          v-if="expandedPlanId === plan.id"
          :size="17"
        />

        <ChevronDown
          v-else
          :size="17"
        />

      </button>

      <div
        v-if="expandedPlanId === plan.id"
        class="plan-services"
      >

        <div class="plan-services-header">

          <div>

            <span class="plan-services-label">
              SERVIÇOS DE MANUTENÇÃO
            </span>

            <h3>
              Rotina preventiva
            </h3>

          </div>


          <span class="plan-services-count">
            {{ plan.items.length }}
            {{ plan.items.length === 1 ? 'item' : 'itens' }}
          </span>

        </div>


        <div class="plan-services-list">

          <div
            v-for="(item, index) in plan.items"
            :key="item.id ?? `${plan.id}-${index}`"
            class="plan-service-item"
          >

            <div class="plan-service-number">
              {{ String(index + 1).padStart(2, '0') }}
            </div>

            <div class="plan-service-description">

              <span>Serviço</span>

              <strong>
                {{ item.serviceDescription }}
              </strong>

            </div>

            <div class="plan-service-data">

              <span>Intervalo</span>

              <strong>
                {{ item.intervalHours }}h
              </strong>

            </div>

            <div class="plan-service-data plan-service-alert">

              <span>Alerta preventivo</span>

              <strong>
                {{ item.alertMarginHours }}h antes
              </strong>

            </div>

          </div>

        </div>

      </div>

    </article>

    <div
      v-if="plans.length === 0"
      class="plan-empty-state"
    >

      <div class="plan-empty-icon">
        <Wrench
          :size="25"
          stroke-width="1.7"
        />
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