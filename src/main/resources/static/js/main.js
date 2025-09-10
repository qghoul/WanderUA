
document.addEventListener("DOMContentLoaded", function() {
    const cityInput = document.getElementById("cityInput");
    const suggestions = document.getElementById("suggestions");

    const allCities = [
        "Київ", "Львів", "Одеса", "Харків", "Дніпро", "Івано-Франківськ",
        "Тернопіль", "Ужгород", "Чернівці", "Запоріжжя", "Полтава",
        "Чернігів", "Миколаїв", "Херсон", "Кам'янець-Подільський", "Буковель"
    ];

    const popularCities = [
        "Київ", "Львів", "Одеса", "Харків", "Івано-Франківськ",
        "Чернівці", "Кам'янець-Подільський", "Тернопіль", "Буковель"
    ];

    function showGroupedSuggestions(filtered) {
        suggestions.innerHTML = "";

        const matchedPopular = popularCities.filter(city => filtered.includes(city));
        const matchedOther = filtered.filter(city => !popularCities.includes(city)).sort();

        if (matchedPopular.length > 0) {
            const label = document.createElement("li");
            label.textContent = "Найпопулярніші";
            label.className = "section-label";
            suggestions.appendChild(label);

            matchedPopular.forEach(city => {
                const li = document.createElement("li");
                li.textContent = city;
                li.addEventListener("click", () => {
                    selectCity(city);
                });
                suggestions.appendChild(li);
            });
        }

        if (matchedOther.length > 0) {
            const label = document.createElement("li");
            label.textContent = "Інші міста (за алфавітом)";
            label.className = "section-label";
            suggestions.appendChild(label);

            matchedOther.forEach(city => {
                const li = document.createElement("li");
                li.textContent = city;
                li.addEventListener("click", () => {
                    selectCity(city);
                });
                suggestions.appendChild(li);
            });
        }

        suggestions.style.display = "block";
    }

    function selectCity(city) {
        cityInput.value = city;
        suggestions.innerHTML = "";
        suggestions.style.display = "none";
        redirectToCatalog(city);
    }

    function redirectToCatalog(city) {
        if (city && city.trim() !== '') {
            window.location.href = `/catalog?city=${encodeURIComponent(city.trim())}`;
        } else {
            window.location.href = `/catalog`;
        }
    }

    if (cityInput) {
        cityInput.addEventListener("focus", () => {
            showGroupedSuggestions(allCities);
        });

        cityInput.addEventListener("input", () => {
            const value = cityInput.value.toLowerCase().trim();
            const filtered = allCities.filter(city => city.toLowerCase().startsWith(value));
            if (filtered.length > 0) {
                showGroupedSuggestions(filtered);
            } else {
                suggestions.innerHTML = "";
                suggestions.style.display = "none";
            }
        });

        cityInput.addEventListener("keypress", (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                const city = cityInput.value.trim();
                redirectToCatalog(city);
            }
        });

        document.addEventListener("click", (e) => {
            if (!suggestions.contains(e.target) && e.target !== cityInput) {
                suggestions.style.display = "none";
            }
        });
    }

    const categoryButtons = document.querySelectorAll('.main-category-button');
    if (categoryButtons.length > 0) {
        categoryButtons.forEach(button => {
            button.addEventListener('click', () => {
                const category = button.dataset.category;
                const city = cityInput ? cityInput.value.trim() : '';

                let url = '/catalog';
                const params = new URLSearchParams();

                if (city) params.append('city', city);
                if (category) params.append('category', category);

                if (params.toString()) {
                    url += '?' + params.toString();
                }

                window.location.href = url;
            });
        });
    }
});