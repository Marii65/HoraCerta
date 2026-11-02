<script setup>
import WelcomeBanner
  from '../components/dashboard/WelcomeBanner.vue';

import StatsCards
  from '../components/dashboard/StatsCards.vue';

import UpcomingMaintenance
  from '../components/dashboard/UpcomingMaintenance.vue';

import FleetDistribution
  from '../components/dashboard/FleetDistribution.vue';

import QuickActions
  from '../components/dashboard/QuickActions.vue';


import {
  dashboardStatsMock,
  dashboardFleetMock,
  upcomingMaintenancesMock
} from '../mocks/dashboard';

import '../styles/pages/dashboard.css';

const stats = dashboardStatsMock;

const fleet = dashboardFleetMock;

const maintenances = upcomingMaintenancesMock;
</script>


<template>
  <div class="dashboard-page">

    <WelcomeBanner />

    <StatsCards
      :stats="stats"
    />

    <div class="dashboard-grid">

      <div class="dashboard-main">

        <UpcomingMaintenance
          :maintenances="maintenances"
        />

      </div>

      <aside class="dashboard-side">

        <FleetDistribution
          :fleet="fleet"
        />

        <QuickActions />

      </aside>

    </div>

  </div>
</template>