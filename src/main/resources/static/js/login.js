document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('login-form');
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');

    Api.get('/api/auth/me')
        .then(() => {
            window.location.href = '/dashboard.html';
        })
        .catch(() => {
        });

    form.addEventListener('submit', async (event) => {
        event.preventDefault();

        const payload = {
            username: usernameInput.value.trim(),
            password: passwordInput.value
        };

        const messageTarget = document.getElementById('login-message');
        messageTarget.innerHTML = '';

        try {
            if (!payload.username || !payload.password) {
                throw new Error('Username and password are required.');
            }

            await Api.post('/api/auth/login', payload);
            window.location.href = '/dashboard.html';
        } catch (error) {
            messageTarget.innerHTML = `<div class="message error">${error.message}</div>`;
        }
    });
});
