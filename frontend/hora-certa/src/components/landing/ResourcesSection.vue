<script setup>
import {
  Construction,
  ClipboardList,
  Gauge,
  BellRing,
  Wrench,
  ArrowRight
} from 'lucide-vue-next';

const resources = [
  {
    icon: Construction,
    title: 'Equipamentos',
    description:
      'Centralize seus equipamentos e acompanhe informações importantes de operação.'
  },
  {
    icon: ClipboardList,
    title: 'Planos de manutenção',
    description:
      'Crie rotinas preventivas com serviços, intervalos e margens de alerta.'
  },
  {
    icon: Gauge,
    title: 'Leituras de horímetro',
    description:
      'Registre as horas trabalhadas e mantenha o histórico de utilização atualizado.'
  },
  {
    icon: BellRing,
    title: 'Alertas preventivos',
    description:
      'Identifique rapidamente manutenções próximas ou que já ultrapassaram o prazo.'
  },
  {
    icon: Wrench,
    title: 'Ordens de serviço',
    description:
      'Organize as manutenções dos equipamentos e acompanhe cada intervenção.'
  }
];
</script>

<template>
  <section id="recursos" class="resources-section">
    <div class="landing-container">

      <header class="resources-header">
        <div>
          <span class="landing-section-eyebrow">
            RECURSOS
          </span>

          <h2>
            Tudo para manter seus equipamentos
            <span>sob controle.</span>
          </h2>
        </div>

        <p>
          Do registro do equipamento até a execução da manutenção,
          o HoraCerta reúne as informações necessárias para acompanhar
          toda a rotina preventiva.
        </p>
      </header>

      <div class="resources-grid">

        <article
          v-for="(resource, index) in resources"
          :key="resource.title"
          class="resource-card"
          :class="{
            'resource-card--featured': index === 0
          }"
        >
          <div class="resource-card-icon">
            <component
              :is="resource.icon"
              :size="22"
              :stroke-width="1.8"
            />
          </div>

          <div class="resource-card-content">
            <h3>{{ resource.title }}</h3>

            <p>
              {{ resource.description }}
            </p>
          </div>

          <div class="resource-card-arrow">
            <ArrowRight
              :size="18"
              :stroke-width="1.8"
            />
          </div>
        </article>

      </div>

    </div>
  </section>
</template>