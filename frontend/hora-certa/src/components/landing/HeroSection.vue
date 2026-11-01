<script setup>
import { useRouter } from 'vue-router';

import {
    ArrowRight,
    Gauge,
    CheckCircle2
} from 'lucide-vue-next';

const router = useRouter();

function goToLogin() {
    router.push('/login');
}

function goToResources() {
    document
        .getElementById('recursos')
        ?.scrollIntoView({
            behavior: 'smooth'
        });
}
</script>

<template>
    <section id="inicio" class="landing-hero">
        <div class="landing-container hero-content">
            <div class="hero-text">
                <span class="hero-eyebrow">
                    <Gauge :size="17" />
                    Gestão de manutenção preventiva
                </span>

                <h1>
                    Manutenção no
                    <span>momento certo.</span>
                </h1>

                <p class="hero-description">
                    Monitore seus equipamentos, acompanhe horímetros
                    e antecipe manutenções.
                </p>

                <div class="hero-actions">
                    <button class="hero-primary-button" type="button" @click="goToLogin">
                        Acessar sistema
                        <ArrowRight :size="18" />
                    </button>

                    <button class="hero-secondary-button" type="button" @click="goToResources">
                        Conhecer recursos
                    </button>
                </div>

                <div class="hero-benefits">

                    <span>
                        <CheckCircle2 :size="17" />
                        Monitoramento centralizado
                    </span>

                    <span>
                        <CheckCircle2 :size="17" />
                        Alertas preventivos
                    </span>
                </div>
            </div>

            <div class="hero-visual">
                <div class="hero-visual-decoration"></div>
                <div class="hero-dashboard-preview">

                    <div class="preview-header">
                        <div>
                            <span>Visão geral</span>
                            <strong>Monitoramento da frota</strong>
                        </div>

                        <span class="preview-status">
                            Operação ativa
                        </span>
                    </div>


                    <div class="preview-equipment">

                        <div class="preview-equipment-icon">
                            <Gauge :size="24" />
                        </div>

                        <div class="preview-equipment-info">
                            <span>Escavadeira Hidráulica 320</span>
                            <strong>1.485 h</strong>
                        </div>

                        <span class="preview-equipment-badge">
                            Normal
                        </span>
                    </div>


                    <div class="preview-maintenance">

                        <div class="preview-maintenance-header">
                            <div>
                                <span>Próxima manutenção</span>
                                <strong>Troca de óleo do motor</strong>
                            </div>

                            <strong>15 h</strong>
                        </div>

                        <div class="preview-progress">
                            <div class="preview-progress-value"></div>
                        </div>

                        <div class="preview-progress-labels">
                            <span>1.485 h</span>
                            <span>1.500 h</span>
                        </div>
                    </div>


                    <div class="preview-stats">

                        <div>
                            <strong>12</strong>
                            <span>Equipamentos</span>
                        </div>

                        <div>
                            <strong>03</strong>
                            <span>Próximas</span>
                        </div>

                        <div>
                            <strong>01</strong>
                            <span>Vencida</span>
                        </div>
                    </div>
                </div>

                <div class="hero-floating-alert">
                    <div class="floating-alert-icon">
                        <CheckCircle2 :size="20" />
                    </div>

                    <div>
                        <strong>Manutenção em dia</strong>
                        <span>Monitoramento atualizado</span>
                    </div>
                </div>
            </div>
        </div>
    </section>
</template>