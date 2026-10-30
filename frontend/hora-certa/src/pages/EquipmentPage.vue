<script setup>
import { ref, computed } from 'vue';

import {
  Plus,
  Search,
  SlidersHorizontal,
  MoreVertical
} from 'lucide-vue-next';

import {
  getEquipmentIcon
} from '../utils/equipmentIcons';
import { equipmentMocks } from '../mocks/equipments';
import { equipmentCategories } from '../utils/equipmentCategories';

const equipments = ref([...equipmentMocks]);

const searchTerm = ref('');
const selectedCategory = ref('');
const selectedStatus = ref('');

const filteredEquipments = computed(() => {
  return equipments.value.filter((equipment) => {

    const search = searchTerm.value
      .trim()
      .toLowerCase();

    const matchesSearch =
      !search ||
      equipment.code.toLowerCase().includes(search) ||
      equipment.name.toLowerCase().includes(search) ||
      equipment.model.toLowerCase().includes(search);

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

const equipmentForm = ref({
  code: '',
  name: '',
  model: '',
  serialNumber: '',
  category: '',
  currentHorometer: 0
});

function openEquipmentModal() {
  isEquipmentModalOpen.value = true;
}

function closeEquipmentModal() {
  isEquipmentModalOpen.value = false;

  resetEquipmentForm();
}

function getStatusLabel(status) {
  return status === 'ACTIVE'
    ? 'Ativo'
    : 'Inativo';
}

function handleCreateEquipment() {
  const newEquipment = {
    id: Date.now(),

    code: equipmentForm.value.code.trim(),
    name: equipmentForm.value.name.trim(),
    model: equipmentForm.value.model.trim(),
    serialNumber: equipmentForm.value.serialNumber.trim(),
    category: equipmentForm.value.category,

    currentHorometer:
      Number(equipmentForm.value.currentHorometer),

    status: 'ACTIVE'
  };

  equipments.value.unshift(newEquipment);

  closeEquipmentModal();
}

function resetEquipmentForm() {
  equipmentForm.value = {
    code: '',
    name: '',
    model: '',
    serialNumber: '',
    category: '',
    currentHorometer: 0
  };
}

</script>

<template>
  <div class="equipment-page">

    <header class="page-header">

      <div>
        <h1>Equipamentos</h1>

        <p>
          Gerencie os equipamentos cadastrados na sua frota.
        </p>
      </div>

      <button class="new-equipment-button" type="button" @click="openEquipmentModal">
        <Plus :size="18" />
        Novo Equipamento
      </button>

    </header>

    <section class="equipment-filters">

      <div class="equipment-search">
        <Search :size="18" />

        <input v-model="searchTerm" type="text" placeholder="Buscar por código, nome ou modelo..." />
      </div>

      <div class="filter-actions">

        <select v-model="selectedCategory">
          <option value="">
            Todas as categorias
          </option>

          <option v-for="category in equipmentCategories" :key="category" :value="category">
            {{ category }}
          </option>
        </select>
        <button class="filter-button" type="button" aria-label="Mais filtros">
          <SlidersHorizontal :size="18" />
        </button>

      </div>

    </section>

    <section class="equipment-card">

      <div class="equipment-table-wrapper">

        <table class="equipment-table">

          <thead>
            <tr>
              <th>Equipamento</th>
              <th>Categoria</th>
              <th>Horímetro atual</th>
              <th>Status</th>
              <th></th>
            </tr>
          </thead>

          <tbody>

            <tr v-for="equipment in filteredEquipments" :key="equipment.id">

              <td>
                <div class="equipment-cell">

                  <div class="equipment-thumbnail">
                    <img :src="getEquipmentIcon(equipment.category)" :alt="equipment.category" />
                  </div>

                  <div class="equipment-info">
                    <strong>
                      {{ equipment.code }}
                    </strong>

                    <span>
                      {{ equipment.name }}
                    </span>

                    <small>
                      {{ equipment.model }}
                    </small>
                  </div>

                </div>
              </td>

              <td>
                {{ equipment.category }}
              </td>

              <td>
                <strong class="horometer-value">
                  {{ equipment.currentHorometer }} h
                </strong>
              </td>

              <td>
                <span class="equipment-status" :class="{
                  'equipment-status--active':
                    equipment.status === 'ACTIVE',

                  'equipment-status--inactive':
                    equipment.status === 'INACTIVE'
                }">
                  <span class="status-dot"></span>

                  {{ getStatusLabel(equipment.status) }}
                </span>
              </td>

              <td>
                <button class="more-button" type="button" aria-label="Opções do equipamento">
                  <MoreVertical :size="18" />
                </button>
              </td>

            </tr>

            <tr v-if="filteredEquipments.length === 0">
              <td colspan="5" class="empty-state">
                <Search :size="26" />

                <strong>
                  Nenhum equipamento encontrado
                </strong>

                <span>
                  Tente alterar os termos da busca ou os filtros.
                </span>
              </td>
            </tr>
          </tbody>

          <div v-if="isEquipmentModalOpen" class="modal-overlay" @click.self="closeEquipmentModal">
            <section class="equipment-modal">

              <header class="modal-header">
                <div>
                  <h2>Novo Equipamento</h2>
                  <p>Cadastre um novo equipamento na frota.</p>
                </div>

                <button type="button" class="modal-close" @click="closeEquipmentModal">
                  ×
                </button>
              </header>

              <form class="equipment-form" @submit.prevent="handleCreateEquipment">

                <div class="form-grid">

                  <div class="form-group">
                    <label for="code">
                      Código *
                    </label>

                    <input id="code" v-model="equipmentForm.code" type="text" placeholder="Ex: ESC-01" />
                  </div>

                  <div class="form-group">
                    <label for="name">
                      Nome *
                    </label>

                    <input id="name" v-model="equipmentForm.name" type="text"
                      placeholder="Ex: Escavadeira Hidráulica" />
                  </div>

                  <div class="form-group">
                    <label for="model">
                      Modelo
                    </label>

                    <input id="model" v-model="equipmentForm.model" type="text" placeholder="Ex: CAT 320" />
                  </div>

                  <div class="form-group">
                    <label for="serialNumber">
                      Número de série
                    </label>

                    <input id="serialNumber" v-model="equipmentForm.serialNumber" type="text"
                      placeholder="Ex: CAT0320ABC123" />
                  </div>

                  <div class="form-group">
                    <label for="category">
                      Categoria
                    </label>

                    <select id="category" v-model="equipmentForm.category">
                      <option value="">
                        Selecione uma categoria
                      </option>

                      <option v-for="category in equipmentCategories" :key="category" :value="category">
                        {{ category }}
                      </option>
                    </select>
                  </div>

                  <div class="form-group">
                    <label for="currentHorometer">
                      Horímetro atual *
                    </label>

                    <div class="horometer-input">
                      <input id="currentHorometer" v-model.number="equipmentForm.currentHorometer" type="number" min="0"
                        step="0.01" />

                      <span>h</span>
                    </div>
                  </div>

                </div>

                <!-- Preview automático da categoria -->
                <div v-if="equipmentForm.category" class="category-preview">
                  <div class="category-preview-image">
                    <img :src="getEquipmentIcon(equipmentForm.category)" :alt="equipmentForm.category" />
                  </div>

                  <div>
                    <span>Categoria selecionada</span>

                    <strong>
                      {{ equipmentForm.category }}
                    </strong>

                    <small>
                      O ícone será atribuído automaticamente.
                    </small>
                  </div>
                </div>

                <footer class="modal-footer">

                  <button type="button" class="cancel-button" @click="closeEquipmentModal">
                    Cancelar
                  </button>

                  <button type="submit" class="save-button">
                    Cadastrar Equipamento
                  </button>

                </footer>

              </form>

            </section>
          </div>


        </table>

      </div>

    </section>

  </div>
</template>
<style scoped>
.equipment-page {
  display: flex;
  flex-direction: column;
  gap: 18px;
}


/*CABEÇALHO*/

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
}

.page-header h1 {
  margin-bottom: 5px;

  color: var(--color-text-primary);

  font-size: 25px;
  font-weight: 700;
}

.page-header p {
  color: var(--color-text-secondary);

  font-size: 13px;
}

.new-equipment-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;

  min-height: 42px;

  padding: 0 17px;

  border-radius: var(--radius-md);

  background-color: var(--color-primary-dark);
  color: white;

  font-size: 12px;
  font-weight: 600;

  transition:
    background-color 0.2s ease,
    transform 0.2s ease;
}

.new-equipment-button:hover {
  background-color: var(--color-primary);
  transform: translateY(-1px);
}


/*FILTROS*/

.equipment-filters {
  display: flex;
  align-items: center;
  justify-content: space-between;

  gap: 18px;

  padding: 14px;

  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);

  background-color: var(--color-surface);

  box-shadow: var(--shadow-sm);
}

