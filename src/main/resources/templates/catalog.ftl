<#ftl encoding='UTF-8'>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="/css/styles.css">
    <title>Каталог: ${selectedCity!""}</title>
</head>
<body>
<script src="/js/auth-menu.js"></script>
<script src="/js/catalog.js"></script>
<header>
    <ul class="menu">
        <div class="leftPart">
            <li id="forBussines"><a href="/forBussines">Інформація для бізнесу</a></li>
            <li><a href="/aboutProject">Про проєкт</a></li>
            <li><a href="/catalog?category=tour">Військовий туризм</a></li>
            <li><a href="/help">FAQ</a></li>
        </div>
        <div class="rightPart">
            <li id="login"><a href="/login">Увійти</a></li>
            <li id="createAdd"><a href="/registration">Зареєструватись</a></li>
        </div>
    </ul>
</header>

<div class="category-buttons-container">
    <button class="category-button" data-category="" id="all-category">Усі</button>
    <button class="category-button" data-category="TOUR" id="tours">Тури</button>
    <button class="category-button" data-category="RESTAURANT" id="food">Де поїсти</button>
    <button class="category-button" data-category="ENTERTAINMENT" id="entertainment">Розваги</button>
    <button class="category-button" data-category="PUBLIC_ATTRACTION" id="sights">Пам'ятки</button>
    <button class="category-button" data-category="ACCOMODATION" id="accommodation">Житло</button>
</div>

<div class="container">
    <div class="filters">
        <h3>Фільтрація</h3>
        <div class="filter-group">
            <div class="filter-type">Населений пункт</div>
            <input type="text" id="cityInputCatalog" placeholder="" autocomplete="off" value="${selectedCity!""}">
            <ul id="suggestionsCatalog"></ul>

            <div class="filter-type">Налаштування</div>
            <label class="checkbox-container">Лише сталі пропозиції
                <input type="checkbox" id="permanentOnly" value="true">
                <span class="checkmark"></span>
            </label>

            <div class="price-filter">
                <div class="filter-type">Ціновий діапазон (UAH):</div>
                <div class="price-values">
                    <span id="minPrice">₴0</span> — <span id="maxPrice">₴100000</span>
                </div>
                <div class="slider-container">
                    <input type="range" id="minRange" min="0" max="100000" value="0" step="100">
                    <input type="range" id="maxRange" min="0" max="100000" value="100000" step="100">
                    <div class="slider-track"></div>
                </div>
            </div>

            <div class="sort-filter">
                <div class="filter-type">Сортування:</div>
                <select id="sortSelect" class="sort-select">
                    <option value="popular">За популярністю</option>
                    <option value="rating">За рейтингом</option>
                    <option value="newest">За новизною</option>
                    <option value="price">За ціною</option>
                </select>
            </div>

            <div class="filter-actions">
                <button class="search-button" onclick="loadAdvertisements()">Пошук</button>
            </div>
        </div>
    </div>

    <div class="catalog" id="catalog-container">
        <div id="loading-message" style="text-align: center; padding: 40px; font-size: 18px; color: #666;">
            Завантаження...
        </div>
    </div>
</div>

<div id="pagination-container" style="text-align: center; margin: 20px 0; display: none;">
    <button id="prev-page" class="search-button" style="margin: 0 5px;" disabled>Попередня</button>
    <span id="page-info" style="margin: 0 15px; font-size: 16px;"></span>
    <button id="next-page" class="search-button" style="margin: 0 5px;" disabled>Наступна</button>
</div>

<footer>
    <div class="footer-content">
        <p>Copyright &copy; 2025 WanderUA. All rights reserved.</p>
    </div>
</footer>

<script>

</script>

</body>
</html>