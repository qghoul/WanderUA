<#ftl encoding='UTF-8'>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Створення оголошення</title>
    <link rel="stylesheet" type="text/css" href="/static/css/styles.css">
    <style>
        .dynamic-fields {
            margin-top: -10px;
        }
        .error-message {
            color: #d32f2f;
            font-size: 0.875rem;
            margin-top: 5px;
            padding: 10px;
            background-color: #ffebee;
            border-radius: 5px;
            border-left: 4px solid #d32f2f;
        }
        .success-message {
            color: #2e7d32;
            font-size: 0.875rem;
            margin-top: 5px;
            padding: 10px;
            background-color: #e8f5e8;
            border-radius: 5px;
            border-left: 4px solid #2e7d32;
        }
        .loading {
            opacity: 0.6;
            pointer-events: none;
        }
        .submit-form:disabled {
            background-color: #ccc;
            cursor: not-allowed;
        }
        #messages {
            margin-bottom: 15px;
        }
    </style>
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
    <h2 class="advertTitle">Створення пропозиції</h2>
    <div id="messages"></div>
    <div class="addForm-container">
        <form class="addForm" id="advertForm">
            <label for="title"><b>Назва пропозиції:</b></label>
            <input type="text" id="title" name="title" required>

            <label for="category"><b>Категорія пропозиції:</b></label>
            <select id="category" name="category" class="business-select" required>
                <option value="">Оберіть категорію</option>
                <option value="restaurant">Заклад харчування</option>
                <option value="tour">Тур</option>
                <option value="accomodation">Житло</option>
                <option value="entertainment">Розваги</option>
                <option value="public_attraction">Пам'ятки</option>
            </select>

            <div id="category-specific"></div>

            <label for="website"><b>Веб-сайт:</b></label>
            <input type="text" id="website" name="website ">

            <label for="contacts"><b>Контакти:</b></label>
            <input type="text" id="contacts" name="contacts">

            <label for="description"><b>Опис:</b></label>
            <textarea id="description" name="description" rows="5"></textarea>

            <label for="images"><b>Додати фото:</b></label>
            <input type="file" id="images" name="images" accept="image/*" onchange="checkFileCount(this)" multiple required>
            <div class="image-preview"></div>

            <button type="submit" class="submit-form">Опублікувати пропозицію</button>
        </form>
    </div>
</div>

<script src="/static/js/auth-menu.js"></script>