.equipment-search {
  display: flex;
  align-items: center;
  gap: 10px;

  width: min(430px, 45%);

  padding: 0 13px;

  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);

  color: var(--color-text-muted);
}

.equipment-search input {
  width: 100%;
  height: 39px;

  border: none;
  outline: none;

  background: transparent;

  color: var(--color-text-primary);

  font-size: 12px;
}

.equipment-search input::placeholder {
  color: var(--color-text-muted);
}

.filter-actions {
  display: flex;
  align-items: center;
  gap: 9px;
}

.filter-actions select {
  height: 39px;

  padding: 0 32px 0 12px;

  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);

  outline: none;

  background-color: var(--color-surface);

  color: var(--color-text-secondary);

  font-size: 11px;
}

.filter-button {
  display: flex;
  align-items: center;
  justify-content: center;

  width: 39px;
  height: 39px;

  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);

  background-color: var(--color-surface);

  color: var(--color-text-secondary);
}


/*CARD / TABELA*/

.equipment-card {
  overflow: hidden;

  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);

  background-color: var(--color-surface);

  box-shadow: var(--shadow-sm);
}

.equipment-table-wrapper {
  width: 100%;
  overflow-x: auto;
}

.equipment-table {
  width: 100%;

  border-collapse: collapse;
}

