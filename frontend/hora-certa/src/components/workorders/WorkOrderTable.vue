<script setup>
import { ref } from 'vue';

import {
  CalendarDays,
  ChevronDown,
  ClipboardList,
  Gauge,
  Wrench
} from 'lucide-vue-next';

import { getEquipmentIcon }
  from '../../utils/equipmentIcons';

import StatusBadge from '../common/StatusBadge.vue';

defineProps({
  workOrders: {
    type: Array,
    required: true
  },

  canManage: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits([
  'start',
  'cancel',
  'complete'
]);

const expandedWorkOrderId = ref(null);

function toggleWorkOrder(workOrderId) {

  expandedWorkOrderId.value =
    expandedWorkOrderId.value === workOrderId
      ? null
      : workOrderId;

}

function isExpanded(workOrderId) {

  return expandedWorkOrderId.value === workOrderId;

}

function formatDate(date) {

  if (!date) {
    return '—';
  }

  return new Intl.DateTimeFormat(
    'pt-BR'
  ).format(
    new Date(`${date}T00:00:00`)
  );

}

function formatDateTime(date) {

  if (!date) {
    return '—';
  }

  return new Intl.DateTimeFormat(
    'pt-BR',
    {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    }
  ).format(new Date(date));

}

function formatHorometer(value) {

  if (
    value === null ||
    value === undefined
  ) {
    return '—';
  }

  return new Intl.NumberFormat(
    'pt-BR',
    {
      maximumFractionDigits: 1
    }
  ).format(value);

}

function getStatusLabel(status) {
  const labels = {
    SCHEDULED: 'Agendada',
    IN_PROGRESS: 'Em andamento',
    COMPLETED: 'Concluída',
    CANCELLED: 'Cancelada'
  };
  return labels[status] ?? status;
}

function getStatusVariant(status) {
  const variants = {
    SCHEDULED: 'warning',
    IN_PROGRESS: 'info',
    COMPLETED: 'success',
    CANCELLED: 'neutral'
  };

  return variants[status] ?? 'neutral';
}

</script>


<template>
  <div class="work-order-table-wrapper">
    <table class="system-table work-order-table">
      <thead>
        <tr>
          <th>Ordem</th>
          <th>Equipamento</th>
          <th>Manutenção</th>
          <th>Agendamento</th>
          <th>Status</th>
          <th class="work-order-table-actions">
            Ações
          </th>
        </tr>
      </thead>

      <tbody>
        <template v-for="workOrder in workOrders" :key="workOrder.id">
          <tr class="work-order-row" :class="{
            'work-order-row--expanded':
              isExpanded(workOrder.id)
          }">
            <td>
              <strong class="work-order-number">
                {{ workOrder.orderNumber }}
              </strong>
            </td>

            <td>
              <div class="work-order-equipment">
                <div class="work-order-equipment-icon">
                  <img :src="getEquipmentIcon(
                    workOrder.equipmentCategory
                  )
                    " :alt="workOrder.equipmentCategory" />
                </div>

                <div class="work-order-equipment-info">
                  <strong>
                    {{ workOrder.equipmentCode }}
                  </strong>
                  <span>
                    {{ workOrder.equipmentName }}
                  </span>
                </div>
              </div>
            </td>

            <td>
              <div class="work-order-maintenance">
                <div class="work-order-maintenance-title">
                  <Wrench :size="14" stroke-width="1.8" />
                  <strong>
                    {{ workOrder.maintenancePlanName }}
                  </strong>
                </div>

                <div v-if="workOrder.scheduledHorometer" class="work-order-horometer">
                  <Gauge :size="13" stroke-width="1.8" />
                  <span>
                    {{
                      formatHorometer(
                        workOrder.scheduledHorometer
                      )
                    }} h
                  </span>
                </div>
              </div>
            </td>

            <td>
              <div class="work-order-date">
                <CalendarDays :size="14" stroke-width="1.8" />
                <span>
                  {{
                    formatDate(
                      workOrder.scheduledDate
                    )
                  }}
                </span>
              </div>
            </td>

            <td>
              <StatusBadge :label="getStatusLabel(workOrder.status)" :variant="getStatusVariant(workOrder.status)" />
            </td>

            <td class="work-order-table-actions">
              <button class="work-order-expand-button" :class="{
                'work-order-expand-button--active':
                  isExpanded(workOrder.id)
              }" type="button" :title="isExpanded(workOrder.id)
                ? 'Recolher ordem'
                : 'Visualizar ordem'
                " @click="toggleWorkOrder(workOrder.id)">

                <ChevronDown :size="18" stroke-width="1.8" />

              </button>
            </td>
          </tr>

          <tr v-if="isExpanded(workOrder.id)" class="work-order-details-row">
            <td colspan="6">
              <div class="work-order-details">
                <div class="work-order-details-grid">
                  <div class="work-order-detail">

                    <span>
                      Equipamento
                    </span>

                    <strong>
                      {{ workOrder.equipmentCode }}
                    </strong>

                    <small>
                      {{ workOrder.equipmentName }}
                    </small>
                  </div>

                  <div class="work-order-detail">
                    <span>
                      Plano de manutenção
                    </span>

                    <strong>
                      {{ workOrder.maintenancePlanName }}
                    </strong>
                  </div>

                  <div class="work-order-detail">
                    <span>
                      Horímetro previsto
                    </span>

                    <strong>
                      {{
                        formatHorometer(
                          workOrder.scheduledHorometer
                        )
                      }} h
                    </strong>
                  </div>

                  <div class="work-order-detail">
                    <span>
                      Data agendada
                    </span>

                    <strong>
                      {{
                        formatDate(
                          workOrder.scheduledDate
                        )
                      }}
                    </strong>
                  </div>
                </div>

                <div v-if="workOrder.status === 'COMPLETED'" class="work-order-execution">
                  <div>
                    <span>
                      Concluída em
                    </span>

                    <strong>
                      {{
                        formatDateTime(
                          workOrder.completedAt
                        )
                      }}
                    </strong>
                  </div>

                  <div>
                    <span>
                      Horímetro na conclusão
                    </span>

                    <strong>
                      {{
                        formatHorometer(
                          workOrder.completionHorometer
                        )
                      }} h
                    </strong>
                  </div>
                </div>

                <div class="work-order-observation">
                  <span>
                    Observação
                  </span>

                  <p>
                    {{
                      workOrder.observation ||
                      'Nenhuma observação registrada.'
                    }}
                  </p>
                </div>

                <div v-if="
                  canManage && (
                    workOrder.status === 'SCHEDULED' ||
                    workOrder.status === 'IN_PROGRESS'
                  )" class="work-order-details-actions">

                  <template v-if="workOrder.status === 'SCHEDULED'">
                    <button class="work-order-action work-order-action--danger" type="button"
                      @click="emit('cancel', workOrder)">
                      Cancelar OS
                    </button>

                    <button class="work-order-action work-order-action--primary" type="button"
                      @click="emit('start', workOrder)">
                      Iniciar OS
                    </button>
                  </template>

                  <button v-if="
                    workOrder.status === 'IN_PROGRESS'
                  " class="work-order-action work-order-action--primary" type="button"
                    @click="emit('complete', workOrder)">
                    Concluir OS
                  </button>
                </div>
              </div>
            </td>
          </tr>
        </template>

        <tr v-if="workOrders.length === 0">
          <td colspan="6" class="work-orders-empty">
            <div class="work-orders-empty-content">

              <ClipboardList :size="28" stroke-width="1.6" />

              <strong>
                Nenhuma ordem encontrada
              </strong>

              <span>
                Tente alterar os filtros utilizados.
              </span>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>