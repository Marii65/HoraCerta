<script setup>
import {
  computed,
  onMounted,
  ref
} from 'vue';

import {
  Plus,
  Search
} from 'lucide-vue-next';

import PageHeader from '../components/common/PageHeader.vue';
import WorkOrderTable from '../components/workorders/WorkOrderTable.vue';
import CompleteWorkOrderModal from '../components/workorders/CompleteWorkOrderModal.vue';
import WorkOrderFormModal from '../components/workorders/WorkOrderFormModal.vue';

import { getEquipments } from '../services/equipmentService';
import { getEquipmentPlans } from '../services/equipmentPlanService';
import { getWorkOrders, createWorkOrder, startWorkOrder, completeWorkOrder, cancelWorkOrder } from '../services/workOrderService';

import '../styles/pages/work-orders.css';

const workOrders = ref([]);
const equipments = ref([]);

const isLoading = ref(false);
const errorMessage = ref('');

const searchTerm = ref('');
const selectedStatus = ref('all');

const selectedWorkOrder = ref(null);

const isCompleteModalOpen = ref(false);
const isWorkOrderModalOpen = ref(false);

async function mapWorkOrders(apiWorkOrders) {
  const equipmentPlanIds = [
    ...new Set(
      apiWorkOrders.map(
        (workOrder) =>
          workOrder.equipmentPlanId
      )
    )
  ];

  const linksById = new Map();
  await Promise.all(
    equipments.value.map(
      async (equipment) => {
        const links =
          await getEquipmentPlans(equipment.id);

        links.forEach((link) => {
          linksById.set(link.id, link);
        });
      }
    )
  );

  return apiWorkOrders.map((workOrder) => {
    const equipmentPlan =
      linksById.get(
        workOrder.equipmentPlanId
      );
    const equipment =
      equipments.value.find(
        (item) =>
          item.id ===
          equipmentPlan?.equipmentId
      );
    return {
      ...workOrder,
      orderNumber:
        `OS-${String(workOrder.id).padStart(
          3,
          '0'
        )}`,
      equipmentId:
        equipment?.id ?? null,
      equipmentCode:
        equipment?.code ?? '—',
      equipmentName:
        equipment?.name ??
        'Equipamento não encontrado',
      equipmentCategory:
        equipment?.category ?? '',
      maintenancePlanId:
        equipmentPlan?.maintenancePlanId ??
        null,
      maintenancePlanName:
        equipmentPlan?.maintenancePlanName ??
        'Plano não encontrado',
      completionHorometer:
        workOrder.executedHorometer,
      observation:
        workOrder.notes
    };
  });
}

async function loadWorkOrdersPage() {
  isLoading.value = true;
  errorMessage.value = '';
  try {
    equipments.value =
      await getEquipments();
    const apiWorkOrders =
      await getWorkOrders();
    workOrders.value =
      await mapWorkOrders(apiWorkOrders);
  } catch (error) {
    errorMessage.value =
      error.message ||
      'Não foi possível carregar as ordens de serviço.';
  } finally {
    isLoading.value = false;
  }
}

const filteredWorkOrders = computed(() => {
  const search =
    searchTerm.value
      .trim()
      .toLowerCase();
  return workOrders.value.filter(
    (workOrder) => {
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
        workOrder.status ===
        selectedStatus.value;
      return (
        matchesSearch &&
        matchesStatus
      );
    }
  );
});

const selectedWorkOrderEquipment =
  computed(() => {
    if (!selectedWorkOrder.value) {
      return null;
    }
    return equipments.value.find(
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

async function handleSaveWorkOrder(formData) {
  errorMessage.value = '';
  try {
    await createWorkOrder(formData);
    closeWorkOrderModal();
    await loadWorkOrdersPage();
  } catch (error) {
    errorMessage.value =
      error.message ||
      'Não foi possível criar a ordem de serviço.';
  }
}

async function handleStartWorkOrder(workOrder) {
  if (workOrder.status !== 'SCHEDULED') {
    return;
  }
  errorMessage.value = '';
  try {
    await startWorkOrder(workOrder.id);
    await loadWorkOrdersPage();
  } catch (error) {
    errorMessage.value =
      error.message ||
      'Não foi possível iniciar a ordem de serviço.';
  }
}

async function handleCancelWorkOrder(workOrder) {
  if (workOrder.status !== 'SCHEDULED') {
    return;
  }
  const confirmed =
    window.confirm(
      `Deseja realmente cancelar a ${workOrder.orderNumber}?`
    );
  if (!confirmed) {
    return;
  }
  errorMessage.value = '';
  try {
    await cancelWorkOrder(workOrder.id);
    await loadWorkOrdersPage();
  } catch (error) {
    errorMessage.value =
      error.message ||
      'Não foi possível cancelar a ordem de serviço.';
  }
}

function openCompleteWorkOrderModal(
  workOrder
) {
  if (
    workOrder.status !== 'IN_PROGRESS'
  ) {
    return;
  }
  selectedWorkOrder.value = workOrder;
  isCompleteModalOpen.value = true;
}

function closeCompleteWorkOrderModal() {
  isCompleteModalOpen.value = false;
  selectedWorkOrder.value = null;
}

async function handleCompleteWorkOrder(
  formData
) {
  if (!selectedWorkOrder.value) {
    return;
  }
  errorMessage.value = '';
  try {
    await completeWorkOrder(
      selectedWorkOrder.value.id,
      formData.completionHorometer
    );
    closeCompleteWorkOrderModal();
    await loadWorkOrdersPage();
  } catch (error) {
    errorMessage.value =
      error.message ||
      'Não foi possível concluir a ordem de serviço.';
  }
}

onMounted(loadWorkOrdersPage);
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

    <p v-if="errorMessage" class="work-orders-error">
      {{ errorMessage }}
    </p>

    <p v-if="isLoading" class="work-orders-loading">
      Carregando ordens de serviço...
    </p>

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

          <option value="CANCELLED">
            Cancelada
          </option>
        </select>

      </div>

      <WorkOrderTable :work-orders="filteredWorkOrders" @start="handleStartWorkOrder" @cancel="handleCancelWorkOrder"
        @complete="openCompleteWorkOrderModal" />

    </section>

    <WorkOrderFormModal v-if="isWorkOrderModalOpen" :equipments="equipments" @close="closeWorkOrderModal"
      @save="handleSaveWorkOrder" />

    <CompleteWorkOrderModal v-if="
      isCompleteModalOpen &&
      selectedWorkOrder &&
      selectedWorkOrderEquipment
    " :work-order="selectedWorkOrder" :equipment="selectedWorkOrderEquipment" @close="closeCompleteWorkOrderModal"
      @complete="handleCompleteWorkOrder" />

  </div>

</template>