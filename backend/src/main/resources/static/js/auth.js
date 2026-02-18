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

        logout() {
            Api.clearToken();
        },

        isLoggedIn() {
            return !!Api.getToken();
        },

        setStatus(msg, type = "info") {
            const el = $("admin-status");
            if (!el) return;
            el.textContent = msg;
            el.className = `status ${type}`;
        },

        showLoggedInUI() {
            $("admin-auth").style.display = "none";
            $("admin-panel").style.display = "block";
        },

        showLoggedOutUI() {
            $("admin-auth").style.display = "block";
            $("admin-panel").style.display = "none";
        }
    };
})();