.equipment-table thead {
  background-color: var(--color-surface-secondary);
}

.equipment-table th {
  padding: 13px 18px;

  color: var(--color-text-muted);

  font-size: 10px;
  font-weight: 600;

  text-align: left;
  text-transform: uppercase;

  letter-spacing: 0.3px;
}

.equipment-table td {
  padding: 15px 18px;

  border-top: 1px solid var(--color-border);

  color: var(--color-text-secondary);

  font-size: 12px;

  vertical-align: middle;
}

.equipment-table tbody tr {
  transition: background-color 0.2s ease;
}

.equipment-table tbody tr:hover {
  background-color: var(--color-surface-secondary);
}


/*EQUIPAMENTO*/

.equipment-cell {
  display: flex;
  align-items: center;

  gap: 13px;

  min-width: 280px;
}

.equipment-thumbnail {
  display: flex;
  align-items: center;
  justify-content: center;

  width: 58px;
  height: 54px;

  flex-shrink: 0;
}

.equipment-thumbnail img {
  display: block;

  width: 54px;
  height: 50px;

  object-fit: contain;
}

.equipment-info {
  display: flex;
  flex-direction: column;

  gap: 3px;
}

.equipment-info strong {
  color: var(--color-text-primary);

  font-size: 13px;
}

.equipment-info span {
  color: var(--color-text-secondary);

  font-size: 11px;
}

.equipment-info small {
  color: var(--color-text-muted);

  font-size: 10px;
}


/*HORÍMETRO*/

.horometer-value {
  color: var(--color-text-primary);

  font-size: 12px;
  font-weight: 600;
}


/*STATUS*/

.equipment-status {
  display: inline-flex;
  align-items: center;

  gap: 7px;

  padding: 6px 10px;

  border-radius: 999px;

  font-size: 11px;
  font-weight: 600;
}

.status-dot {
  width: 6px;
  height: 6px;

  border-radius: 50%;

  background-color: currentColor;
}

.equipment-status--active {
  background-color: var(--color-success-light);
  color: var(--color-success);
}

.equipment-status--inactive {
  background-color: #eef1f0;
  color: var(--color-text-secondary);
}


/*MENU*/

.more-button {
  display: flex;
  align-items: center;
  justify-content: center;

  width: 34px;
  height: 34px;

  border-radius: 50%;

  background: transparent;

  color: var(--color-text-muted);

  transition:
    background-color 0.2s ease,
    color 0.2s ease;
}

.empty-state {
  height: 220px;

  text-align: center;
}

.empty-state svg {
  margin-bottom: 10px;

  color: var(--color-text-muted);
}

.empty-state strong {
  display: block;

  margin-bottom: 5px;

  color: var(--color-text-primary);

  font-size: 13px;
}

.empty-state span {
  display: block;

  color: var(--color-text-muted);

  font-size: 11px;
}

.more-button:hover {
  background-color: var(--color-background);
  color: var(--color-primary);
}


/*RESPONSIVIDADE*/

