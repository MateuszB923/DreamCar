document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("register-form");
    const msg = document.getElementById("msg");

    const redirect = new URLSearchParams(window.location.search).get("redirect") || "/index.html";

    const toLogin = document.getElementById("to-login");
    if (toLogin) {
        toLogin.href = `/html/login.html?redirect=${encodeURIComponent(redirect)}`;
    }

    function setMsg(text, ok = false) {
        msg.textContent = text;
        msg.style.color = ok ? "lightgreen" : "salmon";
    }

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const email = document.getElementById("email").value.trim();
        const p1 = document.getElementById("password").value;
        const p2 = document.getElementById("password2").value;

        if (p1 !== p2) {
            setMsg("Hasła nie są takie same.");
            return;
        }

        setMsg("Rejestracja...", true);

        try {
            await Auth.register(email, p1);
            setMsg("Konto utworzone. Zaloguj.", true);
            window.location.href = `/html/login.html?redirect=${encodeURIComponent(redirect)}`;
        } catch (err) {
            setMsg(err?.message || "Błąd rejestracji");
        }
    });
});
