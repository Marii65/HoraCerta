<script setup>
import LandingHeader from '../components/landing/LandingHeader.vue';
import HeroSection from '../components/landing/HeroSection.vue';
import FeatureStrip from '../components/landing/FeatureStrip.vue';
import MonitoringSection from '../components/landing/MonitoringSection.vue';
import ResourcesSection from '../components/landing/ResourcesSection.vue';
import LandingCta from '../components/landing/LandingCta.vue';
import LandingFooter from '../components/landing/LandingFooter.vue';

import '../styles/pages/landing.css';
</script>

<template>
    <div class="landing-page">
        <LandingHeader />

        <main>
            <HeroSection />
            <FeatureStrip />
            <MonitoringSection />
            <ResourcesSection />
            <LandingCta />
        </main>
        
        <LandingFooter />

    </div>
</template>