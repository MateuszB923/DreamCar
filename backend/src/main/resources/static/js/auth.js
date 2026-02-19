(function () {
    function $(id) { return document.getElementById(id); }

    window.Auth = {
        async login(email, password) {
            const data = await Api.fetchJson("/api/auth/login", {
                method: "POST",
                body: { email, password }
            });
            if (!data?.token) throw new Error("Brak tokenu z /api/auth/login");
            Api.setToken(data.token);
            return data.token;
        },

        async register(email, password) {
            return await Api.fetchJson("/api/auth/register", {
                method: "POST",
                body: { email, password, role: "USER" }
            });
        },

        logout() {
            Api.clearToken();
        },

        isLoggedIn() {
            return !!Api.getToken();
        },

        requireLogin(redirect) {
            const target = redirect || (window.location.pathname + window.location.search);
            window.location.href = `/html/login.html?redirect=${encodeURIComponent(target)}`;
        },

        setStatus(msg, type = "info") {
            const el = $("admin-status");
            if (!el) return;
            el.textContent = msg;
            el.className = `status ${type}`;
        },

        showLoggedInUI() {
            const auth = $("admin-auth");
            const panel = $("admin-panel");
            if (auth) auth.style.display = "none";
            if (panel) panel.style.display = "block";
        },

        showLoggedOutUI() {
            const auth = $("admin-auth");
            const panel = $("admin-panel");
            if (auth) auth.style.display = "block";
            if (panel) panel.style.display = "none";
        }
    };
})();
