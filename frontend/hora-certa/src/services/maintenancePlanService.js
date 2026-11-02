import { apiRequest } from './api';

async function handleResponse(response, defaultMessage) {
    if (response.ok) {
        if (response.status === 204) {
            return null;
        }

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
    } catch {
    }

    throw new Error(message);
}

export async function getMaintenancePlans() {
    const response = await apiRequest(
        '/api/maintenance-plans',
        {
            method: 'GET'
        }
    );

    return handleResponse(
        response,
        'Não foi possível carregar os planos de manutenção.'
    );
}

export async function getMaintenancePlanById(id) {
    const response = await apiRequest(
        `/api/maintenance-plans/${id}`,
        {
            method: 'GET'
        }
    );

    return handleResponse(
        response,
        'Não foi possível carregar o plano de manutenção.'
    );
}

export async function getMaintenancePlanItems(planId) {
    const response = await apiRequest(
        `/api/maintenance-plans/${planId}/items`,
        {
            method: 'GET'
        }
    );

    return handleResponse(
        response,
        'Não foi possível carregar os serviços do plano.'
    );
}

export async function createMaintenancePlan(data) {
    const response = await apiRequest(
        '/api/maintenance-plans',
        {
            method: 'POST',
            body: JSON.stringify(data)
        }
    );

    return handleResponse(
        response,
        'Não foi possível cadastrar o plano de manutenção.'
    );
}

export async function updateMaintenancePlan(id, data) {
    const response = await apiRequest(
        `/api/maintenance-plans/${id}`,
        {
            method: 'PUT',
            body: JSON.stringify(data)
        }
    );

    return handleResponse(
        response,
        'Não foi possível atualizar o plano de manutenção.'
    );
}

export async function toggleMaintenancePlanStatus(id) {
    const response = await apiRequest(
        `/api/maintenance-plans/${id}/status`,
        {
            method: 'PATCH'
        }
    );

    return handleResponse(
        response,
        'Não foi possível alterar o status do plano.'
    );
}

export async function deleteMaintenancePlan(id) {
    const response = await apiRequest(
        `/api/maintenance-plans/${id}`,
        {
            method: 'DELETE'
        }
    );

    return handleResponse(
        response,
        'Não foi possível excluir o plano de manutenção.'
    );
}

export async function addMaintenancePlanItem(planId, data) {
    const payload = {
        maintenancePlanId: planId,
        serviceDescription: data.serviceDescription,
        intervalHours: Number(data.intervalHours),
        alertMarginHours: Number(data.alertMarginHours)
    };

    const response = await apiRequest(
        `/api/maintenance-plans/${planId}/items`,
        {
            method: 'POST',
            body: JSON.stringify(payload)
        }
    );

    return handleResponse(
        response,
        'Não foi possível adicionar o serviço ao plano.'
    );
}

export async function updateMaintenancePlanItem(itemId, data) {
  const response = await apiRequest(
    `/api/maintenance-plans/items/${itemId}`,
    {
      method: 'PUT',
      body: JSON.stringify({
        serviceDescription: data.serviceDescription,
        intervalHours: Number(data.intervalHours),
        alertMarginHours: Number(data.alertMarginHours)
      })
    }
  );

  return handleResponse(
    response,
    'Não foi possível atualizar o serviço.'
  );
}

export async function deleteMaintenancePlanItem(itemId) {
  const response = await apiRequest(
    `/api/maintenance-plans/items/${itemId}`,
    {
      method: 'DELETE'
    }
  );

  return handleResponse(
    response,
    'Não foi possível excluir o serviço.'
  );
}