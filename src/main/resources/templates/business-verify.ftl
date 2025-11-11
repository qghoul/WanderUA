<#ftl encoding='UTF-8'>
<!DOCTYPE html>
<html>
<script src="/static/js/auth-menu.js"></script>
<head>
    <meta charset="UTF-8">
    <title>Верифікація як представник бізнесу</title>
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
    <h2 class="advertTitle">Верифікація як представник бізнесу</h2>
    <div class="addForm-container">
        <form class="addForm" id="businessVerificationForm" enctype="multipart/form-data">
            <hr>
            <label for="fullName"><b>ПІБ:</b></label>
            <input type="text" id="fullName" name="fullName" required>

            <label for="businessName"><b>Назва бізнесу:</b></label>
            <input type="text" id="businessName" name="businessName" required>

            <label for="businessDescription"><b>Опис бізнесу:</b></label>
            <textarea id="businessDescription" name="businessDescription" rows="5" required></textarea>
            <label for="requestComment"><b>Коментар:</b></label>
            <textarea id="requestComment" name="requestComment" rows="5" ></textarea>
            <label for="documents"><b>Документи, що підтверджують представництво:</b></label>
            <input type="file" id="documents" name="documents" accept=".pdf,.jpg,.jpeg,.png,.doc,.docx" multiple required>
            <div class="info-text">
                <p>
                    Для підтвердження статусу представника бізнесу необхідно надати наступні документи: документ, що підтверджує особу; документ, що підтверджує повноваження представляти бізнес.
                </p>
            </div>
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
    document.getElementById('businessVerificationForm').addEventListener('submit', async function(e) {
        e.preventDefault();

        const fullName = document.getElementById('fullName').value;
        const businessName = document.getElementById('businessName').value;
        const businessDescription = document.getElementById('businessDescription').value;
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

            // Extract only IDs from uploaded documents
            const documentIds = uploadedDocuments.map(doc => doc.id);

            // Create verification request with document IDs
            const requestData = {
                businessName: businessName,
                businessDescription: businessDescription,
                representFullName: fullName,
                documentIds: documentIds,  // FIXED: Send IDs instead of full objects
                verifyRequestType: 'BUSINESS_VERIFY',
                comment: requestComment || null
            };

            const response = await fetch('/api/verify-requests/business-verify/send', {
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
</script>

</body>
</html>