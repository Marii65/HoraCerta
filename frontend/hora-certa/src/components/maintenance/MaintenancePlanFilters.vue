<script setup>
import { Search } from 'lucide-vue-next';

defineProps({
  search: {
    type: String,
    default: ''
  },

  status: {
    type: String,
    default: 'all'
  }
});


defineEmits([
  'update:search',
  'update:status'
]);
</script>


<template>
  <section class="plan-filters">

    <div class="plan-filters-toolbar">

      <div class="plan-filter-field plan-search-field">

        <label class="plan-search">

          <Search
            :size="17"
            stroke-width="1.8"
          />

          <input
            :value="search"
            type="text"
            placeholder="Buscar por nome ou descrição..."
            @input="
              $emit(
                'update:search',
                $event.target.value
              )
            "
          />

        </label>

      </div>

      <div class="plan-filter-field plan-status-field">

        <span class="plan-filter-label">
          Status
        </span>

        <select
          class="plan-status-filter"
          :value="status"
          @change="
            $emit(
              'update:status',
              $event.target.value
            )
          "
        >

          <option value="all">
            Todos
          </option>

          <option value="active">
            Ativos
          </option>

          <option value="inactive">
            Inativos
          </option>

        </select>

      </div>

    </div>

  </section>
</template>