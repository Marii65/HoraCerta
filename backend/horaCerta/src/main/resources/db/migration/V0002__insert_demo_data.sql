-- Dados de demonstração
-- Senha dos usuários demo: HoraCerta@2026

-- USERS
INSERT INTO users (
    id,
    username,
    email,
    password_hash,
    role,
    active,
    created_at
) VALUES
(
    UNHEX(REPLACE('11111111-1111-1111-1111-111111111111', '-', '')),
    'Administrador HoraCerta',
    'admin@horacerta.com',
    '$2a$10$IJvj1yO.kJzYU3P4V1Rtv..CCGJFxUETUV.XvKjUly6cbLhEJBAAG',
    'ADMIN',
    TRUE,
    NOW()
),
(
    UNHEX(REPLACE('22222222-2222-2222-2222-222222222222', '-', '')),
    'Carlos Técnico',
    'tecnico@horacerta.com',
    '$2a$10$IJvj1yO.kJzYU3P4V1Rtv..CCGJFxUETUV.XvKjUly6cbLhEJBAAG',
    'TECHNICIAN',
    TRUE,
    NOW()
),
(
    UNHEX(REPLACE('33333333-3333-3333-3333-333333333333', '-', '')),
    'Marcos Operador',
    'operador@horacerta.com',
    '$2a$10$IJvj1yO.kJzYU3P4V1Rtv..CCGJFxUETUV.XvKjUly6cbLhEJBAAG',
    'OPERATOR',
    TRUE,
    NOW()
);

-- EQUIPMENTS
INSERT INTO equipments (
    id,
    code,
    name,
    model,
    category,
    serial_number,
    current_horometer,
    status,
    created_at
) VALUES
(
    1,
    'TR-001',
    'Trator John Deere',
    '6110J',
    'Trator',
    'JD6110J-001',
    485.00,
    'ACTIVE',
    NOW()
),
(
    2,
    'RE-002',
    'Retroescavadeira CAT',
    '416F2',
    'Retroescavadeira',
    'CAT416F2-002',
    720.00,
    'ACTIVE',
    NOW()
),
(
    3,
    'ES-003',
    'Escavadeira Komatsu',
    'PC200',
    'Escavadeira',
    'KOMPC200-003',
    320.00,
    'ACTIVE',
    NOW()
),
(
    4,
    'TR-004',
    'Trator Massey Ferguson',
    'MF 6713',
    'Trator',
    'MF6713-004',
    950.00,
    'UNDER_MAINTENANCE',
    NOW()
),
(
    5,
    'PC-005',
    'Pá Carregadeira Volvo',
    'L60H',
    'Pá Carregadeira',
    'VOL60H-005',
    180.00,
    'ACTIVE',
    NOW()
);

-- MAINTENANCE PLANS
INSERT INTO maintenance_plans (
    id,
    name,
    description,
    active
) VALUES
(
    1,
    'Revisão 250 horas',
    'Plano de manutenção preventiva realizado a cada 250 horas de operação.',
    TRUE
),
(
    2,
    'Revisão 500 horas',
    'Revisão completa para equipamentos com ciclo de manutenção de 500 horas.',
    TRUE
),
(
    3,
    'Lubrificação periódica',
    'Plano destinado à inspeção e lubrificação dos principais componentes.',
    TRUE
);

-- MAINTENANCE PLAN ITEMS
INSERT INTO maintenance_plan_items (
    id,
    plan_id,
    service_description,
    interval_hours,
    alert_margin_hours
) VALUES
(
    1,
    1,
    'Troca de óleo do motor',
    250.00,
    30.00
),
(
    2,
    1,
    'Substituição do filtro de óleo',
    250.00,
    30.00
),
(
    3,
    1,
    'Inspeção do filtro de ar',
    250.00,
    25.00
),
(
    4,
    2,
    'Substituição do filtro de combustível',
    500.00,
    50.00
),
(
    5,
    2,
    'Inspeção do sistema hidráulico',
    500.00,
    50.00
),
(
    6,
    2,
    'Verificação das correias',
    500.00,
    40.00
),
(
    7,
    3,
    'Lubrificação dos pontos de articulação',
    100.00,
    15.00
),
(
    8,
    3,
    'Inspeção de pinos e buchas',
    100.00,
    15.00
);

-- EQUIPMENT PLANS
INSERT INTO equipment_plans (
    id,
    equipment_id,
    maintenance_plan_id,
    start_hour_meter,
    active,
    created_at
) VALUES
(
    1,
    1,
    1,
    250.00,
    TRUE,
    NOW()
),
(
    2,
    2,
    1,
    400.00,
    TRUE,
    NOW()
),
(
    3,
    3,
    2,
    0.00,
    TRUE,
    NOW()
),
(
    4,
    4,
    2,
    600.00,
    TRUE,
    NOW()
),
(
    5,
    5,
    3,
    90.00,
    TRUE,
    NOW()
);

