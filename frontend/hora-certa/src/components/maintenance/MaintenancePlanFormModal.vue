<script setup>
import { computed, ref } from 'vue';

import {
  Plus,
  Trash2
} from 'lucide-vue-next';

import BaseModal from '../common/BaseModal.vue';

const planColors = [
  { value: 'green', label: 'Verde' },
  { value: 'blue', label: 'Azul' },
  { value: 'yellow', label: 'Amarelo' },
  { value: 'orange', label: 'Laranja' },
  { value: 'red', label: 'Vermelho' },
  { value: 'purple', label: 'Roxo' }
];

const props = defineProps({
  plan: {
    type: Object,
    default: null
  }
});


const emit = defineEmits([
  'close',
  'save'
]);

const isEditing = computed(() => {
  return props.plan !== null;
});

function createEmptyItem() {
  return {
    id: null,
    serviceDescription: '',
    intervalHours: '',
    alertMarginHours: 20
  };
}


const planForm = ref({
  name: props.plan?.name ?? '',

  description: props.plan?.description ?? '',

  color: props.plan?.color ?? 'green',

  items: props.plan?.items?.length
    ? props.plan.items.map((item) => ({
      ...item
    }))
    : [
      createEmptyItem()
    ]
});

function addItem() {
  planForm.value.items.push(
    createEmptyItem()
  );
}

function removeItem(index) {

  if (planForm.value.items.length === 1) {
    return;
  }

  planForm.value.items.splice(index, 1);
}

function closeModal() {
  emit('close');
}

function handleSubmit() {

  const formData = {
    name: planForm.value.name.trim(),

    description:
      planForm.value.description.trim(),

    color: planForm.value.color,

    items: planForm.value.items.map((item) => ({
      id: item.id,

      serviceDescription:
        item.serviceDescription.trim(),

      intervalHours:
        Number(item.intervalHours),

      alertMarginHours:
        Number(item.alertMarginHours)
    }))
  };


  emit('save', formData);
}
</script>

<template>
  <BaseModal size="lg" :title="isEditing
    ? 'Editar Plano de Manutenção'
    : 'Novo Plano de Manutenção'
    " :description="isEditing
      ? 'Atualize o plano e seus serviços.'
      : 'Defina o plano e os serviços de manutenção.'
      " @close="closeModal">

    <form id="maintenance-plan-form" class="maintenance-plan-form" @submit.prevent="handleSubmit">
      <div class="plan-form-main">

        <div class="form-group">
          <label for="plan-name">
            Nome *
          </label>

          <input id="plan-name" v-model="planForm.name" type="text" maxlength="100"
            placeholder="Ex: Revisão Preventiva 250h" required />
        </div>

        <div class="form-group">
          <label for="plan-description">
            Descrição
          </label>

          <textarea id="plan-description" v-model="planForm.description" rows="3"
            placeholder="Descreva o objetivo deste plano..."></textarea>
        </div>

        <div class="plan-color-field">
          <div class="plan-color-heading">
            <label>Cor de identificação</label>

            <span>
              Escolha uma cor para identificar este plano.
            </span>
          </div>

          <div class="plan-color-options">
            <button v-for="color in planColors" :key="color.value" type="button" class="plan-color-option" :class="[
              `plan-color-option--${color.value}`,
              {
                'plan-color-option--selected':
                  planForm.color === color.value
              }
            ]" :title="color.label" @click="planForm.color = color.value">
              <span class="plan-color-circle"></span>
            </button>
          </div>
        </div>

      </div>

      <div class="plan-items-editor">

        <div class="plan-items-editor-header">

          <div>
            <h3>
              Serviços de manutenção
            </h3>

            <p>
              Defina os serviços, intervalos e alertas deste plano.
            </p>
          </div>

          <button type="button" class="add-plan-item-button" @click="addItem">
            <Plus :size="16" />
            Adicionar serviço
          </button>

        </div>

        <div v-for="(item, index) in planForm.items" :key="item.id ?? index" class="plan-form-item">

          <div class="plan-form-item-header">

            <strong>
              Serviço {{ index + 1 }}
            </strong>

            <button type="button" class="remove-plan-item-button" :disabled="planForm.items.length === 1"
              aria-label="Remover serviço" @click="removeItem(index)">
              <Trash2 :size="16" />
            </button>

          </div>

          <div class="plan-item-form-grid">

            <div class="form-group service-field">
              <label>
                Serviço *
              </label>

              <input v-model="item.serviceDescription" type="text" maxlength="200"
                placeholder="Ex: Troca de óleo do motor" required />
            </div>

            <div class="form-group">
              <label>
                Intervalo *
              </label>

              <div class="hours-input">
                <input v-model.number="item.intervalHours" type="number" min="0.01" step="0.01" placeholder="250"
                  required />

                <span>h</span>
              </div>
            </div>

            <div class="form-group">
              <label>
                Margem de alerta *
              </label>

              <div class="hours-input">
                <input v-model.number="item.alertMarginHours" type="number" min="0" step="0.01" required />

                <span>h</span>
              </div>
            </div>

          </div>

        </div>

      </div>
    </form>

    <template #footer>
      <button type="button" class="cancel-button" @click="closeModal">
        Cancelar
      </button>

      <button type="submit" form="maintenance-plan-form" class="save-button">
        {{
          isEditing
            ? 'Salvar Alterações'
            : 'Cadastrar Plano'
        }}
      </button>
    </template>

  </BaseModal>
</template>