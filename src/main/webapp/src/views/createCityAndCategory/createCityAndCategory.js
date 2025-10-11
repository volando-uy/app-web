function importFooter() {
    fetch("../footer/footer.html")
        .then(res => res.text())
        .then(data => {
            document.getElementById("footer").innerHTML = data;
        });
}

if (document.getElementById("footer")) importFooter();

fetch("../header/header.html")
    .then(res => res.text())
    .then(data => {
        document.getElementById("header").innerHTML = data;
        const script = document.createElement("script");
        script.src = "../header/header.js";
        script.onload = () => { if (typeof initHeader === "function") initHeader(); };
        document.body.appendChild(script);
    });

document.addEventListener("DOMContentLoaded", () => {

    const sendForm = async (formId, responseId) => {
        const form = document.getElementById(formId);
        const responseDiv = document.getElementById(responseId);

        form.addEventListener("submit", async (e) => {
            e.preventDefault();

            const formData = new FormData(form);
            const res = await fetch(`${CONTEXT_PATH}/createCityAndCategory`, {
                method: "POST",
                body: formData
            });

            const data = await res.json();
            responseDiv.textContent = data.message;
            responseDiv.style.color = data.status === "ok" ? "green" : "red";
        });
    };

    sendForm("city-form", "cityResponse");
    sendForm("category-form", "categoryResponse");
});

