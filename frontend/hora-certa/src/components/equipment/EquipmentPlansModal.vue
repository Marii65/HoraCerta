<script setup>
import {
    computed,
    onMounted,
    ref
} from 'vue';

import {
    Link,
    Power
} from 'lucide-vue-next';

import BaseModal from '../common/BaseModal.vue';

import { getEquipmentPlans, createEquipmentPlan, toggleEquipmentPlanStatus } from '../../services/equipmentPlanService';
import { getMaintenancePlans } from '../../services/maintenancePlanService';

const props = defineProps({
    equipment: {
        type: Object,
        required: true
    },

    role: {
        type: String,
        default: null
    }
});

const canManagePlans = computed(() =>
    ['ADMIN', 'TECHNICIAN'].includes(props.role)
);

const emit = defineEmits([
    'close'
]);

const equipmentPlans = ref([]);
const maintenancePlans = ref([]);

const selectedPlanId = ref('');

const isLoading = ref(false);
const isSaving = ref(false);

const errorMessage = ref('');

const availablePlans = computed(() => {
    const linkedActivePlanIds =
        equipmentPlans.value
            .filter((link) => link.active)
            .map((link) => link.maintenancePlanId);

    return maintenancePlans.value.filter(
        (plan) =>
            plan.active &&
            !linkedActivePlanIds.includes(plan.id)
    );
});

async function loadData() {
    isLoading.value = true;
    errorMessage.value = '';
    try {
        const [
            links,
            plans
        ] = await Promise.all([
            getEquipmentPlans(props.equipment.id),
            getMaintenancePlans()
        ]);
        equipmentPlans.value = links;
        maintenancePlans.value = plans;
    } catch (error) {
        errorMessage.value =
            error.message ||
            'Não foi possível carregar os planos do equipamento.';
    } finally {
        isLoading.value = false;
    }
}

async function handleLinkPlan() {
    if (!selectedPlanId.value) {
        return;
    }
    isSaving.value = true;
    errorMessage.value = '';
    try {
        await createEquipmentPlan(
            props.equipment.id,
            Number(selectedPlanId.value)
        );
        selectedPlanId.value = '';
        await loadData();
    } catch (error) {
        errorMessage.value =
            error.message ||
            'Não foi possível vincular o plano.';
    } finally {
        isSaving.value = false;
    }
}

async function handleToggleStatus(link) {
    errorMessage.value = '';
    try {
        const updatedLink =
            await toggleEquipmentPlanStatus(link.id);

        const index =
            equipmentPlans.value.findIndex(
                (item) => item.id === link.id
            );

        if (index !== -1) {
            equipmentPlans.value[index] =
                updatedLink;
        }
    } catch (error) {
        errorMessage.value =
            error.message ||
            'Não foi possível alterar o vínculo.';
    }
}

onMounted(() => {
    loadData();
});
</script>

<template>
    <BaseModal size="md" title="Planos de manutenção" :description="`${equipment.code} — ${equipment.name}`"
        @close="$emit('close')">
        <div class="equipment-plans-content">
            <p v-if="errorMessage" class="equipment-plans-error">
                {{ errorMessage }}
            </p>

            <p v-if="isLoading">
                Carregando planos...
            </p>

            <template v-else>
                <div v-if="canManagePlans" class="equipment-plan-link-form">
                    <div class="form-group">
                        <label for="maintenance-plan">
                            Vincular plano
                        </label>

                        <select id="maintenance-plan" v-model="selectedPlanId">
                            <option value="">
                                Selecione um plano
                            </option>

                            <option v-for="plan in availablePlans" :key="plan.id" :value="plan.id">
                                {{ plan.name }}
                            </option>
                        </select>
                    </div>

                    <button class="save-button" type="button" :disabled="!selectedPlanId || isSaving"
                        @click="handleLinkPlan">
                        <Link :size="16" />
                        {{
                            isSaving
                                ? 'Vinculando...'
                                : 'Vincular'
                        }}
                    </button>
                </div>

                <div class="equipment-plans-list">
                    <div v-for="link in equipmentPlans" :key="link.id" class="equipment-plan-item">
                        <div class="equipment-plan-info">
                            <div class="equipment-plan-title">
                                <strong>
                                    {{ link.maintenancePlanName }}
                                </strong>

                                <span class="equipment-plan-status" :class="{
                                    'equipment-plan-status--active': link.active,
                                    'equipment-plan-status--inactive': !link.active
                                }">
                                    {{ link.active ? 'Ativo' : 'Inativo' }}
                                </span>
                            </div>

                            <span>
                                Início do monitoramento:
                                {{ link.startHourMeter }} h
                            </span>
                        </div>
                        <button v-if="canManagePlans" type="button" class="equipment-plan-status-button"
                            @click="handleToggleStatus(link)">
                            <Power :size="15" />
                            {{
                                link.active
                                    ? 'Desativar'
                            : 'Ativar'
                            }}
                        </button>
                    </div>

                    <p v-if="equipmentPlans.length === 0" class="equipment-plans-empty">
                        Nenhum plano vinculado a este equipamento.
                    </p>
                </div>
            </template>
        </div>

        <template #footer>
            <button type="button" class="save-button" @click="$emit('close')">
                Fechar
            </button>
        </template>
    </BaseModal>
</template>