@media (max-width: 900px) {
  .equipment-filters {
    align-items: stretch;
    flex-direction: column;
  }

  .equipment-search {
    width: 100%;
  }

  .filter-actions {
    flex-wrap: wrap;
  }

  .equipment-table {
    min-width: 800px;
  }
}

@media (max-width: 600px) {
  .page-header {
    align-items: flex-start;
    flex-direction: column;
  }
}

/*MODAL*/

.modal-overlay {
  position: fixed;
  z-index: 1000;
  inset: 0;

  display: flex;
  align-items: center;
  justify-content: center;

  padding: 24px;

  background-color: rgba(10, 25, 21, 0.45);
  backdrop-filter: blur(2px);
}

.equipment-modal {
  width: min(680px, 100%);
  max-height: 90vh;

  overflow-y: auto;

  border-radius: var(--radius-xl);

  background-color: var(--color-surface);

  box-shadow: 0 24px 60px rgba(10, 25, 21, 0.18);
}


/* HEADER */

.modal-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;

  padding: 22px 24px;

  border-bottom: 1px solid var(--color-border);
}

.modal-header h2 {
  margin-bottom: 4px;

  color: var(--color-text-primary);

  font-size: 19px;
}

.modal-header p {
  color: var(--color-text-secondary);

  font-size: 12px;
}

.modal-close {
  display: flex;
  align-items: center;
  justify-content: center;

  width: 32px;
  height: 32px;

  border-radius: 50%;

  background: transparent;

  color: var(--color-text-secondary);

  font-size: 24px;
  line-height: 1;
}

.modal-close:hover {
  background-color: var(--color-background);
}


/* FORM */

.equipment-form {
  padding: 22px 24px 24px;
}

.form-grid {
  display: grid;

  grid-template-columns: repeat(2, minmax(0, 1fr));

  gap: 18px;
}

.form-group {
  display: flex;
  flex-direction: column;

  gap: 7px;
}

.form-group label {
  color: var(--color-text-primary);

  font-size: 11px;
  font-weight: 600;
}

.form-group input,
.form-group select {
  width: 100%;
  height: 42px;

  padding: 0 12px;

  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);

  outline: none;

  background-color: var(--color-surface);

  color: var(--color-text-primary);

  font-size: 12px;

  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease;
}

.form-group input:focus,
.form-group select:focus {
  border-color: var(--color-primary);

  box-shadow: 0 0 0 3px var(--color-primary-light);
}

.form-group input::placeholder {
  color: var(--color-text-muted);
}


/* HORÍMETRO */

.horometer-input {
  position: relative;
}

.horometer-input input {
  padding-right: 38px;
}

.horometer-input span {
  position: absolute;
  top: 50%;
  right: 13px;

  transform: translateY(-50%);

  color: var(--color-text-muted);

  font-size: 12px;
  font-weight: 600;
}


/* PREVIEW DA CATEGORIA */

.category-preview {
  display: flex;
  align-items: center;

  gap: 16px;

  margin-top: 20px;
  padding: 14px 16px;

  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);

  background-color: var(--color-surface-secondary);
}

.category-preview-image {
  display: flex;
  align-items: center;
  justify-content: center;

  width: 75px;
  height: 65px;

  flex-shrink: 0;
}

.category-preview-image img {
  width: 70px;
  height: 60px;

  object-fit: contain;
}

.category-preview div:last-child {
  display: flex;
  flex-direction: column;

  gap: 3px;
}

.category-preview span {
  color: var(--color-text-muted);

  font-size: 10px;
}

.category-preview strong {
  color: var(--color-text-primary);

  font-size: 13px;
}

.category-preview small {
  color: var(--color-text-secondary);

  font-size: 10px;
}


/* FOOTER */

.modal-footer {
  display: flex;
  justify-content: flex-end;

  gap: 10px;

  margin-top: 24px;
  padding-top: 18px;

  border-top: 1px solid var(--color-border);
}

.cancel-button,
.save-button {
  min-height: 40px;

  padding: 0 17px;

  border-radius: var(--radius-md);

  font-size: 11px;
  font-weight: 600;
}

.cancel-button {
  border: 1px solid var(--color-border);

  background-color: var(--color-surface);

  color: var(--color-text-secondary);
}

.save-button {
  background-color: var(--color-primary-dark);

  color: white;
}

.save-button:hover {
  background-color: var(--color-primary);
}


/* RESPONSIVO */

@media (max-width: 600px) {
  .modal-overlay {
    padding: 12px;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>