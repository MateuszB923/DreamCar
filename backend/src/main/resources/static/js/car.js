document.addEventListener("DOMContentLoaded", async () => {
    const id = new URLSearchParams(window.location.search).get("id");

    if (!id) {
        alert("Brak id auta w URL.");
        window.location.href = "/html/offer.html";
        return;
    }

    try {
        const car = await fetchCar(id);
        renderCar(car);

        await loadReviews(id);

        const loggedIn = !!(window.Api && Api.getToken && Api.getToken());
        const formWrap = document.getElementById("review-form-wrap");
        const hint = document.getElementById("review-login-hint");

        if (loggedIn) {
            if (formWrap) formWrap.style.display = "block";
            if (hint) hint.style.display = "none";

            const btn = document.getElementById("btn-review");
            if (btn) btn.addEventListener("click", () => submitReview(id));
        } else {
            if (formWrap) formWrap.style.display = "none";
            if (hint) hint.style.display = "block";
        }

    } catch (e) {
        console.error(e);
        alert("Nie udało się wczytać danych auta.");
        window.location.href = "/html/offer.html";
    }
});

async function fetchCar(carId) {
    const res = await fetch(`/api/cars/${encodeURIComponent(carId)}`);
    if (!res.ok) throw new Error(`CAR HTTP ${res.status}`);
    return await res.json();
}

function renderCar(car) {
    setText("page-title", `${car.brand} ${car.model}`);
    setText("car-title", `${car.brand} ${car.model}`);
    setText("car-price", `${formatPrice(car.pricePerDay)} PLN / 24h`);

    const img = document.getElementById("car-img");
    if (img) {
        img.src = car.imageUrl || "../images/main.jpg";
        img.alt = `${car.brand} ${car.model}`;
    }

    setText("car-desc", car?.description ?? "");

    setText("spec-0100", car?.zeroToHundredSeconds ?? "-");
    setText("spec-top", car?.topSpeedKmh ?? "-");
    setText("spec-hp", car?.powerHp ?? "-");
    setText("spec-drive", car?.drivetrain ?? "-");
    setText("spec-engine", car?.engine ?? "-");
    setText("spec-mileage", formatIntWithSpaces(car?.mileageKm));
    setText("spec-fuel", car?.fuelConsumptionL100 ?? "-");

    const statusEl = document.getElementById("car-status");
    if (statusEl) {
        const available = !!car.available;
        statusEl.textContent = available ? "WOLNY" : "CHWILOWO NIEDOSTĘPNY";
        statusEl.classList.toggle("available", available);
        statusEl.classList.toggle("unavailable", !available);
    }
}

async function loadReviews(carId) {
    const container = document.getElementById("reviews-list");
    if (container) container.innerHTML = "<p>Ładowanie opinii...</p>";

    try {
        let reviews;
        if (window.Api && Api.fetchJson) {
            reviews = await Api.fetchJson(`/api/cars/${encodeURIComponent(carId)}/reviews`);
        } else {
            const res = await fetch(`/api/cars/${encodeURIComponent(carId)}/reviews`);
            reviews = res.ok ? await res.json() : [];
        }
        renderReviews(reviews);
    } catch (e) {
        console.error("Reviews fetch failed", e);
        renderReviews([]);
    }
}

async function submitReview(carId) {
    const textEl = document.getElementById("review-text");
    const errEl = document.getElementById("review-error");
    const text = (textEl?.value || "").trim();

    if (errEl) {
        errEl.style.display = "none";
        errEl.textContent = "";
    }

    if (!text) return;

    try {
        await Api.fetchJson(`/api/cars/${encodeURIComponent(carId)}/reviews`, {
            method: "POST",
            auth: true,
            body: { review: text }
        });

        if (textEl) textEl.value = "";
        await loadReviews(carId);

    } catch (e) {
        console.error(e);
        if (errEl) {
            errEl.style.display = "block";
            errEl.textContent = `Błąd dodania opinii: ${e.message}`;
        } else {
            alert(`Błąd dodania opinii: ${e.message}`);
        }

        if (e.status === 401 || e.status === 403) {
            if (window.Auth && Auth.logout) Auth.logout();
        }
    }
}

function renderReviews(reviews) {
    const container = document.getElementById("reviews-list");
    if (!container) return;

    if (!Array.isArray(reviews) || reviews.length === 0) {
        container.innerHTML = `
          <div class="review-card">
            <i class="fa-solid fa-star"></i>
            <p>„Brak opinii dla tego auta.”</p>
            <span>- DreamCar</span>
          </div>
        `;
        return;
    }

    container.innerHTML = reviews.map(r => `
        <div class="review-card">
          <i class="fa-solid fa-star"></i>
          <p>„${escapeHtml(r.review)}”</p>
          <span>- ${escapeHtml(r.author)}</span>
        </div>
    `).join("");
}

function setText(id, value) {
    const el = document.getElementById(id);
    if (el) el.textContent = value ?? "";
}

function formatPrice(v) {
    if (v == null) return "-";
    const n = typeof v === "number" ? v : Number(v);
    if (Number.isNaN(n)) return String(v);
    return Math.round(n).toString();
}

function formatIntWithSpaces(v) {
    if (v == null) return "-";
    const n = typeof v === "number" ? v : Number(v);
    if (Number.isNaN(n)) return String(v);
    return n.toString().replace(/\B(?=(\d{3})+(?!\d))/g, " ");
}

function escapeHtml(str) {
    return String(str ?? "").replace(/[&<>"']/g, s => ({
        "&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#039;"
    }[s]));
}
