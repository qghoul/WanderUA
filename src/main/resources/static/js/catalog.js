class CatalogManager {
    constructor() {
        this.currentPage = 0;
        this.totalPages = 0;
        this.maxPriceFromServer = 100000;

        this.selectedCity = window.CATALOG_DATA?.selectedCity || '';
        this.selectedCategory = window.CATALOG_DATA?.selectedCategory || '';

        this.init();
    }

    init() {
        this.initCityAutocomplete();
        this.initCategoryButtons();
        this.initEventHandlers();
        this.loadAdvertisements();
        this.initPriceSlider();
        this.update
    }

    initCityAutocomplete() {
        const allCities = [
            "Київ", "Львів", "Одеса", "Харків", "Дніпро", "Івано-Франківськ",
            "Тернопіль", "Ужгород", "Чернівці", "Запоріжжя", "Полтава",
            "Чернігів", "Миколаїв", "Херсон", "Кам'янець-Подільський", "Буковель"
        ];

        const popularCities = [
            "Київ", "Львів", "Одеса", "Харків", "Івано-Франківськ",
            "Чернівці", "Кам'янець-Подільський", "Тернопіль", "Буковель"
        ];

        const input = document.getElementById("cityInputCatalog");
        const suggestions = document.getElementById("suggestionsCatalog");

        if (!input || !suggestions) return;

        const showGroupedSuggestions = (filtered) => {
            suggestions.innerHTML = "";

            const matchedPopular = popularCities.filter(city => filtered.includes(city));
            const matchedOther = filtered.filter(city => !popularCities.includes(city)).sort();

            if (matchedPopular.length > 0) {
                const label = document.createElement("li");
                label.textContent = "Найпопулярніші";
                label.className = "section-labelCatalog";
                suggestions.appendChild(label);

                matchedPopular.forEach(city => {
                    const li = document.createElement("li");
                    li.textContent = city;
                    li.addEventListener("click", () => {
                        input.value = city;
                        suggestions.innerHTML = "";
                        suggestions.style.display = "none";
                        this.loadAdvertisements();
                    });
                    suggestions.appendChild(li);
                });
            }

            if (matchedOther.length > 0) {
                const label = document.createElement("li");
                label.textContent = "Інші міста (за алфавітом)";
                label.className = "section-labelCatalog";
                suggestions.appendChild(label);

                matchedOther.forEach(city => {
                    const li = document.createElement("li");
                    li.textContent = city;
                    li.addEventListener("click", () => {
                        input.value = city;
                        suggestions.innerHTML = "";
                        suggestions.style.display = "none";
                        this.loadAdvertisements();
                    });
                    suggestions.appendChild(li);
                });
            }

            suggestions.style.display = "block";
        };

        input.addEventListener("focus", () => {
            showGroupedSuggestions(allCities);
        });

        input.addEventListener("input", () => {
            const value = input.value.toLowerCase().trim();
            const filtered = allCities.filter(city => city.toLowerCase().startsWith(value));
            if (filtered.length > 0) {
                showGroupedSuggestions(filtered);
            } else {
                suggestions.innerHTML = "";
                suggestions.style.display = "none";
            }
        });

        document.addEventListener("click", (e) => {
            if (!suggestions.contains(e.target) && e.target !== input) {
                suggestions.style.display = "none";
            }
        });
    }

    initPriceSlider() {
        const minRange = document.getElementById('minRange');
        const maxRange = document.getElementById('maxRange');
        const minPrice = document.getElementById('minPrice');
        const maxPrice = document.getElementById('maxPrice');
        const sliderTrack = document.querySelector('.slider-track');

        if (!minRange || !maxRange || !minPrice || !maxPrice || !sliderTrack) return;

        const updateTrack = () => {
            const min = parseInt(minRange.value);
            const max = parseInt(maxRange.value);

            if (min >= max) {
                minRange.value = max - 100;
            }

            minPrice.textContent = '₴' + minRange.value;
            maxPrice.textContent = '₴' + maxRange.value;

            const minPercent = (minRange.value / minRange.max) * 100;
            const maxPercent = (maxRange.value / maxRange.max) * 100;

            sliderTrack.style.left = minPercent + '%';
            sliderTrack.style.width = (maxPercent - minPercent) + '%';
        };

        [minRange, maxRange].forEach(input => {
            input.addEventListener('input', updateTrack);
        });

        updateTrack();
    }

    togglePriceFilter(category) {
        const priceFilter = document.querySelector('.price-filter');
        if (priceFilter) {
            priceFilter.style.display = (category === 'TOUR') ? 'block' : 'none';
        }
    }

    initCategoryButtons() {
        const categoryButtons = document.querySelectorAll('.category-button');
        const cityInput = document.getElementById('cityInputCatalog');

        // Устанавливаем активную категорию при загрузке
        if (this.selectedCategory) {
            const activeButton = document.querySelector(`[data-category="${this.selectedCategory}"]`);
            if (activeButton) {
                activeButton.classList.add('active');
            }
            this.togglePriceFilter(this.selectedCategory);
        } else {
            const allButton = document.getElementById('all-category');
            if (allButton) {
                allButton.classList.add('active');
            }
            this.togglePriceFilter('');
        }

        categoryButtons.forEach(button => {
            button.addEventListener('click', () => {
                categoryButtons.forEach(btn => btn.classList.remove('active'));
                button.classList.add('active');
                const category = button.dataset.category;
                this.updateCityFieldState(category);
                this.togglePriceFilter(category);
                this.loadAdvertisements();
            });
        });
    }

    updateCityFieldState(category) {
        const cityInput = document.getElementById('cityInputCatalog');
        if (!cityInput) return;

        if (category === 'TOUR') {
            cityInput.style.backgroundColor = '#DDDDDD';
            cityInput.placeholder = 'Для турів пошук по всій Україні';
        } else {
            cityInput.style.backgroundColor = '';
            cityInput.placeholder = '';
        }
    }

    initEventHandlers() {
        const sortSelect = document.getElementById('sortSelect');
        const permanentOnly = document.getElementById('permanentOnly');
        const cityInput = document.getElementById('cityInputCatalog');
        const searchButton = document.querySelector('.search-button');

        if (sortSelect) {
            sortSelect.addEventListener('change', () => this.loadAdvertisements());
        }

        if (permanentOnly) {
            permanentOnly.addEventListener('change', () => this.loadAdvertisements());
        }

        if (cityInput) {
            cityInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') {
                    this.loadAdvertisements();
                }
            });
        }

        if (searchButton) {
            searchButton.addEventListener('click', () => this.loadAdvertisements());
        }
    }

    loadAdvertisements(page = 0) {
        const category = document.querySelector('.category-button.active')?.dataset.category || '';
        const city = (category === 'TOUR') ? '' : (document.getElementById('cityInputCatalog')?.value || '');
        const minPrice = document.getElementById('minRange')?.value || '';
        const maxPrice = document.getElementById('maxRange')?.value || '';
        const sortBy = document.getElementById('sortSelect')?.value || 'popular';
        const permanentOnly = document.getElementById('permanentOnly')?.checked || false;


        const container = document.getElementById('catalog-container');
        if (container) {
            container.innerHTML = '<div id="loading-message" style="text-align: center; padding: 40px; font-size: 18px; color: #666;">Завантаження...</div>';
        }

        const params = new URLSearchParams({
            page: page,
            size: 10,
            sortBy: sortBy,
            permanentOnly: permanentOnly
        });

        if (city) params.append('city', city);
        if (category) params.append('category', category);
        if (category === 'TOUR') {
            if (minPrice && minPrice > 0) params.append('minPrice', minPrice);
            if (maxPrice && maxPrice < 100000) params.append('maxPrice', maxPrice);
        }
        const url = `/api/advertisements/catalog?${params.toString()}`;

        fetch(url)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Помилка завантаження');
                }
                return response.json();
            })
            .then(data => {
                this.displayAdvertisements(data.advertisements);
                this.updatePagination(data);
                this.updateMaxPrice(data.maxPriceInCatalog);
                this.currentPage = data.currentPage;
                this.totalPages = data.totalPages;
            })
            .catch(error => {
                console.error('Error:', error);
                if (container) {
                    container.innerHTML = '<div style="text-align: center; padding: 40px; font-size: 18px; color: #ff0000;">Помилка завантаження оголошень</div>';
                }
            });
    }

    displayAdvertisements(advertisements) {
        const container = document.getElementById('catalog-container');
        if (!container) return;

        if (!advertisements || advertisements.length === 0) {
            container.innerHTML = '<div style="text-align: center; padding: 40px; font-size: 18px; color: #666;">Оголошення не знайдено</div>';
            return;
        }

        container.innerHTML = advertisements.map(ad => this.createAdvertisementCard(ad)).join('');
        this.initCarousels();
        this.initStarRatings();
    }

    createAdvertisementCard(ad) {
        const typeClass = this.getTypeClass(ad.advertisementType);

        let imagesHtml = '';
        if (ad.imageUrls && ad.imageUrls.length > 0) {
            for (let i = 0; i < ad.imageUrls.length; i++) {
                const activeClass = i === 0 ? ' class="active"' : '';
                imagesHtml += '<img src="' + ad.imageUrls[i] + '"' + activeClass + '>';
            }
        }

        const carouselControlsHtml = ad.imageUrls && ad.imageUrls.length > 1 ?
            '<div class="carousel-controls">' +
            '<button class="arrow prev">‹</button>' +
            '<button class="arrow next">›</button>' +
            '</div>' +
            '<div class="indicators"></div>' : '';

        const contentClass = typeClass.replace('-card', '-content');
        const titleClass = typeClass.replace('-card', '-title');
        const infoClass = typeClass.replace('-card', '-info');
        const descriptionClass = typeClass.replace('-card', '-description');
        const priceClass = typeClass.replace('-card', '-price');

        let cardHtml = '<div class="' + typeClass + '" onclick="openAdvertisement(' + ad.id + ')">';
        cardHtml += '<div class="carousel">';
        cardHtml += imagesHtml;
        cardHtml += carouselControlsHtml;
        cardHtml += '</div>';

        cardHtml += '<div class="' + contentClass + '">';
        cardHtml += '<div class="save-button" onclick="event.stopPropagation(); toggleSave(' + ad.id + ')">';
        if (ad.sustainabilityVerify !== false) {
            // Контейнер для значка та тексту з блідо-зеленим фоном та заокругленими кутами
            cardHtml += '<div class="sustainability-badge">';

            // SVG для стилізованого листка (використовуємо простий, але помітний значок)
            cardHtml += '<svg class="leaf-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" stroke="#ffffff" stroke-width="0">';
            // Шлях для значка листка берези (спрощений, зубчастий контур)
            cardHtml += '<path d="M12 2L9 6L10 7L7 11L8 12L5 16C5 19 8 21 12 22C16 21 19 19 19 16L16 12L17 11L14 7L15 6L12 2Z" />';
            cardHtml += '</svg>';

            // Текст "Стала пропозиція"
            cardHtml += '<span class="sustainability-text">Стала пропозиція</span>';

            cardHtml += '</div>'; // Закриття контейнера
        }
        /*cardHtml += '<svg class="heart-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="#ffffff" stroke-width="2">';
        cardHtml += '<path d="M12 21C12 21 5 13.5 5 8.5C5 5.5 7.5 3 10.5 3C12 3 13.5 4 14 5C14.5 4 16 3 17.5 3C20.5 3 23 5.5 23 8.5C23 13.5 16 21 16 21H12Z" />';
        cardHtml += '</svg>';
        cardHtml += '<span class="save-text">Зберегти</span>'; */
        cardHtml += '</div>';

        cardHtml += '<div>';
        cardHtml += '<div class="' + titleClass + '">' + (ad.title || '') + '</div>';

        if (ad.city) {
            cardHtml += '<div class="' + infoClass + '">Місто: ' + ad.city + '</div>';
        }
        if (ad.address) {
            cardHtml += '<div class="' + infoClass + '">Адреса: ' + ad.address + '</div>';
        }
        if (ad.sustainabilityVerify !== false) {

        }

        cardHtml += this.getTypeSpecificInfo(ad);

        /*if (ad.workingHours) {
            cardHtml += '<div class="' + infoClass + '">Час роботи: ' + ad.workingHours + '</div>';
        }*/
        // commented cause workHours only for specific attractions

        cardHtml += '<div class="' + descriptionClass + '">' + this.truncateDescription(ad.description) + '</div>';
        cardHtml += '</div>';

        cardHtml += '<div class="ratingCatalog">';
        cardHtml += '<p><span class="starsCatalog" data-rating="' + (ad.reviewAvgRating || 0) + '"></span>' + (ad.formattedRating || '0.0') + ' ' + (ad.formattedRatingsCount || '') + '</p>';
        cardHtml += '</div>';

        if (ad.priceDisplay) {
            cardHtml += '<div class="' + priceClass + '">' + ad.priceDisplay + '</div>';
        }

        cardHtml += '</div>';
        cardHtml += '</div>';

        return cardHtml;
    }

    getTypeClass(type) {
        switch(type) {
            case 'TOUR': return 'tour-card';
            case 'RESTAURANT': return 'adv-card';
            case 'ENTERTAINMENT': return 'adv-card';
            case 'PUBLIC_ATTRACTION': return 'sight-card';
            case 'ACCOMODATION': return 'tour-card';
            default: return 'adv-card';
        }
    }

    getTypeSpecificInfo(ad) {
        let info = '';
        const data = ad.typeSpecificData || {};

        switch(ad.advertisementType) {
            case 'RESTAURANT':
                if (data.priceCategory) {
                    info += '<div class="adv-info">Цінова категорія: ' + this.formatPriceCategory(data.priceCategory) + '</div>';
                }
                if (data.specialOptionsDisplay) {
                    info += '<div class="adv-info">Особливі пропозиції: ' + data.specialOptionsDisplay + '</div>';
                }
                break;
            case 'TOUR':
                if (data.duration) {
                    info += '<div class="tour-info">Тривалість: ' + data.duration + ' годин(и)</div>';
                }
                if (data.tourType) {
                    info += '<div class="tour-info">Тип: ' + this.formatTourType(data.tourType) + '</div>';
                }
                break;
            case 'ACCOMODATION':
                if (data.accomodationType) {
                    info += '<div class="tour-info">Тип: ' + this.formatAccommodationType(data.accomodationType) + '</div>';
                }
                if (data.starsQuality) {
                    info += '<div class="tour-info">Зірки: ' + data.starsQuality + '</div>';
                }
                break;
            case 'ENTERTAINMENT':
                if (data.ageCategory) {
                    info += '<div class="adv-info">Вікова категорія: ' + this.formatAgeCategory(data.ageCategory) + '</div>';
                }
                break;
        }

        return info;
    }

    formatPriceCategory(category) {
        const categories = {
            'CHEAP': '$ Бюджетна',
            'MID': '$$ Середня',
            'LUXURY': '$$$ Люкс'
        };
        return categories[category] || category;
    }

    formatTourType(type) {
        const types = {
            'WALKING': 'Пішохідний',
            'BUS': 'Автобусний',
            'BIKE': 'Велосипедний',
            'CAR': 'Автомобільний'
        };
        return types[type] || type;
    }

    formatAccommodationType(type) {
        const types = {
            'HOTEL': 'Готель',
            'MOTEL': 'Мотель',
            'AGENCY': 'Агенство',
            'OTHER': 'Інше'
        };
        return types[type] || type;
    }

    formatAgeCategory(category) {
        const categories = {
            'KIDS': 'Діти',
            'TEENS': 'Підлітки',
            'ADULTS': 'Дорослі',
            'ONLY_ADULTS': 'Лише для дорослих'
        };
        return categories[category] || category;
    }

    truncateDescription(description, maxLength = 150) {
        if (!description) return '';
        if (description.length <= maxLength) return description;
        return description.substring(0, maxLength) + '...';
    }

    updatePagination(data) {
        const container = document.getElementById('pagination-container');
        const prevBtn = document.getElementById('prev-page');
        const nextBtn = document.getElementById('next-page');
        const pageInfo = document.getElementById('page-info');

        if (!container || !prevBtn || !nextBtn || !pageInfo) return;

        if (data.totalPages <= 1) {
            container.style.display = 'none';
            return;
        }

        container.style.display = 'block';
        prevBtn.disabled = !data.hasPrevious;
        nextBtn.disabled = !data.hasNext;
        pageInfo.textContent = `Сторінка ${data.currentPage + 1} з ${data.totalPages}`;

        prevBtn.onclick = () => {
            if (data.hasPrevious) {
                this.loadAdvertisements(data.currentPage - 1);
            }
        };

        nextBtn.onclick = () => {
            if (data.hasNext) {
                this.loadAdvertisements(data.currentPage + 1);
            }
        };
    }

    updateMaxPrice(maxPrice) {
        if (maxPrice && maxPrice > 0) {
            this.maxPriceFromServer = maxPrice;
            const maxRange = document.getElementById('maxRange');
            const maxPriceSpan = document.getElementById('maxPrice');

            if (maxRange && maxPriceSpan) {
                maxRange.max = maxPrice;
                if (parseInt(maxRange.value) > maxPrice) {
                    maxRange.value = maxPrice;
                    maxPriceSpan.textContent = '₴' + maxPrice;
                }
            }
        }
    }

    initCarousels() {
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

    initStarRatings() {
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
}
// Global funtions
window.openAdvertisement = function(id) {
    window.location.href = `/advertisements/${id}`;
};


// Catalog initialization
document.addEventListener("DOMContentLoaded", function() {
    new CatalogManager();
});


