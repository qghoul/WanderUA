<#ftl encoding='UTF-8'>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="/css/styles.css">
    <title>Ідея подорожі</title>
    <style>
        .travel-idea-detail-container {
            max-width: 1200px;
            margin: 40px auto;
            padding: 0 20px;
        }

        .travel-idea-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 40px;
            border-radius: 15px;
            margin-bottom: 40px;
            box-shadow: 0 5px 20px rgba(102, 126, 234, 0.3);
        }

        .back-button {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            color: white;
            text-decoration: none;
            font-size: 14px;
            margin-bottom: 20px;
            opacity: 0.9;
            transition: opacity 0.3s;
        }

        .back-button:hover {
            opacity: 1;
        }

        .travel-idea-title {
            font-size: 32px;
            font-weight: 700;
            margin-bottom: 15px;
            line-height: 1.3;
        }

        .travel-idea-description {
            font-size: 16px;
            line-height: 1.6;
            opacity: 0.95;
            margin-bottom: 20px;
        }

        .travel-idea-stats {
            display: flex;
            gap: 30px;
            flex-wrap: wrap;
        }

        .stat-item {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 15px;
        }

        .stat-icon {
            font-size: 20px;
        }

        .advertisements-section {
            margin-top: 30px;
        }

        .section-title {
            font-size: 24px;
            font-weight: 600;
            color: #333;
            margin-bottom: 25px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .loading-container {
            text-align: center;
            padding: 60px 20px;
        }

        .spinner {
            border: 4px solid #f3f3f3;
            border-top: 4px solid #667eea;
            border-radius: 50%;
            width: 50px;
            height: 50px;
            animation: spin 1s linear infinite;
            margin: 0 auto 20px;
        }

        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }

        .empty-advertisements {
            text-align: center;
            padding: 60px 20px;
            background: #f8f9fa;
            border-radius: 12px;
        }

        .empty-icon {
            font-size: 60px;
            margin-bottom: 15px;
        }

        .empty-text {
            font-size: 18px;
            color: #666;
            margin-bottom: 10px;
        }

        .empty-subtext {
            font-size: 14px;
            color: #999;
        }

        /* Reuse catalog card styles */


        .remove-button {
            position: absolute;
            top: 10px;
            right: 10px;
            background: rgba(255, 255, 255, 0.95);
            border: none;
            border-radius: 50%;
            width: 35px;
            height: 35px;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            font-size: 20px;
            color: #d32f2f;
            z-index: 10;
            transition: all 0.3s;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
        }

        .remove-button:hover {
            background: #d32f2f;
            color: white;
            transform: scale(1.1);
        }

        @media (max-width: 768px) {
            .travel-idea-header {
                padding: 25px;
            }

            .travel-idea-title {
                font-size: 24px;
            }

        }
    </style>
</head>
<body>
<script src="/js/auth-menu.js"></script>
<script src="/js/catalog.js"></script>

<header>
    <ul class="menu">
        <div class="leftPart">
            <li id="mainButton"><a href="/">WanderUA</a></li>
            <li id="aboutProject"><a href="/aboutProject">Про проєкт</a></li>
            <li id="businessInfo"><a href="/business_info">Інформація для бізнесу</a></li>
            <li id="faq"><a href="/help">FAQ</a></li>
        </div>
        <div class="rightPart">
            <li id="login"><a href="/login">Увійти</a></li>
            <li id="createAdd"><a href="/registration">Зареєструватись</a></li>
        </div>
    </ul>
</header>

