<script setup>
import { computed, reactive } from 'vue';

import {
    Gauge,
    X
} from 'lucide-vue-next';


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
    readAt: getCurrentDateTime(),
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

function getCurrentDateTime() {

    const now = new Date();

    const offset =
        now.getTimezoneOffset() * 60000;

    return new Date(
        now.getTime() - offset
    )
        .toISOString()
        .slice(0, 16);

}

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
        form.readingValue === '' ||
        !form.readAt
    ) {
        return;
    }


    if (errorMessage.value) {
        return;
    }


    emit('save', {
        equipmentId: Number(form.equipmentId),
        readingValue: Number(form.readingValue),
        readAt: form.readAt,
        observation: form.observation.trim()
    });

}
</script>


<template>

    <div class="modal-overlay" @click.self="$emit('close')">

        <div class="reading-form-modal">

            <header class="modal-header">

                <div>

                    <span class="modal-eyebrow">
                        HORÍMETRO
                    </span>

                    <h2>
                        Nova Leitura
                    </h2>

                    <p>
                        Registre o horímetro atual do equipamento.
                    </p>

                </div>


                <button class="modal-close-button" type="button" title="Fechar" @click="$emit('close')">
                    <X :size="19" />
                </button>

            </header>

            <form class="reading-form" @submit.prevent="submitForm">

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
                            {{ equipment.code }} —
                            {{ equipment.name }}
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
                            {{
                                formatHorometer(
                                    selectedEquipment.currentHorometer
                                )
                            }}
                            h
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

                <div class="reading-form-grid">

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

                        <label for="reading-date">
                            Data e hora
                        </label>

                        <input id="reading-date" v-model="form.readAt" type="datetime-local" required />

                    </div>

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

                <footer class="reading-form-actions">

                    <button class="secondary-button" type="button" @click="$emit('close')">
                        Cancelar
                    </button>

                    <button class="primary-button" type="submit" :disabled="Boolean(errorMessage)">
                        Registrar leitura
                    </button>



                </footer>

            </form>
            <ReadingFormModal v-if="isReadingModalOpen" :equipments="equipmentMocks" @close="closeReadingModal"
                @save="handleSaveReading" />


            <ReadingDetailsModal v-if="selectedReading" :reading="selectedReading" @close="closeReadingDetails" />

        </div>

    </div>

</template>