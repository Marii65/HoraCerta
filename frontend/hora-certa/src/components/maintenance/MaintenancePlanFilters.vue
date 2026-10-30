<script setup>
import { Search } from 'lucide-vue-next';


defineProps({
  search: {
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
  'update:status'
]);
</script>


<template>
  <section class="plan-filters">

    <div class="plan-search">

      <Search :size="18" />

      <input
        :value="search"
        type="text"
        placeholder="Buscar por nome ou descrição..."
        @input="emit('update:search', $event.target.value)"
      />

    </div>

    <select
      class="plan-status-filter"
      :value="status"
      @change="emit('update:status', $event.target.value)"
    >
      <option value="">
        Todos os status
      </option>

      <option value="true">
        Ativos
      </option>

      <option value="false">
        Inativos
      </option>

    </select>

  </section>
</template>