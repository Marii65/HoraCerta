<script setup>
import {
  MonitorCog,
  BellRing,
  History,
  ClipboardCheck
} from 'lucide-vue-next';

const features = [
    {
    icon: MonitorCog,
    title: 'Controle',
    description: 'Equipamentos centralizados'
  },
  {
    icon: BellRing,
    title: 'Alertas',
    description: 'Manutenções no momento certo'
  },
  {
    icon: History,
    title: 'Histórico',
    description: 'Acompanhamento por horímetro'
  },
  {
    icon: ClipboardCheck,
    title: 'Ordens de serviço',
    description: 'Manutenções organizadas'
  }
]
</script>
<template>
    <section class="feature-strip">
        <div class="landing-container feature-strip-content">

            <div
        v-for="feature in features"
        :key="feature.title"
        class="feature-strip-item"
      >
        <div class="feature-strip-icon">
          <component
            :is="feature.icon"
            :size="21"
            :stroke-width="1.8"
          />
        </div>

        <div>
          <strong>{{ feature.title }}</strong>
          <span>{{ feature.description }}</span>
        </div>
      </div>

        </div>
    </section>
</template>