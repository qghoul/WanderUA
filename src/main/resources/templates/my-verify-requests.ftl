<#ftl encoding='UTF-8'>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" type="text/css" href="/css/styles.css">
    <title>Мої запити на верифікацію</title>
    <style>
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

        .status-approved {
            background-color: #28a745;
            color: white;
            padding: 6px 12px;
            border-radius: 4px;
            font-weight: bold;
            font-size: 14px;
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
        <h1 style="font-family: 'Lato', sans-serif; margin: 20px 0;">Мої запити на верифікацію</h1>

        <div id="requests-container" class="my-complaints-container" style="max-width: 1200px; margin: 0 auto;">
            <div class="loading-spinner">
                <div class="spinner"></div>
                <p style="margin-top: 20px; color: #666;">Завантаження...</p>
            </div>
        </div>
    </div>
</div>

<footer>
    <div class="footer-content">
        <p>Copyright &copy; 2025 WanderUA. All rights reserved.</p>
    </div>
</footer>

<script>
    async function loadMyRequests() {
        const container = document.getElementById('requests-container');

        try {
            const response = await fetch('/api/verify-requests/my');

            if (!response.ok) {
                if (response.status === 401) {
                    alert('Необхідна аутентифікація');
                    window.location.href = '/login';
                    return;
                }
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

        if (!requests || requests.length === 0) {
            container.innerHTML = `
                <div class="no-complaints">
                    <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
                    </svg>
                    <h3>У вас поки немає запитів на верифікацію</h3>
                    <p style="margin-top: 10px;">Коли ви подасте запит на верифікацію, він з'явиться тут</p>
                </div>
            `;
            return;
        }

        container.innerHTML = requests.map(request => createRequestCard(request)).join('');
    }
    <#noparse>
    function createRequestCard(request) {
        const isBusinessVerify = request.verifyRequestType === 'BUSINESS_VERIFY';
        const typeClass = isBusinessVerify ? 'badge-business-verify' : 'badge-sustainable-verify';
        const typeDisplay = isBusinessVerify ? 'Верифікація представника бізнесу' : 'Верифікація сталого бізнесу';

        let statusClass, statusText;
        if (!request.resolved) {
            statusClass = 'status-unresolved';
            statusText = 'На розгляді';
        } else if (request.confirmed === true) {
            statusClass = 'status-approved';
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
        if (isBusinessVerify && request.confirmed === false) {
            businessInfo = `
            <div class="deleted-business-notice">
                ℹ️ Інформація про бізнес була видалена після відхилення запиту
            </div>
        `;
        } else if (isBusinessVerify) {
            // Для BusinessRepresentVerify показуємо повну інформацію
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

        let adminResponseHtml = '';
        if (request.adminComment) {
            adminResponseHtml = `
            <div class="admin-comment">
                <strong>Відповідь адміністратора:</strong><br>
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
                ${businessInfo}

                ${request.comment ? `<div class="complaint-comment"><strong>Ваш коментар:</strong><br>${escapeHtml(request.comment)}</div>` : ''}

                <div class="complaint-info">
                    <strong>Дата подання:</strong> ${formatDate(request.createdAt)}
                </div>

                ${documentsHtml}

                ${adminResponseHtml}

                ${request.resolvedAt ? `<div class="complaint-info"><strong>Дата розгляду:</strong> ${formatDate(request.resolvedAt)}</div>` : ''}
            </div>
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

    document.addEventListener('DOMContentLoaded', function() {
        loadMyRequests();
    });
</script>

</body>
</html>
