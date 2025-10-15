<#ftl encoding='UTF-8'>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" type="text/css" href="/css/styles.css">
    <title>Каталог скарг - Адміністратор</title>
    <style>
        .complaint-card {
            background-color: #fff;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 20px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            width: 659px;
        }

        .complaint-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
            padding-bottom: 10px;
            border-bottom: 2px solid #f0f0f0;
        }

        .complaint-type-badge {
            display: inline-block;
            padding: 5px 12px;
            border-radius: 15px;
            font-size: 12px;
            font-weight: bold;
            color: white;
        }

        .badge-spam { background-color: #ff6b6b; }
        .badge-inappropriate_content { background-color: #ee5a6f; }
        .badge-misleading { background-color: #f39c12; }
        .badge-offensive { background-color: #e74c3c; }
        .badge-fraud { background-color: #c0392b; }
        .badge-other { background-color: #95a5a6; }

        .complaint-status {
            font-weight: bold;
            padding: 5px 10px;
            border-radius: 5px;
        }

        .status-unresolved { background-color: #fff3cd; color: #856404; }
        .status-confirmed { background-color: #d4edda; color: #155724; }
        .status-rejected { background-color: #f8d7da; color: #721c24; }

        .complaint-body {
            margin: 15px 0;
        }

        .complaint-info {
            font-size: 14px;
            color: #666;
            margin: 5px 0;
        }

        .complaint-comment {
            background-color: #f8f9fa;
            padding: 10px;
            border-radius: 5px;
            margin: 10px 0;
            font-style: italic;
            border-left: 3px solid #6c757d;
        }

        .admin-comment {
            background-color: #e3f2fd;
            padding: 10px;
            border-radius: 5px;
            margin: 10px 0;
            border-left: 3px solid #2196f3;
        }

        .complaint-actions {
            display: flex;
            gap: 10px;
            margin-top: 15px;
            padding-top: 15px;
            border-top: 1px solid #f0f0f0;
        }

        .btn-resolve {
            flex: 1;
            padding: 10px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
            font-weight: bold;
            transition: all 0.3s;
        }

        .btn-confirm {
            background-color: #dc3545;
            color: white;
        }

        .btn-confirm:hover {
            background-color: #c82333;
        }

        .btn-reject {
            background-color: #6c757d;
            color: white;
        }

        .btn-reject:hover {
            background-color: #5a6268;
        }

        .review-preview {
            background-color: #fff;
            border: 1px solid #ddd;
            border-radius: 5px;
            padding: 15px;
            margin: 10px 0;
        }

        .review-preview-images {
            display: flex;
            gap: 10px;
            margin-top: 10px;
            flex-wrap: wrap;
        }

        .review-preview-images img {
            width: 200px;
            height: 200px;
            object-fit: cover;
            border-radius: 5px;
            cursor: pointer;
            border: 2px solid #ddd;
            transition: transform 0.2s, box-shadow 0.2s;
        }

        .review-preview-images img:hover {
            transform: scale(1.2);
            box-shadow: 0 4px 12px rgba(0,0,0,0.2);
        }

        .filter-radios {
            display: flex;
            gap: 20px;
            margin: 20px 0;
            padding: 15px;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            flex-wrap: wrap;
        }

        .filter-radios label {
            cursor: pointer;
            display: flex;
            align-items: center;
            gap: 5px;
        }

        .tab-buttons {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
        }

        .tab-button {
            padding: 10px 20px;
            background-color: #f0f0f0;
            cursor: pointer;
            border-radius: 5px;
            border: 1px black solid;
            font-size: 16px;
            transition: all 0.3s;
        }

        .tab-button.active {
            background-color: #333;
            color: white;
        }

        /* Form Group Styles */
        .form-group {
            margin-bottom: 20px;
        }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: bold;
            font-size: 16px;
            color: #333;
            font-family: 'Lato', sans-serif;
        }

        .form-group textarea {
            width: 100%;
            min-height: 120px;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
            font-family: 'Lato', sans-serif;
            resize: vertical;
            transition: border-color 0.3s;
        }

        .form-group textarea:focus {
            outline: none;
            border-color: #2196f3;
            box-shadow: 0 0 0 2px rgba(33, 150, 243, 0.1);
        }

        #resolveForm {
            display: flex;
            flex-direction: column;
            gap: 15px;
        }

        .form-actions {
            display: flex;
            gap: 10px;
            margin-top: 10px;
        }

        .form-actions button {
            flex: 1;
            padding: 12px;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
            transition: all 0.3s;
        }

        .form-actions button[type="submit"] {
            background-color: #333;
            color: white;
        }

        .form-actions button[type="submit"]:hover {
            background-color: #555;
        }

        .form-actions button[type="button"] {
            background-color: #6c757d;
            color: white;
        }

        .form-actions button[type="button"]:hover {
            background-color: #5a6268;
        }

        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0,0,0,0.4);
        }

        .modal-content {
            background-color: #fefefe;
            margin: 5% auto;
            padding: 20px;
            border-radius: 8px;
            width: 90%;
            max-width: 600px;
            max-height: 80vh;
            overflow-y: auto;
        }


        .close {
            color: #aaa;
            float: right;
            font-size: 28px;
            font-weight: bold;
            cursor: pointer;
        }

        .close:hover {
            color: black;
        }
    </style>
</head>
<body>
<script src="/js/auth-menu.js"></script>
<script src="/js/image-modal.js"></script>

<header>
    <ul class="menu">
        <div class="leftPart">
            <li id="forBussines"><a href="/forBusiness">Інформація для бізнесу</a></li>
            <li><a href="/aboutProject">Про проєкт</a></li>
            <li><a href="/catalog">Каталог</a></li>
            <li><a href="/help">FAQ</a></li>
        </div>
        <div class="rightPart">
            <li id="login"><a href="/login">Увійти</a></li>
            <li id="createAdd"><a href="/registration">Зареєструватись</a></li>
        </div>
    </ul>
</header>

<div class="content">
    <div class="center-global-container">
        <h1 style="font-family: 'Lato', sans-serif; margin: 20px 0;">Каталог скарг</h1>

        <div class="tab-buttons">
            <button class="tab-button active" onclick="switchTab('advertisement')">Скарги на оголошення</button>
            <button class="tab-button" onclick="switchTab('review')">Скарги на відгуки</button>
        </div>

        <div class="filter-radios">
            <label>
                <input type="radio" name="statusFilter" value="unresolved" checked onchange="loadComplaints()">
                Нерозглянуті
            </label>
            <label>
                <input type="radio" name="statusFilter" value="all" onchange="loadComplaints()">
                Всі
            </label>
            <label>
                <input type="radio" name="statusFilter" value="resolved" onchange="loadComplaints()">
                Розглянуті
            </label>
            <label>
                <input type="radio" name="statusFilter" value="confirmed" onchange="loadComplaints()">
                Підтверджені (видалені)
            </label>
            <label>
                <input type="radio" name="statusFilter" value="rejected" onchange="loadComplaints()">
                Відхилені
            </label>
        </div>

        <div id="complaints-container" style="max-width: 1200px; margin: 0 auto;">
            <div style="text-align: center; padding: 40px; font-size: 18px; color: #666;">
                Завантаження...
            </div>
        </div>
    </div>
</div>

<!-- Resolve Modal -->
<div id="resolveModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeResolveModal()">&times;</span>
        <h2 id="resolveModalTitle">Розгляд скарги</h2>
        <p id="resolveModalAction" style="font-size: 18px; font-weight: bold; margin: 15px 0;"></p>
        <form id="resolveForm">
            <div class="form-group">
                <label for="adminComment">Коментар адміністратора: <span style="color: red;">*</span></label>
                <textarea id="adminComment" name="adminComment" required maxlength="500" placeholder="Опишіть причину вашого рішення..."></textarea>
            </div>

            <div class="form-group" style="display: flex; gap: 10px;">
                <button type="button" class="submit-form" onclick="closeResolveModal()" style="flex: 1; background-color: #6c757d;">Назад</button>
                <button type="submit" class="submit-form" style="flex: 1;">Підтвердити</button>
            </div>
        </form>
    </div>
</div>

<!-- Image Modal -->
<div id="imageModal" class="modal image-modal">
    <span class="close-image" onclick="closeImageModal()">&times;</span>
    <div class="image-modal-content">
        <img id="modalImage" src="" alt="Фото відгуку">
        <div class="image-modal-nav">
            <button class="nav-button prev" onclick="previousImage()" id="prevBtn">&#10094;</button>
            <button class="nav-button next" onclick="nextImage()" id="nextBtn">&#10095;</button>
        </div>
        <div class="image-counter" id="imageCounter"></div>
    </div>
</div>

<footer>
    <div class="footer-content">
        <p>Copyright &copy; 2025 WanderUA. All rights reserved.</p>
    </div>
</footer>

<script>
    let currentTab = 'advertisement';
    let currentComplaintId = null;
    let currentAction = null;

    // Image modal variables
    let currentImageIndex = 0;
    let currentImageGallery = [];

    function switchTab(tab) {
        currentTab = tab;
        document.querySelectorAll('.tab-button').forEach(btn => btn.classList.remove('active'));
        event.target.classList.add('active');
        loadComplaints();
    }

    async function loadComplaints() {
        const status = document.querySelector('input[name="statusFilter"]:checked').value;
        const container = document.getElementById('complaints-container');

        container.innerHTML = '<div style="text-align: center; padding: 40px; font-size: 18px; color: #666;">Завантаження...</div>';

        try {
            const endpoint = currentTab === 'advertisement'
                ? `/api/complaints/advertisement/by-status?status=` + status
                : `/api/complaints/review/by-status?status=` + status;

            const response = await fetch(endpoint);

            if (!response.ok) {
                throw new Error('Failed to load complaints');
            }

            const complaints = await response.json();
            displayComplaints(complaints);
        } catch (error) {
            console.error('Error loading complaints:', error);
            container.innerHTML = '<div style="text-align: center; padding: 40px; font-size: 18px; color: #ff0000;">Помилка завантаження скарг</div>';
        }
    }

    function displayComplaints(complaints) {
        const container = document.getElementById('complaints-container');

        if (complaints.length === 0) {
            container.innerHTML = '<div style="text-align: center; padding: 40px; font-size: 18px; color: #666;">Скарг не знайдено</div>';
            return;
        }

        container.innerHTML = complaints.map(complaint => createComplaintCard(complaint)).join('');
    }
    <#noparse>
    function createComplaintCard(complaint) {
        const typeClass = `badge-` + complaint.type.toLowerCase();

        let statusClass, statusText;
        if (!complaint.resolved) {
            statusClass = 'status-unresolved';
            statusText = 'Нерозглянуто';
        } else if (complaint.confirmed === true) {
            statusClass = 'status-confirmed';
            statusText = 'Підтверджено';
        } else if (complaint.confirmed === false) {
            statusClass = 'status-rejected';
            statusText = 'Відхилено';
        } else {
            statusClass = 'status-unresolved';
            statusText = 'Розглянуто';
        }

        let contentPreview = '';
        if (currentTab === 'advertisement') {
            contentPreview = `
                <div class="complaint-info">
                    <strong>Оголошення:</strong>
                    <a href="/advertisements/${complaint.advertisementId}" target="_blank">${complaint.advertisementTitle}</a>
                </div>
            `;
        } else {
            let reviewImagesHtml = '';
            if (complaint.reviewImageUrls && complaint.reviewImageUrls.length > 0) {
                const imageUrlsJson = JSON.stringify(complaint.reviewImageUrls);
                reviewImagesHtml = `
                    <div class="complaint-info"><strong>Фотографії з відгуку:</strong></div>
                    <div class="review-preview-images">
                        ${complaint.reviewImageUrls.map((url, index) => `
                            <img src="${url}"
                                 alt="Фото відгуку ${index + 1}"
                                 onclick='openImageModal("${url}", ${imageUrlsJson})'
                                 title="Натисніть для перегляду">
                        `).join('')}
                    </div>
                `;
            }

            contentPreview = `
                <div class="review-preview">
                    ${complaint.reviewTitle ? `<div class="complaint-info"><strong>Назва відгуку:</strong> ${complaint.reviewTitle}</div>` : ''}
                    ${complaint.reviewText ? `<div class="complaint-info"><strong>Текст відгуку:</strong> ${complaint.reviewText}</div>` : ''}
                    ${reviewImagesHtml}
                    <div class="complaint-info"><strong>Оголошення:</strong>
                        <a href="/advertisements/${complaint.advertisementId}" target="_blank" style="color: #2196f3;">${complaint.advertisementTitle}</a>
                    </div>
                </div>
            `;
        }

        let actionsHtml = '';
        if (!complaint.resolved) {
            actionsHtml = `
                <div class="complaint-actions">
                    <button class="btn-resolve btn-confirm" onclick="openResolveModal(${complaint.id}, true)">
                        Прийняти та видалити контент
                    </button>
                    <button class="btn-resolve btn-reject" onclick="openResolveModal(${complaint.id}, false)">
                        Відхилити
                    </button>
                </div>
            `;
        }

        let adminCommentHtml = '';
        if (complaint.adminComment) {
            adminCommentHtml = `
                <div class="admin-comment">
                    <strong>Коментар адміністратора:</strong><br>
                    ${complaint.adminComment}
                </div>
            `;
        }
        console.log("createdAt:", complaint.createdAt);
        return `
            <div class="complaint-card">
                <div class="complaint-header">
                    <div>
                        <span class="complaint-type-badge ${typeClass}">${complaint.typeDisplay}</span>
                    </div>
                    <span class="complaint-status ${statusClass}">${statusText}</span>
                </div>

                <div class="complaint-body">
                    <div class="complaint-info"><strong>Від користувача:</strong> ${complaint.username}</div>
                    <div class="complaint-info"><strong>Дата подання:</strong> ${formatDate(complaint.createdAt)}</div>

                    ${contentPreview}

                    ${complaint.comment ? `<div class="complaint-comment">${complaint.comment}</div>` : ''}
                    ${adminCommentHtml}
                    ${complaint.resolvedAt ? `<div class="complaint-info"><strong>Дата розгляду:</strong> ${formatDate(complaint.resolvedAt)}</div>` : ''}
                </div>

                ${actionsHtml}
            </div>
        `;
    }
    </#noparse>

    function formatDate(dateValue) {
        if (!dateValue) return "";

        if (Array.isArray(dateValue)) {
            const [year, month, day, hour, minute, second, nano] = dateValue;
            const millis = nano ? Math.floor(nano / 1_000_000) : 0;
            const date = new Date(year, month - 1, day, hour, minute, second, millis);
            return date.toLocaleString("uk-UA", {
                year: "numeric",
                month: "2-digit",
                day: "2-digit",
                hour: "2-digit",
                minute: "2-digit",
                seconds: false,
            });
        }

        return new Date(dateValue).toLocaleString();
    }

    function openResolveModal(complaintId, confirmed) {
        currentComplaintId = complaintId;
        currentAction = confirmed;

        const modal = document.getElementById('resolveModal');
        const actionText = document.getElementById('resolveModalAction');

        if (confirmed) {
            actionText.textContent = 'Ви збираєтесь ПІДТВЕРДИТИ скаргу та ВИДАЛИТИ контент';
            actionText.style.color = '#dc3545';
        } else {
            actionText.textContent = 'Ви збираєтесь ВІДХИЛИТИ скаргу';
            actionText.style.color = '#6c757d';
        }

        modal.style.display = 'block';
    }

    function closeResolveModal() {
        document.getElementById('resolveModal').style.display = 'none';
        document.getElementById('resolveForm').reset();
        currentComplaintId = null;
        currentAction = null;
    }

    <#noparse>
    document.getElementById('resolveForm').addEventListener('submit', async function(e) {
        e.preventDefault();

        const adminComment = document.getElementById('adminComment').value;

        if (!adminComment.trim()) {
            alert('Будь ласка, додайте коментар');
            return;
        }

        try {
            const endpoint = currentTab === 'advertisement'
                ? `/api/complaints/advertisement/${currentComplaintId}/resolve`
                : `/api/complaints/review/${currentComplaintId}/resolve`;

            const response = await fetch(endpoint, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    confirmed: currentAction,
                    adminComment: adminComment
                })
            });

            if (response.ok) {
                alert('Скаргу успішно розглянуто!');
                closeResolveModal();
                loadComplaints();
            } else {
                const error = await response.json();
                alert(error.error || 'Помилка розгляду скарги');
            }
        } catch (error) {
            console.error('Error resolving complaint:', error);
            alert('Помилка відправки рішення');
        }
    });
    </#noparse>

    // Close modal when clicking outside
    window.addEventListener('click', function(e) {
        const modal = document.getElementById('resolveModal');
        if (e.target === modal) {
            closeResolveModal();
        }
    });

    // Load complaints on page load
    document.addEventListener('DOMContentLoaded', function() {
        loadComplaints();
    });
</script>

</body>
</html>