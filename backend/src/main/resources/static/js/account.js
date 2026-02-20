document.addEventListener("DOMContentLoaded", async () => {
    if (!Auth.isLoggedIn()) {
        const redirect = "/html/account.html";
        window.location.href = `/html/login.html?redirect=${encodeURIComponent(redirect)}`;
        return;
    }

    try {
        await Promise.all([loadMe(), loadReservations(), loadMessages()]);
    } catch (error) {
        console.error(error);
        setTopMsg(error?.message || "Błąd ładowania danych konta", "error");

        if (error?.status === 401 || error?.status === 403) {
            Auth.logout();
            const redirect = "/html/account.html";
            window.location.href = `/html/login.html?redirect=${encodeURIComponent(redirect)}`;
        }
    }
});

function setTopMsg(text, kind = "info") {
    const el = document.getElementById("account-msg");
    if (!el) return;

    el.textContent = text || "";
    el.className = `status ${kind}`;
}

async function loadMe() {
    const box = document.getElementById("me-box");
    if (box) box.textContent = "Ładowanie danych...";

    const me = await Api.fetchJson("/api/me", { auth: true });

    if (box) {
        box.innerHTML = `
      <div class="account-top">
        <h2 class="account-welcome">
          Witaj, <span class="email">${escapeHtml(me.email)}</span>
        </h2>
      </div>
    `;
    }
}

async function loadReservations() {
    const box = document.getElementById("reservations-box");
    if (box) box.textContent = "Ładowanie...";

    const list = await Api.fetchJson("/api/me/reservations", { auth: true });

    if (!Array.isArray(list) || list.length === 0) {
        if (box) box.innerHTML = `<div class="muted">Brak rezerwacji.</div>`;
        return;
    }

    const rows = list.map(r => {
        const status = String(r.status || "").toUpperCase();
        const canCancel = status !== "ANULOWANE";

        return `
      <tr>
        <td>${escapeHtml(r.carName || ("#" + r.carId))}</td>
        <td>${escapeHtml(r.startDate)}</td>
        <td>${escapeHtml(r.endDate)}</td>
        <td>${renderStatusBadge(status)}</td>
        <td class="muted">${formatInstant(r.createdAt)}</td>
        <td style="text-align:right;">
          ${
            canCancel
                ? `<button class="btn danger btn-cancel-res" data-id="${escapeHtml(r.id)}">Anuluj</button>`
                : `<span class="muted">—</span>`
        }
        </td>
      </tr>
    `;
    }).join("");

    if (box) {
        box.innerHTML = `
      <div class="table-wrap">
        <table class="table">
          <thead>
            <tr>
              <th>Auto</th>
              <th>Od</th>
              <th>Do</th>
              <th>Status</th>
              <th>Utworzono</th>
              <th style="text-align:right;">Akcje</th>
            </tr>
          </thead>
          <tbody>${rows}</tbody>
        </table>
      </div>
    `;
    }

    wireCancelButtons();
}

function wireCancelButtons() {
    document.querySelectorAll(".btn-cancel-res").forEach(btn => {
        btn.addEventListener("click", async () => {
            const id = btn.getAttribute("data-id");
            if (!id) return;

            const ok = confirm("Na pewno chcesz anulować tę rezerwację?");
            if (!ok) return;

            btn.disabled = true;
            const oldText = btn.textContent;
            btn.textContent = "Anulowanie...";

            try {
                await Api.fetchJson(`/api/me/reservations/${encodeURIComponent(id)}/cancel`, {
                    method: "PATCH",
                    auth: true
                });

                setTopMsg("Rezerwacja anulowana", "ok");
                await loadReservations();
            } catch (error) {
                console.error(error);
                setTopMsg(error?.message || "Nie udało się anulować rezerwacji", "error");

                if (error?.status === 401 || error?.status === 403) {
                    Auth.logout();
                    const redirect = "/html/account.html";
                    window.location.href = `/html/login.html?redirect=${encodeURIComponent(redirect)}`;
                }
            } finally {
                btn.disabled = false;
                btn.textContent = oldText;
            }
        });
    });
}

async function loadMessages() {
    const box = document.getElementById("messages-box");
    if (box) box.textContent = "Ładowanie...";

    const list = await Api.fetchJson("/api/me/messages", { auth: true });

    if (!Array.isArray(list) || list.length === 0) {
        if (box) box.innerHTML = `<div class="muted">Brak wiadomości.</div>`;
        return;
    }

    const cards = list.map(m => `
    <div class="card-mini">
      <div style="display:flex; justify-content:space-between; gap:10px; align-items:center;">
        <div><b>${escapeHtml(m.subject || "(brak tematu)")}</b></div>
        ${m.status ? renderStatusBadge(String(m.status)) : ``}
      </div>

      <div class="muted" style="margin-top:6px;">
        ${m.createdAt ? formatInstant(m.createdAt) : ""}
      </div>

      <div style="margin-top:8px;">
        ${escapeHtml(shorten(m.message || "", 220))}
      </div>
    </div>
  `).join("");

    if (box) box.innerHTML = cards;
}

wireChangePassword();
wireDeleteAccount();

function showBoxMsg(id, text, type = "info") {
    const el = document.getElementById(id);
    if (!el) return;
    el.style.display = "block";
    el.className = `status ${type}`;
    el.textContent = text;
}

function wireChangePassword() {
    const form = document.getElementById("pass-form");
    if (!form) return;

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        const currentPassword = document.getElementById("pass-current").value;
        const newPassword = document.getElementById("pass-new").value;

        try {
            await Api.fetchJson("/api/me/change-password", {
                method: "POST",
                auth: true,
                body: { currentPassword, newPassword }
            });

            showBoxMsg("pass-msg", "Hasło zmienione", "ok");
            form.reset();

        } catch (err) {
            showBoxMsg("pass-msg", err?.message || "Błąd zmiany hasła", "error");
        }
    });
}

function wireDeleteAccount() {
    const form = document.getElementById("delete-form");
    if (!form) return;

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const password = document.getElementById("delete-pass").value;
        const sure = confirm("Na pewno usunąć konto? Tej operacji nie da się cofnąć.");
        if (!sure) return;

        try {
            await Api.fetchJson("/api/me", {
                method: "DELETE",
                auth: true,
                body: { password }
            });

            Auth.logout();
            showBoxMsg("delete-msg", "Konto usunięte", "ok");
            window.location.href = "/html/index.html";

        } catch (error) {
            showBoxMsg("delete-msg", error?.message || "Błąd usuwania konta", "error");
        }
    });
}

function renderStatusBadge(statusRaw) {
    const s = String(statusRaw || "").toUpperCase();

    let cls = "badge";
    if (s.includes("OCZEKUJACE")) cls += " warn";
    else if (s.includes("POTWIERDZONE")) cls += " ok";
    else if (s.includes("ANULOWANE")) cls += " bad";

    return `<span class="${cls}">${escapeHtml(s)}</span>`;
}

function shorten(s, n) {
    s = String(s || "");
    return s.length > n ? s.slice(0, n - 1) + "…" : s;
}

function formatInstant(v) {
    if (!v) return "";
    const d = new Date(v);
    if (Number.isNaN(d.getTime())) return String(v);
    return d.toLocaleString("pl-PL");
}

function escapeHtml(str) {
    return String(str ?? "").replace(/[&<>"']/g, s => ({
        "&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#039;"
    }[s]));
}