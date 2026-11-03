<script setup>
import {
    computed,
    onMounted,
    ref
} from 'vue';

import {
    Search,
    Shield,
    UserRound,
    Wrench
} from 'lucide-vue-next';

import PageHeader from '../components/common/PageHeader.vue';
import StatusBadge from '../components/common/StatusBadge.vue';

import {
    getUsers,
    updateUserRole,
    toggleUserStatus
} from '../services/userService';

import '../styles/pages/users.css';

const users = ref([]);

const isLoading = ref(false);
const errorMessage = ref('');

const searchTerm = ref('');
const selectedRole = ref('all');
const selectedStatus = ref('all');

const filteredUsers = computed(() => {
    const search = searchTerm.value
        .trim()
        .toLowerCase();

    return users.value.filter((user) => {
        const matchesSearch =
            !search ||
            user.username
                ?.toLowerCase()
                .includes(search) ||
            user.email
                ?.toLowerCase()
                .includes(search);

        const matchesRole =
            selectedRole.value === 'all' ||
            user.role === selectedRole.value;

        const matchesStatus =
            selectedStatus.value === 'all' ||
            (
                selectedStatus.value === 'active'
                    ? user.active
                    : !user.active
            );

        return (
            matchesSearch &&
            matchesRole &&
            matchesStatus
        );
    });
});

async function loadUsers() {
    isLoading.value = true;
    errorMessage.value = '';

    try {
        users.value = await getUsers();
    } catch (error) {
        errorMessage.value =
            error.message ||
            'Não foi possível carregar os usuários.';
    } finally {
        isLoading.value = false;
    }
}

async function handleRoleChange(user, event) {
    const previousRole = user.role;
    const newRole = event.target.value;

    if (newRole === previousRole) {
        return;
    }

    errorMessage.value = '';

    try {
        const updatedUser =
            await updateUserRole(
                user.id,
                newRole
            );

        const index = users.value.findIndex(
            (item) => item.id === user.id
        );

        if (index !== -1) {
            users.value[index] = updatedUser;
        }
    } catch (error) {
        user.role = previousRole;

        errorMessage.value =
            error.message ||
            'Não foi possível alterar o perfil do usuário.';
    }
}

async function handleToggleStatus(user) {
    const action =
        user.active
            ? 'desativar'
            : 'ativar';

    const confirmed = window.confirm(
        `Deseja realmente ${action} ${user.username}?`
    );

    if (!confirmed) {
        return;
    }

    errorMessage.value = '';

    try {
        const updatedUser =
            await toggleUserStatus(user.id);

        const index = users.value.findIndex(
            (item) => item.id === user.id
        );

        if (index !== -1) {
            users.value[index] = updatedUser;
        }
    } catch (error) {
        errorMessage.value =
            error.message ||
            'Não foi possível alterar o status do usuário.';
    }
}

function getRoleLabel(role) {
    const labels = {
        ADMIN: 'Administrador',
        TECHNICIAN: 'Técnico',
        OPERATOR: 'Operador'
    };

    return labels[role] ?? role;
}

function getRoleIcon(role) {
    if (role === 'ADMIN') {
        return Shield;
    }

    if (role === 'TECHNICIAN') {
        return Wrench;
    }

    return UserRound;
}

onMounted(loadUsers);
</script>

<template>
    <div class="users-page">

        <PageHeader title="Usuários" description="Gerencie os usuários e seus níveis de acesso." />

        <p v-if="errorMessage" class="users-error">
            {{ errorMessage }}
        </p>

        <div class="users-filters">

            <label class="users-search">
                <Search :size="17" />

                <input v-model="searchTerm" type="text" placeholder="Buscar por nome ou e-mail..." />
            </label>

            <select v-model="selectedRole">
                <option value="all">
                    Todos os perfis
                </option>

                <option value="ADMIN">
                    Administrador
                </option>

                <option value="TECHNICIAN">
                    Técnico
                </option>

                <option value="OPERATOR">
                    Operador
                </option>
            </select>

            <select v-model="selectedStatus">
                <option value="all">
                    Todos os status
                </option>

                <option value="active">
                    Ativos
                </option>

                <option value="inactive">
                    Inativos
                </option>
            </select>

        </div>

        <p v-if="isLoading" class="users-loading">
            Carregando usuários...
        </p>

        <div v-else class="users-table-wrapper">
            <table class="system-table users-table">

                <thead>
                    <tr>
                        <th>Usuário</th>
                        <th>Perfil</th>
                        <th>Status</th>
                        <th>Cadastro</th>
                        <th>Ações</th>
                    </tr>
                </thead>

                <tbody>

                    <tr v-for="user in filteredUsers" :key="user.id">
                        <td>
                            <div class="user-info">

                                <div class="user-avatar">
                                    <component :is="getRoleIcon(user.role)" :size="18" />
                                </div>

                                <div>
                                    <strong>
                                        {{ user.username }}
                                    </strong>

                                    <span>
                                        {{ user.email }}
                                    </span>
                                </div>

                            </div>
                        </td>

                        <td>
                            <select class="user-role-select" :value="user.role"
                                @change="handleRoleChange(user, $event)">
                                <option value="ADMIN">
                                    Administrador
                                </option>

                                <option value="TECHNICIAN">
                                    Técnico
                                </option>

                                <option value="OPERATOR">
                                    Operador
                                </option>
                            </select>
                        </td>

                        <td>
                            <StatusBadge :label="user.active ? 'Ativo' : 'Inativo'"
                                :variant="user.active ? 'success' : 'neutral'" />
                        </td>

                        <td>
                            {{
                                new Intl.DateTimeFormat('pt-BR')
                                    .format(new Date(user.createdAt)) }}
                        </td>

                        <td>
                            <button type="button" class="user-status-button" :class="{
                                'user-status-button--danger': user.active,
                                'user-status-button--success': !user.active
                            }" @click="handleToggleStatus(user)">
                                {{
                                    user.active
                                        ? 'Desativar'
                                : 'Ativar'
                                }}
                            </button>
                        </td>
                    </tr>

                    <tr v-if="filteredUsers.length === 0">
                        <td colspan="5" class="users-empty">
                            Nenhum usuário encontrado.
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>