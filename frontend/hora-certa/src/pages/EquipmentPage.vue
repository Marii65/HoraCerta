<script setup>
import { computed, ref } from 'vue';
import { Plus } from 'lucide-vue-next';

import EquipmentFilters from '../components/equipment/EquipmentFilters.vue';
import EquipmentTable from '../components/equipment/EquipmentTable.vue';
import EquipmentFormModal from '../components/equipment/EquipmentFormModal.vue';

import { equipmentMocks } from '../mocks/equipments';

import '../styles/pages/equipment.css';
import PageHeader from '../components/common/PageHeader.vue';

// Dados temporários.
// Futuramente serão carregados pela API.
const equipments = ref([...equipmentMocks]);

const searchTerm = ref('');
const selectedCategory = ref('');
const selectedStatus = ref('');


const filteredEquipments = computed(() => {
  const search = searchTerm.value
    .trim()
    .toLowerCase();

  return equipments.value.filter((equipment) => {

    const matchesSearch =
      !search ||
      equipment.code.toLowerCase().includes(search) ||
      equipment.name.toLowerCase().includes(search) ||
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


const isEquipmentModalOpen = ref(false);

const editingEquipment = ref(null);

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

/*
 * Recebe os dados enviados pelo formulário.
 *
 * Enquanto não conectamos a API, adicionamos
 * ou atualizamos o equipamento localmente.
 */
function handleSaveEquipment(formData) {

  if (editingEquipment.value) {
    updateEquipment(formData);
    return;
  }

  createEquipment(formData);
}


/*
 * Cadastra temporariamente um equipamento.
 *
 * Depois esta função será substituída
 * por uma chamada POST para a API.
 */
function createEquipment(formData) {

  const newEquipment = {
    id: Date.now(),

    ...formData,

    status: 'ACTIVE'
  };

  equipments.value.unshift(newEquipment);

  closeEquipmentModal();
}


/*
 * Atualiza temporariamente um equipamento.
 *
 * Depois esta função será substituída
 * por uma chamada PUT/PATCH para a API.
 */
function updateEquipment(formData) {

  const index = equipments.value.findIndex(
    (equipment) =>
      equipment.id === editingEquipment.value.id
  );

  if (index === -1) {
    return;
  }

  equipments.value[index] = {
    ...equipments.value[index],
    ...formData
  };

  closeEquipmentModal();
}

/*
 * Alterna entre equipamento ativo e inativo.
 *
 * Futuramente essa ação será enviada
 * para o backend.
 */
function toggleEquipmentStatus(equipment) {

  const index = equipments.value.findIndex(
    (item) => item.id === equipment.id
  );

  if (index === -1) {
    return;
  }

  const currentEquipment =
    equipments.value[index];

  equipments.value[index] = {
    ...currentEquipment,

    status:
      currentEquipment.status === 'ACTIVE'
        ? 'INACTIVE'
        : 'ACTIVE'
  };
}
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

    <EquipmentTable :equipments="filteredEquipments" @edit="openEditModal" @toggle-status="toggleEquipmentStatus" />

    <EquipmentFormModal v-if="isEquipmentModalOpen" :equipment="editingEquipment" @close="closeEquipmentModal"
      @save="handleSaveEquipment" />

  </div>
</template>