<script setup>
import { ref } from 'vue';

import {
  Search,
  MoreVertical,
  Pencil,
  Power,
  ClipboardList
} from 'lucide-vue-next';

import { getEquipmentIcon } from '../../utils/equipmentIcons';
import StatusBadge from '../common/StatusBadge.vue';


defineProps({
  equipments: {
    type: Array,
    required: true
  }
});


const emit = defineEmits([
  'edit',
  'toggle-status',
  'manage-plans'
]);

const openMenuId = ref(null);

function toggleMenu(equipmentId) {
  openMenuId.value =
    openMenuId.value === equipmentId
      ? null
      : equipmentId;
}

function handleEdit(equipment) {
  openMenuId.value = null;

  emit('edit', equipment);
}

function handleToggleStatus(equipment) {
  openMenuId.value = null;

  emit('toggle-status', equipment);
}

function handleManagePlans(equipment) {
  openMenuId.value = null;

  emit('manage-plans', equipment);
}

</script>

<template>
  <section class="equipment-card">
    <div class="equipment-table-wrapper">
      <table class="system-table equipment-table">
        <thead>
          <tr>
            <th>Equipamento</th>
            <th>Categoria</th>
            <th>Horímetro atual</th>
            <th>Status</th>
            <th></th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="equipment in equipments" :key="equipment.id">
            <td>
              <div class="equipment-cell">
                <div class="equipment-thumbnail">
                  <img :src="getEquipmentIcon(equipment.category)" :alt="equipment.category || 'Equipamento'" />
                </div>

                <div class="equipment-info">
                  <strong>
                    {{ equipment.code }}
                  </strong>
                  <span>
                    {{ equipment.name }}
                  </span>
                  <small>
                    {{ equipment.model || 'Modelo não informado' }}
                  </small>
                </div>
              </div>
            </td>

            <td>
              {{ equipment.category || 'Não informada' }}
            </td>

            <td>
              <div class="horometer-value">
                <p>{{ equipment.currentHorometer }}</p>
                <span>h</span>
              </div>
            </td>

            <td>
              <StatusBadge :label="equipment.status === 'ACTIVE' ? 'Ativo' : 'Inativo'"
                :variant="equipment.status === 'ACTIVE' ? 'success' : 'neutral'" />
            </td>

            <td>
              <div class="equipment-actions">
                <button type="button" aria-label="Opções do equipamento" @click="toggleMenu(equipment.id)">
                  <MoreVertical :size="18" />
                </button>

                <div v-if="openMenuId === equipment.id" class="actions-menu" :class="{
                  'actions-menu-up':
                    equipment.id === equipments[equipments.length - 1]?.id
                }">
                  <button type="button" @click="handleEdit(equipment)">
                    <Pencil :size="15" />
                    Editar
                  </button>

                  <button type="button" @click="handleManagePlans(equipment)">
                    <ClipboardList :size="15" />
                    Planos de manutenção
                  </button>

                  <div class="actions-divider"></div>

                  <button type="button" :class="{
                    'action-danger':
                      equipment.status === 'ACTIVE',

                    'action-success':
                      equipment.status === 'INACTIVE'
                  }" @click="handleToggleStatus(equipment)">
                    <Power :size="15" />
                    {{
                      equipment.status === 'ACTIVE'
                        ? 'Desativar'
                        : 'Ativar'
                    }}
                  </button>
                </div>
              </div>
            </td>
          </tr>

          <tr v-if="equipments.length === 0">
            <td colspan="5" class="empty-state">
              <Search :size="26" />
              <strong>
                Nenhum equipamento encontrado
              </strong>
              <span>
                Tente alterar os termos da busca ou os filtros.
              </span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>