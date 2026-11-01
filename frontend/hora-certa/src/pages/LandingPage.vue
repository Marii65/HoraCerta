<script setup>
import LandingHeader from '../components/landing/LandingHeader.vue';
import HeroSection from '../components/landing/HeroSection.vue';


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