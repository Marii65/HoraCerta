<script setup>
import { ref } from 'vue';

import {
  ChevronDown,
  ChevronRight,
  MoreVertical,
  Pencil,
  Power,
  Search
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
</script>


<template>
  <section class="plan-card">

    <div class="plan-table-wrapper">

      <table class="plan-table">

        <thead>
          <tr>
            <th></th>
            <th>Plano</th>
            <th>Serviços</th>
            <th>Status</th>
            <th></th>
          </tr>
        </thead>


        <tbody>

          <template
            v-for="plan in plans"
            :key="plan.id"
          >

            <tr>

              <td class="expand-column">
                <button
                  class="expand-button"
                  type="button"
                  :aria-label="
                    expandedPlanId === plan.id
                      ? 'Recolher serviços'
                      : 'Exibir serviços'
                  "
                  @click="togglePlanDetails(plan.id)"
                >
                  <ChevronDown
                    v-if="expandedPlanId === plan.id"
                    :size="17"
                  />

                  <ChevronRight
                    v-else
                    :size="17"
                  />
                </button>
              </td>

              <td>
                <div class="plan-info">

                  <strong>
                    {{ plan.name }}
                  </strong>

                  <span>
                    {{
                      plan.description ||
                      'Sem descrição'
                    }}
                  </span>

                </div>
              </td>

              <td>
                <span class="plan-items-count">
                  {{ plan.items.length }}

                  {{
                    plan.items.length === 1
                      ? 'serviço'
                      : 'serviços'
                  }}
                </span>
              </td>

              <td>
                <span
                  class="plan-status"
                  :class="{
                    'plan-status--active':
                      plan.active,

                    'plan-status--inactive':
                      !plan.active
                  }"
                >
                  <span class="status-dot"></span>

                  {{
                    plan.active
                      ? 'Ativo'
                      : 'Inativo'
                  }}
                </span>
              </td>

              <td>
                <div class="plan-actions">

                  <button
                    class="more-button"
                    type="button"
                    aria-label="Opções do plano"
                    @click="toggleMenu(plan.id)"
                  >
                    <MoreVertical :size="18" />
                  </button>


                  <div
                    v-if="openMenuId === plan.id"
                    class="actions-menu"
                  >

                    <button
                      type="button"
                      @click="handleEdit(plan)"
                    >
                      <Pencil :size="15" />

                      Editar
                    </button>


                    <div class="actions-divider"></div>


                    <button
                      type="button"
                      :class="{
                        'action-danger':
                          plan.active,

                        'action-success':
                          !plan.active
                      }"
                      @click="handleToggleStatus(plan)"
                    >
                      <Power :size="15" />

                      {{
                        plan.active
                          ? 'Desativar'
                          : 'Ativar'
                      }}
                    </button>

                  </div>

                </div>
              </td>

            </tr>


            <!-- DETALHES DO PLANO -->
            <tr
              v-if="expandedPlanId === plan.id"
              class="plan-details-row"
            >
              <td colspan="5">

                <div class="plan-details">

                  <div class="plan-details-header">
                    <strong>
                      Serviços do plano
                    </strong>

                    <span>
                      {{ plan.items.length }}
                      itens cadastrados
                    </span>
                  </div>


                  <div class="plan-items">

                    <div
                      v-for="item in plan.items"
                      :key="item.id"
                      class="plan-item"
                    >

                      <div class="plan-item-description">
                        <strong>
                          {{ item.serviceDescription }}
                        </strong>
                      </div>


                      <div class="plan-item-data">
                        <span>
                          Intervalo
                        </span>

                        <strong>
                          {{ item.intervalHours }} h
                        </strong>
                      </div>


                      <div class="plan-item-data">
                        <span>
                          Alerta
                        </span>

                        <strong>
                          {{ item.alertMarginHours }} h antes
                        </strong>
                      </div>

                    </div>

                  </div>

                </div>

              </td>
            </tr>

          </template>


          <!-- ESTADO VAZIO -->
          <tr v-if="plans.length === 0">
            <td
              colspan="5"
              class="plan-empty-state"
            >
              <Search :size="26" />

              <strong>
                Nenhum plano encontrado
              </strong>

              <span>
                Tente alterar os termos da busca ou o filtro.
              </span>
            </td>
          </tr>

        </tbody>

      </table>

    </div>

  </section>
</template>