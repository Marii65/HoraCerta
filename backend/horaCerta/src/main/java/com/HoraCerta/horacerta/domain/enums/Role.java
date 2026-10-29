package com.HoraCerta.horacerta.domain.enums;

/**
 * Define os perfis de acesso disponíveis no sistema,
 * utilizados para controlar as permissões dos usuários.
 *
 * @author Maria Luiza
 */
public enum Role {
    /** Administrador com permissões administrativas do sistema. */
    ADMIN,
    /** Técnico responsável pelas atividades de manutenção. */
    TECHNICIAN,
    /** Operador responsável pelo registro de informações operacionais. */
    OPERATOR
}
