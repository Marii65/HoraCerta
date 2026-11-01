<script setup>
import { X } from 'lucide-vue-next';

defineProps({
    title: {
        type: String,
        required: true
    },

    description: {
        type: String,
        default: ''
    }
});

const emit = defineEmits(['close']);
</script>

<template>
    <div class="modal-overlay" @click.self="emit('close')">
        <div class="base-modal">

            <header class="modal-header">
                <div>
                    <h2>{{ title }}</h2>

                    <p v-if="description">
                        {{ description }}
                    </p>
                </div>

                <button class="modal-close" type="button" aria-label="Fechar" @click="emit('close')">
                    <X :size="19" />
                </button>
            </header>

            <div class="base-modal-content">
                <slot />
            </div>

            <footer v-if="$slots.footer" class="modal-footer">
                <slot name="footer" />
            </footer>

        </div>
    </div>
</template>