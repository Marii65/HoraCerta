<script setup>
import { computed, reactive } from 'vue';

import {
    Gauge,
    X
} from 'lucide-vue-next';

import BaseModal from '../common/BaseModal.vue';

const props = defineProps({
    equipments: {
        type: Array,
        required: true
    }
});


const emit = defineEmits([
    'close',
    'save'
]);

const form = reactive({
    equipmentId: '',
    readingValue: '',
    observation: ''
});


const errorMessage = computed(() => {
    if (!form.equipmentId || form.readingValue === '') {
        return '';
    }

    if (
        Number(form.readingValue) <
        Number(selectedEquipment.value?.currentHorometer)
    ) {
        return 'A nova leitura não pode ser menor que o horímetro atual.';
    }
    return '';
});

const selectedEquipment = computed(() => {
    return props.equipments.find(
        (equipment) =>
            String(equipment.id) ===
            String(form.equipmentId)
    );
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
        !form.equipmentId ||
        form.readingValue === ''
    ) {
        return;
    }
    if (errorMessage.value) {
        return;
    }
    emit('save', {
        equipmentId: Number(form.equipmentId),
        readingValue: Number(form.readingValue),
        observation: form.observation.trim()
    });
}
</script>

<template>
    <BaseModal size="md" title="Nova Leitura" description="Registre o horímetro atual do equipamento."
        @close="$emit('close')">
        <form id="reading-form" class="reading-form" @submit.prevent="submitForm">
            <div class="form-group">
                <label for="reading-equipment">
                    Equipamento
                </label>

                <select id="reading-equipment" v-model="form.equipmentId" required>
                    <option value="" disabled>
                        Selecione um equipamento
                    </option>
                    <option v-for="equipment in equipments" :key="equipment.id" :value="equipment.id"
                        :disabled="equipment.status !== 'ACTIVE'">
                        {{ equipment.code }} — {{ equipment.name }}
                    </option>
                </select>
            </div>

            <div v-if="selectedEquipment" class="current-horometer">
                <div class="current-horometer-icon">
                    <Gauge :size="21" stroke-width="1.8" />
                </div>

                <div>
                    <span>
                        Horímetro atual
                    </span>

                    <strong>
                        {{ formatHorometer(selectedEquipment.currentHorometer) }} h
                    </strong>
                </div>

                <div class="current-equipment">
                    <span>
                        {{ selectedEquipment.code }}
                    </span>

                    <small>
                        {{ selectedEquipment.name }}
                    </small>
                </div>
            </div>

            <div class="form-group">
                <label for="reading-value">
                    Nova leitura (h)
                </label>

                <input id="reading-value" v-model="form.readingValue" type="number" min="0" step="0.1"
                    placeholder="Ex.: 1500" required />

                <span v-if="errorMessage" class="reading-form-error">
                    {{ errorMessage }}
                </span>
            </div>

            <div class="form-group">
                <label for="reading-observation">
                    Observação
                    <span class="optional-field">
                        (opcional)
                    </span>
                </label>

                <textarea id="reading-observation" v-model="form.observation" rows="3"
                    placeholder="Adicione alguma observação sobre a leitura..."></textarea>
            </div>
        </form>

        <template #footer>
            <button class="cancel-button" type="button" @click="$emit('close')">
                Cancelar
            </button>
            <button class="save-button" type="submit" form="reading-form" :disabled="Boolean(errorMessage)">
                Registrar leitura
            </button>
        </template>
    </BaseModal>
</template>