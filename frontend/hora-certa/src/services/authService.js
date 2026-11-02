const API_URL = 'http://localhost:8080/api/auth';

export async function login(credentials, rememberMe = false) {

    const response = await fetch(`${API_URL}/login`,
        {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: credentials.email,
                password: credentials.password
            })
        }
    );

    if (!response.ok) {
        throw new Error(
            'Email ou senha inválidos.'
        );
    }

    const data = await response.json();

    localStorage.removeItem('horacerta_token');
    sessionStorage.removeItem('horacerta_token');

    if(rememberMe){
        localStorage.setItem(
            'horacerta_token',
            data.token
        );
    } else {
        sessionStorage.setItem(
            'horacerta_token',
            data.token
        );
    }

    return data;
}

export async function register(userData) {

    const response = await fetch(
        `${API_URL}/register`,
        {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                username: userData.username,
                email: userData.email,
                password: userData.password
            })
        }
    );

    if (!response.ok) {
        let errorMessage =
            'Não foi possível realizar o cadastro.';

        try {
            const errorData = await response.json();

            errorMessage = errorData.message || errorMessage;
        } catch {
        }
        throw new Error(errorMessage);
    }
    return response.json();
}

export function logout() {
    localStorage.removeItem('horacerta_token');
    sessionStorage.removeItem('horacerta_token');
}

export function isAuthenticated() {

    const token = getToken();

    if (!token) {
        return false;
    }
    try {
        const payload = JSON.parse(
            atob(token.split('.')[1])
        );

        const expirationTime = payload.exp * 1000;
        if (Date.now() >= expirationTime) {
            logout();
            return false;
        }
        return true;
    } catch {
        logout();
        return false;
    }
}

export function getToken() {
    return (
        localStorage.getItem('horacerta_token') ||
        sessionStorage.getItem('horacerta_token')
    );
}

export function getCurrentUser() {
    const token = getToken();

    if (!token) {
        return null;
    }

    try {
        const payload = JSON.parse(
            atob(token.split('.')[1])
        );

        return payload;
    } catch {
        return null;
    }
}


