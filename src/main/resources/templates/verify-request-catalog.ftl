<#ftl encoding='UTF-8'>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" type="text/css" href="/css/styles.css">
    <title>Каталог запитів на верифікацію - Адміністратор</title>
    <style>
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

        .business-details {
            background-color: #f8f9fa;
            padding: 15px;
            border-radius: 5px;
            margin: 15px 0;
        }

        .business-details .detail-row {
            margin: 10px 0;
        }

        .business-details .detail-row strong {
            color: #333;
        }

        .documents-list {
            margin-top: 15px;
        }

        .document-item {
            display: flex;
            align-items: center;
            padding: 10px;
            background-color: #fff;
            border: 1px solid #ddd;
            border-radius: 5px;
            margin-bottom: 10px;
            transition: background-color 0.2s;
        }

        .document-item:hover {
            background-color: #f8f9fa;
        }

        .document-icon {
            margin-right: 10px;
            font-size: 24px;
        }

        .document-link {
            color: #2196f3;
            text-decoration: none;
            flex-grow: 1;
        }

        .document-link:hover {
            text-decoration: underline;
        }

        .badge-business-verify {
            background-color: #007bff;
        }

        .badge-sustainable-verify {
            background-color: #28a745;
        }

        .deleted-business-notice {
            background-color: #fff3cd;
            border: 1px solid #ffc107;
            color: #856404;
            padding: 10px;
            border-radius: 5px;
            margin: 10px 0;
            font-style: italic;
        }
    </style>
</head>
<body>
<script src="/js/auth-menu.js"></script>
<script src="/static/js/notify.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>


<header>
    <ul class="menu">
        <div class="leftPart">
            <li id="mainButton"><a href="/">WanderUA</a></li>
            <li id="aboutProject"><a href="/aboutProject">Про проєкт</a></li>
            <li id="businessInfo"><a href="/business_info">Інформація для бізнесу</a></li>
            <li id ="faq"><a href="/help">FAQ</a></li>
        </div>
        <div class="rightPart">
            <li id="login"><a href="/login">Увійти</a></li>
            <li id="createAdd"><a href="/registration">Зареєструватись</a></li>
        </div>
    </ul>
</header>

<div class="content">
    <div class="center-global-container">
        <h1 style="font-family: 'Lato', sans-serif; margin: 20px 0;">Каталог запитів на верифікацію</h1>

        <div class="tab-buttons">
            <button class="tab-button active" onclick="switchTab('business-represent')">Запити на статус представника бізнесу</button>
            <button class="tab-button" onclick="switchTab('sustainability')">Запити на статус сталого бізнесу</button>
        </div>

        <div class="filter-radios">
            <label>
                <input type="radio" name="statusFilter" value="unresolved" checked onchange="loadRequests()">
                Нерозглянуті
            </label>
            <label>
                <input type="radio" name="statusFilter" value="all" onchange="loadRequests()">
                Всі
            </label>
            <label>
                <input type="radio" name="statusFilter" value="resolved" onchange="loadRequests()">
                Розглянуті
            </label>
            <label>
                <input type="radio" name="statusFilter" value="confirmed" onchange="loadRequests()">
                Підтверджені
            </label>
            <label>
                <input type="radio" name="statusFilter" value="rejected" onchange="loadRequests()">
                Відхилені
            </label>
        </div>

        <div id="requests-container" style="max-width: 1200px; margin: 0 auto;">
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
        <h2 id="resolveModalTitle">Розгляд запиту</h2>
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

<footer>
    <div class="footer-content">
        <p>Copyright &copy; 2025 WanderUA. All rights reserved.</p>
    </div>
</footer>

