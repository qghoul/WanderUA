<#ftl encoding='UTF-8'>
<!DOCTYPE html>
<html lang="uk">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" type="text/css" href="/css/styles.css">
  <title>Деталі оголошення</title>
  <style>
    .loading {
      text-align: center;
      padding: 20px;
      font-size: 18px;
    }
    .error-message {
      color: #d32f2f;
      text-align: center;
      padding: 20px;
      background-color: #ffebee;
      border-radius: 5px;
      margin: 20px 0;
    }
    .hidden {
      display: none;
    }
    /*.dynamic-field {
      margin: 5px 0;
    }
    .working-hours {
      margin: 10px 0;
    } */
    .star {
      color: #ffd700;
      font-size: 16px;
    }
    .star.empty {
      color: #ddd;
    }
    @media (max-width: 768px) {
      .addContainer {
        flex-direction: column;
      }

      .product-image {
        width: 100%;
        max-width: 500px;
        margin: 0 auto;
      }

      .center-global-container {
        padding: 10px;
      }

      .advertTitle {
        font-size: 24px;
      }
    }

  </style>
</head>
<body>
<script src="/js/auth-menu.js"></script>

<header>
  <ul class="menu">
    <div class="leftPart">
      <li id="forBussines"><a href="/forBusiness">Інформація для бізесу</a></li>
      <li><a href="/aboutProject">Про проєкт</a></li>
      <li><a href="/Tourists">Військовий туризм</a></li>
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

    <div id="loading" class="loading">
      Завантаження...
    </div>

    <div id="error" class="error-message hidden">
      Помилка завантаження оголошення
    </div>

    <div id="content" class="hidden">
      <div id="advertTitle" class="advertTitle"><b></b></div>

      <div class="addContainer">
        <div class="product-image">
          <div class="creation-date" id="creation-date-display" style="display: none;">
            Дата створення: <span id="creation-date-value"></span>
          </div>
          <img id="main-image" alt="Зображення оголошення">
        </div>

        <div class="advertDetails">
          <p id="rating"><b>Оцінка:</b> <span class="stars" id="stars-container"></span><span id="rating-value"></span> (<span id="ratings-count"></span> відгуків)</p>

          <div id="contacts-div"><p id="contact"><b>Контакти:</b> <span id="contact-value"></span></p></div>
          <div id="web-site-div"><p id="web-site"><b>Web-site:</b> <span id="website-value"></span></p></div>

          <div id="dynamic-fields"></div>

          <div id="working-hours" class="working-hours hidden">
            <p id="workdays"><b>Години роботи у будні дні:</b> <span id="workdays-value"></span></p>
            <p id="weekends"><b>Години роботи у вихідні дні:</b> <span id="weekends-value"></span></p>
          </div>

          <button class="submit-form" onclick="saveToFavorites()">Зберегти в "Ідею подорожі"</button>
          <button class="submit-form" onclick="openReviewModal()">Залишити відгук</button>
        </div>
      </div>

      <div class="describe">
        <div id="headline"><b>Опис</b></div>
        <p id="description-content"></p>
        <div class="complaintButton" onclick="reportAdvertisement()">Поскаржитись</div>
       <div id="views">Переглядів: <span id="views-count"></span></div>
        <p></p>
      </div>

      <div class="reviews-section">
        <h2>Відгуки</h2>
        <div id="reviews-container">

        </div>
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
  function getAdvertisementIdFromUrl() {
    <#if advertisementId??>
    console.log('Advertisement ID from model: ${advertisementId}');
    return '${advertisementId}';
    <#else>
    const urlParams = new URLSearchParams(window.location.search);
    const id = urlParams.get('id');
    console.log('Advertisement ID from URL params: ', id);
    return id;
    </#if>
  }

  async function loadAdvertisementData() {
    const advertisementId = getAdvertisementIdFromUrl();

    console.log('Loading advertisement with ID:', advertisementId);
    console.log('Type of ID:', typeof advertisementId);

    if (!advertisementId || advertisementId === 'null' || advertisementId === 'undefined') {
      console.error('Invalid advertisement ID:', advertisementId);
      showError('Невірний ID оголошення');
      return;
    }

    try {
      const url = '/api/advertisements/' + advertisementId;
      console.log('Fetching from URL:', url);

      const response = await fetch(url);
      console.log('Response status:', response.status);
      console.log('Response headers:', response.headers);

      if (!response.ok) {
        if (response.status === 404) {
          console.error('Advertisement not found');
          showError('Оголошення не знайдено');
        } else {
          console.error('Error response:', response.status, response.statusText);
          showError('Помилка завантаження оголошення');
        }
        return;
      }

      const data = await response.json();
      console.log('Advertisement data:', data);

      displayAdvertisement(data);

    } catch (error) {
      console.error('Error loading advertisement:', error);
      showError('Помилка завантаження оголошення');
    }
  }

  function displayAdvertisement(data) {
    document.getElementById('loading').classList.add('hidden');
    document.getElementById('content').classList.remove('hidden');

    document.querySelector('#advertTitle b').textContent = data.title || 'Без назви';
    document.getElementById('contact-value').textContent = data.contacts || 'Не вказано';
    if(data.contacts == null) {
      document.getElementById('contacts-div').classList.add('hidden');
    }

    const websiteElement = document.getElementById('website-value');
    if (data.website) {
      websiteElement.innerHTML = '<a href="' + (data.website.startsWith('http') ? data.website : 'http://' + data.website) + '" target="_blank">' + data.website + '</a>';
    } else {
      document.getElementById('web-site-div').classList.add('hidden');
    }

    document.getElementById('description-content').textContent = data.description || 'Опис відсутній';

    displayRating(data.reviewAvgRating, data.ratingsCount);

    setupMainImage(data.imageUrls);
    function setupMainImage(imageUrls) {
      const mainImage = document.getElementById('main-image');

      mainImage.src = imageUrls[0];
      mainImage.alt = 'Зображення оголошення';
    }

    displayWorkingHours(data);

    displayTypeSpecificFields(data);

    /*
    if (data.createdAt) {
      document.getElementById('creation-date-value').textContent = data.createdAt;
    }*/

    document.getElementById('views-count').textContent = data.views || 0;

    document.title = data.title || 'Деталі оголошення';
  }


  function displayRating(rating, count) {
    const starsContainer = document.getElementById('stars-container');
    const ratingValue = document.getElementById('rating-value');
    const ratingsCount = document.getElementById('ratings-count');

    starsContainer.innerHTML = '';

    const totalStars = 5;
    const fullStars = Math.floor(rating || 0);
    const hasHalf = (rating % 1) >= 0.25;

    for (let i = 0; i < totalStars; i++) {
      const star = document.createElement('span');
      star.classList.add('star');

      if (i < fullStars) {
        star.classList.add('filled');
        star.textContent = '★';
      } else if (i === fullStars && hasHalf) {
        star.classList.add('half');
        star.textContent = '★';
      } else {
        star.textContent = '☆';
        star.classList.add('empty');
      }

      starsContainer.appendChild(star);
    }

    ratingValue.textContent = ' ' + (rating ? rating.toFixed(1) : '0.0');
    ratingsCount.textContent = count || 0;
  }

  function displayWorkingHours(data) {
    const workingHoursDiv = document.getElementById('working-hours');
    const workdaysValue = document.getElementById('workdays-value');
    const weekendsValue = document.getElementById('weekends-value');

    let hasWorkingHours = false;

    if (data.weekdayOpen && data.weekdayClose) {
      workdaysValue.textContent = data.weekdayOpen + ' - ' + data.weekdayClose;
      hasWorkingHours = true;
    } else {
      workdaysValue.textContent = 'Не вказано';
    }

    if (data.weekendOpen && data.weekendClose) {
      weekendsValue.textContent = data.weekendOpen + ' - ' + data.weekendClose;
      hasWorkingHours = true;
    } else {
      weekendsValue.textContent = 'Не вказано';
    }

    if (hasWorkingHours) {
      workingHoursDiv.classList.remove('hidden');
    }
  }

  function displayTypeSpecificFields(data) {
    const dynamicFields = document.getElementById('dynamic-fields');
    dynamicFields.innerHTML = '';

    const additionalData = data.additionalData || {};

    switch (data.advertisementType) {
      case 'RESTAURANT':
        displayRestaurantFields(dynamicFields, additionalData);
        break;
      case 'TOUR':
        displayTourFields(dynamicFields, additionalData);
        break;
      case 'ACCOMODATION':
        displayAccommodationFields(dynamicFields, additionalData);
        break;
      case 'ENTERTAINMENT':
        displayEntertainmentFields(dynamicFields, additionalData);
        break;
      case 'PUBLIC_ATTRACTION':
        displayPublicAttractionFields(dynamicFields, additionalData);
        break;
    }

    if (additionalData.address) {
      const addressP = document.createElement('p');
      addressP.id = 'adress';
      addressP.innerHTML = '<b>Адреса:</b> ' + additionalData.address;
      dynamicFields.appendChild(addressP);
    }
  }

  function displayRestaurantFields(container, data) {
    if (data.priceCategoryDisplay) {
      const priceP = document.createElement('p');
      priceP.id = 'price_category';
      priceP.innerHTML = '<b>Цінова категорія:</b> ' + data.priceCategoryDisplay;
      container.appendChild(priceP);
    }

    if (data.specialOptionsDisplay) {
      const optionsP = document.createElement('p');
      optionsP.id = 'food_options';
      optionsP.innerHTML = '<b>Особливі пропозиції:</b> ' + data.specialOptionsDisplay;
      container.appendChild(optionsP);
    }
  }

  function displayTourFields(container, data) {
    if (data.tourType) {
      const typeP = document.createElement('p');
      typeP.innerHTML = '<b>Тип туру:</b> ' + formatTourType(data.tourType);
      container.appendChild(typeP);
    }

    if (data.durationDisplay) {
      const durationP = document.createElement('p');
      durationP.innerHTML = '<b>Тривалість:</b> ' + data.durationDisplay;
      container.appendChild(durationP);
    }

    if (data.priceDisplay) {
      const priceP = document.createElement('p');
      priceP.innerHTML = '<b>Ціна:</b> ' + data.priceDisplay;
      container.appendChild(priceP);
    }

    if (data.themes && data.themes.length > 0) {
      const themesP = document.createElement('p');
      const themesText = data.themes.map(formatTourTheme).join(', ');
      themesP.innerHTML = '<b>Тематика:</b> ' + themesText;
      container.appendChild(themesP);
    }
  }

  function displayAccommodationFields(container, data) {
    if (data.accomodationTypeDisplay) {
      const typeP = document.createElement('p');
      typeP.innerHTML = '<b>Тип житла:</b> ' + data.accomodationTypeDisplay;
      container.appendChild(typeP);
    }
  }

  function displayEntertainmentFields(container, data) {
    if (data.ageCategoryDisplay) {
      const ageP = document.createElement('p');
      ageP.innerHTML = '<b>Вікова категорія:</b> ' + data.ageCategoryDisplay;
      container.appendChild(ageP);
    }
  }

  function displayPublicAttractionFields(container, data) {
    if (data.freeVisitDisplay) {
      const visitP = document.createElement('p');
      visitP.innerHTML = '<b>Вартість відвідування:</b> ' + data.freeVisitDisplay;
      container.appendChild(visitP);
    }
  }

  function formatTourType(type) {
    const types = {
      'WALKING': 'Пішохідний',
      'BUS': 'Автобусний',
      'PRIVATE': 'Приватний',
      'MULTI_DAY': 'Багатоденний',
      'ALL_INCLUSIVE': 'All inclusive'
    };
    return types[type] || type;
  }

  function formatTourTheme(theme) {
    const themes = {
      'NATURE': 'Природа',
      'ARCHITECTURE': 'Архітектура',
      'HISTORY': 'Історія',
      'MILITARY': 'Військовий туризм',
      'GASTRO': 'Гастротуризм',
      'EXTREME': 'Екстримальний туризм',
      'OTHER': 'Інше'
    };
    return themes[theme] || theme;
  }

  function goBack() {
    if (document.referrer) {
      window.history.back();
    } else {
      window.location.href = '/advertisements';
    }
  }

  function saveToFavorites() {
    // TODO: Реализовать сохранение в избранное
    alert('Функція збереження в ідею подорожі буде реалізована пізніше');
  }

  function openReviewModal() {
    // TODO: Реализовать модальное окно для отзывов
    alert('Функція залишення відгуків буде реалізована пізніше');
  }

  function reportAdvertisement() {
    // TODO: Реализовать жалобы на объявления
    alert('Функція скарг буде реалізована пізніше');
  }

  function showError(message) {
    document.getElementById('loading').classList.add('hidden');
    document.getElementById('error').textContent = message;
    document.getElementById('error').classList.remove('hidden');
  }

  document.addEventListener('DOMContentLoaded', function() {
    loadAdvertisementData();
  });
</script>

</body>
</html>