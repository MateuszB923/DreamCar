(function () {
    function $(id) { return document.getElementById(id); }

    let editingId = null;
    let carsCache = [];

    function readForm() {
        const spec = {
            zeroToHundredSeconds: $("f-0100").value ? Number($("f-0100").value) : null,
            topSpeedKmh: $("f-top").value ? Number($("f-top").value) : null,
            powerHp: $("f-hp").value ? Number($("f-hp").value) : null,
            drivetrain: $("f-drive").value,
            engine: $("f-engine").value,
            mileageKm: $("f-mileage").value ? Number($("f-mileage").value) : null,
            fuelConsumptionL100: $("f-fuel").value ? Number($("f-fuel").value) : null,
        };

        return {
            brand: $("f-brand").value.trim(),
            model: $("f-model").value.trim(),
            year: Number($("f-year").value),
            pricePerDay: Number($("f-price").value),
            imageUrl: $("f-imageUrl").value.trim(),
            available: $("f-available").checked,
            spec,
            title: $("f-title").value.trim(),
            description: $("f-desc").value.trim()
        };
    }

    function fillForm(car) {
        $("f-brand").value = car.brand ?? "";
        $("f-model").value = car.model ?? "";
        $("f-year").value = car.year ?? "";
        $("f-price").value = car.pricePerDay ?? "";
        $("f-imageUrl").value = car.imageUrl ?? "";
        $("f-available").checked = !!car.available;

        $("f-0100").value = car.zeroToHundredSeconds ?? "";
        $("f-top").value = car.topSpeedKmh ?? "";
        $("f-hp").value = car.powerHp ?? "";
        $("f-drive").value = car.drivetrain ?? "";  // enum
        $("f-engine").value = car.engine ?? "";
        $("f-mileage").value = car.mileageKm ?? "";
        $("f-fuel").value = car.fuelConsumptionL100 ?? "";

        $("f-title").value = car.title ?? "";
        $("f-desc").value = car.description ?? "";
    }

    function clearForm() {
        editingId = null;
        $("car-form").reset();
        $("form-title").textContent = "Dodaj auto";
        $("btn-save").textContent = "Dodaj";
        $("btn-cancel").style.display = "none";
        if (window.AdminReviews?.hide) window.AdminReviews.hide();
    }

    function rowHtml(car, index) {
        return `
      <tr>
        <td>${index + 1}</td>
        <td>${car.brand} ${car.model}</td>
        <td>${car.year}</td>
        <td>${car.pricePerDay} PLN</td>
        <td>${car.available ? "TAK" : "NIE"}</td>
        <td>
          <button class="btn" data-edit="${car.id}">Edytuj</button>
          <button class="btn danger" data-del="${car.id}">Usuń</button>
        </td>
      </tr>
    `;
    }

    async function loadCars() {
        const tbody = $("cars-tbody");
        tbody.innerHTML = `<tr><td colspan="6">Ładowanie...</td></tr>`;

        try {
            const cars = await Api.fetchJson("/api/admin/cars", { auth: true });
            carsCache = Array.isArray(cars) ? cars : [];
            tbody.innerHTML = cars.length
                ? cars.map((c, idx) => rowHtml(c, idx)).join("")
                : `<tr><td colspan="6">Brak aut</td></tr>`;
        } catch (e) {
            if (e.status === 401 || e.status === 403) {
                Auth.setStatus("Brak dostępu", "error");
                Auth.logout();
                Auth.showLoggedOutUI();
                return;
            }
            tbody.innerHTML = `<tr><td colspan="6">Błąd: ${e.message}</td></tr>`;
        }
    }

    function carLp(id) {
        const idx = carsCache.findIndex(c => Number(c.id) === Number(id));
        return idx >= 0 ? idx + 1 : null;
    }

    async function onEdit(id) {
        try {
            const car = await Api.fetchJson(`/api/admin/cars/${id}`, { auth: true });
            editingId = id;
            fillForm(car);
            const lp = carLp(id);
            $("form-title").textContent = lp ? `Edytuj auto #${lp}` : `Edytuj auto`;
            $("btn-save").textContent = "Zapisz";
            $("btn-cancel").style.display = "inline-block";
            Auth.setStatus("Tryb edycji", "info");
            if (window.AdminReviews?.loadForCar) await window.AdminReviews.loadForCar(id);
        } catch (e) {
            Auth.setStatus(`Błąd pobrania auta: ${e.message}`, "error");
        }
    }

    async function onDelete(id) {
        const lp = carLp(id);
        if (!confirm(lp ? `Usunąć auto #${lp}?` : `Usunąć auto?`)) return;

        try {
            await Api.fetchJson(`/api/admin/cars/${id}`, { method: "DELETE", auth: true });
            Auth.setStatus(lp ? `Usunięto auto #${lp}` : `Usunięto auto`, "ok");
            if (editingId === id) clearForm();
            await loadCars();
        } catch (e) {
            Auth.setStatus(`Błąd usuwania: ${e.message}`, "error");
        }
    }

    async function onSubmit(e) {
        e.preventDefault();

        const payload = readForm();

        if (!payload.brand || !payload.model || !payload.imageUrl || !payload.title || !payload.description) {
            Auth.setStatus("Uzupełnij: brand, model, imageUrl, title, description", "error");
            return;
        }

        if (!payload.spec?.drivetrain) {
            Auth.setStatus("Uzupełnij: drivetrain (enum)", "error");
            return;
        }

        try {
            if (editingId == null) {
                await Api.fetchJson("/api/admin/cars", { method: "POST", body: payload, auth: true });
                Auth.setStatus("Dodano auto ✅", "ok");
            } else {
                await Api.fetchJson(`/api/admin/cars/${editingId}`, { method: "PATCH", body: payload, auth: true });
                Auth.setStatus("Zapisano zmiany ✅", "ok");
            }

            clearForm();
            await loadCars();
        } catch (e2) {
            Auth.setStatus(`Błąd zapisu: ${e2.message}`, "error");
            console.log(e2.data);
        }
    }

    function bindTableClicks() {
        $("cars-table").addEventListener("click", (e) => {
            const editId = e.target?.getAttribute?.("data-edit");
            const delId = e.target?.getAttribute?.("data-del");
            if (editId) void onEdit(Number(editId));
            if (delId) void onDelete(Number(delId));
        });
    }

    function bindAuth() {
        $("login-form").addEventListener("submit", async (e) => {
            e.preventDefault();
            const email = $("login-email").value.trim();
            const password = $("login-password").value;

            try {
                await Auth.login(email, password);
                Auth.setStatus("Zalogowano ✅", "ok");
                Auth.showLoggedInUI();
                await loadCars();
            } catch (err) {
                Auth.setStatus(`Błąd logowania: ${err.message}`, "error");
            }
        });

        $("btn-logout").addEventListener("click", () => {
            Auth.logout();
            Auth.setStatus("Wylogowano", "info");
            Auth.showLoggedOutUI();
            clearForm();
        });
    }

    function init() {
        if (Auth.isLoggedIn()) {
            Auth.showLoggedInUI();
            void loadCars();
        } else {
            Auth.showLoggedOutUI();
        }

        bindAuth();
        bindTableClicks();
        $("car-form").addEventListener("submit", onSubmit);
        $("btn-cancel").addEventListener("click", (e) => {
            e.preventDefault();
            clearForm();
            Auth.setStatus("Anulowano edycję", "info");
        });

        clearForm();
    }

    document.addEventListener("DOMContentLoaded", init);
})();
