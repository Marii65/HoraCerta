<script setup>
import { computed, ref } from 'vue';

import { equipmentCategories } from '../../utils/equipmentCategories';
import { getEquipmentIcon } from '../../utils/equipmentIcons';

import BaseModal from '../common/BaseModal.vue';

const props = defineProps({
  equipment: {
    type: Object,
    default: null
  }
});

const emit = defineEmits([
  'close',
  'save'
]);

const isEditing = computed(() => {
  return props.equipment !== null;
});

const equipmentForm = ref({
  code: props.equipment?.code ?? '',
  name: props.equipment?.name ?? '',
  model: props.equipment?.model ?? '',
  serialNumber: props.equipment?.serialNumber ?? '',
  category: props.equipment?.category ?? '',
  currentHorometer: props.equipment?.currentHorometer ?? 0
});

function closeModal() {
  emit('close');
}


function handleSubmit() {

  const formData = {
    code: equipmentForm.value.code.trim(),
    name: equipmentForm.value.name.trim(),
    model: equipmentForm.value.model.trim(),
    serialNumber: equipmentForm.value.serialNumber.trim(),
    category: equipmentForm.value.category,

    currentHorometer:
      Number(equipmentForm.value.currentHorometer)
  };

  emit('save', formData);
}
</script>

<template>
  <BaseModal :title="isEditing ? 'Editar Equipamento' : 'Novo Equipamento'" :description="isEditing
      ? 'Atualize as informações do equipamento.'
      : 'Cadastre um novo equipamento na frota.'
    " @close="closeModal">

    <form id="equipment-form" class="equipment-form" @submit.prevent="handleSubmit">
      <div class="form-grid">
        <div class="form-group">
          <label for="equipment-code">
            Código *
          </label>

          <input id="equipment-code" v-model="equipmentForm.code" type="text" maxlength="50" placeholder="Ex: ESC-01"
            required />
        </div>

        <div class="form-group">
          <label for="equipment-name">
            Nome *
          </label>

          <input id="equipment-name" v-model="equipmentForm.name" type="text" maxlength="100"
            placeholder="Ex: Escavadeira Hidráulica" required />
        </div>

        <div class="form-group">
          <label for="equipment-model">
            Modelo
          </label>

          <input id="equipment-model" v-model="equipmentForm.model" type="text" maxlength="50"
            placeholder="Ex: CAT 320" />
        </div>

        <div class="form-group">
          <label for="equipment-serial-number">
            Número de série
          </label>

          <input id="equipment-serial-number" v-model="equipmentForm.serialNumber" type="text" maxlength="50"
            placeholder="Ex: CAT0320ABC123" />
        </div>

        <div class="form-group">
          <label for="equipment-category">
            Categoria
          </label>

          <select id="equipment-category" v-model="equipmentForm.category">
            <option value="">
              Selecione uma categoria
            </option>

            <option v-for="category in equipmentCategories" :key="category" :value="category">
              {{ category }}
            </option>
          </select>
        </div>

        <div class="form-group">
          <label for="equipment-horometer">
            Horímetro atual *
          </label>

          <div class="horometer-input">
            <input id="equipment-horometer" v-model.number="equipmentForm.currentHorometer" type="number" min="0"
              step="0.01" required />

            <span>h</span>
          </div>
        </div>

      </div>

      <div v-if="equipmentForm.category" class="category-preview">
        <div class="category-preview-image">
          <img :src="getEquipmentIcon(equipmentForm.category)" :alt="equipmentForm.category" />
        </div>

        <div class="category-preview-info">
          <span>
            Categoria selecionada
          </span>

          <strong>
            {{ equipmentForm.category }}
          </strong>

          <small>
            O ícone será atribuído automaticamente.
          </small>
        </div>
      </div>

    </form>


    <template #footer>
      <button type="button" class="cancel-button" @click="closeModal">
        Cancelar
      </button>

      <button type="submit" form="equipment-form" class="save-button">
        {{
          isEditing
            ? 'Salvar Alterações'
            : 'Cadastrar Equipamento'
        }}
      </button>
    </template>

  </BaseModal>
</template>