<#ftl encoding='UTF-8'>
<!DOCTYPE html>
<html>
<script src="/static/js/auth-menu.js"></script>
<head>
    <meta charset="UTF-8">
    <title>Верифікація бізнесу як сталий</title>
    <link rel="stylesheet" type="text/css" href="css/styles.css">
</head>
<body>
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

<div class="center-global-container">
    <h2 class="advertTitle">Верифікація бізнесу як сталий</h2>

    <div class="info-text">
        <p>
            Після проходження верифікації як сталий бізнес, ви отримаєте позначку на платформі, що свідчить про відповідальне ставлення до довкілля та суспільства.
            Це підвищить довіру з боку туристів, сприятиме зростанню популярності та ваші пропозиції матимуть особливий пріорітет та будуть відображатись першими.
        </p>
    </div>

    <div class="addForm-container">
        <form class="addForm" id="sustainabilityVerificationForm" enctype="multipart/form-data">
            <hr>
            <label for="businessId"><b>Оберіть бізнес:</b></label>
            <select class="business-select" id="businessId" name="businessId" required>
                <option value="">Завантаження...</option>
            </select>

            <label for="requestComment"><b>Коментар:</b></label>
            <textarea id="requestComment" name="requestComment" rows="5"></textarea>

            <label for="documents"><b>Документи, що підтверджують проходження сертифікації на відповідність принципам сталого розвитку:</b></label>
            <input type="file" id="documents" name="documents" accept=".pdf,.jpg,.jpeg,.png,.doc,.docx" multiple required>

            <button type="submit" class="submit-form">Надіслати запит</button>
        </form>
    </div>
</div>

<footer>
    <div class="footer-content">
        <p>Copyright &copy; 2025 WanderUA. All rights reserved.</p>
    </div>
</footer>

<script>
    let userBusiness = null;

    // Load user's business on page load
    document.addEventListener('DOMContentLoaded', async function() {
        await loadUserBusiness();
    });

    <#noparse>
    async function loadUserBusiness() {
        try {
            const response = await fetch('/api/user/business');

            if (!response.ok) {
                if (response.status === 401) {
                    alert('Необхідна аутентифікація');
                    window.location.href = '/login';
                    return;
                }
                throw new Error('Failed to load business');
            }

            const business = await response.json();
            userBusiness = business;

            const select = document.getElementById('businessId');

            if (business && business.id) {
                select.innerHTML = `<option value="${business.id}">${business.name}</option>`;
            } else {
                select.innerHTML = '<option value="">У вас немає бізнесу. Спочатку пройдіть верифікацію як представник бізнесу.</option>';
                document.querySelector('button[type="submit"]').disabled = true;
            }
        } catch (error) {
            console.error('Error loading business:', error);
            document.getElementById('businessId').innerHTML = '<option value="">Помилка завантаження</option>';
        }
    }

    document.getElementById('sustainabilityVerificationForm').addEventListener('submit', async function(e) {
        e.preventDefault();

        if (!userBusiness || !userBusiness.id) {
            alert('Не знайдено бізнес. Спочатку пройдіть верифікацію як представник бізнесу.');
            return;
        }

        const requestComment = document.getElementById('requestComment').value;
        const documentsInput = document.getElementById('documents');

        if (!documentsInput.files || documentsInput.files.length === 0) {
            alert('Будь ласка, додайте необхідні документи');
            return;
        }

        try {
            // Upload documents first and get their IDs
            const uploadedDocuments = await uploadDocuments(documentsInput.files);

            if (!uploadedDocuments || uploadedDocuments.length === 0) {
                alert('Помилка завантаження документів');
                return;
            }

            // FIXED: Extract only IDs from uploaded documents
            const documentIds = uploadedDocuments.map(doc => doc.id);

            // Create verification request with document IDs
            const requestData = {
                documentIds: documentIds,  // FIXED: Send IDs instead of full objects
                verifyRequestType: 'SUSTAINABLE_VERIFY',
                comment: requestComment || null
            };

            const response = await fetch('/api/verify-requests/sustainability-status/send', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(requestData)
            });

            const result = await response.json();

            if (response.ok) {
                alert('Запит на верифікацію успішно надіслано! Очікуйте на розгляд адміністратором.');
                window.location.href = '/my-verify-requests';
            } else {
                alert(result.error || 'Помилка відправки запиту');
            }
        } catch (error) {
            console.error('Error submitting verification request:', error);
            alert('Помилка відправки запиту. Спробуйте пізніше.');
        }
    });

    async function uploadDocuments(files) {
        const formData = new FormData();

        for (let i = 0; i < files.length; i++) {
            formData.append('files', files[i]);
        }

        try {
            const response = await fetch('/api/documents/upload', {
                method: 'POST',
                body: formData
            });

            if (!response.ok) {
                throw new Error('Document upload failed');
            }

            const documents = await response.json();
            return documents;  // Returns array with [{id, fileName, ...}, ...]
        } catch (error) {
            console.error('Error uploading documents:', error);
            return null;
        }
    }
    </#noparse>
</script>
</body>
</html>
