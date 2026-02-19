document.addEventListener("DOMContentLoaded", async () => {
    const msg = document.getElementById("msg");
    const carTitleEl = document.getElementById("car-title");
    const form = document.getElementById("reservation-form");

    const params = new URLSearchParams(window.location.search);
    const carId = params.get("carId");

    const redirectHere = `/html/reservations.html?carId=${encodeURIComponent(carId || "")}`;

    function setMsg(text, ok = false) {
        if (!msg) return;
        msg.textContent = text || "";
        msg.style.color = ok ? "lightgreen" : "salmon";
    }

    if (!carId) {
        setMsg("Brak carId w URL. Wejdź z poziomu strony auta.", false);
        if (form) form.style.display = "none";
        return;
    }

    if (!window.Auth?.isLoggedIn || !Auth.isLoggedIn()) {
        window.location.href = `/html/login.html?redirect=${encodeURIComponent(redirectHere)}`;
        return;
    }

    const today = new Date();
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, "0");
    const dd = String(today.getDate()).padStart(2, "0");
    const todayStr = `${yyyy}-${mm}-${dd}`;

    const startInput = document.getElementById("startDate");
    const endInput = document.getElementById("endDate");
    if (startInput) startInput.min = todayStr;
    if (endInput) endInput.min = todayStr;

    try {
        const car = await Api.fetchJson(`/api/cars/${encodeURIComponent(carId)}`);
        if (carTitleEl) carTitleEl.textContent = `${car.brand} ${car.model}`;
    } catch {}

    if (!form) return;

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const startDate = startInput.value;
        const endDate = endInput.value;
        const note = (document.getElementById("note")?.value || "").trim();

        if (!startDate || !endDate) {
            setMsg("Uzupełnij daty.", false);
            return;
        }

        setMsg("Wysyłanie rezerwacji...", true);

        try {
            const res = await Api.fetchJson("/api/reservations", {
                method: "POST",
                auth: true,
                body: { carId: Number(carId), startDate, endDate, note }
            });

            setMsg("Rezerwacja wysłana", true);
        } catch (err) {
            setMsg(err?.message || "Błąd rezerwacji");
        }
    });
});
