<script setup>
import {
  CalendarDays,
  Gauge,
  UserRound
} from 'lucide-vue-next';

import BaseModal from '../common/BaseModal.vue';

defineProps({
  reading: {
    type: Object,
    required: true
  }
});


defineEmits([
  'close'
]);

function formatHorometer(value) {

  return new Intl.NumberFormat(
    'pt-BR',
    {
      maximumFractionDigits: 1
    }
  ).format(value ?? 0);

}

function formatDateTime(date) {

  return new Intl.DateTimeFormat(
    'pt-BR',
    {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    }
  ).format(new Date(date));

}

function getDifference(reading) {

  if (
    reading.previousReadingValue === null ||
    reading.previousReadingValue === undefined
  ) {
    return null;
  }


  return (
    reading.readingValue -
    reading.previousReadingValue
  );

}
</script>

<template>
  <BaseModal size="md" :title="reading.equipmentCode" :description="reading.equipmentName" @close="$emit('close')">
    <div class="reading-details-content">
      <section class="reading-details-highlight">
        <div class="reading-details-gauge">
          <Gauge :size="25" stroke-width="1.7" />
        </div>

        <div>
          <span>
            Horímetro registrado
          </span>

          <strong>
            {{ formatHorometer(reading.readingValue) }}
            <small>h</small>
          </strong>

          <p v-if="getDifference(reading) !== null">
            +{{ formatHorometer(getDifference(reading)) }}h
            desde a leitura anterior
          </p>
        </div>
      </section>

      <section class="reading-details-grid">
        <div class="reading-detail-item">
          <CalendarDays :size="18" stroke-width="1.7" />
          <div>
            <span>
              Data e hora
            </span>

            <strong>
              {{ formatDateTime(reading.readAt) }}
            </strong>
          </div>
        </div>

        <div class="reading-detail-item">
          <UserRound :size="18" stroke-width="1.7" />
          <div>
            <span>
              Responsável
            </span>

            <strong>
              {{ reading.responsibleName }}
            </strong>
          </div>
        </div>
      </section>

      <section class="reading-details-section">

        <span class="reading-details-label">
          Observação
        </span>

        <p v-if="reading.observation">
          {{ reading.observation }}
        </p>

        <p v-else class="reading-details-empty">
          Nenhuma observação registrada.
        </p>

      </section>
    </div>

    <template #footer>
      <button class="save-button" type="button" @click="$emit('close')">
        Fechar
      </button>
    </template>
  </BaseModal>
</template>