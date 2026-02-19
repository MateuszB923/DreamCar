document.addEventListener("DOMContentLoaded", () => {
    const slot = document.getElementById("nav-auth");
    if (!slot) return;

    const redirect = window.location.pathname + window.location.search;

    const isLoggedIn =
        (window.Auth && Auth.isLoggedIn && Auth.isLoggedIn()) ||
        (window.Auth && Auth.isLoggedIn && Auth.isLoggedIn());

    const logout = () => {
        if (window.Auth?.logout) Auth.logout();
        if (window.Auth?.logout) Auth.logout();
    };

    if (isLoggedIn) {
        slot.innerHTML = `
      <a href="/html/account.html">Konto</a>
      <span style="opacity:.5; margin: 0 8px;">|</span>
      <a href="/html/index.html" id="logout-link">Wyloguj</a>
    `;

        const link = document.getElementById("logout-link");
        if (link) {
            link.addEventListener("click", (e) => {
                e.preventDefault();
                logout();
                window.location.href = "/html/index.html";
            });
        }
    } else {
        slot.innerHTML = `
      <a href="/html/login.html?redirect=${encodeURIComponent(redirect)}">Zaloguj</a>
    `;
    }
});
