package com.HoraCerta.horacerta.domain.repository;

import com.HoraCerta.horacerta.domain.entity.MaintenancePlanItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenancePlanItemRepository extends JpaRepository<MaintenancePlanItem, Long> {

    List<MaintenancePlanItem> findByMaintenancePlanId(Long maintenancePlanId);


}
