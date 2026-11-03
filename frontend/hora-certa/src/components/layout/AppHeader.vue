<script setup>
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

import {
  CalendarDays,
  Bell,
  ChevronDown,
  UserRound,
  Users,
  LogOut
} from 'lucide-vue-next';

import { logout } from '../../services/authService';
import { getCurrentUser } from '../../services/userService';

const router = useRouter();

const isProfileMenuOpen = ref(false);
const currentUser = ref(null);

const isAdmin = computed(() =>
  currentUser.value?.role === 'ADMIN'
);

const roleLabels = {
  ADMIN: 'Administrador',
  TECHNICIAN: 'Técnico',
  OPERATOR: 'Operador'
};

const userInitials = computed(() => {

  if (!currentUser.value?.username) {
    return '--';
  }

  const names = currentUser.value.username
    .trim()
    .split(/\s+/);

  if (names.length === 1) {
    return names[0]
      .substring(0, 2)
      .toUpperCase();
  }

  return (
    names[0][0] +
    names[names.length - 1][0]
  ).toUpperCase();
});

const userRole = computed(() => {
  return (
    roleLabels[currentUser.value?.role] ||
    'Usuário'
  );
});

function toggleProfileMenu() {
  isProfileMenuOpen.value =
    !isProfileMenuOpen.value;
}

async function handleLogout() {
  logout();

  isProfileMenuOpen.value = false;

  await router.push('/login');
}

async function loadCurrentUser() {
  try {
    currentUser.value =
      await getCurrentUser();

  } catch (error) {
    console.error(
      'Erro ao carregar usuário:',
      error
    );
  }
}

onMounted(() => {
  loadCurrentUser();
});

async function goToUsers() {
  isProfileMenuOpen.value = false;

  await router.push('/app/usuarios');
}
</script>

<template>
  <header class="app-header">

    <div class="header-actions">

      <!-- Data -->
      <div class="header-date">
        <CalendarDays :size="19" />

        <div>
          <strong>26 de Outubro de 2026</strong>
          <span>Segunda-feira</span>
        </div>
      </div>

      <button class="notification-button" type="button" aria-label="Notificações">
        <Bell :size="20" />

        <span class="notification-badge">
          3
        </span>
      </button>

      <div class="user-profile-wrapper">

        <button class="user-profile" type="button" :aria-expanded="isProfileMenuOpen" @click="toggleProfileMenu">
          <div class="user-avatar">
            {{ userInitials }}
          </div>

          <div class="user-info">
            <strong>
              {{ currentUser?.username || 'Carregando...' }}
            </strong>

            <span>
              {{ userRole }}
            </span>
          </div>

          <ChevronDown :size="17" class="profile-chevron" :class="{ 'profile-chevron-open': isProfileMenuOpen }" />
        </button>

        <div v-if="isProfileMenuOpen" class="profile-dropdown">
          <button class="profile-dropdown-item" type="button">
            <UserRound :size="17" />
            <span>Meu perfil</span>
          </button>

          <button v-if="isAdmin" class="profile-dropdown-item" type="button" @click="goToUsers">
            <Users :size="17" />
            <span>Gerenciar usuários</span>
          </button>

          <div class="profile-dropdown-divider"></div>

          <button class="profile-dropdown-item profile-dropdown-logout" type="button" @click="handleLogout">
            <LogOut :size="17" />
            <span>Sair</span>
          </button>
        </div>

      </div>

    </div>

  </header>
</template>