<script>
    async function checkAdvertisementAccess() {
        try {
            // Проверка: если пользователь дошел до этой страницы через Spring Security значит у него есть права
            return {
                canCreate: true,
                message: 'Доступ дозволено'
            };
        } catch (error) {
            console.error('Error checking access:', error);
            return {
                canCreate: false,
                message: 'Помилка перевірки доступу: ' + error.message
            };
        }
    }
    function showMessage(message, type = 'error') {
        const messagesContainer = document.getElementById('messages');
        messagesContainer.innerHTML = `
        <div class="${r"${type}"}-message">
            ${r"${message}"}
        </div>
    `;

        setTimeout(() => {
            messagesContainer.innerHTML = '';
        }, 5000);
    }

    function collectFormData() {
        const formData = new FormData(document.getElementById('advertForm'));
        const category = formData.get('category');
        const categoryToType = {
            'restaurant': 'RESTAURANT',
            'tour': 'TOUR',
            'accomodation': 'ACCOMODATION',
            'entertainment': 'ENTERTAINMENT',
            'public_attraction': 'PUBLIC_ATTRACTION'
        };
        const data = {
            title: formData.get('title'),
            advertisementType: categoryToType[category],
            website: formData.get('website') || null,
            contacts: formData.get('contacts') || null,
            description: formData.get('description') || null,
            workdays: formData.get('workdays') || null,
            weekends: formData.get('weekends') || null
        };

        if (category === 'restaurant') {
            data.address = formData.get('address');
            data.priceCategory = formData.get('priceCategory');

            const specialOptions = [];
            const checkboxes = document.querySelectorAll('input[name="specialOptions"]:checked');
            checkboxes.forEach(cb => specialOptions.push(cb.value));
            data.specialOptions = specialOptions;

        } else if (category === 'tour') {
            data.tourType = formData.get('tourType');
            data.duration = formData.get('duration') ? formData.get('duration') : null;

            data.priceUsd = formData.get('priceUsd') ? formData.get('priceUsd') : null;
            data.priceUah = formData.get('priceUah') ? formData.get('priceUah') : null;

            const tourThemes = [];
            const themeCheckboxes = document.querySelectorAll('input[name="tourTheme"]:checked');
            themeCheckboxes.forEach(cb => tourThemes.push(cb.value));
            data.tourTheme = tourThemes;

        } else if (category === 'accomodation') {
            data.address = formData.get('address');
            data.accomodationType = formData.get('accomodationType');

        } else if (category === 'entertainment') {
            data.address = formData.get('address');
            data.ageCategory = formData.get('ageCategory');

        } else if (category === 'public_attraction') {
            data.address = formData.get('address');
            data.freeVisit = formData.get('freeVisit') === 'True';
        }

        return data;
    }

    // Create adds using REST API
    async function createAdvertisement(advertisementData, images) {
        try {
            const formData = new FormData();

            formData.append('data', JSON.stringify(advertisementData));

            if (images && images.length > 0) {
                for (let i = 0; i < images.length; i++) {
                    formData.append('images', images[i]);
                }
            }

            console.log('Sending request to /api/advertisements');
            console.log('Data:', advertisementData);
            console.log('Images count:', images ? images.length : 0);

            const response = await fetch('/api/advertisements', {
                method: 'POST',
                body: formData
            });

            if (response.ok) {
                const result = await response.json();
                return result;
            } else {
                const contentType = response.headers.get("content-type");
                if (contentType && contentType.includes("application/json")) {
                    const errorData = await response.json();
                    throw new Error(errorData.error || errorData.message || `HTTP error! status:` + response.status);
                } else {
                    const errorText = await response.text();
                    throw new Error('HTTP error! status: ' + response.status);
                }
            }

        } catch (error) {
            console.error('Error creating advertisement:', error);
            throw error;
        }
    }

    function validateForm() {
        const form = document.getElementById('advertForm');
        const category = document.getElementById('category').value;

        if (!document.getElementById('title').value.trim()) {
            showMessage('Поле "Назва пропозиції" є обов\'язковим');
            document.getElementById('title').focus();
            return false;
        }

        if (!category) {
            showMessage('Поле "Категорія пропозиції" є обов\'язковим');
            document.getElementById('category').focus();
            return false;
        }

        /*if (!document.getElementById('contacts').value.trim()) {
            showMessage('Поле "Контакти" є обов\'язковим');
            document.getElementById('contacts').focus();
            return false;
        } */

        if (category === 'restaurant' || category === 'entertainment' || category === 'accomodation' || category === 'public_attraction') {
            const addressField = document.getElementById('address');
            if (!addressField || !addressField.value.trim()) {
                showMessage('Поле "Адреса" є обов\'язковим для цієї категорії');
                if (addressField) addressField.focus();
                return false;
            }
        }

        if (category === 'restaurant') {
            const priceCategoryField = document.getElementById('priceCategory');
            if (!priceCategoryField || !priceCategoryField.value) {
                showMessage('Поле "Цінова категорія" є обов\'язковим');
                if (priceCategoryField) priceCategoryField.focus();
                return false;
            }
        }

        if (category === 'tour') {
            const tourTypeField = document.getElementById('tourType');
            if (!tourTypeField || !tourTypeField.value) {
                showMessage('Поле "Тип туру" є обов\'язковим');
                if (tourTypeField) tourTypeField.focus();
                return false;
            }
        }

        if (category === 'entertainment') {
            const ageCategoryField = document.getElementById('ageCategory');
            if (!ageCategoryField || !ageCategoryField.value) {
                showMessage('Поле "Вікова категорія" є обов\'язковим');
                if (ageCategoryField) ageCategoryField.focus();
                return false;
            }
        }

        if (category === 'accomodation') {
            const accomodationTypeField = document.getElementById('accomodationType');
            if (!accomodationTypeField || !accomodationTypeField.value) {
                showMessage('Поле "Тип житла" є обов\'язковим');
                if (accomodationTypeField) accomodationTypeField.focus();
                return false;
            }
        }

        const images = document.getElementById('images').files;
        if (images.length === 0) {
            showMessage('Додайте хоча б одне зображення');
            document.getElementById('images').focus();
            return false;
        }

        if (images.length > 5) {
            showMessage('Максимум 5 зображень');
            document.getElementById('images').focus();
            return false;
        }

        return true;
    }

    document.addEventListener('DOMContentLoaded', function() {
        const form = document.getElementById('advertForm');
        const submitButton = form.querySelector('.submit-form');

        form.addEventListener('submit', async function(e) {
            e.preventDefault();

            const accessCheck = await checkAdvertisementAccess();
            if (!accessCheck.canCreate) {
                showMessage(accessCheck.message);
                return;
            }

            submitButton.disabled = true;
            submitButton.textContent = 'Створюємо...';
            form.classList.add('loading');

            try {
                const isValid = validateForm();
                if (!isValid) {
                    return;
                }

                const advertisementData = collectFormData();
                const images = document.getElementById('images').files;

                console.log('Sending data:', advertisementData);

                const result = await createAdvertisement(advertisementData, images);

                showMessage('Оголошення успішно створено!', 'success');
                form.reset();
                document.getElementById('category-specific').innerHTML = '';
                document.querySelector('.image-preview').innerHTML = '';

                setTimeout(() => {
                    if (result && result.id) {
                        window.location.href = '/advertisements/' + result.id;
                    } else {
                        window.location.href = '/advertisement?id=' + result.id;
                    }
                }, 2000);

            } catch (error) {
                console.error('Error:', error);
                showMessage('Помилка при створенні оголошення: ' + error.message);
            } finally {
                submitButton.disabled = false;
                submitButton.textContent = 'Опублікувати пропозицію';
                form.classList.remove('loading');
            }
        });
    });

    document.getElementById("category").addEventListener("change", function () {
        const selected = this.value;
        const container = document.getElementById("category-specific");
        container.innerHTML = "";

        if (selected === "restaurant") {
            container.innerHTML = `
      <label for="priceCategory"><b>Цінова категорія:</b></label>
      <select class="business-select" id="priceCategory" name="priceCategory" required>
        <option value="">Оберіть категорію</option>
        <option value="CHEAP">$ Бюджетна</option>
        <option value="MID">$ Середня</option>
        <option value="LUXURY">$$ Люкс</option>
      </select>

      <label><b>Особливі пропозиції:</b></label>
      <div class="checkbox-group">
        <label><input type="checkbox" name="specialOptions" value="halal"> Halal опції</label>
        <label><input type="checkbox" name="specialOptions" value="vegan"> Веган опції</label>
      </div>

      <label for="address"><b>Адреса:</b></label>
      <input type="text" id="address" name="address" required>

      <label for="workdays"><b>Години роботи у будні дні:</b></label>
      <input type="text" id="workdays" name="workdays" placeholder="Наприклад: 09:00 - 18:00">

      <label for="weekends"><b>Години роботи у вихідні:</b></label>
      <input type="text" id="weekends" name="weekends" placeholder="Наприклад: 10:00 - 16:00">
    `;
        }

        if (selected === "tour") {
            container.innerHTML = `
      <label><b>Тематика туру:</b></label>
      <div class="checkbox-group">
        <label><input type="checkbox" name="tourTheme" value="NATURE"> Природа</label>
        <label><input type="checkbox" name="tourTheme" value="ARCHITECTURE"> Архітектура</label>
        <label><input type="checkbox" name="tourTheme" value="HISTORY"> Історія</label>
        <label><input type="checkbox" name="tourTheme" value="MILITARY"> Військовий туризм</label>
        <label><input type="checkbox" name="tourTheme" value="GASTRO"> Гастротуризм</label>
        <label><input type="checkbox" name="tourTheme" value="EXTREME"> Екстримальний туризм</label>
        <label><input type="checkbox" name="tourTheme" value="OTHER"> Інше</label>
      </div>

      <label for="tourType"><b>Тип туру:</b></label>
      <select class="business-select" id="tourType" name="tourType" required>
        <option value="">Оберіть тип</option>
        <option value="WALKING">Пішохідний</option>
        <option value="BUS">Автобусний</option>
        <option value="PRIVATE">Приватний</option>
        <option value="MULTI_DAY">Багатоденний</option>
        <option value="ALL_INCLUSIVE">All inclusive</option>
      </select>

      <label for="duration"><b>Тривалість (у годинах):</b></label>
      <input type="number" id="duration" name="duration" placeholder="Наприклад: 6" min="1" max="1000">

      <label for="priceUsd"><b>Ціна (USD):</b></label>
      <input type="number" id="priceUsd" name="priceUsd" step="0.01" min="0">

      <label for="priceUah"><b>Ціна (UAH):</b></label>
      <input type="number" id="priceUah" name="priceUah" step="0.01" min="0">
    `;
        }

        if (selected === "entertainment") {
            container.innerHTML = `
      <label for="ageCategory"><b>Вікова категорія:</b></label>
      <select class="business-select" id="ageCategory" name="ageCategory" required>
        <option value="">Оберіть категорію</option>
        <option value="KIDS">Діти</option>
        <option value="TEENS">Підлітки</option>
        <option value="ADULTS">Дорослі</option>
        <option value="ONLY_ADULTS">Лише для дорослих</option>
      </select>

      <label for="address"><b>Адреса:</b></label>
      <input type="text" id="address" name="address" required>

      <label for="workdays"><b>Години роботи у будні дні:</b></label>
      <input type="text" id="workdays" name="workdays" placeholder="Наприклад: 09:00 - 18:00">

      <label for="weekends"><b>Години роботи у вихідні:</b></label>
      <input type="text" id="weekends" name="weekends" placeholder="Наприклад: 10:00 - 16:00">
    `;
        }

        if (selected === "accomodation") {
            container.innerHTML = `
      <label for="address"><b>Адреса:</b></label>
      <input type="text" id="address" name="address" required>

      <label for="accomodationType"><b>Тип житла:</b></label>
      <select class="business-select" id="accomodationType" name="accomodationType" required>
        <option value="">Оберіть тип</option>
        <option value="HOTEL">Готель</option>
        <option value="MOTEL">Мотель</option>
        <option value="AGENCY">Агенство нерухомості</option>
        <option value="OTHER">Інше</option>
      </select>
    `;
        }

        if (selected === "public_attraction") {
            container.innerHTML = `
      <label for="address"><b>Адреса:</b></label>
      <input type="text" id="address" name="address" required>

      <label for="freeVisit"><b>Безкоштовний візит:</b></label>
      <select class="business-select" id="freeVisit" name="freeVisit" required>
        <option value="">Оберіть опцію</option>
        <option value="False">Ні</option>
        <option value="True">Так</option>
      </select>
    `;
        }
    });

    function checkFileCount(input) {
        if (input.files.length > 5) {
            showMessage('Максимум 5 файлів!');
            input.value = '';
        } else {
            previewImages(input);
        }
    }

    function previewImages(input) {
        const files = input.files;
        const preview = document.querySelector('.image-preview');
        preview.innerHTML = '';

        Array.from(files).forEach((file, i) => {
            const reader = new FileReader();
            reader.onload = function (e) {
                const img = document.createElement('img');
                img.src = e.target.result;
                img.classList.add('preview-image');
                preview.appendChild(img);

                const removeButton = document.createElement('button');
                removeButton.textContent = 'X';
                removeButton.classList.add('remove-button');
                removeButton.onclick = function () {
                    const updatedFiles = removeFile(files, i);
                    input.files = updatedFiles;
                    previewImages(input);
                };
                preview.appendChild(removeButton);
            };
            reader.readAsDataURL(file);
        });
    }

    function removeFile(files, index) {
        const dt = new DataTransfer();
        for (let i = 0; i < files.length; i++) {
            if (i !== index) dt.items.add(files[i]);
        }
        return dt.files;
    }
</script>
</body>
</html>