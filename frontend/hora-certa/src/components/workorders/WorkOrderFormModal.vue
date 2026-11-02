<script setup>
import { computed, reactive, ref, watch } from 'vue';
import { ClipboardPlus } from 'lucide-vue-next';

import BaseModal from '../common/BaseModal.vue';

import {
    getEquipmentPlans
} from '../../services/equipmentPlanService';

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
    equipmentPlanId: '',
    maintenanceType: 'PREVENTIVE',
    scheduledDate: '',
    scheduledHorometer: '',
    responsibleTechnician: '',
    notes: ''
});

const equipmentPlans = ref([]);
const isLoadingPlans = ref(false);
const planError = ref('');

const activeEquipments = computed(() => {
    return props.equipments.filter(
        (equipment) =>
            equipment.status === 'ACTIVE'
    );
});

const selectedEquipment = computed(() => {
    return props.equipments.find(
        (equipment) =>
            equipment.id === Number(form.equipmentId)
    ) ?? null;
});

const activeEquipmentPlans = computed(() => {
    return equipmentPlans.value.filter(
        (link) => link.active
    );
});

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
        Number(
            selectedEquipment.value.currentHorometer
        );

    if (scheduledHorometer < currentHorometer) {
        return `O horímetro previsto não pode ser menor que ${currentHorometer.toLocaleString(
            'pt-BR'
        )} h.`;
    }

    return '';
});

watch(
    () => form.equipmentId,

    async (equipmentId) => {
        form.equipmentPlanId = '';
        equipmentPlans.value = [];
        planError.value = '';

        if (!equipmentId) {
            return;
        }

        isLoadingPlans.value = true;

        try {
            equipmentPlans.value =
                await getEquipmentPlans(
                    Number(equipmentId)
                );
        } catch (error) {
            planError.value =
                error.message ||
                'Não foi possível carregar os planos do equipamento.';
        } finally {
            isLoadingPlans.value = false;
        }
    }
);

function submitForm() {
    if (
        !form.equipmentId ||
        !form.equipmentPlanId ||
        !form.maintenanceType ||
        !form.scheduledDate ||
        form.scheduledHorometer === '' ||
        horometerError.value
    ) {
        return;
    }

    emit('save', {
        equipmentPlanId:
            Number(form.equipmentPlanId),

        maintenancePlanItemId: null,

        maintenanceType:
            form.maintenanceType,

        scheduledDate:
            form.scheduledDate,

        scheduledHorometer:
            Number(form.scheduledHorometer),

        responsibleTechnician:
            form.responsibleTechnician.trim() || null,

        notes:
            form.notes.trim() || null
    });
}
</script>

<template>
    <BaseModal size="md" title="Nova Ordem" description="Programe uma nova manutenção para um equipamento."
        @close="$emit('close')">
        <form id="work-order-form" class="work-order-form" @submit.prevent="submitForm">
            <div class="form-field">
                <label for="work-order-equipment">
                    Equipamento *
                </label>

                <select id="work-order-equipment" v-model="form.equipmentId" required>
                    <option value="" disabled>
                        Selecione um equipamento
                    </option>

                    <option v-for="equipment in activeEquipments" :key="equipment.id" :value="equipment.id">
                        {{ equipment.code }} — {{ equipment.name }}
                    </option>
                </select>
            </div>

            <div v-if="selectedEquipment" class="work-order-form-equipment-info">
                <span>
                    Horímetro atual
                </span>

                <strong>
                    {{
                        Number(
                            selectedEquipment.currentHorometer
                        ).toLocaleString('pt-BR')
                    }}
                    h
                </strong>
            </div>

            <div class="form-field">
                <label for="work-order-plan">
                    Plano de Manutenção *
                </label>

                <select id="work-order-plan" v-model="form.equipmentPlanId" :disabled="!form.equipmentId ||
                    isLoadingPlans
                    " required>
                    <option value="" disabled>
                        {{
                            isLoadingPlans
                                ? 'Carregando planos...'
                                : 'Selecione um plano'
                        }}
                    </option>

                    <option v-for="link in activeEquipmentPlans" :key="link.id" :value="link.id">
                        {{ link.maintenancePlanName }}
                    </option>
                </select>

                <span v-if="
                    form.equipmentId &&
                    !isLoadingPlans &&
                    activeEquipmentPlans.length === 0 &&
                    !planError
                " class="work-order-form-error">
                    Este equipamento não possui planos ativos vinculados.
                </span>

                <span v-if="planError" class="work-order-form-error">
                    {{ planError }}
                </span>
            </div>

            <div class="form-field">
                <label for="work-order-type">
                    Tipo de manutenção *
                </label>

                <select id="work-order-type" v-model="form.maintenanceType" required>
                    <option value="PREVENTIVE">
                        Preventiva
                    </option>

                    <option value="CORRECTIVE">
                        Corretiva
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
                        step="0.1" placeholder="Ex.: 1500" required />

                    <span v-if="horometerError" class="work-order-form-error">
                        {{ horometerError }}
                    </span>
                </div>
            </div>

            <div class="form-field">
                <label for="work-order-technician">
                    Técnico responsável

                    <span class="optional-field">
                        (opcional)
                    </span>
                </label>

                <input id="work-order-technician" v-model="form.responsibleTechnician" type="text" maxlength="100"
                    placeholder="Nome do técnico responsável" />
            </div>

            <div class="form-field">
                <label for="work-order-notes">
                    Observação

                    <span class="optional-field">
                        (opcional)
                    </span>
                </label>

                <textarea id="work-order-notes" v-model="form.notes" rows="4" maxlength="2000"
                    placeholder="Adicione informações importantes sobre a manutenção..."></textarea>
            </div>
        </form>

        <template #footer>
            <button class="cancel-button" type="button" @click="$emit('close')">
                Cancelar
            </button>

            <button class="save-button" type="submit" form="work-order-form"
                :disabled="Boolean(horometerError) || isLoadingPlans">
                <ClipboardPlus :size="16" />
                Criar Ordem
            </button>
        </template>
    </BaseModal>
</template>