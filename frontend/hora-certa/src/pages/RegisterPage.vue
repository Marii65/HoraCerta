<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

import {
    Eye,
    EyeOff,
    LockKeyhole,
    Mail,
    User,
    LogIn
} from 'lucide-vue-next';

import horaCertaLogo from '../assets/images/logo/horacerta-logo.png';
import fundoLogin from '../assets/images/fundo-login.png';

import '../styles/pages/login.css';
import { register } from '../services/authService';


const router = useRouter();

const name = ref('');
const email = ref('');
const password = ref('');
const confirmPassword = ref('');

const showPassword = ref(false);
const showConfirmPassword = ref(false);

const errorMessage = ref('');
const isLoading = ref(false);


function togglePasswordVisibility() {
    showPassword.value = !showPassword.value;
}


function toggleConfirmPasswordVisibility() {
    showConfirmPassword.value = !showConfirmPassword.value;
}


function goToLogin() {
    router.push('/login');
}


async function handleRegister() {

    errorMessage.value = '';

    if (
        !name.value ||
        !email.value ||
        !password.value ||
        !confirmPassword.value
    ) {
        errorMessage.value =
            'Preencha todos os campos.';

        return;
    }

    if (password.value.length < 8) {
        errorMessage.value =
            'A senha deve  possuir pelo menos 8 caracteres';
        return;
    }

    if (password.value !== confirmPassword.value) {
        errorMessage.value =
            'As senhas não coincidem.';

        return;
    }

    try {
        isLoading.value = true;

        await register({
            username: name.value.trim(),
            email: email.value.trim(),
            password: password.value
        });

        await router.push('/login');
    } catch (error) {
        errorMessage.value =
            error.message ||
            'Não foi possivel realizar o cadastro.'
    } finally {
        isLoading.value = false;
    }
}

</script>

<template>
    <main class="login-page">
        <section class="login-card">
            <div class="login-presentation">
                <img :src="fundoLogin" alt="" class="login-background-image" />

                <div class="login-presentation-content">
                    <img :src="horaCertaLogo" alt="HoraCerta" class="login-logo" />

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

            <div class="login-access register-access">
                <div class="login-access-content">
                    <header class="login-header">

                        <h2>
                            Cadastre-se
                        </h2>

                        <p>
                            Crie sua conta para acessar a central
                            <br>
                            de monitoramento
                        </p>
                    </header>

                    <form class="login-form" @submit.prevent="handleRegister">
                        <div class="login-field">
                            <label for="name">
                                Nome
                            </label>

                            <div class="login-input">

                                <User :size="14" />

                                <input id="name" v-model="name" type="text" autocomplete="name"
                                    placeholder="Digite seu nome" />
                            </div>
                        </div>

                        <div class="login-field">
                            <label for="email">
                                E-mail
                            </label>

                            <div class="login-input">
                                <Mail :size="14" />

                                <input id="email" v-model="email" type="email" autocomplete="email"
                                    placeholder="Digite seu e-mail" />
                            </div>
                        </div>

                        <div class="login-field">
                            <label for="password">
                                Senha
                            </label>

                            <div class="login-input">
                                <LockKeyhole :size="14" />
                                <input id="password" v-model="password" :type="showPassword ? 'text' : 'password'"
                                    autocomplete="new-password" placeholder="Digite sua senha" />

                                <button class="password-toggle" type="button" @click="togglePasswordVisibility">

                                    <EyeOff v-if="showPassword" :size="14" />

                                    <Eye v-else :size="14" />
                                </button>
                            </div>
                        </div>

                        <div class="login-field">
                            <label for="confirmPassword">
                                Confirmar senha
                            </label>

                            <div class="login-input">
                                <LockKeyhole :size="14" />

                                <input id="confirmPassword" v-model="confirmPassword"
                                    :type="showConfirmPassword ? 'text' : 'password'" autocomplete="new-password"
                                    placeholder="Digite sua senha novamente" />

                                <button class="password-toggle" type="button" @click="toggleConfirmPasswordVisibility">

                                    <EyeOff v-if="showConfirmPassword" :size="14" />

                                    <Eye v-else :size="14" />
                                </button>
                            </div>
                        </div>

                        <p v-if="errorMessage" class="login-error">
                            {{ errorMessage }}
                        </p>

                        <button class="login-submit" type="submit" :disabled="isLoading">
                            {{
                                isLoading
                                    ? 'Cadastrando...'
                                    : 'Cadastrar'
                            }}
                        </button>

                        <div class="login-divider">
                            <span></span>

                            <small>
                                ou
                            </small>

                            <span></span>
                        </div>

                        <button class="login-qr-button" type="button" @click="goToLogin">

                            <LogIn :size="15" />

                            Já possui uma conta? Entrar

                        </button>
                    </form>
                </div>
            </div>
        </section>
    </main>
</template>