<script setup>
import { computed, reactive } from "vue";

import { ClipboardPlus, X } from "lucide-vue-next";

const props = defineProps({
    equipments: {
        type: Array,
        required: true
    },

    maintenancePlans: {
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
    maintenancePlanId: '',
    scheduledDate: '',
    scheduledHorometer: '',
    observation: ''
});

const activeEquipments = computed(() => {
    return props.equipments.filter(
        (equipment) =>
            equipment.status === 'ACTIVE'
    );
});

const ActiveMaintenancePlans = computed(() => {
    return props.maintenancePlans.filter(
        (plan) =>
            plan.active
    );
});

const selectedEquipment = computed(() => {
    return props.equipments.find(
        (equipment) =>
            equipment.id === Number(form.equipmentId)
    );
}) ?? null;

const horometerError = computed(() => {
    if (
        !selectedEquipment.value ||
        form.scheduledHorometer === ''
    ) {
        return '';
    }

    const scheduledHorometer =
        Number(form.scheduledHorometer);

    const currentHorometer =
        Number(selectedEquipment.value.currentHorometer);

    if (scheduledHorometer < currentHorometer) {
        return `O horímetro previsto não pode ser menor que ${currentHorometer.toLocaleString(
            'pt-BR'
        )} h.`;

    }
    return '';
});


function submitForm() {
    if (
        !form.equipmentId ||
        !form.maintenancePlanId ||
        !form.scheduledDate ||
        !form.scheduledHorometer === '' ||
        horometerError.value
    ) {
        return;
    }

    emit('save', {
        equipmentId: Number(form.equipmentId),
        maintenancePlanId: Number(form.maintenancePlanId),
        scheduledDate: form.scheduledDate,
        scheduledHorometer: Number(form.scheduledHorometer),
        observation: form.observation.trim()
    });
}
</script>
<template>
    <div class="modal-overlay" @click.self="$emit('close')">
        <div class="work-order-form-modal">
            <header class="modal-header">
                <div class="work-order-form-heading">
                    <div class="work-order-form-heading-icon">
                        <ClipboardPlus :size="20" stroke-width="1.8" />
                    </div>

                    <div>
                        <span class="modal-eyebrow">
                            ORDEM DE SERVIÇO
                        </span>
                        <h2>
                            Nova Ordem
                        </h2>
                        <p>
                            Programe uma nova manutenção para um equipamento.
                        </p>
                    </div>

                </div>

                <button class="modal-close-button" type="button" title="Fechar" @click="$emit('close')">
                    <X :size="19" />
                </button>

            </header>

            <form class="work-order-form" @submit.prevent="submitForm">
                <div class="form-field">
                    <label for="work-order-equipment">
                        Equipamento *
                    </label>

                    <select id="work-order-equipment" v-model="form.equipmentId" required>
                        <option value="" disabled>
                            Selecione um equipamento
                        </option>

                        <option v-for="equipment in activeEquipments" :key="equipment.id" :value="equipment.id">
                            {{ equipment.code }}.
                            {{ equipment.name }}
                        </option>
                    </select>
                </div>

                <div v-if="selectedEquipment" class="work-order-form-equipment-info">
                    <span>
                        Horímetro atual
                    </span>
                    <strong>
                        {{ Number(selectedEquipment.currentHorometer).toLocaleString('pt-BR') }}
                        h
                    </strong>
                </div>

                <div class="form-field">
                    <label for="work-order-plan">
                        Plano de Manutenção *
                    </label>
                    <select id="work-order-plan" v-model="form.maintenancePlanId" required>
                        <option value="" disabled>
                            Selecione um Plano
                        </option>
                        <option v-for="plan in ActiveMaintenancePlans" :key="plan.id" :value="plan.id">
                            {{ plan.name }}
                        </option>
                    </select>
                </div>

                <div class="work-order-form-row">
                    <div class="form-field">
                        <label for="work-order-date">
                            Data agendada *
                        </label>

                        <input id="work-order-date" v-model="form.scheduledDate" type="date" required />

                    </div>

                    <div class="form-field">
                        <label for="work-order-horometer">
                            Horímetro previsto *
                        </label>

                        <input id="work-order-horometer" v-model.number="form.scheduledHorometer" type="number" min="0"
                            step="0.1" placeholder="Ex.: 1500" required>

                        <span v-if="horometerError" class="work-order-form-error">
                            {{ horometerError }}
                        </span>

                    </div>
                </div>

                <div class="form-field">
                    <label for="work-order-observation">
                        Observação

                        <span class="optional-field">
                            (Opcional)
                        </span>
                    </label>

                    <textarea id="work-order-observation" v-model="form.observation" rows="4"
                        placeholder="Adicione informações importantes sobre a manutenção..." />
                </div>

                <div class="work-order-form-actions">
                    <button class="secondary-button" type="button" @click="$emit('close')">
                        Cancelar
                    </button>

                    <button class="primary-button" type="submit" :disabled="Boolean(horometerError)">
                        <ClipboardPlus :size="16" />
                        Criar Ordem
                    </button>
                </div>
            </form>
        </div>
    </div>
</template>