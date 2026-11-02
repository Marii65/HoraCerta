import { apiRequest } from './api';

async function handleResponse(response, defaultMessage) {
    if (response.ok) {
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

export async function getCurrentUser() {
    const response = await apiRequest(
        '/api/users/me',
        {
            method: 'GET'
        }
    );
    return handleResponse(
        response,
        'Não foi possível carregar os dados do usuário.'
    );
}