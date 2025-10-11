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
    /* Review Modal Styles */
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

    .form-group {
      margin-bottom: 15px;
    }

    .form-group label {
      display: block;
      margin-bottom: 5px;
      font-weight: bold;
    }

    .form-group input,
    .form-group select,
    .form-group textarea {
      width: 100%;
      padding: 8px;
      border: 1px solid #ddd;
      border-radius: 4px;
      box-sizing: border-box;
    }

    .form-group textarea {
      height: 100px;
      resize: vertical;
    }

    .rating-input {
      display: flex;
      gap: 5px;
      margin-bottom: 10px;
    }

    .rating-input .star-input {
      font-size: 24px;
      color: #ddd;
      cursor: pointer;
      transition: color 0.2s;
    }

    .rating-input .star-input.active,
    .rating-input .star-input:hover {
      color: #ffd700;
    }
    .image-upload-area {
      border: 2px dashed #ddd;
      padding: 20px;
      text-align: center;
      cursor: pointer;
      border-radius: 4px;
      transition: border-color 0.3s;
    }

    .image-upload-area:hover {
      border-color: #007bff;
    }

    .image-preview {
      display: flex;
      gap: 10px;
      margin-top: 10px;
      flex-wrap: wrap;
    }

    .image-preview img {
      width: 80px;
      height: 80px;
      object-fit: cover;
      border-radius: 4px;
      border: 1px solid #ddd;
    }

    /* Reviews Section Styles */
    .reviews-section {
      margin-top: 30px;
    }

    .review {
      display: flex;
      border: 1px solid #ddd;
      border-radius: 8px;
      margin-bottom: 20px;
      padding: 20px;
      background-color: #fafafa;
    }

    .review-left {
      min-width: 200px;
      padding-right: 20px;
      border-right: 1px solid #eee;
    }

    .review-right {
      flex: 1;
      padding-left: 20px;
    }

    .nickname {
      font-weight: bold;
      margin-bottom: 10px;
    }

    .review-stars {
      margin-bottom: 10px;
    }

    .visit-date, .visit-type {
      margin-bottom: 5px;
      font-size: 14px;
    }

    .review-title {
      margin-bottom: 10px;
      color: #333;
    }

    .review-text {
      margin-bottom: 15px;
      line-height: 1.6;
    }

    .review-photos {
      display: flex;
      gap: 10px;
      margin-bottom: 15px;
      flex-wrap: wrap;
    }

    .review-photos img {
      width: 100px;
      height: 100px;
      object-fit: cover;
      border-radius: 4px;
      cursor: pointer;
    }

    .usefulButton, .complaintButtonReview {
      padding: 5px 10px;
      margin-right: 10px;
      border: 1px solid #ddd;
      background-color: white;
      border-radius: 4px;
      cursor: pointer;
      font-size: 12px;
      color: black;
    }

    .usefulButton:hover, .complaintButtonReview:hover {
      background-color: #f0f0f0;
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
      .modal-content {
        width: 95%;
        margin: 10% auto;
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

<!-- Review Modal -->
<div id="reviewModal" class="modal">
  <div class="modal-content">
    <span class="close">&times;</span>
    <h2>Залишити відгук</h2>
    <form id="reviewForm" enctype="multipart/form-data">
      <div class="form-group">
        <label for="reviewTitle">Заголовок відгуку:</label>
        <input type="text" id="reviewTitle" name="title" maxlength="255" placeholder="Коротко опишіть ваш досвід">
      </div>
      <input type="hidden" name="advertisementId" value= ${advertisementId}/>
      <div class="form-group">
        <label>Оцінка: <span style="color: red;">*</span></label>
        <div class="rating-input" id="ratingInput">
          <span class="star-input" data-rating="1">★</span>
          <span class="star-input" data-rating="2">★</span>
          <span class="star-input" data-rating="3">★</span>
          <span class="star-input" data-rating="4">★</span>
          <span class="star-input" data-rating="5">★</span>
        </div>
        <input type="hidden" id="reviewRating" name="rating" required>
      </div>

      <div class="form-group">
        <label for="reviewComment">Коментар:</label>
        <textarea id="reviewComment" name="comment" maxlength="1000" placeholder="Поділіться своїм досвідом..."></textarea>
      </div>

      <div class="form-group">
        <label for="goWith">З ким відвідували:</label>
        <select id="goWith" name="goWith">
          <option value="">Оберіть варіант</option>
          <option value="SOLO">Один/одна</option>
          <option value="COUPLE">Пара</option>
          <option value="FAMILY">Сім'я</option>
          <option value="FRIENDS">Друзі</option>
          <option value="BUSINESS">Бізнес</option>
          <option value="OTHER">Інше</option>
        </select>
      </div>

      <div class="form-group">
        <label for="reviewImages">Фотографії (необов'язково):</label>
        <div class="image-upload-area" onclick="document.getElementById('reviewImages').click()">
          <input type="file" id="reviewImages" name="images" multiple accept="image/*" style="display: none;">
          <p>Натисніть або перетягніть фотографії сюди</p>
          <small>Максимум 5 зображень, до 5MB кожне</small>
        </div>
        <div id="imagePreview" class="image-preview"></div>
      </div>

      <div class="form-group">
        <button type="submit" class="submit-form" style="width: 100%;">Опублікувати відгук</button>
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

      await loadReviews(advertisementId);

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

  async function loadReviews(advertisementId) {
    try {
      const response = await fetch(`/api/reviews/advertisement/` + advertisementId); //
      if (response.ok) {
        const reviews = await response.json();
        displayReviews(reviews);
      }
    } catch (error) {
      console.error('Error loading reviews:', error);
    }
  }
  <#noparse>
  function displayReviews(reviews) {
    const container = document.getElementById('reviews-container');

    if (reviews.length === 0) {
      container.innerHTML = '<p>Поки що немає відгуків. Будьте першим!</p>';
      return;
    }

    container.innerHTML = reviews.map(review => `
      <div class="review">
        <div class="review-left">
          <p class="nickname">${review.username || 'Анонім'}</p>
          <div class="review-stars">${generateStars(review.rating)}</div>
          ${review.dateDisplay ? `<p class="visit-date">Дата візиту <b>${review.dateDisplay}</b></p>` : ''}
  ${review.goWithDisplay ? `<p class="visit-type">Тип візиту <b>${review.goWithDisplay}</b></p>` : ''}
        </div>
        <div class="review-right">
          ${review.title ? `<h3 class="review-title">${review.title}</h3>` : ''}
  ${review.comment ? `<p class="review-text">${review.comment}</p>` : ''}
  ${review.imageUrls && review.imageUrls.length > 0 ?
    `<div class="review-photos">
      ${review.imageUrls.map(url => `<img src="${url}" alt="Фото відгуку" onclick="openImageModal('${url}')">`).join('')}
  </div>` : ''}
  <button class="usefulButton" onclick="markReviewAsUseful(${review.id})">Корисно (${review.usefulScore || 0})</button>
  <button class="complaintButtonReview" onclick="reportAdvertisement()">Поскаржитись</button>
  </div>
  </div>
  `).join('');
  }
  </#noparse>

    function generateStars(rating) {
      let stars = '';
      for (let i = 1; i <= 5; i++) {
        if (i <= rating) {
          stars += '<span class="star filled">★</span>';
        } else {
          stars += '<span class="star empty">☆</span>';
        }
      }
      return stars;
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

  // Review Modal Functions
  async function openReviewModal() {
    let currentAdvertisementId = getAdvertisementIdFromUrl();
    // Check if user can review
    try {
      const url = `/api/reviews/can-review/` + currentAdvertisementId;
      console.log('Checking review permission at:', url);

      const response = await fetch(url);

      console.log('Response status:', response.status);
      console.log('Response ok:', response.ok);


      const result = await response.json();
      console.log('Can review result:', result);

      if (result.needLogin) {
        alert('Для залишення відгуку необхідно увійти в систему');
        window.location.href = '/login';
        return;
      }

      if (!result.canReview) {
        alert('Ви вже залишили відгук для цього оголошення');
        return;
      }

      document.getElementById('reviewModal').style.display = 'block';

    } catch (error) {
      console.error('Error checking review permission:', error);
      console.error('Error details:', error.message);

      // If there's a network error or server error, still allow opening the modal
      // The actual submission will handle authentication
      console.warn('Opening modal anyway, will check authentication on submit');
      document.getElementById('reviewModal').style.display = 'block';
    }
  }

  function closeReviewModal() {
    document.getElementById('reviewModal').style.display = 'none';
    resetReviewForm();
  }

  function resetReviewForm() {
    document.getElementById('reviewForm').reset();
    document.getElementById('reviewRating').value = '';
    document.querySelectorAll('.star-input').forEach(star => star.classList.remove('active'));
    document.getElementById('imagePreview').innerHTML = '';
  }

  // Star rating functionality
  document.addEventListener('DOMContentLoaded', function() {
    const stars = document.querySelectorAll('.star-input');

    stars.forEach((star, index) => {
      star.addEventListener('click', () => {
        const rating = parseInt(star.dataset.rating);
        document.getElementById('reviewRating').value = rating;

        stars.forEach((s, i) => {
          if (i < rating) {
            s.classList.add('active');
          } else {
            s.classList.remove('active');
          }
        });
      });

      star.addEventListener('mouseenter', () => {
        const rating = parseInt(star.dataset.rating);
        stars.forEach((s, i) => {
          if (i < rating) {
            s.style.color = '#ffd700';
          } else {
            s.style.color = '#ddd';
          }
        });
      });
    });

    document.querySelector('.rating-input').addEventListener('mouseleave', () => {
      const currentRating = parseInt(document.getElementById('reviewRating').value) || 0;
      stars.forEach((s, i) => {
        if (i < currentRating) {
          s.style.color = '#ffd700';
        } else {
          s.style.color = '#ddd';
        }
      });
    });
  });

  // Image upload functionality
  document.getElementById('reviewImages').addEventListener('change', function(e) {
    const files = Array.from(e.target.files);
    const previewContainer = document.getElementById('imagePreview');
    previewContainer.innerHTML = '';

    if (files.length > 5) {
      alert('Максимум 5 зображень');
      return;
    }

    files.forEach((file, index) => {
      if (file.size > 5 * 1024 * 1024) {
        alert(`Файл занадто великий (максимум 5MB)`);
        return;
      }

      const reader = new FileReader();
      reader.onload = function(e) {
        const img = document.createElement('img');
        img.src = e.target.result;
        previewContainer.appendChild(img);
      };
      reader.readAsDataURL(file);
    });
  });

  // Form submission
  document.getElementById('reviewForm').addEventListener('submit', async function(e) {
    e.preventDefault();

    const rating = document.getElementById('reviewRating').value;
    if (!rating) {
      alert('Будь ласка, оберіть оцінку');
      return;
    }

    const formData = new FormData();
    let currentAdvertisementId = getAdvertisementIdFromUrl();
    formData.append('advertisementId', currentAdvertisementId);
    formData.append('rating', rating);
    let title = document.getElementById('reviewTitle').value;
    formData.append('title', title);
    let comment = document.getElementById('reviewComment').value;
    formData.append('comment', comment);
    let goWith = document.getElementById('goWith').value || null;
    formData.append('goWith', goWith);

    console.log('advertisementId', currentAdvertisementId);
    console.log('rating', rating);
    console.log('title', title);
    console.log('comment', comment);
    console.log('goWith', goWith);


    // Add images
    const images = document.getElementById('reviewImages').files;
    for (let i = 0; i < images.length; i++) {
      formData.append('images', images[i]);
    }
    try {
      console.log('Sending review to server...');
      const response = await fetch('/api/reviews', {
        method: 'POST',
        body: formData
      });

      console.log('Response status:', response.status);
      console.log('Response ok:', response.ok);

      if (response.ok) {
        alert('Відгук успішно опубліковано!');
        closeReviewModal();
        loadReviews(currentAdvertisementId);
        loadAdvertisementData(); // Reload to update rating
      } else {
        const error = await response.json();
        console.error('Error response:', error);
        alert(error.error || 'Помилка створення відгуку');
      }
    } catch (error) {
      console.error('Error submitting review:', error);
      alert('Помилка відправки відгуку' + error.message);
    }
  });

  // Modal close functionality
  document.querySelector('.close').addEventListener('click', closeReviewModal);

  window.addEventListener('click', function(e) {
    const modal = document.getElementById('reviewModal');
    if (e.target === modal) {
      closeReviewModal();
    }
  });

  async function markReviewAsUseful(reviewId) {
    try {
      const response = await fetch(`/api/reviews/` + reviewId + `/useful`, {
        method: 'POST'
      });

      if (response.ok) {
        const advertisementId = getAdvertisementIdFromUrl();
        loadReviews(advertisementId);
      } else {
        const error = await response.json();
        alert(error.error || 'Помилка');
      }
    } catch (error) {
      console.error('Error marking review as useful:', error);
      alert('Помилка відправки запиту');
    }
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

 /* function openReviewModal() {
    // TODO: Реализовать модальное окно для отзывов
    alert('Функція залишення відгуків буде реалізована пізніше');
  } */

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