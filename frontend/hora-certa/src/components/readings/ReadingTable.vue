<script setup>
import {
  ArrowUp,
  Eye,
  Gauge,
  Image
} from 'lucide-vue-next';

import { getEquipmentIcon } from '../../utils/equipmentIcons';


defineProps({
  readings: {
    type: Array,
    required: true
  }
});

const emit = defineEmits([
  'view'
]);

function formatDate(date) {

  return new Intl.DateTimeFormat(
    'pt-BR',
    {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    }
  ).format(new Date(date));

}

function formatTime(date) {

  return new Intl.DateTimeFormat(
    'pt-BR',
    {
      hour: '2-digit',
      minute: '2-digit'
    }
  ).format(new Date(date));

}

function formatHorometer(value) {

  return new Intl.NumberFormat(
    'pt-BR',
    {
      maximumFractionDigits: 1
    }
  ).format(value);

}

function getReadingDifference(reading) {

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
  <div class="reading-table-wrapper">

    <table class="reading-table">

      <thead>
        <tr>
          <th>Data e hora</th>

          <th>Equipamento</th>

          <th>Leitura</th>

          <th>Responsável</th>

          <th class="reading-table-evidence">
            Evidência
          </th>

          <th class="reading-table-actions">
            Ações
          </th>
        </tr>
      </thead>


      <tbody>

        <tr v-for="reading in readings" :key="reading.id">

          <td>

            <div class="reading-date">

              <strong>
                {{ formatDate(reading.readAt) }}
              </strong>

              <span>
                {{ formatTime(reading.readAt) }}
              </span>

            </div>

          </td>

          <td>

            <div class="reading-equipment">

              <div class="reading-equipment-icon">

                <img :src="getEquipmentIcon(reading.equipmentCategory)" :alt="reading.equipmentCategory"
                  class="reading-equipment-image" />

              </div>


              <div class="reading-equipment-info">

                <strong>
                  {{ reading.equipmentCode }}
                </strong>

                <span>
                  {{ reading.equipmentName }}
                </span>

              </div>

            </div>

          </td>

          <td>

            <div class="reading-value">

              <div class="reading-value-main">

                <Gauge :size="15" stroke-width="1.8" />

                <strong>
                  {{
                    formatHorometer(
                      reading.readingValue
                    )
                  }}
                  <small>h</small>
                </strong>

              </div>


              <div v-if="
                getReadingDifference(reading) !== null
              " class="reading-difference">

                <ArrowUp :size="12" stroke-width="2" />

                <span>
                  +{{
                    formatHorometer(
                      getReadingDifference(reading)
                    )
                  }}h desde a anterior
                </span>

              </div>

            </div>

          </td>

          <td>

            <span class="reading-responsible">
              {{ reading.responsibleName }}
            </span>

          </td>

          <td class="reading-table-evidence">

            <div v-if="reading.evidence" class="reading-evidence reading-evidence--available">

              <Image :size="14" />

              <span>
                Anexada
              </span>

            </div>


            <span v-else class="reading-evidence reading-evidence--empty">
              —
            </span>

          </td>

          <td class="reading-table-actions">

            <button class="reading-view-button" type="button" title="Visualizar leitura" @click="emit('view', reading)">
              <Eye :size="17" stroke-width="1.8" />
            </button>

          </td>

        </tr>

        <tr v-if="readings.length === 0">

          <td colspan="6" class="reading-empty">

            <div class="reading-empty-content">

              <Gauge :size="26" stroke-width="1.6" />

              <strong>
                Nenhuma leitura encontrada
              </strong>

              <span>
                Tente alterar os filtros utilizados.
              </span>

            </div>

          </td>

        </tr>

      </tbody>

    </table>

  </div>
</template>