<div class="content">
    <div class="travel-idea-detail-container">
        <div id="loadingContainer" class="loading-container">
            <div class="spinner"></div>
            <p>Завантаження ідеї подорожі...</p>
        </div>

        <div id="travelIdeaContent" style="display: none;">
            <div class="travel-idea-header">
                <a href="/travel-ideas" class="back-button">
                    <span>←</span>
                    <span>Назад до всіх ідей</span>
                </a>

                <h1 class="travel-idea-title" id="travelIdeaTitle"></h1>
                <p class="travel-idea-description" id="travelIdeaDescription"></p>

                <div class="travel-idea-stats">
                    <div class="stat-item">
                        <span class="stat-icon">📍</span>
                        <span id="advertisementCount">Збережено пропозицій: 0</span>
                    </div>
                </div>
            </div>

            <div class="advertisements-section">
                <h2 class="section-title">
                    <span>🗺️</span>
                    <span>Збережені місця</span>
                </h2>

                <div id="emptyAdvertisements" class="empty-advertisements" style="display: none;">
                    <div class="empty-icon">📭</div>
                    <p class="empty-text">Ще немає збережених місць</p>
                    <p class="empty-subtext">Почніть додавати цікаві оголошення до цієї ідеї подорожі</p>
                </div>

                <div class="catalog" id="advertisementsCatalog">
                    <!-- Advertisement cards will be populated by JavaScript -->
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
    let currentTravelIdeaId = null;
    let travelIdeaData = null;

    // Get travel idea ID from URL
    function getTravelIdeaIdFromUrl() {
        const pathParts = window.location.pathname.split('/');
        return pathParts[pathParts.length - 1];
    }

    // Load travel idea on page load
    document.addEventListener('DOMContentLoaded', function() {
        currentTravelIdeaId = getTravelIdeaIdFromUrl();
        loadTravelIdeaDetail();
    });

    async function loadTravelIdeaDetail() {
        const loadingContainer = document.getElementById('loadingContainer');
        const content = document.getElementById('travelIdeaContent');

        loadingContainer.style.display = 'block';
        content.style.display = 'none';

        try {
            const response = await fetch(`/api/travel-ideas/` + currentTravelIdeaId);

            if (!response.ok) {
                throw new Error('Failed to load travel idea content');
            }

            travelIdeaData = await response.json();

            loadingContainer.style.display = 'none';
            content.style.display = 'block';

            displayTravelIdeaDetail();

        } catch (error) {
            console.error('Error loading travel idea:', error);
            loadingContainer.innerHTML = '<p style="color: #ff4444;">Помилка завантаження ідеї подорожі</p>';
        }
    }
    <#noparse>
    function displayTravelIdeaDetail() {
        document.getElementById('travelIdeaTitle').textContent = travelIdeaData.title;

        const descriptionElement = document.getElementById('travelIdeaDescription');
        if (travelIdeaData.description) {
            descriptionElement.textContent = travelIdeaData.description;
            descriptionElement.style.display = 'block';
        } else {
            descriptionElement.style.display = 'none';
        }

        const count = travelIdeaData.advertisementCount || 0;
        document.getElementById('advertisementCount').textContent = `${count} ${getPlacesWord(count)}`;

        displayAdvertisements();
    }
    </#noparse>

    function displayAdvertisements() {
        const catalog = document.getElementById('advertisementsCatalog');
        const emptyState = document.getElementById('emptyAdvertisements');
        const advertisements = travelIdeaData.savedAdvertisements || [];

        if (advertisements.length === 0) {
            emptyState.style.display = 'block';
            catalog.innerHTML = '';
            return;
        }

        emptyState.style.display = 'none';

        catalog.innerHTML = advertisements.map(ad => createAdvertisementCard(ad)).join('');

        initCarousels();
        initStarRatings();
    }

    <#noparse>
    function createAdvertisementCard(ad) {
        const typeClass = getTypeClass(ad.advertisementType);

        let imagesHtml = '';
        if (ad.imageUrls && ad.imageUrls.length > 0) {
            for (let i = 0; i < ad.imageUrls.length; i++) {
                const activeClass = i === 0 ? ' class="active"' : '';
                imagesHtml += `<img src="${ad.imageUrls[i]}"${activeClass}>`;
            }
        }

        const carouselControlsHtml = ad.imageUrls && ad.imageUrls.length > 1 ?
            `<div class="carousel-controls">
                <button class="arrow prev">‹</button>
                <button class="arrow next">›</button>
            </div>
            <div class="indicators"></div>` : '';

        const contentClass = typeClass.replace('-card', '-content');
        const titleClass = typeClass.replace('-card', '-title');
        const infoClass = typeClass.replace('-card', '-info');
        const descriptionClass = typeClass.replace('-card', '-description');
        const priceClass = typeClass.replace('-card', '-price');

        let cardHtml = `<div class="${typeClass}" style="position: relative;">`;

        // Add remove button
        cardHtml += `<button class="remove-button" onclick="removeAdvertisement(${ad.id}, event)" title="Видалити з ідеї">×</button>`;

        cardHtml += `<div class="carousel" onclick="openAdvertisement(${ad.id})">`;
        cardHtml += imagesHtml;
        cardHtml += carouselControlsHtml;
        cardHtml += '</div>';

        cardHtml += `<div class="${contentClass}" onclick="openAdvertisement(${ad.id})">`;
        cardHtml += '<div>';
        cardHtml += `<div class="${titleClass}">${ad.title || ''}</div>`;

        if (ad.city) {
            cardHtml += `<div class="${infoClass}">Місто: ${ad.city}</div>`;
        }
        if (ad.address) {
            cardHtml += `<div class="${infoClass}">Адреса: ${ad.address}</div>`;
        }

        cardHtml += getTypeSpecificInfo(ad);
        cardHtml += `<div class="${descriptionClass}">${truncateDescription(ad.description)}</div>`;
        cardHtml += '</div>';

        cardHtml += '<div class="ratingCatalog">';
        cardHtml += `<p><span class="starsCatalog" data-rating="${ad.reviewAvgRating || 0}"></span>${ad.formattedRating || '0.0'} ${ad.formattedRatingsCount || ''}</p>`;
        cardHtml += '</div>';

        if (ad.priceDisplay) {
            cardHtml += `<div class="${priceClass}">${ad.priceDisplay}</div>`;
        }

        cardHtml += '</div>';
        cardHtml += '</div>';

        return cardHtml;
    }

    async function removeAdvertisement(adId, event) {
        event.stopPropagation();

        if (!confirm('Видалити це місце з ідеї подорожі?')) {
            return;
        }

        try {
            const response = await fetch(`/api/travel-ideas/${currentTravelIdeaId}/advertisements/${adId}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                throw new Error('Failed to remove advertisement');
            }

            // Reload the data
            await loadTravelIdeaDetail();

        } catch (error) {
            console.error('Error removing advertisement:', error);
            alert('Помилка видалення оголошення');
        }
    }

    // Helper functions from catalog.js
    function getTypeClass(type) {
        switch(type) {
            case 'TOUR': return 'tour-card';
            case 'RESTAURANT': return 'adv-card';
            case 'ENTERTAINMENT': return 'adv-card';
            case 'PUBLIC_ATTRACTION': return 'sight-card';
            case 'ACCOMODATION': return 'tour-card';
            default: return 'adv-card';
        }
    }

    function getTypeSpecificInfo(ad) {
        let info = '';
        const data = ad.typeSpecificData || {};

        switch(ad.advertisementType) {
            case 'RESTAURANT':
                if (data.priceCategory) {
                    info += `<div class="adv-info">Цінова категорія: ${formatPriceCategory(data.priceCategory)}</div>`;
                }
                if (data.specialOptionsDisplay) {
                    info += `<div class="adv-info">Особливі пропозиції: ${data.specialOptionsDisplay}</div>`;
                }
                break;
            case 'TOUR':
                if (data.duration) {
                    info += `<div class="tour-info">Тривалість: ${data.duration} годин(и)</div>`;
                }
                if (data.tourType) {
                    info += `<div class="tour-info">Тип: ${formatTourType(data.tourType)}</div>`;
                }
                break;
            case 'ACCOMODATION':
                if (data.accomodationType) {
                    info += `<div class="tour-info">Тип: ${formatAccommodationType(data.accomodationType)}</div>`;
                }
                break;
            case 'ENTERTAINMENT':
                if (data.ageCategory) {
                    info += `<div class="adv-info">Вікова категорія: ${formatAgeCategory(data.ageCategory)}</div>`;
                }
                break;
        }

        return info;
    }

    function formatPriceCategory(category) {
        const categories = {
            'CHEAP': '$ Бюджетна',
            'MID': '$$ Середня',
            'LUXURY': '$$$ Люкс'
        };
        return categories[category] || category;
    }

    function formatTourType(type) {
        const types = {
            'WALKING': 'Пішохідний',
            'BUS': 'Автобусний',
            'BIKE': 'Велосипедний',
            'CAR': 'Автомобільний'
        };
        return types[type] || type;
    }

    function formatAccommodationType(type) {
        const types = {
            'HOTEL': 'Готель',
            'MOTEL': 'Мотель',
            'AGENCY': 'Агенство',
            'OTHER': 'Інше'
        };
        return types[type] || type;
    }

    function formatAgeCategory(category) {
        const categories = {
            'KIDS': 'Діти',
            'TEENS': 'Підлітки',
            'ADULTS': 'Дорослі',
            'ONLY_ADULTS': 'Лише для дорослих'
        };
        return categories[category] || category;
    }

    function truncateDescription(description, maxLength = 150) {
        if (!description) return '';
        if (description.length <= maxLength) return description;
        return description.substring(0, maxLength) + '...';
    }

    function getPlacesWord(count) {
        if (count % 10 === 1 && count % 100 !== 11) {
            return 'місце';
        } else if ([2, 3, 4].includes(count % 10) && ![12, 13, 14].includes(count % 100)) {
            return 'місця';
        } else {
            return 'місць';
        }
    }

    function initCarousels() {
        document.querySelectorAll('.carousel').forEach(carousel => {
            const images = carousel.querySelectorAll('img');
            const prevBtn = carousel.querySelector('.prev');
            const nextBtn = carousel.querySelector('.next');
            const indicatorsContainer = carousel.querySelector('.indicators');

            if (images.length <= 1) return;

            let current = 0;

            if (indicatorsContainer) {
                indicatorsContainer.innerHTML = '';
                images.forEach((_, index) => {
                    const indicator = document.createElement('div');
                    indicator.classList.add('indicator');
                    if (index === 0) indicator.classList.add('active');
                    indicator.addEventListener('click', (e) => {
                        e.stopPropagation();
                        showImage(index);
                    });
                    indicatorsContainer.appendChild(indicator);
                });
            }

            const indicators = indicatorsContainer ? indicatorsContainer.querySelectorAll('.indicator') : [];

            function showImage(index) {
                images[current].classList.remove('active');
                if (indicators[current]) indicators[current].classList.remove('active');
                current = index;
                images[current].classList.add('active');
                if (indicators[current]) indicators[current].classList.add('active');
            }

            if (prevBtn) {
                prevBtn.addEventListener('click', (e) => {
                    e.stopPropagation();
                    showImage((current - 1 + images.length) % images.length);
                });
            }

            if (nextBtn) {
                nextBtn.addEventListener('click', (e) => {
                    e.stopPropagation();
                    showImage((current + 1) % images.length);
                });
            }
        });
    }

    function initStarRatings() {
        document.querySelectorAll(".starsCatalog").forEach(container => {
            const rating = parseFloat(container.dataset.rating);
            const fullStars = Math.floor(rating);
            const hasHalf = rating % 1 >= 0.25;
            const totalStars = 5;

            container.innerHTML = '';

            for (let i = 0; i < totalStars; i++) {
                const star = document.createElement("span");
                star.classList.add("starCatalog");

                if (i < fullStars) {
                    star.classList.add("filled");
                } else if (i === fullStars && hasHalf) {
                    star.classList.add("half");
                }

                container.appendChild(star);
            }
        });
    }

    window.openAdvertisement = function(id) {
        window.location.href = `/advertisements/${id}`;
    };
    </#noparse>
</script>

</body>
</html>