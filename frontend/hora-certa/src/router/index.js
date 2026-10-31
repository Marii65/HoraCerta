
import { createRouter, createWebHistory } from 'vue-router';

import AppLayout from '../layouts/AppLayout.vue';

import DashboardPage from '../pages/DashboardPage.vue';
import EquipmentPage from '../pages/EquipmentPage.vue';
import MaintenancePlansPage from '../pages/MaintenancePlansPage.vue';
import ReadingsPage from '../pages/ReadingsPage.vue';
import WorkOrdersPage from '../pages/WorkOrdersPage.vue';
import LoginPage from '../pages/LoginPage.vue';
import RegisterPage from '../pages/RegisterPage.vue';

import { isAuthenticated } from '../services/authService';

const routes = [

  {
    path: '/',
    redirect: '/login'
  },

  // Rotas públicas
  {
    path: '/login',
    name: 'login',
    component: LoginPage,
    meta: {
      guestOnly: true
    }
  },

  {
    path: '/cadastro',
    name: 'register',
    component: RegisterPage,
    meta: {
      guestOnly: true
    }
  },

  // Sistema
  {
    path: '/app',
    component: AppLayout,
    meta: {
      requiresAuth: true
    },

    children: [
      {
        path: '',
        name: 'dashboard',
        component: DashboardPage,
        meta: {
          title: 'Painel Principal'
        }
      },

      {
        path: 'equipamentos',
        name: 'equipments',
        component: EquipmentPage,
        meta: {
          title: 'Equipamentos'
        }
      },

      {
        path: 'planos-manutencao',
        name: 'maintenance-plans',
        component: MaintenancePlansPage,
        meta: {
          title: 'Planos de Manutenção'
        }
      },

      {
        path: 'leituras',
        name: 'readings',
        component: ReadingsPage,
        meta: {
          title: 'Leituras'
        }
      },

      {
        path: 'ordens-servico',
        name: 'work-orders',
        component: WorkOrdersPage,
        meta: {
          title: 'Ordens de Serviço'
        }
      }
    ]
  },

  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
];


const router = createRouter({
  history: createWebHistory(),
  routes
});


router.beforeEach((to) => {

  const authenticated = isAuthenticated();

  if (
    to.matched.some(
      route => route.meta.requiresAuth
    ) &&
    !authenticated
  ) {
    return {
      name: 'login',
      query: {
        redirect: to.fullPath
      }
    };
  }

  if (
    to.meta.guestOnly &&
    authenticated
  ) {
    return {
      name: 'dashboard'
    };
  }
});


export default router;
