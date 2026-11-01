<script setup>
import {
  Gauge,
  BellRing,
  CheckCircle2
} from 'lucide-vue-next';


const monitoringSteps = [
  {
    icon: Gauge,
    title: 'Registre o horímetro',
    description:
      'Mantenha as horas de operação dos equipamentos sempre atualizadas.'
  },
  {
    icon: BellRing,
    title: 'Antecipe manutenções',
    description:
      'Acompanhe a proximidade das revisões definidas nos planos de manutenção.'
  },
  {
    icon: CheckCircle2,
    title: 'Mantenha tudo em dia',
    description:
      'Organize as intervenções e reduza o risco de manutenções esquecidas.'
  }
];
</script>

<template>
  <section id="sobre" class="monitoring-section">
    <div class="landing-container monitoring-content">

      <div class="monitoring-text">

        <span class="landing-section-eyebrow">
          MONITORAMENTO POR HORÍMETRO
        </span>

        <h2>
          Saiba o momento certo de
          <span>realizar cada manutenção.</span>
        </h2>

        <p class="monitoring-description">
          O HoraCerta acompanha as horas de operação dos equipamentos
          e ajuda a identificar manutenções próximas ou vencidas.
        </p>

        <div class="monitoring-steps">

          <div
            v-for="step in monitoringSteps"
            :key="step.title"
            class="monitoring-step"
          >
            <div class="monitoring-step-icon">
              <component
                :is="step.icon"
                :size="20"
                :stroke-width="1.8"
              />
            </div>

            <div>
              <strong>{{ step.title }}</strong>
              <p>{{ step.description }}</p>
            </div>
          </div>

        </div>
      </div>

      <div class="monitoring-visual">

        <div class="monitoring-panel">

          <div class="monitoring-panel-header">
            <div>
              <span>Monitoramento</span>
              <strong>Escavadeira Hidráulica 320</strong>
            </div>

            <span class="monitoring-status">
              Em operação
            </span>
          </div>

          <div class="monitoring-hourmeter">
            <span>Horímetro atual</span>

            <div>
              <strong>1.485</strong>
              <span>horas</span>
            </div>
          </div>

          <div class="monitoring-maintenance-card">

            <div class="monitoring-maintenance-title">
              <div>
                <span>PRÓXIMA MANUTENÇÃO</span>
                <strong>Revisão Preventiva 1.500h</strong>
              </div>

              <strong class="monitoring-hours-left">
                15h
              </strong>
            </div>

            <div class="monitoring-progress">
              <div class="monitoring-progress-value"></div>
            </div>

            <div class="monitoring-progress-info">
              <span>1.485h atuais</span>
              <span>1.500h manutenção</span>
            </div>

          </div>

          <div class="monitoring-alert">

            <div class="monitoring-alert-icon">
              <BellRing :size="19" />
            </div>

            <div>
              <strong>Manutenção se aproximando</strong>
              <span>
                Faltam 15 horas para atingir o intervalo planejado.
              </span>
            </div>

          </div>

        </div>

      </div>

    </div>
  </section>
</template>