const API_URL = 'http://localhost:8080/api/auth';

export async function login(credentials) {

    const response = await fetch(`${API_URL}/auth/login`,
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

    localStorage.setItem(
        'horacerta_token',
        data.token
    );

    return data;
}

export function logout(){
    localStorage.removeItem(
        'horacerta_token'
    );
}

export function isAuthenticated(){
    return Boolean(
        localStorage.getItem(
            'horacerta_token'
        )
    );
}