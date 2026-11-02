<script setup>
import { computed, onMounted, ref } from 'vue';
import {
  Plus,
  Search
} from 'lucide-vue-next';

import PageHeader from '../components/common/PageHeader.vue';
import ReadingTable from '../components/readings/ReadingTable.vue';
import ReadingFormModal from '../components/readings/ReadingFormModal.vue';
import ReadingDetailsModal from '../components/readings/ReadingDetailsModal.vue';

import { getEquipments } from '../services/equipmentService';

import {
  getHorometerReadings,
  createHorometerReading
} from '../services/horometerReadingService';

import '../styles/pages/readings.css';

const readings = ref([]);
const equipments = ref([]);

const searchTerm = ref('');
const selectedEquipment = ref('all');

const isReadingModalOpen = ref(false);
const selectedReading = ref(null);

const isLoading = ref(false);
const errorMessage = ref('');

function mapReadings(apiReadings) {
  const readingsByEquipment = new Map();
  apiReadings.forEach((reading) => {
    if (!readingsByEquipment.has(reading.equipmentId)) {
      readingsByEquipment.set(
        reading.equipmentId,
        []
      );
    }
    readingsByEquipment
      .get(reading.equipmentId)
      .push(reading);
  });

  const previousValues = new Map();

  readingsByEquipment.forEach(
    (equipmentReadings) => {
      const ordered = [...equipmentReadings].sort(
        (a, b) =>
          new Date(a.readAt) -
          new Date(b.readAt)
      );
      ordered.forEach((reading, index) => {
        previousValues.set(
          reading.id,
          index === 0
            ? null
            : ordered[index - 1].readingValue
        );
      });
    }
  );
  return apiReadings.map((reading) => {
    const equipment = equipments.value.find(
      (item) =>
        item.id === reading.equipmentId
    );
    return {
      ...reading,
      equipmentCategory:
        equipment?.category ?? '',
      responsibleName:
        reading.username,
      previousReadingValue:
        previousValues.get(reading.id) ?? null,
    };
  });
}

async function loadReadingsPage() {
  isLoading.value = true;
  errorMessage.value = '';
  try {
    equipments.value =
      await getEquipments();
    const apiReadings =
      await getHorometerReadings();
    readings.value =
      mapReadings(apiReadings);
  } catch (error) {
    errorMessage.value =
      error.message ||
      'Não foi possível carregar as leituras.';
    console.error(error);
  } finally {
    isLoading.value = false;
  }
}

const equipmentOptions = computed(() => {
  return equipments.value;
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

function openReadingModal() {
  isReadingModalOpen.value = true;
}

function closeReadingModal() {
  isReadingModalOpen.value = false;
}

async function handleSaveReading(formData) {
  try {
    await createHorometerReading({
      equipmentId: formData.equipmentId,
      readingValue: formData.readingValue,
      observation: formData.observation
    });
    await loadReadingsPage();
    closeReadingModal();
  } catch (error) {
    errorMessage.value =
      error.message ||
      'Não foi possível registrar a leitura.';
    console.error(error);
  }
}

function openReadingDetails(reading) {
  selectedReading.value = reading;
}

function closeReadingDetails() {
  selectedReading.value = null;
}

onMounted(() => {
  loadReadingsPage();
});
</script>


<template>
  <div class="readings-page">
    <PageHeader title="Leituras de Horímetro"
      description="Registre e acompanhe as horas de utilização dos equipamentos.">
      <template #actions>
        <button class="new-reading-button" type="button" @click="openReadingModal">
          <Plus :size="17" />
          Nova Leitura
        </button>
      </template>
    </PageHeader>

    <section class="readings-history">
      <div class="readings-summary">
        <span>
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
          <Search :size="17" />

          <input v-model="searchTerm" type="text" placeholder="Buscar por código, nome ou modelo..." />
        </label>

        <select v-model="selectedEquipment" class="readings-equipment-select">
          <option value="all">
            Todos os equipamentos
          </option>

          <option v-for="equipment in equipmentOptions" :key="equipment.id" :value="String(equipment.id)">
            {{ equipment.code }} — {{ equipment.name }}
          </option>
        </select>

      </div>

      <ReadingTable :readings="filteredReadings" @view="openReadingDetails" />
    </section>

    <ReadingFormModal v-if="isReadingModalOpen" :equipments="equipments" @close="closeReadingModal"
      @save="handleSaveReading" />

    <ReadingDetailsModal v-if="selectedReading" :reading="selectedReading" @close="closeReadingDetails" />
  </div>
</template>