(function () {
    function $(id) { return document.getElementById(id); }

    let cache = [];
    let currentMsgId = null;

    function esc(str) {
        return String(str ?? "").replace(/[&<>"']/g, s => ({
            "&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#039;"
        }[s]));
    }

    function fmtDate(iso) {
        if (!iso) return "";
        try {
            const d = new Date(iso);
            return d.toLocaleString();
        } catch {
            return String(iso);
        }
    }

    function badgeStatus(st) {
        if (st === "NEW") return `<span class="badge bad">NOWE</span>`;
        if (st === "READ") return `<span class="badge ok">PRZECZYTANE</span>`;
        if (st === "ARCHIVED") return `<span class="badge">ZARCHIWIZOWANE</span>`;
        return `<span class="badge">${esc(st)}</span>`;
    }

    function rowHtml(m, index) {
        const who = m.userEmail || m.email || "";

        return `
          <tr>
            <td>${index + 1}</td>
            <td>${esc(fmtDate(m.createdAt))}</td>
            <td style="white-space: normal; word-break: break-word;">${esc(who)}</td>
            <td style="white-space: normal; word-break: break-word;">${esc(m.subject)}</td>
            <td>${esc(m.category)}</td>
            <td>${badgeStatus(m.status)}</td>
            <td class="row-actions">
              <button class="btn" data-show="${m.id}">Pokaż</button>
              <button class="btn primary" data-read="${m.id}">PRZECZYTANE</button>
              <button class="btn danger" data-arch="${m.id}">ARCHIWIZUJ</button>
            </td>
          </tr>
        `;
    }

    async function load() {
        const tbody = $("messages-tbody");
        if (!tbody) return;

        const status = $("msg-status")?.value || "";
        const url = status
            ? `/api/admin/messages?status=${encodeURIComponent(status)}`
            : `/api/admin/messages`;

        tbody.innerHTML = `<tr><td colspan="7">Ładowanie...</td></tr>`;

        try {
            const list = await Api.fetchJson(url, { auth: true });
            cache = Array.isArray(list) ? list : [];

            tbody.innerHTML = cache.length
                ? cache.map((m, idx) => rowHtml(m, idx)).join("")
                : `<tr><td colspan="7">Brak wiadomości</td></tr>`;

        } catch (e) {
            if (e.status === 401 || e.status === 403) {
                tbody.innerHTML = `<tr><td colspan="7">Brak dostępu (ADMIN).</td></tr>`;
                return;
            }
            tbody.innerHTML = `<tr><td colspan="7">Błąd: ${esc(e.message)}</td></tr>`;
            Auth.setStatus(`Błąd: ${e.message}`, "error");
        }
    }

    async function setStatus(id, status) {
        await Api.fetchJson(`/api/admin/messages/${id}/status`, {
            method: "PATCH",
            auth: true,
            body: { status }
        });
    }

    function modalEl(id) { return document.getElementById(id); }

    function openModal() {
        const modal = modalEl("msg-modal");
        if (modal) modal.style.display = "block";
        document.body.style.overflow = "hidden";
    }

    function closeModal() {
        const modal = modalEl("msg-modal");
        if (modal) modal.style.display = "none";
        document.body.style.overflow = "";
        currentMsgId = null;
    }

    function setText(id, value) {
        const el = modalEl(id);
        if (el) el.textContent = value ?? "";
    }

    function showMessage(id) {
        const m = cache.find(x => Number(x.id) === Number(id));
        if (!m) return;

        currentMsgId = Number(id);

        setText("msg-modal-title", `Wiadomość użytkownika ${m.userEmail}`);
        setText("m-created", fmtDate(m.createdAt));
        setText("m-from", `${m.name} <${m.email}>`);
        setText("m-user", m.userEmail || "-");
        setText("m-cat", m.category || "-");
        setText("m-subject", m.subject || "-");
        setText("m-status", m.status || "-");

        const body = modalEl("m-message");
        if (body) body.textContent = m.message || "";

        openModal();
    }

    function bindModal() {
        modalEl("msg-modal-close")?.addEventListener("click", closeModal);
        document.querySelector("#msg-modal .modal-backdrop")?.addEventListener("click", closeModal);

        document.addEventListener("keydown", (e) => {
            if (e.key === "Escape") closeModal();
        });

        modalEl("m-btn-read")?.addEventListener("click", async () => {
            if (!currentMsgId) return;
            try {
                await setStatus(currentMsgId, "READ");
                Auth.setStatus(`Oznaczono jako PRZECZYTANE #${currentMsgId}`, "ok");
                closeModal();
                await load();
            } catch (e) {
                Auth.setStatus(`Błąd: ${e.message}`, "error");
            }
        });

        modalEl("m-btn-arch")?.addEventListener("click", async () => {
            if (!currentMsgId) return;
            try {
                await setStatus(currentMsgId, "ARCHIVED");
                Auth.setStatus(`Zarchiwizowano #${currentMsgId}`, "ok");
                closeModal();
                await load();
            } catch (e) {
                Auth.setStatus(`Błąd: ${e.message}`, "error");
            }
        });
    }

    function bindClicks() {
        const table = $("messages-table");
        if (!table) return;

        table.addEventListener("click", async (e) => {
            const t = e.target;
            if (!t) return;

            const showId = t.getAttribute?.("data-show");
            const readId = t.getAttribute?.("data-read");
            const archId = t.getAttribute?.("data-arch");

            try {
                if (showId) return void showMessage(Number(showId));

                if (readId) {
                    await setStatus(Number(readId), "READ");
                    Auth.setStatus(`Oznaczono jako PRZECZYTANE #${readId}`, "ok");
                    return void load();
                }

                if (archId) {
                    await setStatus(Number(archId), "ARCHIVED");
                    Auth.setStatus(`Zarchiwizowano #${archId}`, "ok");
                    return void load();
                }
            } catch (err) {
                Auth.setStatus(`Błąd: ${err.message}`, "error");
            }
        });

        $("btn-messages-refresh")?.addEventListener("click", () => void load());
        $("msg-status")?.addEventListener("change", () => void load());
    }

    async function init() {
        bindModal();
        bindClicks();

        const adminPanel = $("admin-panel");
        const visible = adminPanel && adminPanel.style.display !== "none";
        if (visible && Auth?.isLoggedIn?.()) await load();
    }

    window.AdminMessages = { init, load };

    document.addEventListener("DOMContentLoaded", () => { void init(); });
})();