document.addEventListener("DOMContentLoaded", async () => {
    const id = new URLSearchParams(window.location.search).get("id");

    if (!id) {
        alert("Brak id auta w URL.");
        window.location.href = "/html/offer.html";
        return;
    }

    try {
        const carRes = await fetch(`/api/cars/${encodeURIComponent(id)}`);
        if (!carRes.ok) throw new Error(`CAR HTTP ${carRes.status}`);
        const car = await carRes.json();

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

        const reviews = await fetchReviews(id);
        renderReviews(reviews);

    } catch (e) {
        console.error(e);
        alert("Nie udało się wczytać danych auta.");
        window.location.href = "/html/offer.html";
    }
});

async function fetchReviews(carId) {
    try {
        const res = await fetch(`/api/cars/${encodeURIComponent(carId)}/reviews`);
        if (!res.ok) return [];
        return await res.json();
    } catch (e) {
        console.error("Reviews fetch failed", e);
        return [];
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
