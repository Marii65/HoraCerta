<script setup>
import { computed, ref } from 'vue';

import {
  Plus,
  Search
} from 'lucide-vue-next';

import PageHeader from '../components/common/PageHeader.vue';

import { workOrderMocks } from '../mocks/workOrders';
import { equipmentMocks } from '../mocks/equipments';

import WorkOrderTable from '../components/workorders/WorkOrderTable.vue';

import CompleteWorkOrderModal from '../components/workorders/CompleteWorkOrderModal.vue';

import '../styles/pages/work-orders.css';

import WorkOrderFormModal from '../components/workorders/WorkOrderFormModal.vue';
import { maintenancePlanMocks } from '../mocks/maintenancePlans';

const workOrders = ref([
  ...workOrderMocks
]);


const searchTerm = ref('');

const selectedStatus = ref('all');

const selectedWorkOrder = ref(null);

const isCompleteModalOpen = ref(false);

const isWorkOrderModalOpen = ref(false);

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
  isWorkOrderModalOpen.value = true;
}

function closeWorkOrderModal() {
  isWorkOrderModalOpen.value = false;
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

function handleSaveWorkOrder(formData) {
  const equipment = equipmentMocks.find(
    (item) =>
      item.id === formData.equipmentId
  );


  const maintenancePlan =
    maintenancePlanMocks.find(
      (item) =>
        item.id === formData.maintenancePlanId
    );

  if (!equipment || !maintenancePlan) {
    return;
  }

  const nextNumber =
    Math.max(
      0,
      ...workOrders.value.map(
        (workOrder) =>
          Number(
            workOrder.orderNumber
              .replace('OS-', '')
          ) || 0
      )
    ) + 1;


  const newWorkOrder = {

    id: Date.now(),

    orderNumber:
      `OS-${String(nextNumber).padStart(3, '0')}`,

    equipmentId:
      equipment.id,

    equipmentCode:
      equipment.code,

    equipmentName:
      equipment.name,

    equipmentCategory:
      equipment.category,

    maintenancePlanId:
      maintenancePlan.id,

    maintenancePlanName:
      maintenancePlan.name,

    scheduledDate:
      formData.scheduledDate,

    scheduledHorometer:
      formData.scheduledHorometer,

    status: 'SCHEDULED',

    observation:
      formData.observation,

    startedAt: null,

    completedAt: null,

    completionHorometer: null

  };

  workOrders.value.unshift(
    newWorkOrder
  );


  closeWorkOrderModal();

}

</script>


<template>

  <div class="work-orders-page">
    <PageHeader title="Ordens de Serviço"
      description="Acompanhe as manutenções programadas, em andamento e concluídas.">
      <template #actions>
        <button class="new-work-order-button" type="button" @click="openWorkOrderModal">
          <Plus :size="17" />
          Nova Ordem
        </button>
      </template>
    </PageHeader>

    <section class="work-orders-list">

      <div class="work-orders-list-heading">

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
          <Search :size="17" />

          <input v-model="searchTerm" type="text" placeholder="Buscar por número, equipamento ou plano..." />
        </label>

        <select v-model="selectedStatus" class="work-orders-status-select">
          <option value="all">
            Todos os status
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

      </div>

      <WorkOrderTable :work-orders="filteredWorkOrders" @start="handleStartWorkOrder" @cancel="handleCancelWorkOrder"
        @complete="openCompleteWorkOrderModal" />

    </section>

    <WorkOrderFormModal v-if="isWorkOrderModalOpen" :equipments="equipmentMocks"
      :maintenance-plans="maintenancePlanMocks" @close="closeWorkOrderModal" @save="handleSaveWorkOrder" />

    <CompleteWorkOrderModal v-if="
      isCompleteModalOpen &&
      selectedWorkOrder &&
      selectedWorkOrderEquipment
    " :work-order="selectedWorkOrder" :equipment="selectedWorkOrderEquipment" @close="closeCompleteWorkOrderModal"
      @complete="handleCompleteWorkOrder" />

  </div>

</template>