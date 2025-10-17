<#ftl encoding='UTF-8'>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" type="text/css" href="/css/styles.css">
    <title>Мої скарги</title>
</head>
<body>
<script src="/js/auth-menu.js"></script>

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
        <h1 style="font-family: 'Lato', sans-serif; margin: 20px 0;">Мої скарги</h1>

        <div id="complaints-container" class="my-complaints-container" style="max-width: 1200px; margin: 0 auto;">
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
    async function loadMyComplaints() {
        const container = document.getElementById('complaints-container');

        try {
            const response = await fetch('/api/complaints/my');

            if (!response.ok) {
                if (response.status === 401) {
                    alert('Необхідна аутентифікація');
                    window.location.href = '/login';
                    return;
                }
                throw new Error('Failed to load complaints');
            }

            const data = await response.json();

            const allComplaints = [
                ...data.advertisementComplaints.map(c => ({...c})),
                ...data.reviewComplaints.map(c => ({...c}))
            ];

            displayComplaints(allComplaints);

        } catch (error) {
            console.error('Error loading complaints:', error);
            container.innerHTML = '<div style="text-align: center; padding: 40px; font-size: 18px; color: #ff0000;">Помилка завантаження скарг</div>';
        }
    }

    function displayComplaints(complaints) {
        const container = document.getElementById('complaints-container');

        if (!complaints || complaints.length === 0) {
            container.innerHTML = `
                <div class="no-complaints">
                    <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
                    </svg>
                    <h3>У вас поки немає поданих скарг</h3>
                    <p style="margin-top: 10px;">Коли ви подасте скаргу на оголошення або відгук, вона з'явиться тут</p>
                </div>
            `;
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
            statusText = 'На розгляді';
        } else if (complaint.confirmed === true) {
            statusClass = 'status-approved';
            statusText = 'Підтверджено';
        } else if (complaint.confirmed === false) {
            statusClass = 'status-rejected';
            statusText = 'Відхилено';
        } else {
            statusClass = 'status-unresolved';
            statusText = 'Розглянуто';
        }

        const targetInfo = complaint.complaintType === 'advertisement'
            ? `<strong>Оголошення:</strong> <a href="/advertisements/${complaint.advertisementId}" target="_blank">${escapeHtml(complaint.advertisementTitle)}</a>`
            : `<strong>Відгук:</strong> ${escapeHtml(complaint.reviewTitle || 'Без назви')}<br><small style="color: #666;">На оголошення: <a href="/advertisements/${complaint.advertisementId}" target="_blank">${escapeHtml(complaint.advertisementTitle)}</a></small>`;

        let adminResponseHtml = '';
        if (complaint.adminComment) {
            adminResponseHtml = `
                <div class="admin-comment">
                    <strong>Відповідь адміністратора:</strong><br>
                    ${escapeHtml(complaint.adminComment)}
                </div>
            `;
        }

        return `
            <div class="complaint-card">
                <div class="complaint-header">
                    <div>
                        <span class="complaint-type-badge ${typeClass}">${complaint.typeDisplay}</span>
                    </div>
                    <span class="complaint-status ${statusClass}">${statusText}</span>
                </div>

                <div class="complaint-body">
                    <div class="complaint-target">
                        ${targetInfo}
                    </div>

                    ${complaint.comment ? `<div class="complaint-comment"><strong>Ваш коментар:</strong><br>${escapeHtml(complaint.comment)}</div>` : ''}

                    <div class="complaint-info">
                        <strong>Дата подання:</strong> ${formatDate(complaint.createdAt)}
                    </div>

                    ${adminResponseHtml}

                    ${complaint.resolvedAt ? `<div class="complaint-info"><strong>Дата розгляду:</strong> ${formatDate(complaint.resolvedAt)}</div>` : ''}
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
        loadMyComplaints();
    });
</script>

</body>
</html>