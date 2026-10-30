<script setup>
import { computed, reactive } from 'vue';
import {
  CheckCircle2,
  Gauge,
  X
} from 'lucide-vue-next';


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

  <div
    class="modal-overlay"
    @click.self="$emit('close')"
  >

    <div class="complete-work-order-modal">

      <header class="modal-header">

        <div>

          <span class="modal-eyebrow">
            CONCLUIR MANUTENÇÃO
          </span>

          <h2>
            {{ workOrder.orderNumber }}
          </h2>

          <p>
            {{ workOrder.equipmentCode }}
            ·
            {{ workOrder.equipmentName }}
          </p>

        </div>


        <button
          class="modal-close-button"
          type="button"
          title="Fechar"
          @click="$emit('close')"
        >
          <X :size="19" />
        </button>

      </header>


      <form
        class="complete-work-order-form"
        @submit.prevent="submitForm"
      >

        <div class="complete-work-order-current">

          <div class="complete-work-order-current-icon">

            <Gauge
              :size="21"
              stroke-width="1.8"
            />

          </div>


          <div>

            <span>
              Horímetro atual
            </span>

            <strong>
              {{
                formatHorometer(
                  equipment.currentHorometer
                )
              }}
              h
            </strong>

          </div>

        </div>

        <div class="form-field">

          <label for="completion-horometer">
            Horímetro na conclusão *
          </label>

          <input
            id="completion-horometer"
            v-model.number="form.completionHorometer"
            type="number"
            min="0"
            step="0.1"
            required
          />


          <span
            v-if="errorMessage"
            class="reading-form-error"
          >
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

          <textarea
            id="completion-observation"
            v-model="form.completionObservation"
            rows="4"
            placeholder="Descreva observações sobre a manutenção realizada..."
          />

        </div>

        <div class="complete-work-order-actions">

          <button
            class="secondary-button"
            type="button"
            @click="$emit('close')"
          >
            Cancelar
          </button>


          <button
            class="primary-button"
            type="submit"
            :disabled="Boolean(errorMessage)"
          >

            <CheckCircle2 :size="16" />

            Concluir OS

          </button>

        </div>

      </form>

    </div>

  </div>

</template>