document.addEventListener("DOMContentLoaded", () => {
    const grid = document.getElementById("cars-grid");
    if (!grid) return;

    fetch("/api/cars")
        .then((r) => {
            if (!r.ok) throw new Error("API error: " + r.status);
            return r.json();
        })
        .then((cars) => {
            grid.innerHTML = "";
            cars.forEach((car) => grid.appendChild(renderCard(car)));
        })
        .catch((err) => {
            console.error(err);
            grid.innerHTML = `<p style="color:white;text-align:center;">Nie udało się wczytać oferty.</p>`;
        });

    function renderCard(car) {
        const a = document.createElement("a");
        a.className = "offer-card";
        a.href = mapToStaticDetailsPage(car);

        const img = document.createElement("img");
        img.src = car.imageUrl;              // u Ciebie: "/images/aston.jpg" itd.
        img.alt = `${car.brand} ${car.model}`;

        const info = document.createElement("div");
        info.className = "offer-info";

        const h3 = document.createElement("h3");
        h3.textContent = `${car.brand} ${car.model}`;

        const p1 = document.createElement("p");
        p1.textContent = `Silnik: ${car.engine ?? "-"}`;

        const p2 = document.createElement("p");
        p2.textContent = `Prędkość maks: ${car.topSpeedKmh ?? "-"} km/h`;

        const p3 = document.createElement("p");
        p3.textContent = `Cena: ${formatPrice(car.pricePerDay)} PLN / 24h`;

        info.append(h3, p1, p2, p3);
        a.append(img, info);
        return a;
    }

    function formatPrice(value) {
        if (value == null) return "-";
        const n = typeof value === "number" ? value : Number(value);
        if (Number.isNaN(n)) return String(value);
        // jak chcesz bez groszy:
        return Math.round(n).toString();
    }

    function mapToStaticDetailsPage(car) {
        // masz statyczne strony dla 4 aut:
        const key = `${(car.brand || "").toLowerCase()} ${(car.model || "").toLowerCase()}`;

        if (key.includes("aston")) return "/html/cars/aston.html";
        if (key.includes("porsche")) return "/html/cars/porsche.html";
        if (key.includes("mercedes") || key.includes("amg")) return "/html/cars/benz.html";
        if (key.includes("audi")) return "/html/cars/audi.html";

        // fallback żeby nie było 404
        return "/html/offer.html";
    }
});
