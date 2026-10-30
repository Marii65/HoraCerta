<script setup>
import {
  Search,
  SlidersHorizontal
} from 'lucide-vue-next';

import { equipmentCategories } from '../../utils/equipmentCategories';


defineProps({
  search: {
    type: String,
    default: ''
  },

  category: {
    type: String,
    default: ''
  },

  status: {
    type: String,
    default: ''
  }
});


const emit = defineEmits([
  'update:search',
  'update:category',
  'update:status'
]);
</script>


<template>
  <section class="equipment-filters">

    <div class="equipment-search">

      <Search :size="18" />

      <input
        :value="search"
        type="text"
        placeholder="Buscar por código, nome ou modelo..."
        @input="emit('update:search', $event.target.value)"
      />

    </div>

    <div class="filter-actions">

      <select
        :value="category"
        @change="emit('update:category', $event.target.value)"
      >
        <option value="">
          Todas as categorias
        </option>

        <option
          v-for="equipmentCategory in equipmentCategories"
          :key="equipmentCategory"
          :value="equipmentCategory"
        >
          {{ equipmentCategory }}
        </option>
      </select>

      <select
        :value="status"
        @change="emit('update:status', $event.target.value)"
      >
        <option value="">
          Todos os status
        </option>

        <option value="ACTIVE">
          Ativos
        </option>

        <option value="INACTIVE">
          Inativos
        </option>
      </select>


      <button
        class="filter-button"
        type="button"
        aria-label="Mais filtros"
      >
        <SlidersHorizontal :size="18" />
      </button>

    </div>

  </section>
</template>