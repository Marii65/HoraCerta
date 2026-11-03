<script setup>
import {
  onMounted,
  ref
} from 'vue';

import WelcomeBanner from '../components/dashboard/WelcomeBanner.vue';
import StatsCards from '../components/dashboard/StatsCards.vue';
import UpcomingMaintenance from '../components/dashboard/UpcomingMaintenance.vue';
import FleetDistribution from '../components/dashboard/FleetDistribution.vue';
import QuickActions from '../components/dashboard/QuickActions.vue';

import { getEquipments } from '../services/equipmentService';
import { getEquipmentPlans } from '../services/equipmentPlanService';
import { getMaintenancePlanItems } from '../services/maintenancePlanService';

import '../styles/pages/dashboard.css';

const stats = ref([
  {
    id: 'equipment',
    value: 0,
    label: 'Equipamentos',
    description: 'Total da frota',
    variant: 'default'
  },
  {
    id: 'ok',
    value: 0,
    label: 'Em dia',
    description: '0% da frota',
    variant: 'success'
  },
  {
    id: 'warning',
    value: 0,
    label: 'Em atenção',
    description: 'Necessitam planejamento',
    variant: 'warning'
  },
  {
    id: 'overdue',
    value: 0,
    label: 'Vencidos',
    description: 'Ação necessária',
    variant: 'danger'
  }
]);

const fleet = ref({
  total: 0,
  ok: 0,
  warning: 0,
  overdue: 0
});

const maintenances = ref([]);

function calculateMaintenance(
  equipment,
  equipmentPlan,
  item
) {
  const currentHorometer =
    Number(equipment.currentHorometer);

  const startHourMeter =
    Number(equipmentPlan.startHourMeter);

  const intervalHours =
    Number(item.intervalHours);

  const alertMarginHours =
    Number(item.alertMarginHours);

  const nextDue =
    startHourMeter + intervalHours;

  const alertStart =
    nextDue - alertMarginHours;

  let status = 'ok';

  if (currentHorometer >= nextDue) {
    status = 'overdue';
  } else if (
    currentHorometer >= alertStart
  ) {
    status = 'warning';
  }

  const elapsed =
    currentHorometer - startHourMeter;

  const progress =
    intervalHours > 0
      ? Math.max(
        0,
        Math.round(
          (elapsed / intervalHours) * 100
        )
      )
      : 0;
  const difference =
    nextDue - currentHorometer;

  let remainingText;

  if (difference < 0) {
    remainingText =
      `atraso de ${Math.abs(
        Math.round(difference)
      )}h`;
  } else if (difference === 0) {
    remainingText = 'vence agora';
  } else {
    remainingText =
      `em ${Math.round(difference)}h`;
  }

  return {
    id: `${equipmentPlan.id}-${item.id}`,
    code: equipment.code,
    equipment: equipment.name,
    category: equipment.category,
    maintenanceType: item.serviceDescription, progress, status,
    expectedDate:
      `${nextDue.toLocaleString(
        'pt-BR'
      )} h`,
    remainingText
  };
}

function getEquipmentMaintenanceStatus(
  equipmentMaintenances
) {
  if (
    equipmentMaintenances.some(
      (maintenance) =>
        maintenance.status === 'overdue'
    )
  ) {
    return 'overdue';
  }
  if (
    equipmentMaintenances.some(
      (maintenance) =>
        maintenance.status === 'warning'
    )
  ) {
    return 'warning';
  }
  return 'ok';
}

async function loadDashboard() {
  try {
    const equipments =
      await getEquipments();

    const allMaintenances = [];

    const equipmentStatuses = [];

    for (const equipment of equipments) {
      const equipmentPlans =
        await getEquipmentPlans(
          equipment.id
        );

      const activeEquipmentPlans =
        equipmentPlans.filter(
          (equipmentPlan) =>
            equipmentPlan.active
        );

      const equipmentMaintenances = [];

      for (
        const equipmentPlan
        of activeEquipmentPlans
      ) {
        const items =
          await getMaintenancePlanItems(
            equipmentPlan.maintenancePlanId
          );

        for (const item of items) {
          const maintenance =
            calculateMaintenance(
              equipment,
              equipmentPlan,
              item
            );

          allMaintenances.push(
            maintenance
          );

          equipmentMaintenances.push(
            maintenance
          );
        }
      }

      equipmentStatuses.push(
        getEquipmentMaintenanceStatus(
          equipmentMaintenances
        )
      );
    }

    const statusPriority = {
      overdue: 0,
      warning: 1,
      ok: 2
    };

    allMaintenances.sort(
      (a, b) => {
        const statusDifference =
          statusPriority[a.status] -
          statusPriority[b.status];

        if (statusDifference !== 0) {
          return statusDifference;
        }

        return b.progress - a.progress;
      }
    );

    const uniqueEquipmentMaintenances = [];

    const equipmentCodes = new Set();

    for (const maintenance of allMaintenances) {
      if (!equipmentCodes.has(maintenance.code)) {
        equipmentCodes.add(maintenance.code);
        uniqueEquipmentMaintenances.push(maintenance);
      }
    }

    maintenances.value =
      uniqueEquipmentMaintenances.slice(0, 4);


    const total =
      equipments.length;

    const ok =
      equipmentStatuses.filter(
        (status) => status === 'ok'
      ).length;

    const warning =
      equipmentStatuses.filter(
        (status) =>
          status === 'warning'
      ).length;

    const overdue =
      equipmentStatuses.filter(
        (status) =>
          status === 'overdue'
      ).length;

    fleet.value = {
      total,
      ok,
      warning,
      overdue
    };

    const okPercentage =
      total > 0
        ? Math.round(
          (ok / total) * 100
        )
        : 0;

    stats.value = [
      {
        id: 'equipment',
        value: total,
        label: 'Equipamentos',
        description: 'Total da frota',
        variant: 'default'
      },

      {
        id: 'ok',
        value: ok,
        label: 'Em dia',
        description:
          `${okPercentage}% da frota`,
        variant: 'success'
      },

      {
        id: 'warning',
        value: warning,
        label: 'Em atenção',
        description:
          'Necessitam planejamento',
        variant: 'warning'
      },

      {
        id: 'overdue',
        value: overdue,
        label: 'Vencidos',
        description:
          'Ação necessária',
        variant: 'danger'
      }
    ];

  } catch (error) {
    console.error(
      'Erro ao carregar dashboard:',
      error
    );
  }
}


onMounted(loadDashboard);
</script>


<template>
  <div class="dashboard-page">

    <WelcomeBanner />

    <StatsCards :stats="stats" />

    <div class="dashboard-grid">

      <div class="dashboard-main">

        <UpcomingMaintenance :maintenances="maintenances" />

      </div>

      <aside class="dashboard-side">

        <FleetDistribution :fleet="fleet" />

        <QuickActions />

      </aside>

    </div>

  </div>
</template>