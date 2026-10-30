<script setup>
import { computed, ref } from 'vue';
import {
  Plus,
  Gauge
} from 'lucide-vue-next';

import { readingMocks } from '../mocks/readings';

import '../styles/pages/readings.css';

import ReadingTable
  from '../components/readings/ReadingTable.vue';

import { equipmentMocks } from '../mocks/equipments';
import ReadingFormModal from '../components/readings/ReadingFormModal.vue';
import ReadingDetailsModal from '../components/readings/ReadingDetailsModal.vue';

const readings = ref([
  ...readingMocks
]);


const searchTerm = ref('');

const selectedEquipment = ref('all');

const equipmentOptions = computed(() => {

  const equipments = new Map();


  readings.value.forEach((reading) => {

    if (!equipments.has(reading.equipmentId)) {

      equipments.set(
        reading.equipmentId,
        {
          id: reading.equipmentId,
          code: reading.equipmentCode,
          name: reading.equipmentName
        }
      );

    }

  });


  return Array.from(equipments.values());

});

const filteredReadings = computed(() => {

  const search = searchTerm.value
    .trim()
    .toLowerCase();


  return readings.value.filter((reading) => {

    const matchesSearch =
      !search ||

      reading.equipmentCode
        .toLowerCase()
        .includes(search) ||

      reading.equipmentName
        .toLowerCase()
        .includes(search);


    const matchesEquipment =
      selectedEquipment.value === 'all' ||

      String(reading.equipmentId) ===
      selectedEquipment.value;


    return matchesSearch && matchesEquipment;

  });

});


const isReadingModalOpen = ref(false);

const selectedReading = ref(null);

function openReadingModal() {
  isReadingModalOpen.value = true;
}

function closeReadingModal() {
  isReadingModalOpen.value = false;
}

function handleSaveReading(formData) {

  const equipment = equipmentMocks.find(
    (item) => item.id === formData.equipmentId
  );


  if (!equipment) {
    return;
  }


  const previousReadingValue =
    equipment.currentHorometer;


  const newReading = {
    id: Date.now(),

    equipmentId: equipment.id,
    equipmentCode: equipment.code,
    equipmentName: equipment.name,
    equipmentCategory: equipment.category,

    readingValue: formData.readingValue,
    previousReadingValue,

    readAt: formData.readAt,

    responsibleName: 'Maria Luiza',

    observation: formData.observation,

    evidence: null
  };


  readings.value.unshift(newReading);


  equipment.currentHorometer =
    formData.readingValue;


  closeReadingModal();

}

function openReadingDetails(reading) {

  selectedReading.value = reading;

}

function closeReadingDetails() {

  selectedReading.value = null;

}

</script>


<template>
  <div class="readings-page">


    <header class="readings-header">

      <div class="readings-header-content">

        <div class="readings-title-icon">
          <Gauge :size="22" stroke-width="1.8" />
        </div>


        <div>
          <h1>
            Leituras de Horímetro
          </h1>

          <p>
            Registre e acompanhe as horas de utilização
            dos equipamentos.
          </p>
        </div>

      </div>


      <button class="new-reading-button" type="button" @click="openReadingModal">
        <Plus :size="17" />

        Nova Leitura
      </button>

    </header>


    <section class="readings-history">

      <div class="readings-history-heading">

        <div>
          <span class="readings-eyebrow">
            HISTÓRICO
          </span>

          <h2>
            Leituras registradas
          </h2>
        </div>


        <span class="readings-total">
          {{ filteredReadings.length }}
          {{
            filteredReadings.length === 1
              ? 'registro'
              : 'registros'
          }}
        </span>

      </div>

      <div class="readings-filters">

        <label class="readings-search">

          <span>
            Buscar
          </span>

          <input v-model="searchTerm" type="text" placeholder="Código ou nome do equipamento..." />

        </label>


        <label class="readings-equipment-filter">

          <span>
            Equipamento
          </span>

          <select v-model="selectedEquipment">

            <option value="all">
              Todos
            </option>

            <option v-for="equipment in equipmentOptions" :key="equipment.id" :value="String(equipment.id)">
              {{ equipment.code }} — {{ equipment.name }}
            </option>

          </select>

        </label>

      </div>


      <ReadingTable :readings="filteredReadings" @view="openReadingDetails" />

    </section>

    <ReadingFormModal v-if="isReadingModalOpen" :equipments="equipmentMocks" @close="closeReadingModal"
      @save="handleSaveReading" />

  </div>
</template>