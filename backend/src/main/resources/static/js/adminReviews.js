(function () {
    function $(id) { return document.getElementById(id); }

    let currentCarId = null;
    let reviewsCache = [];

    function reviewLp(reviewId) {
        const idx = reviewsCache.findIndex(r => Number(r.id) === Number(reviewId));
        return idx >= 0 ? idx + 1 : null;
    }

    function escapeHtml(str) {
        return String(str ?? "").replace(/[&<>"']/g, s => ({
            "&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#039;"
        }[s]));
    }

    function showCard(carId) {
        currentCarId = carId;

        const card = $("reviews-card");
        if (card) card.style.display = "block";

        const title = $("reviews-title");
        if (title) title.textContent = `OPINIE • Auto #${carId}`;
    }

    function hideCard() {
        currentCarId = null;

        const card = $("reviews-card");
        if (card) card.style.display = "none";

        const tbody = $("reviews-tbody");
        if (tbody) tbody.innerHTML = `<tr><td colspan="4">Wybierz auto (Edytuj), aby zobaczyć opinie.</td></tr>`;

        const title = $("reviews-title");
        if (title) title.textContent = "OPINIE";
    }

    function rowHtml(r, index) {
        return `
    <tr>
      <td>${index + 1}</td>
      <td>${escapeHtml(r.author)}</td>
      <td>${escapeHtml(r.review)}</td>
      <td>
        <button class="btn danger" data-review-del="${r.id}">Usuń</button>
      </td>
    </tr>
  `;
    }

    async function loadForCar(carId) {
        showCard(carId);

        const tbody = $("reviews-tbody");
        if (tbody) tbody.innerHTML = `<tr><td colspan="4">Ładowanie...</td></tr>`;

        try {
            const reviews = await Api.fetchJson(`/api/cars/${carId}/reviews`); // GET jest publiczny
            reviewsCache = Array.isArray(reviews) ? reviews : [];

            if (!reviews.length) {
                if (tbody) tbody.innerHTML = `<tr><td colspan="4">Brak opinii dla tego auta.</td></tr>`;
                return;
            }
            if (tbody) tbody.innerHTML = reviews.map((r, idx) => rowHtml(r, idx)).join("");
        } catch (e) {
            if (tbody) tbody.innerHTML = `<tr><td colspan="4">Błąd: ${escapeHtml(e.message)}</td></tr>`;
        }
    }

    async function deleteReview(reviewId) {
        if (!currentCarId) return;

        const lp = reviewLp(reviewId);
        if (!confirm(lp ? `Usunąć opinię #${lp}?` : `Usunąć opinię?`)) return;

        try {
            await Api.fetchJson(`/api/admin/reviews/${reviewId}`, { method: "DELETE", auth: true });
            Auth.setStatus(lp ? `Usunięto opinię #${lp}` : `Usunięto opinię`, "ok");
            await loadForCar(currentCarId);
        } catch (e) {
            Auth.setStatus(`Błąd usuwania opinii: ${e.message}`, "error");
        }
    }

    function bindClicks() {
        const table = $("reviews-table");
        if (!table) return;

        table.addEventListener("click", (e) => {
            const rid = e.target?.getAttribute?.("data-review-del");
            if (rid) void deleteReview(Number(rid));
        });
    }

    window.AdminReviews = {
        loadForCar,
        hide: hideCard
    };

    document.addEventListener("DOMContentLoaded", bindClicks);
})();

