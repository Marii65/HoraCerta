<script setup>
import { useRouter } from 'vue-router';

import horaCertaLogo from '../../assets/images/logo/horacerta-logo.png';

const router = useRouter();

function goToLogin() {
    router.push('/login');
}

function scrollToSection(sectionId) {
    document
        .getElementById(sectionId)
        ?.scrollIntoView({
            behavior: 'smooth'
        });
}
</script>
<template>
    <header class="landing-header">
        <div class="landing-container landing-header-content">
            <button class="landing-logo" type="button" aria-label="Ir para o início" @click="scrollToSection('inicio')">
                <img :src="horaCertaLogo" alt="HoraCerta" />
            </button>
            <nav class="landing-nav">
                <button type="button" @click="scrollToSection('inicio')">
                    Início
                </button>
                <button type="button" @click="scrollToSection('recursos')">
                    Recursos
                </button>
                <button type="button" @click="scrollToSection('sobre')">
                    Sobre
                </button>
            </nav>
            <button class="landing-login-button" type="button" @click="goToLogin">
                Entrar
            </button>
        </div>  
    </header>
</template>