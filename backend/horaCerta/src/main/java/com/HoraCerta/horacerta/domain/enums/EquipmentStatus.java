package com.HoraCerta.horacerta.domain.enums;

/**
 * Define os possíveis estados operacionais de um equipamento.
 *
 * @author Maria Luiza
 */
public enum EquipmentStatus {
    /** Equipamento disponível para operação. */
    ACTIVE,
    /** Equipamento desativado no sistema. */
    INACTIVE,
    /** Equipamento em manutenção. */
    UNDER_MAINTENANCE
}
