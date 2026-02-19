document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("contact-form");
    const msg = document.getElementById("contact-msg");
    const redirectHere = window.location.pathname + window.location.search;

    const setMsg = (t, ok=false) => {
        if (!msg) return;
        msg.textContent = t;
        msg.style.color = ok ? "lightgreen" : "salmon";
    };

    const params = new URLSearchParams(window.location.search);
    const carId = params.get("carId") ? Number(params.get("carId")) : null;

    form?.addEventListener("submit", async (e) => {
        e.preventDefault();

        if (!Auth.isLoggedIn()) {
            window.location.href = `/html/login.html?redirect=${encodeURIComponent(redirectHere)}`;
            return;
        }

        const payload = {
            name: document.getElementById("contact-name").value.trim(),
            email: document.getElementById("contact-email").value.trim(),
            subject: document.getElementById("contact-subject").value.trim(),
            message: document.getElementById("contact-message").value.trim(),
            category: carId ? "RESERVATION" : "GENERAL",
            carId
        };

        setMsg("Wysyłanie...", true);

        try {
            await Api.fetchJson("/api/contact", { method: "POST", body: payload, auth: true });
            setMsg("Wysłano", true);
            form.reset();
        } catch (err) {
            if (err?.status === 401) {
                window.location.href = `/html/login.html?redirect=${encodeURIComponent(redirectHere)}`;
                return;
            }
            setMsg(err?.message || "Błąd wysyłania");
        }
    });
});
