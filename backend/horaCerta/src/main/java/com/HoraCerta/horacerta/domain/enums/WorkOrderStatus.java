package com.HoraCerta.horacerta.domain.enums;

/**
 * Define os possíveis estados de uma ordem de serviço,
 * desde o agendamento até a conclusão ou o cancelamento.
 *
 * @author Maria Luiza
 */
public enum WorkOrderStatus {
    /** Ordem de serviço agendada, aguardando execução. */
    SCHEDULED,
    /** Ordem de serviço cuja execução foi iniciada. */
    IN_PROGRESS,
    /** Ordem de serviço concluída. */
    COMPLETED,
    /** Ordem de serviço cancelada. */
    CANCELLED
}
