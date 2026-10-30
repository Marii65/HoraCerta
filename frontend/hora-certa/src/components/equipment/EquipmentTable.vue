<script setup>
import { ref } from 'vue';

import {
  Search,
  MoreVertical,
  Pencil,
  Power
} from 'lucide-vue-next';

import { getEquipmentIcon } from '../../utils/equipmentIcons';


defineProps({
  equipments: {
    type: Array,
    required: true
  }
});


const emit = defineEmits([
  'edit',
  'toggle-status'
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


function getStatusLabel(status) {
  return status === 'ACTIVE'
    ? 'Ativo'
    : 'Inativo';
}
</script>


<template>
  <section class="equipment-card">

    <div class="equipment-table-wrapper">

      <table class="equipment-table">

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

          <tr
            v-for="equipment in equipments"
            :key="equipment.id"
          >

            <td>
              <div class="equipment-cell">

                <div class="equipment-thumbnail">
                  <img
                    :src="getEquipmentIcon(equipment.category)"
                    :alt="equipment.category"
                  />
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
              <strong class="horometer-value">
                {{ equipment.currentHorometer }} h
              </strong>
            </td>

            <td>
              <span
                class="equipment-status"
                :class="{
                  'equipment-status--active':
                    equipment.status === 'ACTIVE',

                  'equipment-status--inactive':
                    equipment.status === 'INACTIVE'
                }"
              >
                <span class="status-dot"></span>

                {{ getStatusLabel(equipment.status) }}
              </span>
            </td>

            <td>
              <div class="equipment-actions">

                <button
                  class="more-button"
                  type="button"
                  aria-label="Opções do equipamento"
                  @click="toggleMenu(equipment.id)"
                >
                  <MoreVertical :size="18" />
                </button>


                <div
                  v-if="openMenuId === equipment.id"
                  class="actions-menu"
                >

                  <button
                    type="button"
                    @click="handleEdit(equipment)"
                  >
                    <Pencil :size="15" />

                    Editar
                  </button>


                  <div class="actions-divider"></div>


                  <button
                    type="button"
                    :class="{
                      'action-danger':
                        equipment.status === 'ACTIVE',

                      'action-success':
                        equipment.status === 'INACTIVE'
                    }"
                    @click="handleToggleStatus(equipment)"
                  >
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

            <td
              colspan="5"
              class="empty-state"
            >
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