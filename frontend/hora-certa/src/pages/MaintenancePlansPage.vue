<script setup>
import { computed, onMounted, ref } from 'vue';

import { Plus } from 'lucide-vue-next';

import MaintenancePlanFilters from '../components/maintenance/MaintenancePlanFilters.vue';
import MaintenancePlanTable from '../components/maintenance/MaintenancePlanTable.vue';
import MaintenancePlanFormModal from '../components/maintenance/MaintenancePlanFormModal.vue';
import PageHeader from '../components/common/PageHeader.vue';

import { getCurrentUser } from '../services/userService';

import {
  getMaintenancePlans,
  getMaintenancePlanItems,
  createMaintenancePlan,
  updateMaintenancePlan,
  toggleMaintenancePlanStatus,
  addMaintenancePlanItem,
  updateMaintenancePlanItem,
  deleteMaintenancePlanItem
} from '../services/maintenancePlanService';

import '../styles/pages/maintenance-plans.css';

const maintenancePlans = ref([]);

const currentUser = ref(null);

const currentRole = computed(() => currentUser.value?.role ?? null);
const canManagePlans = computed(() => ['ADMIN', 'TECHNICIAN'].includes(currentRole.value));

const searchTerm = ref('');
const selectedStatus = ref('all');

const isPlanModalOpen = ref(false);
const editingPlan = ref(null);

const isLoading = ref(false);
const errorMessage = ref('');

const PLAN_COLORS_KEY = 'horacerta-maintenance-plan-colors';


function getSavedPlanColors() {
  try {
    return JSON.parse(
      localStorage.getItem(PLAN_COLORS_KEY)
    ) ?? {};
  } catch {
    return {};
  }
}

function savePlanColor(planId, color) {
  const colors = getSavedPlanColors();

  colors[planId] = color;

  localStorage.setItem(
    PLAN_COLORS_KEY,
    JSON.stringify(colors)
  );
}

async function loadMaintenancePlans() {
  isLoading.value = true;
  errorMessage.value = '';

  try {
    const plans = await getMaintenancePlans();

    const savedColors = getSavedPlanColors();

    maintenancePlans.value = await Promise.all(
      plans.map(async (plan) => {
        const items =
          await getMaintenancePlanItems(plan.id);

        return {
          ...plan,
          items,
          color: savedColors[plan.id] ?? 'green'
        };
      })
    );
  } catch (error) {
    errorMessage.value =
      error.message ||
      'Não foi possível carregar os planos de manutenção.';

    console.error(error);
  } finally {
    isLoading.value = false;
  }
}

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

async function handleSavePlan(formData) {
  try {
    if (editingPlan.value) {
      await updatePlan(formData);
      return;
    }

    await createPlan(formData);
  } catch (error) {
    errorMessage.value =
      error.message ||
      'Não foi possível salvar o plano de manutenção.';

    console.error(error);
  }
}
async function createPlan(formData) {
  const createdPlan =
    await createMaintenancePlan({
      name: formData.name,
      description: formData.description || null
    });

  const createdItems = [];

  for (const item of formData.items) {
    const createdItem =
      await addMaintenancePlanItem(
        createdPlan.id,
        {
          serviceDescription:
            item.serviceDescription,

          intervalHours:
            item.intervalHours,

          alertMarginHours:
            item.alertMarginHours
        }
      );

    createdItems.push(createdItem);
  }
  savePlanColor(
    createdPlan.id,
    formData.color
  );

  maintenancePlans.value.unshift({
    ...createdPlan,
    color: formData.color,
    items: createdItems
  });

  closePlanModal();
}

async function updatePlan(formData) {
  const planId = editingPlan.value.id;
  const updatedPlan =
    await updateMaintenancePlan(
      planId,
      {
        name: formData.name,
        description: formData.description || null
      }
    );

  const submittedItemIds = formData.items
    .filter((item) => item.id !== null)
    .map((item) => item.id);

  const removedItems =
    editingPlan.value.items.filter(
      (item) =>
        !submittedItemIds.includes(item.id)
    );

  for (const item of removedItems) {
    await deleteMaintenancePlanItem(item.id);
  }
  const updatedItems = [];

  for (const item of formData.items) {
    if (item.id !== null) {
      const updatedItem =
        await updateMaintenancePlanItem(
          item.id,
          {
            serviceDescription:
              item.serviceDescription,

            intervalHours:
              item.intervalHours,

            alertMarginHours:
              item.alertMarginHours
          }
        );
      updatedItems.push(updatedItem);
      continue;
    }
    const createdItem =
      await addMaintenancePlanItem(
        planId,
        {
          serviceDescription:
            item.serviceDescription,

          intervalHours:
            item.intervalHours,

          alertMarginHours:
            item.alertMarginHours
        }
      );
    updatedItems.push(createdItem);
  }
  savePlanColor(
    planId,
    formData.color
  );

  const index =
    maintenancePlans.value.findIndex(
      (plan) => plan.id === planId
    );

  if (index !== -1) {
    maintenancePlans.value[index] = {
      ...maintenancePlans.value[index],
      ...updatedPlan,
      color: formData.color,
      items: updatedItems
    };
  }
  closePlanModal();
}

async function togglePlanStatus(plan) {
  try {
    const updatedPlan =
      await toggleMaintenancePlanStatus(plan.id);

    const index =
      maintenancePlans.value.findIndex(
        (item) => item.id === plan.id
      );

    if (index === -1) {
      return;
    }

    maintenancePlans.value[index] = {
      ...maintenancePlans.value[index],
      ...updatedPlan
    };
  } catch (error) {
    errorMessage.value =
      error.message ||
      'Não foi possível alterar o status do plano.';

    console.error(error);
  }
}

async function loadCurrentUser() {
  try {
    currentUser.value = await getCurrentUser();
  } catch (error) {
    console.error(
      'Erro ao carregar usuário autenticado:',
      error
    );
  }
}
onMounted(() => {
  loadMaintenancePlans();
  loadCurrentUser();
});
</script>


<template>
  <div class="maintenance-plans-page">

    <PageHeader title="Planos de Manutenção" description="Organize as rotinas preventivas e os serviços de manutenção.">
      <template #actions>
        <template v-if="canManagePlans">
          <button class="new-plan-button" type="button" @click="openCreateModal">
            <Plus :size="18" />
            Novo Plano
          </button>
        </template>
      </template>
    </PageHeader>

    <MaintenancePlanFilters v-model:search="searchTerm" v-model:status="selectedStatus" />

    <MaintenancePlanTable :plans="filteredMaintenancePlans" :can-manage="canManagePlans" @edit="openEditModal"
      @toggle-status="togglePlanStatus" />

    <MaintenancePlanFormModal v-if="isPlanModalOpen" :plan="editingPlan" @close="closePlanModal"
      @save="handleSavePlan" />

  </div>
</template>