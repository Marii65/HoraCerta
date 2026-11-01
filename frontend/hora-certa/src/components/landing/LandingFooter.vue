<script setup>
import horaCertaLogo from '../../assets/images/logo/horacerta-logo.png';

function scrollToSection(sectionId) {
    document
        .getElementById(sectionId)
        ?.scrollIntoView({
            behavior: 'smooth'
        });
}
</script>

<template>
    <footer class="landing-footer">
        <div class="landing-container">

            <div class="landing-footer-main">

                <div class="landing-footer-brand">
                    <img :src="horaCertaLogo" alt="HoraCerta" />

                    <p>
                        Gestão de manutenção preventiva
                        baseada em horímetro.
                    </p>
                </div>

                <div class="landing-footer-navigation">
                    <span>Navegação</span>

                    <button type="button" @click="scrollToSection('inicio')">
                        Início
                    </button>

                    <button type="button" @click="scrollToSection('recursos')">
                        Recursos
                    </button>

                    <button type="button" @click="scrollToSection('sobre')">
                        Sobre
                    </button>
                </div>

            </div>

            <div class="landing-footer-bottom">
                <span>
                    © 2026 HoraCerta
                </span>

                <span>
                    Sistema de gestão de manutenção preventiva
                </span>
            </div>

        </div>
    </footer>
</template>