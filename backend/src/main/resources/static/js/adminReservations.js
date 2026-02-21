(function () {
    function getById(elementId) { return document.getElementById(elementId); }

    let reservationsCache = [];

    function escapeHtml(value) {
        return String(value ?? "").replace(/[&<>"']/g, (ch) => ({
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#039;"
        }[ch]));
    }

    function formatInstantToLocale(instantString) {
        if (!instantString) return "";
        try {
            const date = new Date(instantString);
            return date.toLocaleString();
        } catch {
            return String(instantString);
        }
    }

    function formatDateOnly(dateString) {
        return dateString ? String(dateString) : "";
    }

    function renderReservationStatusBadge(status) {
        if (status === "POTWIERDZONE") return `<span class="badge ok">POTWIERDZONE</span>`;
        if (status === "ANULOWANE") return `<span class="badge bad">ANULOWANE</span>`;
        if (status === "OCZEKUJACE") return `<span class="badge info">OCZEKUJACE</span>`;
        return `<span class="badge">${escapeHtml(status)}</span>`;
    }

    function renderReservationRow(reservationDto, rowIndex) {
        const canConfirm = reservationDto.status === "OCZEKUJACE";
        const canCancel = reservationDto.status !== "ANULOWANE";

        const carLabel = reservationDto.carName || ("#" + reservationDto.carId);

        return `
          <tr>
            <td>${rowIndex + 1}</td>
            <td>${escapeHtml(formatInstantToLocale(reservationDto.createdAt))}</td>
            <td style="white-space: normal; word-break: break-word;">${escapeHtml(reservationDto.userEmail)}</td>
            <td style="white-space: normal; word-break: break-word;">${escapeHtml(carLabel)}</td>
            <td>${escapeHtml(formatDateOnly(reservationDto.startDate))}</td>
            <td>${escapeHtml(formatDateOnly(reservationDto.endDate))}</td>
            <td>${renderReservationStatusBadge(reservationDto.status)}</td>
            <td class="row-actions">
              <button class="btn primary" data-confirm="${reservationDto.id}" ${canConfirm ? "" : "disabled"}>Potwierdź</button>
              <button class="btn danger" data-cancel="${reservationDto.id}" ${canCancel ? "" : "disabled"}>Anuluj</button>
            </td>
          </tr>
        `;
    }

    function buildReservationsUrl() {
        const statusFilter = getById("res-status")?.value || "";
        const carIdFilter = (getById("res-carId")?.value || "").trim();

        const queryParams = new URLSearchParams();
        if (statusFilter) queryParams.set("status", statusFilter);
        if (carIdFilter) queryParams.set("carId", carIdFilter);

        const queryString = queryParams.toString();
        return queryString ? `/api/admin/reservations?${queryString}` : `/api/admin/reservations`;
    }

    async function loadReservations() {
        const tableBody = getById("reservations-tbody");
        if (!tableBody) return;

        tableBody.innerHTML = `<tr><td colspan="8">Ładowanie...</td></tr>`;

        try {
            const apiUrl = buildReservationsUrl();
            const list = await Api.fetchJson(apiUrl, { auth: true });

            reservationsCache = Array.isArray(list) ? list : [];

            tableBody.innerHTML = reservationsCache.length
                ? reservationsCache.map((reservation, index) => renderReservationRow(reservation, index)).join("")
                : `<tr><td colspan="8">Brak rezerwacji</td></tr>`;

        } catch (error) {
            if (error.status === 401 || error.status === 403) {
                tableBody.innerHTML = `<tr><td colspan="8">Brak dostępu (ADMIN).</td></tr>`;
                return;
            }
            tableBody.innerHTML = `<tr><td colspan="8">Błąd: ${escapeHtml(error.message)}</td></tr>`;
            Auth.setStatus(`Błąd: ${error.message}`, "error");
        }
    }

    async function updateReservationStatus(reservationId, newStatus) {
        await Api.fetchJson(`/api/admin/reservations/${reservationId}/status`, {
            method: "PATCH",
            auth: true,
            body: { status: newStatus }
        });
    }

    function bindReservationTableClicks() {
        const table = getById("reservations-table");
        if (!table) return;

        table.addEventListener("click", async (event) => {
            const target = event.target;
            if (!target) return;

            const confirmReservationId = target.getAttribute?.("data-confirm");
            const cancelReservationId = target.getAttribute?.("data-cancel");

            try {
                if (confirmReservationId) {
                    await updateReservationStatus(Number(confirmReservationId), "POTWIERDZONE");
                    Auth.setStatus(`Potwierdzono rezerwację #${confirmReservationId}`, "ok");
                    return void loadReservations();
                }

                if (cancelReservationId) {
                    const ok = confirm(`Anulować rezerwację #${cancelReservationId}?`);
                    if (!ok) return;

                    await updateReservationStatus(Number(cancelReservationId), "ANULOWANE");
                    Auth.setStatus(`Anulowano rezerwację #${cancelReservationId}`, "ok");
                    return void loadReservations();
                }
            } catch (err) {
                Auth.setStatus(`Błąd: ${err.message}`, "error");
            }
        });

        getById("btn-res-refresh")?.addEventListener("click", () => void loadReservations());
        getById("res-status")?.addEventListener("change", () => void loadReservations());
        getById("res-carId")?.addEventListener("change", () => void loadReservations());
    }

    async function init() {
        bindReservationTableClicks();

        const adminPanel = getById("admin-panel");
        const panelVisible = adminPanel && !adminPanel.classList.contains("hidden") && adminPanel.style.display !== "none";

        if (panelVisible && Auth?.isLoggedIn?.()) {
            await loadReservations();
        }
    }

    window.AdminReservations = { init, load: loadReservations };
    document.addEventListener("DOMContentLoaded", () => { void init(); });
})();