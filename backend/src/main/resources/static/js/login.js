document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("login-form");
    const msg = document.getElementById("msg");

    const redirect = new URLSearchParams(window.location.search).get("redirect") || "/index.html";
    const toRegister = document.getElementById("to-register");
    if (toRegister) {
        toRegister.href = `/html/register.html?redirect=${encodeURIComponent(redirect)}`;
    }

    function setMsg(text, ok = false) {
        msg.textContent = text;
        msg.style.color = ok ? "lightgreen" : "salmon";
    }

    if (window.Auth?.isLoggedIn()) {
        window.location.href = redirect;
        return;
    }

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value;

        setMsg("Logowanie...", true);

        try {
            await Auth.login(email, password);
            setMsg("Zalogowano", true);
            window.location.href = redirect;
        } catch (err) {
            setMsg(err?.message || "Błąd logowania");
        }
    });
});