<script>
    let currentTab = 'business-represent';
    let currentRequestId = null;
    let currentAction = null;

    function switchTab(tab) {
        currentTab = tab;

        const buttons = document.querySelectorAll('.tab-button');
        buttons.forEach(btn => btn.classList.remove('active'));

        event.target.classList.add('active');

        loadRequests();
    }

    function getSelectedStatus() {
        const selected = document.querySelector('input[name="statusFilter"]:checked');
        return selected ? selected.value : 'unresolved';
    }
    <#noparse>
    async function loadRequests() {
        const container = document.getElementById('requests-container');
        const status = getSelectedStatus();

        container.innerHTML = '<div style="text-align: center; padding: 40px; font-size: 18px; color: #666;">Завантаження...</div>';

        try {
            const endpoint = currentTab === 'business-represent'
                ? `/api/verify-requests/business-represent/by-status?status=${status}`
                : `/api/verify-requests/sustainability-status/by-status?status=${status}`;

            const response = await fetch(endpoint);

            if (!response.ok) {
                throw new Error('Failed to load requests');
            }

            const requests = await response.json();
            displayRequests(requests);

        } catch (error) {
            console.error('Error loading requests:', error);
            container.innerHTML = '<div style="text-align: center; padding: 40px; font-size: 18px; color: #ff0000;">Помилка завантаження запитів</div>';
        }
    }

    function displayRequests(requests) {
        const container = document.getElementById('requests-container');

        if (requests.length === 0) {
            container.innerHTML = '<div style="text-align: center; padding: 40px; font-size: 18px; color: #666;">Запитів не знайдено</div>';
            return;
        }

        container.innerHTML = requests.map(request => createRequestCard(request)).join('');
    }

    function createRequestCard(request) {
        const typeClass = currentTab === 'business-represent' ? 'badge-business-verify' : 'badge-sustainable-verify';
        const typeDisplay = currentTab === 'business-represent' ? 'Верифікація представника бізнесу' : 'Верифікація сталого бізнесу';

        let statusClass, statusText;
        if (!request.resolved) {
            statusClass = 'status-unresolved';
            statusText = 'Нерозглянуто';
        } else if (request.confirmed === true) {
            statusClass = 'status-confirmed';
            statusText = 'Підтверджено';
        } else if (request.confirmed === false) {
            statusClass = 'status-rejected';
            statusText = 'Відхилено';
        } else {
            statusClass = 'status-unresolved';
            statusText = 'Розглянуто';
        }

        let businessInfo = '';

        // Для відхилених BusinessRepresentVerify запитів бізнес видаляється
        if (currentTab === 'business-represent' && request.confirmed === false) {
            businessInfo = `
            <div class="deleted-business-notice">
                ℹ️ Інформація про бізнес була видалена після відхилення запиту
            </div>
        `;
        } else if (currentTab === 'business-represent') {
            // Для BusinessRepresentVerify показуємо повну інформацію про бізнес
            businessInfo = `
            <div class="business-details">
                <div class="detail-row"><strong>Назва бізнесу:</strong> ${request.businessName || 'Не вказано'}</div>
                <div class="detail-row"><strong>ПІБ представника:</strong> ${request.representFullName || 'Не вказано'}</div>
                ${request.businessDescription ? `<div class="detail-row"><strong>Опис бізнесу:</strong> ${request.businessDescription}</div>` : ''}
            </div>
        `;
        } else {
            // Для SustainabilityStatus показуємо тільки назву бізнесу
            businessInfo = `
            <div class="business-details">
                <div class="detail-row"><strong>Бізнес:</strong> ${request.businessName || 'Не вказано'}</div>
            </div>
        `;
        }

        // ВИПРАВЛЕНО: Правильна обробка documentSet
        let documentsHtml = '';
        console.log('Documents data:', request.documentSet); // Debug log

        // Перевірка різних варіантів структури даних
        let documents = [];
        if (request.documentSet) {
            if (Array.isArray(request.documentSet)) {
                documents = request.documentSet;
            } else if (typeof request.documentSet === 'object') {
                // Якщо це Set, перетворюємо в масив
                documents = Object.values(request.documentSet);
            }
        }

        if (documents && documents.length > 0) {
            documentsHtml = `
            <div class="documents-list">
                <strong>Прикріплені документи:</strong>
                ${documents.map(doc => {
                // Перевірка що doc існує і має необхідні поля
                if (!doc || !doc.id || !doc.fileName) {
                    console.warn('Invalid document:', doc);
                    return '';
                }
                return `
                        <div class="document-item">
                            <span class="document-icon">📄</span>
                            <a href="/api/documents/${doc.id}/download" class="document-link" target="_blank">
                                ${escapeHtml(doc.fileName)}
                            </a>
                        </div>
                    `;
            }).filter(html => html !== '').join('')}
            </div>
        `;
        } else {
            console.log('No documents found for request:', request.id);
        }

        let actionsHtml = '';
        if (!request.resolved) {
            actionsHtml = `
            <div class="complaint-actions">
                <button class="btn-resolve btn-confirm" onclick="openResolveModal(${request.id}, true)">
                    Підтвердити запит
                </button>
                <button class="btn-resolve btn-reject" onclick="openResolveModal(${request.id}, false)">
                    Відхилити
                </button>
            </div>
        `;
        }

        let adminCommentHtml = '';
        if (request.adminComment) {
            adminCommentHtml = `
            <div class="admin-comment">
                <strong>Коментар адміністратора:</strong><br>
                ${escapeHtml(request.adminComment)}
            </div>
        `;
        }

        return `
        <div class="complaint-card">
            <div class="complaint-header">
                <div>
                    <span class="complaint-type-badge ${typeClass}">${typeDisplay}</span>
                </div>
                <span class="complaint-status ${statusClass}">${statusText}</span>
            </div>

            <div class="complaint-body">
                <div class="complaint-info"><strong>Від користувача:</strong> ${request.username}</div>
                <div class="complaint-info"><strong>Дата подання:</strong> ${formatDate(request.createdAt)}</div>

                ${businessInfo}

                ${request.comment ? `<div class="complaint-comment"><strong>Коментар користувача:</strong><br>${escapeHtml(request.comment)}</div>` : ''}

                ${documentsHtml}

                ${adminCommentHtml}
                ${request.resolvedAt ? `<div class="complaint-info"><strong>Дата розгляду:</strong> ${formatDate(request.resolvedAt)}</div>` : ''}
            </div>

            ${actionsHtml}
        </div>
    `;
    }

    // Додайте функцію escapeHtml якщо її немає
    function escapeHtml(text) {
        if (!text) return '';
        const map = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#039;'
        };
        return text.toString().replace(/[&<>"']/g, m => map[m]);
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

    function openResolveModal(requestId, confirmed) {
        currentRequestId = requestId;
        currentAction = confirmed;

        const modal = document.getElementById('resolveModal');
        const actionText = document.getElementById('resolveModalAction');

        if (confirmed) {
            actionText.textContent = 'Ви збираєтесь ПІДТВЕРДИТИ запит на верифікацію';
            actionText.style.color = '#28a745';
        } else {
            actionText.textContent = 'Ви збираєтесь ВІДХИЛИТИ запит на верифікацію';
            actionText.style.color = '#dc3545';
        }

        modal.style.display = 'block';
    }

    function closeResolveModal() {
        document.getElementById('resolveModal').style.display = 'none';
        document.getElementById('resolveForm').reset();
        currentRequestId = null;
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
            const endpoint = currentTab === 'business-represent'
                ? `/api/verify-requests/business-represent/${currentRequestId}/resolve`
                : `/api/verify-requests/sustainability-status/${currentRequestId}/resolve`;

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
                alert('Запит успішно розглянуто!');
                closeResolveModal();
                loadRequests();
            } else {
                const error = await response.json();
                alert(error.error || 'Помилка розгляду запиту');
            }
        } catch (error) {
            console.error('Error resolving request:', error);
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

    // Load requests on page load
    document.addEventListener('DOMContentLoaded', function() {
        loadRequests();
    });
</script>

</body>
</html>
