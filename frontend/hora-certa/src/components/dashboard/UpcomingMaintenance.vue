<script setup>
import { ArrowRight } from 'lucide-vue-next';

import {
  getEquipmentIcon
} from '../../utils/equipmentIcons';


defineProps({
  maintenances: {
    type: Array,
    required: true
  }
});

function getStatusLabel(status) {

  const labels = {
    ok: 'Em dia',
    warning: 'Em atenção',
    overdue: 'Vencida'
  };

  return labels[status] ?? 'Indefinido';
}
</script>


<template>
  <section class="upcoming-maintenance">

    <div class="maintenance-header">

      <h2>
        Próximas manutenções
      </h2>


      <RouterLink
        to="/ordens-servico"
        class="view-all"
      >
        Ver todas

        <ArrowRight :size="15" />
      </RouterLink>

    </div>

    <div class="table-wrapper">

      <table class="maintenance-table">

        <thead>
          <tr>
            <th>Equipamento</th>
            <th>Tipo de manutenção</th>
            <th>Progresso</th>
            <th>Status</th>
            <th>Prevista em</th>
          </tr>
        </thead>


        <tbody>

          <tr
            v-for="maintenance in maintenances"
            :key="maintenance.id"
          >

            <td>

              <div class="equipment-cell">

                <div class="equipment-image">

                  <img
                    :src="getEquipmentIcon(maintenance.category)"
                    :alt="`Ícone de ${maintenance.category}`"
                  />

                </div>


                <div class="equipment-info">

                  <strong>
                    {{ maintenance.code }}
                  </strong>

                  <span>
                    {{ maintenance.equipment }}
                  </span>

                </div>

              </div>

            </td>

            <td>

              <span class="maintenance-type">
                {{ maintenance.maintenanceType }}
              </span>

            </td>


            <td>

              <div class="progress-cell">

                <strong>
                  {{ maintenance.progress }}%
                </strong>


                <div class="progress-track">

                  <div
                    class="progress-bar"
                    :class="`progress-bar--${maintenance.status}`"
                    :style="{
                      width: `${Math.min(maintenance.progress, 100)}%`
                    }"
                  ></div>

                </div>

              </div>

            </td>

            <td>

              <span
                class="status-badge"
                :class="`status-badge--${maintenance.status}`"
              >
                {{ getStatusLabel(maintenance.status) }}
              </span>

            </td>

            <td>

              <div class="expected-date">

                <strong>
                  {{ maintenance.expectedDate }}
                </strong>

                <span
                  :class="{
                    'expected-date--overdue':
                      maintenance.status === 'overdue'
                  }"
                >
                  {{ maintenance.remainingText }}
                </span>

              </div>

            </td>

          </tr>

          <tr v-if="maintenances.length === 0">

            <td
              colspan="5"
              class="maintenance-empty"
            >
              Nenhuma manutenção próxima.
            </td>

          </tr>

        </tbody>

      </table>

    </div>

  </section>
</template>


<style scoped>
.upcoming-maintenance {
  width: 100%;

  overflow: hidden;

  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);

  background-color: var(--color-surface);

  box-shadow: var(--shadow-sm);
}


/* CABEÇALHO */

.maintenance-header {
  display: flex;
  align-items: center;
  justify-content: space-between;

  padding: 18px 20px 14px;
}

.maintenance-header h2 {
  color: var(--color-text-primary);

  font-size: 16px;
  font-weight: 700;
}

.view-all {
  display: inline-flex;
  align-items: center;

  gap: 6px;

  color: var(--color-primary);

  font-size: 12px;
  font-weight: 600;

  transition: opacity 0.2s ease;
}

.view-all:hover {
  opacity: 0.7;
}


/* TABELA */

.table-wrapper {
  width: 100%;

  overflow-x: auto;
}

.maintenance-table {
  width: 100%;

  border-collapse: separate;
  border-spacing: 0;

  background-color: transparent;
}


/* CABEÇALHO DA TABELA */

.maintenance-table thead th {
  padding: 11px 14px;

  background-color: #f1f5f4;

  color: var(--color-text-secondary);

  font-size: 12px;
  font-weight: 500;

  text-align: left;
}

.maintenance-table thead th:first-child {
  border-radius: 7px 0 0 7px;
}

.maintenance-table thead th:last-child {
  border-radius: 0 7px 7px 0;
}


/* LINHAS */

.maintenance-table tbody td {
  padding: 18px 12px;

  border-bottom: 1px solid var(--color-border);

  color: var(--color-text-secondary);

  font-size: 13px;

  vertical-align: middle;
}

.maintenance-table tbody tr:last-child td {
  border-bottom: none;
}


/* EQUIPAMENTO */

.equipment-cell {
  display: flex;
  align-items: center;

  gap: 12px;

  min-width: 210px;
}

.equipment-image {
  width: 56px;
  height: 52px;

  flex: 0 0 56px;
}

.equipment-image img {
  width: 52px;
  height: 48px;

  object-fit: contain;
}

.maintenance-table tbody tr:hover .equipment-image img {
  transform: scale(1.05);
}

.equipment-info {
  display: flex;
  flex-direction: column;

  gap: 4px;
}

.equipment-info strong {
  color: var(--color-text-primary);

  font-size: 13px;
  font-weight: 700;
}

.equipment-info span {
  color: var(--color-text-secondary);

  font-size: 11px;
}


/* TIPO DE MANUTENÇÃO */

.maintenance-type {
  display: block;

  max-width: 160px;

  color: var(--color-text-secondary);

  line-height: 1.5;
}


/* PROGRESSO */

.progress-cell {
  width: 110px;
}

.progress-cell strong {
  display: block;

  margin-bottom: 7px;

  color: var(--color-text-primary);

  font-size: 12px;
}

.progress-track {
  width: 90px;
  height: 7px;

  overflow: hidden;

  border-radius: 999px;

  background-color: #e3ebe8;
}

.progress-bar {
  height: 100%;

  border-radius: inherit;
}

.progress-bar--ok {
  background-color: var(--color-success);
}

.progress-bar--warning {
  background-color: var(--color-warning);
}

.progress-bar--overdue {
  background-color: var(--color-danger);
}


/* STATUS */

.status-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;

  min-width: 88px;

  padding: 7px 13px;

  border-radius: 999px;

  font-size: 11px;
  font-weight: 600;
}

.status-badge--ok {
  background-color: var(--color-success-light);

  color: var(--color-success);
}

.status-badge--warning {
  background-color: var(--color-warning-light);

  color: #d7890c;
}

.status-badge--overdue {
  background-color: var(--color-danger-light);

  color: var(--color-danger);
}


/* DATA PREVISTA */

.expected-date {
  display: flex;
  flex-direction: column;

  gap: 4px;

  min-width: 95px;
}

.expected-date strong {
  color: var(--color-text-primary);

  font-size: 11px;
  font-weight: 600;
}

.expected-date span {
  color: var(--color-text-secondary);

  font-size: 11px;
}

.expected-date .expected-date--overdue {
  color: var(--color-danger);
}


/* ESTADO VAZIO */

.maintenance-empty {
  height: 130px;

  color: var(--color-text-muted) !important;

  text-align: center;
}


/* RESPONSIVIDADE */

@media (max-width: 900px) {

  .maintenance-table {
    min-width: 850px;
  }

}
</style>