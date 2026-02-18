(function () {
    const TOKEN_KEY = "dreamcar_token";

    window.Api = {
        getToken: () => localStorage.getItem(TOKEN_KEY),
        setToken: (t) => localStorage.setItem(TOKEN_KEY, t),
        clearToken: () => localStorage.removeItem(TOKEN_KEY),

        async fetchJson(url, { method = "GET", body = undefined, auth = false } = {}) {
            method = (method || "GET").toUpperCase();

            const headers = { "Accept": "application/json" };

            if (auth) {
                const token = Api.getToken();
                if (token) headers["Authorization"] = `Bearer ${token}`;
            }

            const options = { method, headers };

            if (body !== undefined) {
                headers["Content-Type"] = "application/json";
                options.body = JSON.stringify(body);
            }

            const res = await fetch(url, options);

            if (res.status === 204) return null;

            const ct = res.headers.get("content-type") || "";
            const isJson = ct.includes("application/json");

            const data = isJson
                ? await res.json().catch(() => null)
                : await res.text().catch(() => null);

            if (!res.ok) {
                const msg =
                    (typeof data === "string" && data) ? data :
                        (data && data.message) ? data.message :
                            `HTTP ${res.status}`;
                const err = new Error(msg);
                err.status = res.status;
                err.data = data;
                throw err;
            }
            return data;
        }
    };
}) ();
