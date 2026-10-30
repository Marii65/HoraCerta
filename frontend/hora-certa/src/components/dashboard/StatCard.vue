<script setup>
defineProps({
  value: {
    type: Number,
    required: true
  },

  label: {
    type: String,
    required: true
  },

  description: {
    type: String,
    required: true
  },

  icon: {
  type: [Object, Function],
  required: true
},

  variant: {
    type: String,
    default: 'default'
  }
});
</script>

<template>
  <article
    class="stat-card"
    :class="`stat-card--${variant}`"
  >
    <div class="stat-icon">
      <component
        :is="icon"
        :size="22"
        stroke-width="1.8"
      />
    </div>

    <div class="stat-content">
      <strong class="stat-value">
        {{ value }}
      </strong>

      <span class="stat-label">
        {{ label }}
      </span>

      <small class="stat-description">
        {{ description }}
      </small>
    </div>
  </article>
</template>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;

  min-height: 120px;

  padding: 22px;

  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);

  background-color: var(--color-surface);

  box-shadow: var(--shadow-sm);

  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}


/*ÍCONE*/

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;

  width: 52px;
  height: 52px;

  flex-shrink: 0;

  border-radius: 14px;

  background-color: var(--color-primary-light);
  color: var(--color-primary);
}


/*CONTEÚDO*/

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-value {
  color: var(--color-text-primary);

  font-size: 27px;
  line-height: 1;
}

.stat-label {
  margin-top: 6px;

  color: var(--color-text-primary);

  font-size: 13px;
  font-weight: 600;
}

.stat-description {
  margin-top: 5px;

  color: var(--color-text-muted);

  font-size: 11px;
}


/*VARIAÇÕES*/

.stat-card--success .stat-icon {
  background-color: var(--color-success-light);
  color: var(--color-success);
}

.stat-card--success .stat-description {
  color: var(--color-success);
}


.stat-card--warning .stat-icon {
  background-color: var(--color-warning-light);
  color: var(--color-warning);
}

.stat-card--warning .stat-description {
  color: #c68112;
}


.stat-card--danger .stat-icon {
  background-color: var(--color-danger-light);
  color: var(--color-danger);
}

.stat-card--danger .stat-description {
  color: var(--color-danger);
}
</style>