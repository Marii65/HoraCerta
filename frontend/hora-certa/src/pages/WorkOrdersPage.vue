<script setup>
import { computed, ref } from 'vue';

import {
  ClipboardList,
  Plus
} from 'lucide-vue-next';

import { workOrderMocks } from '../mocks/workOrders';
import { equipmentMocks } from '../mocks/equipments';

import WorkOrderTable
  from '../components/workorders/WorkOrderTable.vue';

import CompleteWorkOrderModal
  from '../components/workorders/CompleteWorkOrderModal.vue';

import '../styles/pages/work-orders.css';


const workOrders = ref([
  ...workOrderMocks
]);


const searchTerm = ref('');

const selectedStatus = ref('all');

const selectedWorkOrder = ref(null);

const isCompleteModalOpen = ref(false);

const filteredWorkOrders = computed(() => {

  const search = searchTerm.value
    .trim()
    .toLowerCase();


  return workOrders.value.filter((workOrder) => {

    const matchesSearch =
      !search ||

      workOrder.orderNumber
        .toLowerCase()
        .includes(search) ||

      workOrder.equipmentCode
        .toLowerCase()
        .includes(search) ||

      workOrder.equipmentName
        .toLowerCase()
        .includes(search) ||

      workOrder.maintenancePlanName
        .toLowerCase()
        .includes(search);


    const matchesStatus =
      selectedStatus.value === 'all' ||
      workOrder.status === selectedStatus.value;


    return matchesSearch && matchesStatus;

  });

});

const selectedWorkOrderEquipment = computed(() => {

  if (!selectedWorkOrder.value) {
    return null;
  }


  return equipmentMocks.find(
    (equipment) =>
      equipment.id ===
      selectedWorkOrder.value.equipmentId
  ) ?? null;

});

function openWorkOrderModal() {

  console.log('Abrir modal de nova ordem');

}

function handleStartWorkOrder(workOrder) {

  if (workOrder.status !== 'SCHEDULED') {
    return;
  }


  workOrder.status = 'IN_PROGRESS';

  workOrder.startedAt =
    new Date().toISOString();

}

function handleCancelWorkOrder(workOrder) {

  if (workOrder.status !== 'SCHEDULED') {
    return;
  }


  const confirmed = window.confirm(
    `Deseja realmente cancelar a ${workOrder.orderNumber}?`
  );


  if (!confirmed) {
    return;
  }


  workOrder.status = 'CANCELED';

}

function openCompleteWorkOrderModal(workOrder) {

  if (workOrder.status !== 'IN_PROGRESS') {
    return;
  }


  selectedWorkOrder.value = workOrder;

  isCompleteModalOpen.value = true;

}

function closeCompleteWorkOrderModal() {

  isCompleteModalOpen.value = false;

  selectedWorkOrder.value = null;

}

function handleCompleteWorkOrder(formData) {

  if (
    !selectedWorkOrder.value ||
    !selectedWorkOrderEquipment.value
  ) {
    return;
  }


  const workOrder =
    selectedWorkOrder.value;

  const equipment =
    selectedWorkOrderEquipment.value;


  if (workOrder.status !== 'IN_PROGRESS') {
    return;
  }


  workOrder.status = 'COMPLETED';

  workOrder.completedAt =
    new Date().toISOString();

  workOrder.completionHorometer =
    formData.completionHorometer;

  equipment.currentHorometer =
    formData.completionHorometer;

  if (formData.completionObservation) {

    workOrder.observation =
      formData.completionObservation;

  }


  closeCompleteWorkOrderModal();

}
</script>


<template>

  <div class="work-orders-page">

    <!-- CABEÇALHO -->

    <header class="work-orders-header">

      <div class="work-orders-header-content">

        <div class="work-orders-title-icon">

          <ClipboardList
            :size="22"
            stroke-width="1.8"
          />

        </div>


        <div>

          <h1>
            Ordens de Serviço
          </h1>

          <p>
            Acompanhe as manutenções programadas,
            em andamento e concluídas.
          </p>

        </div>

      </div>


      <button
        class="new-work-order-button"
        type="button"
        @click="openWorkOrderModal"
      >

        <Plus :size="17" />

        Nova Ordem

      </button>

    </header>

    <section class="work-orders-list">

      <div class="work-orders-list-heading">

        <div>

          <span class="work-orders-eyebrow">
            MANUTENÇÕES
          </span>

          <h2>
            Ordens registradas
          </h2>

        </div>


        <span class="work-orders-total">

          {{ filteredWorkOrders.length }}

          {{
            filteredWorkOrders.length === 1
              ? 'ordem'
              : 'ordens'
          }}

        </span>

      </div>

      <div class="work-orders-filters">

        <label class="work-orders-search">

          <span>
            Buscar
          </span>

          <input
            v-model="searchTerm"
            type="text"
            placeholder="Número, equipamento ou plano..."
          />

        </label>


        <label class="work-orders-status-filter">

          <span>
            Status
          </span>

          <select v-model="selectedStatus">

            <option value="all">
              Todos
            </option>

            <option value="SCHEDULED">
              Agendada
            </option>

            <option value="IN_PROGRESS">
              Em andamento
            </option>

            <option value="COMPLETED">
              Concluída
            </option>

            <option value="CANCELED">
              Cancelada
            </option>

          </select>

        </label>

      </div>

      <WorkOrderTable
        :work-orders="filteredWorkOrders"
        @start="handleStartWorkOrder"
        @cancel="handleCancelWorkOrder"
        @complete="openCompleteWorkOrderModal"
      />

    </section>

    <CompleteWorkOrderModal
      v-if="
        isCompleteModalOpen &&
        selectedWorkOrder &&
        selectedWorkOrderEquipment
      "
      :work-order="selectedWorkOrder"
      :equipment="selectedWorkOrderEquipment"
      @close="closeCompleteWorkOrderModal"
      @complete="handleCompleteWorkOrder"
    />

  </div>

</template>