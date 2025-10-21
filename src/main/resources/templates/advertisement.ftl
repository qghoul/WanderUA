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

    .form-group select{
      width: 100%;
      padding: 8px;
      border: 1px solid #ddd;
      border-radius: 4px;
      box-sizing: border-box;
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

    .usefulButton, .complaintButtonReview, .deleteButtonReview {
      padding: 5px 10px;
      margin-right: 10px;
      border: 1px solid #ddd;
      background-color: white;
      border-radius: 4px;
      cursor: pointer;
      font-size: 12px;
      color: black;
      transition: all 0.3s ease;
    }
    .deleteButtonReview:hover {background-color: pink;}

    .usefulButton:hover, .complaintButtonReview:hover {
      background-color: #f0f0f0;
    }
    .usefulButton.marked {
      background-color: lightgreen;
      border-color: #4CAF50;
    }
    .product-image .carouselForAdvPage {
      position: relative;
      width: 100%;
      height: 100%;
      overflow: hidden;
      border-radius: 8px;
    }

    .product-image .carouselForAdvPage img {
      position: absolute;
      width: 100%;
      height: 100%;
      object-fit: cover;
      opacity: 0;
      transition: opacity 0.3s ease;
    }

    .product-image .carouselForAdvPage img.active {
      opacity: 1;
    }

    .product-image .carousel-controls-for-adv-page {
      position: absolute;
      top: 50%;
      width: 100%;
      display: flex;
      justify-content: space-between;
      transform: translateY(-50%);
      z-index: 10;
      pointer-events: none;
    }

    .product-image .carouselForAdvPage .arrowForAdvPage {
      pointer-events: all;
      background-color: rgba(0, 0, 0, 0.5);
      color: white;
      border: none;
      padding: 10px 15px;
      cursor: pointer;
      font-size: 24px;
      border-radius: 4px;
      margin: 0 10px;
      transition: background-color 0.3s;
    }

    .product-image .carouselForAdvPage .arrowForAdvPage:hover {
      background-color: rgba(0, 0, 0, 0.7);
    }

    .product-image .indicators {
      position: absolute;
      bottom: 15px;
      left: 50%;
      transform: translateX(-50%);
      display: flex;
      gap: 8px;
      z-index: 10;
    }

    .product-image .indicator {
      width: 10px;
      height: 10px;
      border-radius: 50%;
      background-color: rgba(255, 255, 255, 0.5);
      cursor: pointer;
      transition: background-color 0.3s;
    }

    .product-image .indicator.active {
      background-color: white;
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
      <li id="mainButton"><a href="/">WanderUA</a></li>
      <li id="aboutProject"><a href="/aboutProject">Про проєкт</a></li>
      <li id="businessInfo"><a href="/business_info">Інформація для бізнесу</a></li>
      <li id ="faq"><a href="/help"></a></li>
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
          <div class="carouselForAdvPage" id="main-carousel">
          </div>
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
        <div class="reviews-sort">
          <label for="sortReviews">Сортувати:</label>
          <select id="sortReviews" onchange="onSortChange()">
            <option value="useful">За популярністю</option>
            <option value="date">За датою створення</option>
          </select>
        </div>
        <div id="reviews-container">

        </div>
      </div>
    </div>
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
        <select id="goWith" name="goWith" required>
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

<!-- Complaint Modal -->
<div id="complaintModal" class="modal">
  <div class="modal-content">
    <span class="close" onclick="closeComplaintModal()">&times;</span>
    <h2 id="complaintModalTitle">Поскаржитись</h2>
    <form id="complaintForm">
      <div class="form-group">
        <label for="complaintType">Тип скарги: <span style="color: red;">*</span></label>
        <select id="complaintType" name="type" required>
          <option value="">Оберіть тип скарги</option>
          <option value="SPAM">Спам</option>
          <option value="INAPPROPRIATE_CONTENT">Неприйнятний контент</option>
          <option value="FRAUD">Шахрайство</option>
          <option value="OTHER">Інше</option>
        </select>
      </div>

      <div class="form-group">
        <label for="complaintComment">Коментар (необов'язково):</label>
        <textarea id="complaintComment" name="comment" maxlength="500" placeholder="Опишіть проблему детальніше..."></textarea>
      </div>

      <div class="form-group">
        <button type="submit" class="submit-form" style="width: 100%;">Подати скаргу</button>
      </div>
    </form>
  </div>
</div>

<!-- Travel Idea Save Modal -->
<div id="travelIdeaModal" class="travel-idea-modal">
  <div class="travel-idea-modal-content">
    <div class="travel-idea-modal-header">
      <h2>Зберегти в ідею подорожі</h2>
      <button class="travel-idea-close" onclick="closeTravelIdeaModal()">&times;</button>
    </div>

    <div class="travel-idea-modal-body">
      <div class="success-message" id="successMessage">
        <span class="success-icon">✓</span>
        <span>Оголошення успішно додано до ідеї подорожі!</span>
      </div>

      <!-- Tabs -->
      <div class="travel-idea-tabs">
        <button class="travel-idea-tab active" onclick="switchTab('existing')">
          Існуючі ідеї
        </button>
        <button class="travel-idea-tab" onclick="switchTab('new')">
          Створити нову
        </button>
      </div>

      <!-- Tab: Existing Travel Ideas -->
      <div id="existingTab" class="travel-idea-tab-content active">
        <div class="travel-idea-search">
          <input type="text" id="searchTravelIdeas" placeholder="🔍 Пошук ідей подорожі..." onkeyup="filterTravelIdeas()">
        </div>

        <div id="travelIdeasListContainer">
          <!-- Loading State -->
          <div class="loading-state" id="loadingState">
            <div class="spinner"></div>
            <p>Завантаження ідей подорожі...</p>
          </div>

          <!-- Travel Ideas List -->
          <div class="travel-ideas-list" id="travelIdeasList" style="display: none;">
          </div>

          <!-- Empty State -->
          <div class="empty-state" id="emptyState" style="display: none;">
            <div class="empty-state-icon">📝</div>
            <div class="empty-state-text">У вас ще немає ідей подорожі</div>
            <div class="empty-state-subtext">Створіть нову ідею, щоб почати збирати цікаві місця</div>
          </div>
        </div>
      </div>

      <!-- Tab: Create New Travel Idea -->
      <div id="newTab" class="travel-idea-tab-content">
        <div class="create-new-form">
          <div class="form-group">
            <label for="newTravelIdeaTitle">
              Назва <span class="required-mark">*</span>
            </label>
            <input type="text"
                   id="newTravelIdeaTitle"
                   placeholder="Наприклад: Вихідні у Львові"
                   maxlength="100"
                   required>
          </div>

          <div class="form-group">
            <label for="newTravelIdeaDescription">
              Опис
            </label>
            <textarea id="newTravelIdeaDescription"
                      placeholder="Опишіть вашу ідею подорожі..."
                      maxlength="1000"></textarea>
          </div>
        </div>
      </div>
    </div>

    <div class="travel-idea-modal-footer">
      <button class="btn-travel-idea btn-cancel" onclick="closeTravelIdeaModal()">
        Скасувати
      </button>
      <button class="btn-travel-idea btn-save" id="saveButton" onclick="handleSave()">
        Зберегти
      </button>
    </div>
  </div>
</div>

<footer>
  <div class="footer-content">
    <p>Copyright &copy; 2025 WanderUA. All rights reserved.</p>
  </div>
</footer>

<script>
  let currentTab = 'existing';
  let selectedTravelIdeaId = null;
  let currentAdvertisementId = null;
  let travelIdeas = [];

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

    displayWorkingHours(data);

    displayTypeSpecificFields(data);

    /*
    if (data.createdAt) {
      document.getElementById('creation-date-value').textContent = data.createdAt;
    }*/

    document.getElementById('views-count').textContent = data.views || 0;

    document.title = data.title || 'Деталі оголошення';
  }

  function setupMainImage(imageUrls) {
    const carousel = document.getElementById('main-carousel');
    if (!imageUrls || imageUrls.length === 0) {
      carousel.innerHTML = '<img src="/images/no-image.jpg" alt="Зображення відсутнє">';
      return;
    }
    carousel.innerHTML = '';
    imageUrls.forEach((url, index) => {
      const img = document.createElement('img');
      img.src = url;
      img.alt = `Зображення оголошення` + (index + 1);
      if (index === 0) img.classList.add('active');
      carousel.appendChild(img);
    });
    // Add controls if more than one image
    if (imageUrls.length > 1) {
      const controls = document.createElement('div');
      controls.className = 'carousel-controls-for-adv-page';
      controls.innerHTML = `
      <button class="arrowForAdvPage prev" onclick="stopPropagation()">‹</button>
      <button class="arrowForAdvPage next" onclick="stopPropagation()">›</button>
        `;
      carousel.appendChild(controls);

      // Add indicators
      const indicatorsContainer = document.createElement('div');
      indicatorsContainer.className = 'indicators';
      imageUrls.forEach((_, index) => {
        const indicator = document.createElement('div');
        indicator.className = 'indicator' + (index === 0 ? ' active' : '');
        indicatorsContainer.appendChild(indicator);
      });
      carousel.appendChild(indicatorsContainer);

      // Initialize carousel functionality
      initMainCarousel(imageUrls.length);
    }

    carousel.addEventListener('click', () => {
      if (imageUrls && imageUrls.length > 0) {
        const currentImage = carousel.querySelector('img.active');
        const currentIndex = Array.from(carousel.querySelectorAll('img')).indexOf(currentImage);
        openImageModal(imageUrls[currentIndex], imageUrls);
      }
    });
  }

  function initMainCarousel(imageCount) {
    const carousel = document.getElementById('main-carousel');
    const images = carousel.querySelectorAll('img');
    const prevBtn = carousel.querySelector('.prev');
    const nextBtn = carousel.querySelector('.next');
    const indicators = carousel.querySelectorAll('.indicator');

    let current = 0;

    function showImage(index) {
      images[current].classList.remove('active');
      indicators[current].classList.remove('active');
      current = index;
      images[current].classList.add('active');
      indicators[current].classList.add('active');
    }

    if (prevBtn) {
      prevBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        showImage((current - 1 + imageCount) % imageCount);
      });
    }

    if (nextBtn) {
      nextBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        showImage((current + 1) % imageCount);
      });
    }

    indicators.forEach((indicator, index) => {
      indicator.addEventListener('click', (e) => {
        e.stopPropagation();
        showImage(index);
      });
    });

    // Keyboard navigation
    document.addEventListener('keydown', (e) => {
      if (e.key === 'ArrowLeft') {
        showImage((current - 1 + imageCount) % imageCount);
      } else if (e.key === 'ArrowRight') {
        showImage((current + 1) % imageCount);
      }
    });
  }

  // global variable for SortType
  let currentSortType = 'useful';

  function onSortChange() {
    const sortSelect = document.getElementById('sortReviews');
    currentSortType = sortSelect.value;

    console.log('Sort changed to:', currentSortType);

    const advertisementId = getAdvertisementIdFromUrl();
    loadReviews(advertisementId);
  }

  async function loadReviews(advertisementId) {
    try {
      const url = `/api/reviews/advertisement/` + advertisementId +`?sort=` + currentSortType;
      console.log('Fetching reviews from:', url);

      const reviewsResponse = await fetch(url);

      if (!reviewsResponse.ok) {
        console.error('Failed to load reviews');
        return;
      }
        const reviews = await reviewsResponse.json();
        console.log('Reviews received:', reviews.length);

        // upload statuses by single request
        const statusResponse = await fetch(`/api/reviews/advertisement/${advertisementId}/useful-status`);
        const statusData = await statusResponse.json();
        const statuses = statusData.statuses || {};

        // Add status to every review
        const reviewsWithStatus = reviews.map(review => ({
          ...review,
          markedByCurrentUser: statuses[review.id] || false
        }));

        displayReviews(reviewsWithStatus);
      }
      catch (error) {
      console.error('Error loading reviews:', error);
    }
  }
  async function checkIfMarkedAsUseful(reviewId) {
    try {
      const response = await fetch(`/api/reviews/`+ reviewId + `/useful/status`);

      if (response.ok) {
        const data = await response.json();
        return data.markedAsUseful;
      }

      // if user don't authenticated
      return false;
    } catch (error) {
      console.error('Error checking useful status:', error);
      return false;
    }
  }
  <#noparse>
  function displayReviews(reviews) {
    const container = document.getElementById('reviews-container');

    if (reviews.length === 0) {
      container.innerHTML = '<div class="without-reviews-message">Поки що немає відгуків. Будьте першим!</div>';
      return;
    }

    container.innerHTML = reviews.map(review => {
      const isMarked = review.markedByCurrentUser;
      const buttonClass = isMarked ? 'usefulButton marked' : 'usefulButton';
      const buttonText = isMarked ? 'Відмічено' : 'Корисно';
      const buttonOnClick = isMarked
              ? `unmarkReviewAsUseful(${review.id})`
              : `markReviewAsUseful(${review.id})`;
      const deleteButton = review.isAuthor
              ? `<button class="deleteButtonReview" onclick="deleteReview(${review.id})">Видалити</button>`
              : '';

      const imageUrlsJson = review.imageUrls ? JSON.stringify(review.imageUrls) : '[]';

      return `
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
              ${review.imageUrls.map((url, index) =>
                      `<img src="${url}"
                      alt="Фото відгуку ${index + 1}"
                      onclick='openImageModal("${url}", ${imageUrlsJson})'
                      title="Натисніть для перегляду">`
              ).join('')}
            </div>` : ''}
          <button class="${buttonClass}" onclick="${buttonOnClick}">
            ${buttonText} (${review.usefulScore || 0})
          </button>
           ${deleteButton}
          <button class="complaintButtonReview" onclick="reportReview(${review.id})">Поскаржитись</button>
        </div>
      </div>
    `;
    }).join('');
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
  async function unmarkReviewAsUseful(reviewId) {
    try {
      const response = await fetch(`/api/reviews/` + reviewId + `/useful`, {
        method: 'DELETE'
      });

      if (response.ok) {
        console.log('Review unmarked as useful successfully');
        const advertisementId = getAdvertisementIdFromUrl();
        await loadReviews(advertisementId);
      } else {
        const error = await response.json();
        alert(error.error || 'Помилка');
      }
    } catch (error) {
      console.error('Error unmarking review as useful:', error);
      alert('Помилка відправки запиту');
    }
  }

  async function deleteReview(reviewId) {
    if (!confirm('Ви впевнені, що хочете видалити цей відгук?')) {
      return;
    }

    try {
      const response = await fetch(`/api/reviews/` + reviewId, {
        method: 'DELETE'
      });

      if (response.ok) {
        console.log('Review deleted successfully');
        alert('Відгук успішно видалено');
        const advertisementId = getAdvertisementIdFromUrl();
        await loadReviews(advertisementId);
        await loadAdvertisementData();
      } else {
        const error = await response.json();
        alert(error.error || 'Помилка видалення відгуку');
      }
    } catch (error) {
      console.error('Error deleting review:', error);
      alert('Помилка видалення відгуку');
    }
  }

  // Global variables for image gallery
  let currentImageIndex = 0;
  let currentImageGallery = [];

  function openImageModal(imageUrl, galleryUrls = null) {
    const modal = document.getElementById('imageModal');
    const modalImage = document.getElementById('modalImage');

    if (galleryUrls && Array.isArray(galleryUrls)) {
      currentImageGallery = galleryUrls;
      currentImageIndex = galleryUrls.indexOf(imageUrl);
    } else {
      currentImageGallery = [imageUrl];
      currentImageIndex = 0;
    }

    updateModalImage();
    modal.style.display = 'block';

    document.body.style.overflow = 'hidden';
  }

  function closeImageModal() {
    const modal = document.getElementById('imageModal');
    modal.style.display = 'none';
    document.body.style.overflow = 'auto';
    currentImageGallery = [];
    currentImageIndex = 0;
  }

  function updateModalImage() {
    const modalImage = document.getElementById('modalImage');
    const imageCounter = document.getElementById('imageCounter');
    const prevBtn = document.getElementById('prevBtn');
    const nextBtn = document.getElementById('nextBtn');

    modalImage.src = currentImageGallery[currentImageIndex];

    if (currentImageGallery.length > 1) {
      imageCounter.textContent = currentImageIndex + 1;
      imageCounter.style.display = 'block';
    } else {
      imageCounter.style.display = 'none';
    }

    if (currentImageGallery.length > 1) {
      prevBtn.style.display = 'block';
      nextBtn.style.display = 'block';

      prevBtn.disabled = currentImageIndex === 0;
      nextBtn.disabled = currentImageIndex === currentImageGallery.length - 1;
    } else {
      prevBtn.style.display = 'none';
      nextBtn.style.display = 'none';
    }
  }

  function previousImage() {
    if (currentImageIndex > 0) {
      currentImageIndex--;
      updateModalImage();
    }
  }

  function nextImage() {
    if (currentImageIndex < currentImageGallery.length - 1) {
      currentImageIndex++;
      updateModalImage();
    }
  }

  // keyboard listener
  document.addEventListener('keydown', function(e) {
    const modal = document.getElementById('imageModal');

    if (modal.style.display === 'block') {
      if (e.key === 'Escape') {
        closeImageModal();
      } else if (e.key === 'ArrowLeft') {
        previousImage();
      } else if (e.key === 'ArrowRight') {
        nextImage();
      }
    }
  });

  //Complaint Section functions
  async function reportAdvertisement() {
    const isAuthenticated = await checkAuthentication();
    if (!isAuthenticated) {
      alert('Для подання скарги необхідно увійти в систему');
      window.location.href = '/login';
      return;
    }
    openComplaintModal('advertisement', getAdvertisementIdFromUrl());
  }

  async function reportReview(reviewId) {
    const isAuthenticated = await checkAuthentication();
    if (!isAuthenticated) {
      alert('Для подання скарги необхідно увійти в систему');
      window.location.href = '/login';
      return;
    }
    openComplaintModal('review', reviewId);
  }

  async function checkAuthentication() {
    try {
      const response = await fetch('/api/auth/status', {
        method: 'GET',
        credentials: 'include'
      });

      if (!response.ok) {return false;}

      const data = await response.json();

      return data.authenticated === true;

    } catch (error) {
      console.error('Error checking authentication:', error);
      return false;
    }
  }

  function openComplaintModal(type, targetId) {
    const modal = document.getElementById('complaintModal');
    const modalTitle = document.getElementById('complaintModalTitle');

    // Store type and targetId for submission
    modal.dataset.complaintType = type;
    modal.dataset.targetId = targetId;

    if (type === 'advertisement') {
      modalTitle.textContent = 'Поскаржитись на оголошення';
    } else {
      modalTitle.textContent = 'Поскаржитись на відгук';
    }

    modal.style.display = 'block';
  }

  function closeComplaintModal() {
    const modal = document.getElementById('complaintModal');
    modal.style.display = 'none';
    document.getElementById('complaintForm').reset();
  }

  document.getElementById('complaintForm').addEventListener('submit', async function(e) {
    e.preventDefault();

    const modal = document.getElementById('complaintModal');
    const type = modal.dataset.complaintType;
    const targetId = modal.dataset.targetId;

    const complaintType = document.getElementById('complaintType').value;
    const complaintComment = document.getElementById('complaintComment').value;

    if (!complaintType) {
      alert('Будь ласка, оберіть тип скарги');
      return;
    }

    const endpoint = type === 'advertisement'
            ? `/api/complaints/advertisement/` + targetId
            : `/api/complaints/review/` + targetId;

    try {
      const response = await fetch(endpoint, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          type: complaintType,
          comment: complaintComment
        })
      });

      if (response.ok) {
        alert('Скаргу успішно подано!');
        closeComplaintModal();
      } else {
        const error = await response.json();
        alert(error.error || 'Помилка подання скарги');
      }
    } catch (error) {
      console.error('Error submitting complaint:', error);
      alert('Помилка відправки скарги');
    }
  });

  // Open modal and load travel ideas
  async function openTravelIdeaModal(advertisementId) {
    console.log('Opening modal for advertisement:', advertisementId);

    currentAdvertisementId = advertisementId;
    selectedTravelIdeaId = null;

    const modal = document.getElementById('travelIdeaModal');
    if (!modal) {
      console.error('Modal not found!');
      return;
    }

    modal.style.display = 'block';
    document.body.style.overflow = 'hidden';

    switchTab('existing');

    await loadTravelIdeas();;
  }

  function closeTravelIdeaModal() {
    const modal = document.getElementById('travelIdeaModal');
    if (modal) {
      modal.style.display = 'none';
    }
    document.body.style.overflow = 'auto';

    // Reset form
    const titleInput = document.getElementById('newTravelIdeaTitle');
    const descInput = document.getElementById('newTravelIdeaDescription');
    const searchInput = document.getElementById('searchTravelIdeas');
    const successMsg = document.getElementById('successMessage');

    if (titleInput) titleInput.value = '';
    if (descInput) descInput.value = '';
    if (searchInput) searchInput.value = '';
    if (successMsg) successMsg.classList.remove('show');

    selectedTravelIdeaId = null;
  }

  function switchTab(tabName) {
    currentTab = tabName;

    console.log('Switching to tab:', tabName);

    const tabs = document.querySelectorAll('.travel-idea-tab');
    tabs.forEach(tab => {
      tab.classList.remove('active');
      const tabText = tab.textContent.trim().toLowerCase();
      if ((tabName === 'existing' && tabText.includes('існуючі')) ||
              (tabName === 'new' && tabText.includes('створити'))) {
        tab.classList.add('active');
      }
    });

    const existingTab = document.getElementById('existingTab');
    const newTab = document.getElementById('newTab');

    if (existingTab) {
      existingTab.classList.remove('active');
      if (tabName === 'existing') {
        existingTab.classList.add('active');
      }
    }

    if (newTab) {
      newTab.classList.remove('active');
      if (tabName === 'new') {
        newTab.classList.add('active');
      }
    }

    updateSaveButton();
  }

  function switchTabSafe(event, tabName) {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }
    switchTab(tabName);
  }
  <#noparse>
  async function loadTravelIdeas() {
    const loadingState = document.getElementById('loadingState');
    const listContainer = document.getElementById('travelIdeasList');
    const emptyState = document.getElementById('emptyState');

    if (loadingState) loadingState.style.display = 'block';
    if (listContainer) listContainer.style.display = 'none';
    if (emptyState) emptyState.style.display = 'none';

    try {
      console.log('Fetching from /api/travel-ideas...');
      const response = await fetch('/api/travel-ideas', {
        method: 'GET',
        credentials: 'include',
        headers: {
          'Accept': 'application/json'
        }
      });

      console.log('Response status:', response.status);

      if (!response.ok) {
        const errorText = await response.text();
        console.error('Error response:', errorText);
        throw new Error(`Помилка: ${response.status}`);
      }

      const responseText = await response.text();
      console.log('Response received:', responseText.substring(0, 100) + '...');

      travelIdeas = JSON.parse(responseText);
      console.log('Parsed', travelIdeas.length, 'travel ideas');

      if (!Array.isArray(travelIdeas)) {
        throw new Error('Невірний формат даних');
      }

      if (loadingState) loadingState.style.display = 'none';

      if (travelIdeas.length === 0) {
        console.log('No travel ideas found');
        if (emptyState) emptyState.style.display = 'block';
      } else {
        console.log('Displaying travel ideas');
        if (listContainer) listContainer.style.display = 'block';
        displayTravelIdeas(travelIdeas);
      }
    } catch (error) {
      console.error('Error loading travel ideas:', error);
      loadingState.innerHTML = '<p style="color: #ff4444;">Помилка завантаження ідей подорожі</p>';
    }
  }


  function displayTravelIdeas(ideas) {
    const listContainer = document.getElementById('travelIdeasList');

    listContainer.innerHTML = ideas.map(idea => `
            <div class="travel-idea-item" onclick="selectTravelIdea(${idea.id})" data-id="${idea.id}">
                <div class="travel-idea-item-info">
                    <div class="travel-idea-item-title">${escapeHtml(idea.title)}</div>
                    ${idea.description ? `<div class="travel-idea-item-description">${escapeHtml(idea.description.substring(0, 80))}${idea.description.length > 80 ? '...' : ''}</div>` : ''}
                    <div class="travel-idea-item-count">📍 ${idea.advertisementCount || 0} місць</div>
                </div>
                <div class="travel-idea-item-check"></div>
            </div>
        `).join('');
    }

  function selectTravelIdea(id) {
    selectedTravelIdeaId = id;
    document.querySelectorAll('.travel-idea-item').forEach(item => {
      item.classList.remove('selected');
    });
    const selectedItem = document.querySelector(`.travel-idea-item[data-id="${id}"]`);
    if (selectedItem) {
      selectedItem.classList.add('selected');
    }
    updateSaveButton();
  }


  function filterTravelIdeas() {
    const searchInput = document.getElementById('searchTravelIdeas');
    if (!searchInput) return;

    const searchTerm = searchInput.value.toLowerCase();
    const filteredIdeas = travelIdeas.filter(idea =>
            idea.title.toLowerCase().includes(searchTerm) ||
            (idea.description && idea.description.toLowerCase().includes(searchTerm))
    );

    displayTravelIdeas(filteredIdeas);
  }

  function updateSaveButton() {
    const saveButton = document.getElementById('saveButton');

    if (currentTab === 'existing') {
      saveButton.textContent = 'Додати до ідеї';
      saveButton.disabled = selectedTravelIdeaId === null;
    } else {
      saveButton.textContent = 'Створити і додати';
      saveButton.disabled = false;
    }
  }

  async function handleSave() {
    if (currentTab === 'existing') {
      await addToExistingTravelIdea();
    } else {
      await createNewTravelIdea();
    }
  }

  async function addToExistingTravelIdea() {
    if (!selectedTravelIdeaId) {
      alert('Будь ласка, оберіть ідею подорожі');
      return;
    }

    const saveButton = document.getElementById('saveButton');
    saveButton.disabled = true;
    saveButton.textContent = 'Збереження...';

    try {
      const response = await fetch(`/api/travel-ideas/${selectedTravelIdeaId}/advertisements/${currentAdvertisementId}`, {
        method: 'POST',
        credentials: 'include'
      });

      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.error || 'Помилка збереження');
      }

      const successMsg = document.getElementById('successMessage');
      successMsg.classList.add('show');

      setTimeout(() => {
        closeTravelIdeaModal();
      }, 1500);

    } catch (error) {
      console.error('Error adding to travel idea:', error);
      alert(error.message || 'Помилка додавання до ідеї подорожі');
      saveButton.disabled = false;
      saveButton.textContent = 'Додати до ідеї';
    }
  }

  async function createNewTravelIdea() {
    const title = document.getElementById('newTravelIdeaTitle').value.trim();
    const description = document.getElementById('newTravelIdeaDescription').value.trim();

    if (!title) {
      alert('Будь ласка, вкажіть назву ідеї подорожі');
      return;
    }

    const saveButton = document.getElementById('saveButton');
    saveButton.disabled = true;
    saveButton.textContent = 'Створення...';

    try {
      const createResponse = await fetch('/api/travel-ideas', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          title: title,
          description: description
        })
      });

      if (!createResponse.ok) {
        const error = await createResponse.json();
        throw new Error(error.error || 'Помилка створення ідеї');
      }

      const result = await createResponse.json();
      const newTravelIdeaId = result['travel-idea'].id;

      const addResponse = await fetch(`/api/travel-ideas/${newTravelIdeaId}/advertisements/${currentAdvertisementId}`, {
        method: 'POST'
      });

      if (!addResponse.ok) {
        throw new Error('Помилка додавання оголошення');
      }

      const successMsg = document.getElementById('successMessage');
      successMsg.classList.add('show');

      setTimeout(() => {
        closeTravelIdeaModal();
      }, 1500);

    } catch (error) {
      console.error('Error creating travel idea:', error);
      alert(error.message || 'Помилка створення ідеї подорожі');
      saveButton.disabled = false;
      saveButton.textContent = 'Створити і додати';
    }
  }

  function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }

  window.onclick = function(event) {
    const modal = document.getElementById('travelIdeaModal');
    if (event.target === modal) {
      closeTravelIdeaModal();
    }
  }

  document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
      const modal = document.getElementById('travelIdeaModal');
      if (modal.style.display === 'block') {
        closeTravelIdeaModal();
      }
    }
  });

  async function saveToFavorites() {
    const advertisementId = getAdvertisementIdFromUrl();

    const isAuthenticated = await checkAuthentication();
    if (!isAuthenticated) {
      alert('Для збереження оголошення необхідно увійти в систему');
      window.location.href = '/login';
      return;
    }

    openTravelIdeaModal(advertisementId);
  }
  </#noparse>

  function goBack() {
    if (document.referrer) {
      window.history.back();
    } else {
      window.location.href = '/advertisements';
    }
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