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

export async function getUsers() {
    const response = await apiRequest(
        '/api/users',
        {
            method: 'GET'
        }
    );

    return handleResponse(
        response,
        'Não foi possível carregar os usuários.'
    );
}

export async function updateUserRole(id, role) {
    const response = await apiRequest(
        `/api/users/${id}/role`,
        {
            method: 'PATCH',
            body: JSON.stringify({
                role
            })
        }
    );

    return handleResponse(
        response,
        'Não foi possível alterar o perfil do usuário.'
    );
}

export async function toggleUserStatus(id) {
    const response = await apiRequest(
        `/api/users/${id}/status`,
        {
            method: 'PATCH'
        }
    );

    return handleResponse(
        response,
        'Não foi possível alterar o status do usuário.'
    );
}