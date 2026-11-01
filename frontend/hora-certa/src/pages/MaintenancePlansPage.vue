<script setup>
import { computed, ref } from 'vue';

import { Plus } from 'lucide-vue-next';

import MaintenancePlanFilters
  from '../components/maintenance/MaintenancePlanFilters.vue';

import MaintenancePlanTable
  from '../components/maintenance/MaintenancePlanTable.vue';

import MaintenancePlanFormModal
  from '../components/maintenance/MaintenancePlanFormModal.vue';

import { maintenancePlanMocks }
  from '../mocks/maintenancePlans';

import '../styles/pages/maintenance-plans.css';
import PageHeader from '../components/common/PageHeader.vue';

const maintenancePlans = ref([
  ...maintenancePlanMocks
]);

const searchTerm = ref('');

const selectedStatus = ref('all');

const filteredMaintenancePlans = computed(() => {

  const search = searchTerm.value
    .trim()
    .toLowerCase();


  return maintenancePlans.value.filter((plan) => {

    const matchesSearch =
      !search ||
      plan.name
        .toLowerCase()
        .includes(search) ||
      (plan.description ?? '')
        .toLowerCase()
        .includes(search);

    const matchesStatus =
      selectedStatus.value === 'all' ||

      (
        selectedStatus.value === 'active' &&
        plan.active
      ) ||

      (
        selectedStatus.value === 'inactive' &&
        !plan.active
      );


    return matchesSearch && matchesStatus;

  });

});

const isPlanModalOpen = ref(false);

const editingPlan = ref(null);

function openCreateModal() {
  editingPlan.value = null;

  isPlanModalOpen.value = true;
}

function openEditModal(plan) {
  editingPlan.value = plan;

  isPlanModalOpen.value = true;
}

function closePlanModal() {
  isPlanModalOpen.value = false;

  editingPlan.value = null;
}

function handleSavePlan(formData) {

  if (editingPlan.value) {
    updatePlan(formData);

    return;
  }

  createPlan(formData);
}

function createPlan(formData) {

  const newPlan = {
    id: Date.now(),

    ...formData,

    active: true
  };


  maintenancePlans.value.unshift(newPlan);

  closePlanModal();
}

function updatePlan(formData) {

  const index = maintenancePlans.value.findIndex(
    (plan) => plan.id === editingPlan.value.id
  );


  if (index === -1) {
    return;
  }


  maintenancePlans.value[index] = {
    ...maintenancePlans.value[index],
    ...formData
  };


  closePlanModal();
}

function togglePlanStatus(plan) {

  const index = maintenancePlans.value.findIndex(
    (item) => item.id === plan.id
  );


  if (index === -1) {
    return;
  }


  const currentPlan =
    maintenancePlans.value[index];


  maintenancePlans.value[index] = {
    ...currentPlan,

    active: !currentPlan.active
  };

}
</script>


<template>
  <div class="maintenance-plans-page">

    <PageHeader title="Planos de Manutenção" description="Organize as rotinas preventivas e os serviços de manutenção.">
      <template #actions>
        <button class="new-plan-button" type="button" @click="openCreateModal">
          <Plus :size="18" />
          Novo Plano
        </button>
      </template>
    </PageHeader>

    <MaintenancePlanFilters v-model:search="searchTerm" v-model:status="selectedStatus" />

    <MaintenancePlanTable :plans="filteredMaintenancePlans" @edit="openEditModal" @toggle-status="togglePlanStatus" />

    <MaintenancePlanFormModal v-if="isPlanModalOpen" :plan="editingPlan" @close="closePlanModal"
      @save="handleSavePlan" />

  </div>
</template>