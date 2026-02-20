(function () {
    function $(id) { return document.getElementById(id); }

    let usersCache = [];
    let myEmail = null;

    function escapeHtml(str) {
        return String(str ?? "").replace(/[&<>"']/g, s => ({
            "&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#039;"
        }[s]));
    }

    function badge(locked) {
        return locked
            ? `<span class="badge bad">ZABLOKOWANE</span>`
            : `<span class="badge ok">AKTYWNE</span>`;
    }

    async function loadMe() {
        try {
            const me = await Api.fetchJson("/api/me", { auth: true });
            myEmail = (me?.email || me?.username || "").toLowerCase();
        } catch {
            myEmail = null;
        }
    }

    function isAdmin(email) {
        if (!myEmail || !email) return false;
        return String(email).toLowerCase() === myEmail;
    }

    function rowHtml(user, index) {
        const locked = !!user.locked;

        if (isAdmin(user.email)) {
            return `
              <tr>
                <td>${index + 1}</td>
                <td style="white-space: normal; word-break: break-word;">${escapeHtml(user.email)}</td>
                <td>${escapeHtml(user.role)}</td>
                <td>${badge(locked)}</td>
                <td><span class="badge ok">ADMIN</span></td>
              </tr>
            `;
        }

        const lockBtn = locked
            ? `<button class="btn" data-unblock="${user.id}">Odblokuj</button>`
            : `<button class="btn danger" data-block="${user.id}">Zablokuj</button>`;

        return `
          <tr>
            <td>${index + 1}</td>
            <td style="white-space: normal; word-break: break-word;">${escapeHtml(user.email)}</td>
            <td>${escapeHtml(user.role)}</td>
            <td>${badge(locked)}</td>
            <td class="row-actions">
              ${lockBtn}
              <button class="btn primary" data-reset="${user.id}">Reset hasła</button>
              <button class="btn danger" data-del-user="${user.id}">Usuń</button>
            </td>
          </tr>
        `;
    }

    async function load() {
        const tbody = $("users-tbody");
        if (!tbody) return;

        tbody.innerHTML = `<tr><td colspan="5">Ładowanie...</td></tr>`;

        try {
            await loadMe();

            const users = await Api.fetchJson("/api/admin/users", { auth: true });
            usersCache = Array.isArray(users) ? users : [];

            tbody.innerHTML = usersCache.length
                ? usersCache.map((user, idx) => rowHtml(user, idx)).join("")
                : `<tr><td colspan="5">Brak użytkowników</td></tr>`;

        } catch (e) {
            if (e.status === 401 || e.status === 403) {
                tbody.innerHTML = `<tr><td colspan="5">Brak dostępu (ADMIN).</td></tr>`;
                return;
            }
            tbody.innerHTML = `<tr><td colspan="5">Błąd: ${escapeHtml(e.message)}</td></tr>`;
        }
    }

    function findUserEmailById(id) {
        const user = usersCache.find(x => Number(x.id) === Number(id));
        return user?.email ?? null;
    }

    function guardNotMe(id) {
        const email = findUserEmailById(id);
        if (isAdmin(email)) {
            Auth.setStatus("Nie możesz wykonać tej akcji na swoim koncie admina", "error");
            return false;
        }
        return true;
    }

    async function blockUser(id) {
        if (!guardNotMe(id)) return;
        if (!confirm(`Zablokować użytkownika #${id}?`)) return;
        await Api.fetchJson(`/api/admin/users/${id}/block`, { method: "PATCH", auth: true });
        Auth.setStatus(`Zablokowano użytkownika #${id}`, "ok");
        await load();
    }

    async function unblockUser(id) {
        if (!guardNotMe(id)) return;
        await Api.fetchJson(`/api/admin/users/${id}/unblock`, { method: "PATCH", auth: true });
        Auth.setStatus(`Odblokowano użytkownika #${id}`, "ok");
        await load();
    }

    async function deleteUser(id) {
        if (!guardNotMe(id)) return;
        if (!confirm(`USUNĄĆ użytkownika #${id}?\nTo może usunąć jego rezerwacje/wiadomości.`)) return;
        await Api.fetchJson(`/api/admin/users/${id}`, { method: "DELETE", auth: true });
        Auth.setStatus(`Usunięto użytkownika #${id}`, "ok");
        await load();
    }

    async function resetPassword(id) {
        if (!guardNotMe(id)) return;
        if (!confirm(`Zresetować hasło użytkownika #${id}?`)) return;

        const res = await Api.fetchJson(`/api/admin/users/${id}/reset-password`, { method: "POST", auth: true });
        const tmp = res?.temporaryPassword ?? "(brak)";

        Auth.setStatus(`Zresetowano hasło usera #${id}`, "ok");
        alert(
            `Tymczasowe hasło dla użytkownika #${id}:\n\n${tmp}\n\n` +
            `Przekaż je użytkownikowi i poinformuj, aby po zalogowaniu zmienił hasło w "Konto".`
        );

        await load();
    }

    function bindClicks() {
        const table = $("users-table");
        if (!table) return;

        table.addEventListener("click", (e) => {
            const t = e.target;
            if (!t) return;

            const blockId = t.getAttribute?.("data-block");
            const unblockId = t.getAttribute?.("data-unblock");
            const delId = t.getAttribute?.("data-del-user");
            const resetId = t.getAttribute?.("data-reset");

            if (blockId) return void blockUser(Number(blockId));
            if (unblockId) return void unblockUser(Number(unblockId));
            if (delId) return void deleteUser(Number(delId));
            if (resetId) return void resetPassword(Number(resetId));
        });

        const refreshBtn = $("btn-users-refresh");
        if (refreshBtn) refreshBtn.addEventListener("click", () => void load());
    }

    async function init() {
        bindClicks();

        const adminPanel = $("admin-panel");
        const visible = adminPanel && adminPanel.style.display !== "none";

        if (visible && Auth?.isLoggedIn?.()) {
            await load();
        }
    }

    window.AdminUsers = { init, load };

    document.addEventListener("DOMContentLoaded", () => { void init(); });
})();