window.Api = {
    async request(url, options = {}) {
        const requestOptions = {
            method: options.method || 'GET',
            credentials: 'same-origin',
            headers: {
                ...(options.body ? { 'Content-Type': 'application/json' } : {}),
                ...(options.headers || {})
            },
            body: options.body ? JSON.stringify(options.body) : undefined
        };

        const response = await fetch(url, requestOptions);
        const contentType = response.headers.get('content-type') || '';
        const data = contentType.includes('application/json') ? await response.json() : null;

        if (!response.ok) {
            const validationMessages = data?.errors ? Object.values(data.errors).join(' ') : '';
            const message = [data?.message || 'Request failed. Please try again.', validationMessages]
                .filter(Boolean)
                .join(' ')
                .trim();

            const error = new Error(message);
            error.status = response.status;
            error.data = data;
            throw error;
        }

        return data;
    },

    get(url) {
        return this.request(url);
    },

    post(url, body) {
        return this.request(url, { method: 'POST', body });
    }
};
