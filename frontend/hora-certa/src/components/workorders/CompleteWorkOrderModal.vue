<script setup>
import { computed, reactive } from 'vue';
import {
  CheckCircle2,
  Gauge
} from 'lucide-vue-next';

import BaseModal from '../common/BaseModal.vue';


const props = defineProps({
  workOrder: {
    type: Object,
    required: true
  },

  equipment: {
    type: Object,
    required: true
  }
});


const emit = defineEmits([
  'close',
  'complete'
]);


const form = reactive({
  completionHorometer:
    props.equipment.currentHorometer ?? '',

  completionObservation: ''
});

const errorMessage = computed(() => {

  if (
    form.completionHorometer === '' ||
    form.completionHorometer === null
  ) {
    return '';
  }

  const value = Number(
    form.completionHorometer
  );

  if (
    value <
    props.equipment.currentHorometer
  ) {
    return `O horímetro não pode ser menor que ${formatHorometer(
      props.equipment.currentHorometer
    )} h.`;
  }

  return '';

});

function formatHorometer(value) {

  return new Intl.NumberFormat(
    'pt-BR',
    {
      maximumFractionDigits: 1
    }
  ).format(value ?? 0);

}

function submitForm() {

  if (
    form.completionHorometer === '' ||
    errorMessage.value
  ) {
    return;
  }

  emit(
    'complete',
    {
      completionHorometer:
        Number(form.completionHorometer),

      completionObservation:
        form.completionObservation.trim()
    }
  );

}
</script>

<template>
  <BaseModal size="md" :title="`Concluir ${workOrder.orderNumber}`"
    :description="`${workOrder.equipmentCode} · ${workOrder.equipmentName}`" @close="$emit('close')">
    <form id="complete-work-order-form" class="complete-work-order-form" @submit.prevent="submitForm">
      <div class="complete-work-order-current">

        <div class="complete-work-order-current-icon">
          <Gauge :size="21" stroke-width="1.8" />
        </div>

        <div>
          <span>
            Horímetro atual
          </span>

          <strong>
            {{ formatHorometer(equipment.currentHorometer) }} h
          </strong>
        </div>

      </div>

      <div class="form-field">
        <label for="completion-horometer">
          Horímetro na conclusão *
        </label>

        <input id="completion-horometer" v-model.number="form.completionHorometer" type="number" min="0" step="0.1"
          required />

        <span v-if="errorMessage" class="reading-form-error">
          {{ errorMessage }}
        </span>
      </div>

      <div class="form-field">
        <label for="completion-observation">
          Observação da conclusão

          <span class="optional-field">
            (opcional)
          </span>
        </label>

        <textarea id="completion-observation" v-model="form.completionObservation" rows="4"
          placeholder="Descreva observações sobre a manutenção realizada..."></textarea>
      </div>
    </form>

    <template #footer>
      <button class="cancel-button" type="button" @click="$emit('close')">
        Cancelar
      </button>

      <button class="save-button" type="submit" form="complete-work-order-form" :disabled="Boolean(errorMessage)">
        <CheckCircle2 :size="16" />
        Concluir OS
      </button>
    </template>

  </BaseModal>
</template>