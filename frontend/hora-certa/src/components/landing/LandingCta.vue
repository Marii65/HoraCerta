<script setup>
import { useRouter } from 'vue-router';

import {
    ArrowRight,
    CheckCircle2
} from 'lucide-vue-next';

import dashboardMaintenance from '../../assets/images/dashboard-maintenance.png';

const router = useRouter();

function goToLogin() {
    router.push('/login');
}
</script>

<template>
    <section class="landing-cta" :style="{
        '--cta-background-image': `url(${dashboardMaintenance})`
    }">
        <div class="landing-container">

            <div class="landing-cta-content">

                <div class="landing-cta-text">
                    <span class="landing-cta-eyebrow">
                        HORA CERTA PARA CUIDAR DOS SEUS EQUIPAMENTOS
                    </span>

                    <h2>
                        Antecipe manutenções.
                        <span>Mantenha tudo sob controle.</span>
                    </h2>

                    <p>
                        Centralize equipamentos, acompanhe horímetros e organize
                        suas manutenções preventivas em um só lugar.
                    </p>

                    <div class="landing-cta-benefits">
                        <span>
                            <CheckCircle2 :size="16" />
                            Monitoramento por horímetro
                        </span>

                        <span>
                            <CheckCircle2 :size="16" />
                            Alertas preventivos
                        </span>

                        <span>
                            <CheckCircle2 :size="16" />
                            Gestão centralizada
                        </span>
                    </div>
                </div>

                <div class="landing-cta-action">
                    <button type="button" @click="goToLogin">
                        Acessar o HoraCerta
                        <ArrowRight :size="18" />
                    </button>

                    <span>
                        Entre na sua conta para começar.
                    </span>
                </div>

            </div>

        </div>
    </section>
</template>