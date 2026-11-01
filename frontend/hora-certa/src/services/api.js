import { getToken, logout } from './authService';

const API_URL = 'http://localhost:8080';

export async function apiRequest(endpoint, options = {}) {

    const token = getToken();

    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };

    if (token) {
        headers.Authorization = `Bearer ${token}`;
    }

    const response = await fetch(
        `${API_URL}${endpoint}`,
        {
            ...options,
            headers
        }
    );

    if (response.status === 401) {
        logout();

        window.location.href = '/login';

        throw new Error(
            'Sua sessão expirou. Faça login novamente.'
        );
    }

    return response;
}