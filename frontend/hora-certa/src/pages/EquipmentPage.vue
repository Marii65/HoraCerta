<script setup>
import { computed, onMounted, ref } from 'vue';

import { Plus } from 'lucide-vue-next';

import EquipmentFilters from '../components/equipment/EquipmentFilters.vue';
import EquipmentTable from '../components/equipment/EquipmentTable.vue';
import EquipmentFormModal from '../components/equipment/EquipmentFormModal.vue';
import PageHeader from '../components/common/PageHeader.vue';
import EquipmentPlansModal from '../components/equipment/EquipmentPlansModal.vue';

import { getEquipments, createEquipment, updateEquipment } from '../services/equipmentService';

import '../styles/pages/equipment.css';



const equipments = ref([]);

const isLoading = ref(false);
const errorMessage = ref('');

const searchTerm = ref('');
const selectedCategory = ref('');
const selectedStatus = ref('');

const isEquipmentModalOpen = ref(false);
const editingEquipment = ref(null);

const selectedEquipmentForPlans = ref(null);

async function loadEquipments() {
  isLoading.value = true;
  errorMessage.value = '';

  try {
    equipments.value = await getEquipments();
  } catch (error) {
    errorMessage.value =
      error.message ||
      'Não foi possível carregar os equipamentos.';
  } finally {
    isLoading.value = false;
  }
}

const filteredEquipments = computed(() => {
  const search = searchTerm.value
    .trim()
    .toLowerCase();

  return equipments.value.filter((equipment) => {
    const matchesSearch =
      !search ||
      equipment.code
        ?.toLowerCase()
        .includes(search) ||
      equipment.name
        ?.toLowerCase()
        .includes(search) ||
      (equipment.model ?? '')
        .toLowerCase()
        .includes(search);

    const matchesCategory =
      !selectedCategory.value ||
      equipment.category === selectedCategory.value;

    const matchesStatus =
      !selectedStatus.value ||
      equipment.status === selectedStatus.value;

    return (
      matchesSearch &&
      matchesCategory &&
      matchesStatus
    );
  });
});

function openCreateModal() {
  editingEquipment.value = null;
  isEquipmentModalOpen.value = true;
}

function openEditModal(equipment) {
  editingEquipment.value = equipment;
  isEquipmentModalOpen.value = true;
}

function closeEquipmentModal() {
  isEquipmentModalOpen.value = false;
  editingEquipment.value = null;
}

async function handleSaveEquipment(formData) {
  errorMessage.value = '';

  try {
    if (editingEquipment.value) {
      await handleUpdateEquipment(formData);
      return;
    }
    await handleCreateEquipment(formData);
  } catch (error) {
    errorMessage.value =
      error.message ||
      'Não foi possível salvar o equipamento.';
  }
}

async function handleCreateEquipment(formData) {
  const payload = {
    code: formData.code,
    name: formData.name,
    model: formData.model || null,
    category: formData.category || null,
    serialNumber: formData.serialNumber || null
  };
  const createdEquipment =
    await createEquipment(payload);
  equipments.value.unshift(createdEquipment);
  closeEquipmentModal();
}

async function handleUpdateEquipment(formData) {
  const payload = {
    code: formData.code,
    name: formData.name,
    category: formData.category || null,
    model: formData.model || null,
    serialNumber: formData.serialNumber || null,
    status:
      formData.status ||
      editingEquipment.value.status
  };
  const updatedEquipment =
    await updateEquipment(
      editingEquipment.value.id,
      payload
    );
  const index = equipments.value.findIndex(
    (equipment) =>
      equipment.id === updatedEquipment.id
  );
  if (index !== -1) {
    equipments.value[index] =
      updatedEquipment;
  }
  closeEquipmentModal();
}

async function toggleEquipmentStatus(equipment) {
  errorMessage.value = '';

  const newStatus =
    equipment.status === 'ACTIVE'
      ? 'INACTIVE'
      : 'ACTIVE';

  const payload = {
    code: equipment.code,
    name: equipment.name,
    category: equipment.category || null,
    model: equipment.model || null,
    serialNumber: equipment.serialNumber || null,
    status: newStatus
  };

  try {
    const updatedEquipment =
      await updateEquipment(
        equipment.id,
        payload
      );

    const index = equipments.value.findIndex(
      (item) => item.id === equipment.id
    );

    if (index !== -1) {
      equipments.value[index] =
        updatedEquipment;
    }
  } catch (error) {
    errorMessage.value =
      error.message ||
      'Não foi possível alterar o status do equipamento.';
  }
}

function openEquipmentPlans(equipment) {
  selectedEquipmentForPlans.value =
    equipment;
}

function closeEquipmentPlans() {
  selectedEquipmentForPlans.value =
    null;
}

onMounted(() => {
  loadEquipments();
});
</script>

<template>
  <div class="equipment-page">
    <PageHeader title="Equipamentos" description="Gerencie os equipamentos cadastrados.">
      <template #actions>
        <button class="new-equipment-button" type="button" @click="openCreateModal">
          <Plus :size="18" />
          Novo Equipamento
        </button>
      </template>
    </PageHeader>

    <EquipmentFilters v-model:search="searchTerm" v-model:category="selectedCategory" v-model:status="selectedStatus" />
    <p v-if="errorMessage" class="equipment-error">
      {{ errorMessage }}
    </p>

    <p v-if="isLoading" class="equipment-loading">
      Carregando equipamentos...
    </p>

    <EquipmentTable v-else :equipments="filteredEquipments" @edit="openEditModal" @toggle-status="toggleEquipmentStatus"
      @manage-plans="openEquipmentPlans" />

    <EquipmentFormModal v-if="isEquipmentModalOpen" :equipment="editingEquipment" @close="closeEquipmentModal"
      @save="handleSaveEquipment" />

    <EquipmentPlansModal v-if="selectedEquipmentForPlans" :equipment="selectedEquipmentForPlans"
      @close="closeEquipmentPlans" />
  </div>
</template>