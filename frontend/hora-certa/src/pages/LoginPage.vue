<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

import {
    ArrowLeft,
    UserPlus,
    Eye,
    EyeOff,
    LockKeyhole,
    Mail
} from 'lucide-vue-next';

import { login } from '../services/authService';

import horaCertaLogo from '../assets/images/logo/horacerta-logo.png';
import fundoLogin from '../assets/images/fundo-login.png';

import '../styles/pages/login.css';


const router = useRouter();

const email = ref('');
const password = ref('');

const rememberMe = ref(false);
const showPassword = ref(false);

const errorMessage = ref('');
const isLoading = ref(false);

function goToHome() {
    router.push('/');
}

function togglePasswordVisibility() {
    showPassword.value = !showPassword.value;
}

async function handleLogin() {
    errorMessage.value = '';

    if (!email.value || !password.value) {
        errorMessage.value = 'Preencha o e-mail e a senha.';
        return;
    }

    try {
        isLoading.value = true;

        await login(
            {
                email: email.value.trim(),
                password: password.value
            },
            rememberMe.value
        );

        const redirect =
            router.currentRoute.value.query.redirect;

        await router.push(
            typeof redirect === 'string'
                ? redirect
                : '/app'
        );

    } catch (error) {
        errorMessage.value =
            error.message ||
            'Não foi possível realizar o login.';
    } finally {
        isLoading.value = false;
    }
}

function goToRegister() {
    router.push('/cadastro');
}

</script>


<template>

    <main class="login-page">

        <section class="login-card">

            <div class="login-presentation">

                <img :src="fundoLogin" alt="" class="login-background-image" />

                <div class="login-presentation-content">

                    <button class="login-logo-button" type="button" aria-label="Voltar para a página inicial"
                        @click="goToHome">
                        <img :src="horaCertaLogo" alt="HoraCerta" class="login-logo" />
                    </button>

                    <div class="login-presentation-text">

                        <h1>
                            Manutenção no
                            <br>
                            momento certo
                        </h1>

                        <span class="login-title-line"></span>

                        <p>
                            Controle seus equipamentos,
                            <br>
                            registre leituras e mantenha
                            <br>
                            a manutenção preventiva sempre
                            <br>
                            em dia.
                        </p>

                    </div>

                </div>

            </div>

            <div class="login-access">

                <div class="login-access-content">

                    <header class="login-header">

                        <button class="back-home-button" type="button" @click="goToHome">
                            <ArrowLeft :size="16" />
                            Voltar ao início
                        </button>

                        <h2>
                            Bem-vindo(a)
                        </h2>

                        <p>
                            Faça login para acessar a central
                            <br>
                            de monitoramento
                        </p>

                    </header>


                    <form class="login-form" @submit.prevent="handleLogin">

                        <div class="login-field">

                            <label for="email">
                                Usuário ou e-mail
                            </label>

                            <div class="login-input">

                                <Mail :size="14" />

                                <input id="email" v-model="email" type="email" autocomplete="email"
                                    placeholder="Digite seu usuário ou e-mail" />

                            </div>

                        </div>

                        <div class="login-field">

                            <label for="password">
                                Senha
                            </label>

                            <div class="login-input">

                                <LockKeyhole :size="14" />

                                <input id="password" v-model="password" :type="showPassword
                                    ? 'text'
                                    : 'password'
                                    " autocomplete="current-password" placeholder="Digite sua senha" />

                                <button class="password-toggle" type="button" @click="togglePasswordVisibility">
                                    <EyeOff v-if="showPassword" :size="14" />

                                    <Eye v-else :size="14" />
                                </button>

                            </div>
                        </div>

                        <div class="login-options">
                            <label class="remember-option">
                                <input v-model="rememberMe" type="checkbox" />

                                <span>
                                    Lembrar de mim
                                </span>
                            </label>

                            <button class="forgot-password" type="button">
                                Esqueceu a senha?
                            </button>

                        </div>


                        <p v-if="errorMessage" class="login-error">
                            {{ errorMessage }}
                        </p>

                        <button class="login-submit" type="submit" :disabled="isLoading">
                            {{
                                isLoading
                                    ? 'Entrando...'
                                    : 'Entrar'
                            }}
                        </button>

                        <div class="login-divider">
                            <span></span>

                            <small>
                                ou
                            </small>

                            <span></span>
                        </div>

                        <button class="login-qr-button" type="button" @click="goToRegister">

                            <UserPlus :size="15" />

                            Cadastrar-se

                        </button>

                    </form>
                </div>
            </div>
        </section>
    </main>
</template>