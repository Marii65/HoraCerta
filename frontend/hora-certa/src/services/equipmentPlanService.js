import { apiRequest } from './api';

async function handleResponse(response, defaultMessage) {
  if (response.ok) {
    if (response.status === 204) return null;
    return response.json();
  }

  if (response.status === 403) {
    throw new Error(
      'Você não possui permissão para realizar esta ação.'
    );
  }

  let message = defaultMessage;

  try {
    const errorData = await response.json();

    message =
      errorData.message ||
      errorData.error ||
      defaultMessage;
  } catch {}

  throw new Error(message);
}

export async function createEquipmentPlan(
  equipmentId,
  maintenancePlanId,
  startHourMeter = null
) {
  const response = await apiRequest(
    '/api/equipment-plans',
    {
      method: 'POST',
      body: JSON.stringify({
        equipmentId: Number(equipmentId),
        maintenancePlanId: Number(maintenancePlanId),
        startHourMeter:
          startHourMeter === null ||
          startHourMeter === ''
            ? null
            : Number(startHourMeter)
      })
    }
  );

  return handleResponse(
    response,
    'Não foi possível vincular o plano ao equipamento.'
  );
}

export async function getEquipmentPlans(
  equipmentId
) {
  const response = await apiRequest(
    `/api/equipment-plans/equipment/${equipmentId}`,
    {
      method: 'GET'
    }
  );

  return handleResponse(
    response,
    'Não foi possível carregar os planos do equipamento.'
  );
}

export async function toggleEquipmentPlanStatus(
  equipmentPlanId
) {
  const response = await apiRequest(
    `/api/equipment-plans/${equipmentPlanId}/toggle`,
    {
      method: 'PATCH'
    }
  );

  return handleResponse(
    response,
    'Não foi possível alterar o vínculo do plano.'
  );
}