-- HOROMETER READINGS
INSERT INTO horometer_reading (
    user_id,
    equipment_id,
    reading_value,
    read_at,
    observation
) VALUES
(
    UNHEX(REPLACE('33333333-3333-3333-3333-333333333333', '-', '')),
    1,
    420.00,
    DATE_SUB(NOW(), INTERVAL 20 DAY),
    'Leitura periódica do equipamento.'
),
(
    UNHEX(REPLACE('33333333-3333-3333-3333-333333333333', '-', '')),
    1,
    460.00,
    DATE_SUB(NOW(), INTERVAL 10 DAY),
    'Equipamento operando normalmente.'
),
(
    UNHEX(REPLACE('33333333-3333-3333-3333-333333333333', '-', '')),
    1,
    485.00,
    DATE_SUB(NOW(), INTERVAL 2 DAY),
    'Última leitura registrada.'
),
(
    UNHEX(REPLACE('33333333-3333-3333-3333-333333333333', '-', '')),
    2,
    650.00,
    DATE_SUB(NOW(), INTERVAL 15 DAY),
    'Leitura durante operação em campo.'
),
(
    UNHEX(REPLACE('33333333-3333-3333-3333-333333333333', '-', '')),
    2,
    720.00,
    DATE_SUB(NOW(), INTERVAL 1 DAY),
    'Horímetro atualizado após turno de operação.'
),
(
    UNHEX(REPLACE('22222222-2222-2222-2222-222222222222', '-', '')),
    3,
    280.00,
    DATE_SUB(NOW(), INTERVAL 18 DAY),
    'Inspeção realizada sem irregularidades.'
),
(
    UNHEX(REPLACE('33333333-3333-3333-3333-333333333333', '-', '')),
    3,
    320.00,
    DATE_SUB(NOW(), INTERVAL 3 DAY),
    'Equipamento disponível para operação.'
),
(
    UNHEX(REPLACE('22222222-2222-2222-2222-222222222222', '-', '')),
    4,
    900.00,
    DATE_SUB(NOW(), INTERVAL 12 DAY),
    'Equipamento encaminhado para avaliação.'
),
(
    UNHEX(REPLACE('22222222-2222-2222-2222-222222222222', '-', '')),
    4,
    950.00,
    DATE_SUB(NOW(), INTERVAL 1 DAY),
    'Equipamento em manutenção.'
),
(
    UNHEX(REPLACE('33333333-3333-3333-3333-333333333333', '-', '')),
    5,
    180.00,
    DATE_SUB(NOW(), INTERVAL 7 DAY),
    'Última leitura antes da inativação.'
);

-- WORK ORDERS
-- Agendada
INSERT INTO work_order (
    equipment_plan_id,
    maintenance_plan_item_id,
    maintenance_type,
    status,
    scheduled_horometer,
    scheduled_date,
    responsible_technician,
    notes
) VALUES (
    1,
    1,
    'PREVENTIVE',
    'SCHEDULED',
    500.00,
    DATE_ADD(CURDATE(), INTERVAL 3 DAY),
    'Carlos Técnico',
    'Troca preventiva de óleo programada.'
);

-- Em andamento
INSERT INTO work_order (
    equipment_plan_id,
    maintenance_plan_item_id,
    maintenance_type,
    status,
    scheduled_horometer,
    scheduled_date,
    responsible_technician,
    notes
) VALUES (
    4,
    4,
    'PREVENTIVE',
    'IN_PROGRESS',
    900.00,
    CURDATE(),
    'Carlos Técnico',
    'Revisão preventiva em andamento.'
);

-- Concluída
INSERT INTO work_order (
    equipment_plan_id,
    maintenance_plan_item_id,
    maintenance_type,
    status,
    scheduled_horometer,
    executed_horometer,
    scheduled_date,
    completed_at,
    responsible_technician,
    notes
) VALUES (
    3,
    5,
    'PREVENTIVE',
    'COMPLETED',
    250.00,
    255.00,
    DATE_SUB(CURDATE(), INTERVAL 15 DAY),
    DATE_SUB(NOW(), INTERVAL 14 DAY),
    'Carlos Técnico',
    'Manutenção concluída sem irregularidades.'
);

-- Cancelada
INSERT INTO work_order (
    equipment_plan_id,
    maintenance_plan_item_id,
    maintenance_type,
    status,
    scheduled_horometer,
    scheduled_date,
    responsible_technician,
    notes
) VALUES (
    2,
    2,
    'PREVENTIVE',
    'CANCELLED',
    650.00,
    DATE_SUB(CURDATE(), INTERVAL 5 DAY),
    'Carlos Técnico',
    'Ordem cancelada e reagendada para nova avaliação.'
);

-- Corretiva
INSERT INTO work_order (
    equipment_plan_id,
    maintenance_plan_item_id,
    maintenance_type,
    status,
    scheduled_date,
    responsible_technician,
    notes
) VALUES (
    2,
    NULL,
    'CORRECTIVE',
    'SCHEDULED',
    DATE_ADD(CURDATE(), INTERVAL 1 DAY),
    'Carlos Técnico',
    'Avaliação de ruído anormal identificado durante a operação.'
);