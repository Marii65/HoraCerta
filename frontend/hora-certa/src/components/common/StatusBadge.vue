<script setup>
defineProps({
    label: {
        type: String,
        required: true
    },

    variant: {
        type: String,
        default: 'neutral',
        validator: (value) =>
            ['success', 'warning', 'danger', 'info', 'neutral'].includes(value)
    }
});
</script>

<template>
    <span class="status-badge" :class="`status-badge--${variant}`">
        {{ label }}
    </span>
</template>