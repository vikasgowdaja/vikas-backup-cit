function confirmDelete(event) {
    return window.confirm("Delete this student ID card permanently?");
}

async function downloadIdCardImage() {
    const card = document.getElementById("id-card-canvas");
    const button = document.getElementById("download-card-btn");
    if (!card || !button || typeof html2canvas === "undefined") {
        return;
    }

    const studentCode = card.dataset.studentCode || "student-id-card";
    const safeCode = studentCode.replace(/[^a-zA-Z0-9-_]/g, "_");

    button.disabled = true;
    button.textContent = "Generating Image...";
    try {
        const canvas = await html2canvas(card, {
            backgroundColor: null,
            scale: 2,
            useCORS: true
        });
        const link = document.createElement("a");
        link.download = safeCode + ".png";
        link.href = canvas.toDataURL("image/png");
        link.click();
    } finally {
        button.disabled = false;
        button.textContent = "Download ID Card Image";
    }
}

document.addEventListener("DOMContentLoaded", function () {
    const button = document.getElementById("download-card-btn");
    if (button) {
        button.addEventListener("click", downloadIdCardImage);
    }

    const fileInput = document.getElementById("photoFile");
    const preview = document.querySelector(".photo-preview");
    if (fileInput && preview) {
        fileInput.addEventListener("change", function (event) {
            const file = event.target.files && event.target.files[0];
            if (!file) {
                return;
            }
            preview.src = URL.createObjectURL(file);
        });
    }
});
