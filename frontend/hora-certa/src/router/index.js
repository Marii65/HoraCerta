
import { createRouter, createWebHistory } from 'vue-router';

import AppLayout from '../layouts/AppLayout.vue';

import DashboardPage from '../pages/DashboardPage.vue';
import EquipmentPage from '../pages/EquipmentPage.vue';
import MaintenancePlansPage from '../pages/MaintenancePlansPage.vue';
import ReadingsPage from '../pages/ReadingsPage.vue';
import WorkOrdersPage from '../pages/WorkOrdersPage.vue';

const routes = [
  {
    path: '/',
    component: AppLayout,
    children: [
      {
        path: '',
        name: 'dashboard',
        component: DashboardPage,
        meta: { title: 'Painel Principal' }
      },
      {
        path: 'equipamentos',
        name: 'equipments',
        component: EquipmentPage,
        meta: { title: 'Equipamentos' }
      },
      {
        path: 'planos-manutencao',
        name: 'maintenance-plans',
        component: MaintenancePlansPage,
        meta: { title: 'Planos de Manutenção' }
      },
      {
        path: 'leituras',
        name: 'readings',
        component: ReadingsPage,
        meta: { title: 'Leituras de Horímetro' }
      },
      {
        path: 'ordens-servico',
        name: 'work-orders',
        component: WorkOrdersPage,
        meta: { title: 'Ordens de Serviço' }
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

export default router;
