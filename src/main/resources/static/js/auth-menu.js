async function checkAuthStatus() {
    try {
        const response = await fetch('/api/auth/status', {
            method: 'GET',
            credentials: 'same-origin' // Session cookies
        });

        if (response.ok) {
            const authData = await response.json();
            return authData;
        } else if (response.status === 401) {
            // if user dont identify
            return { authenticated: false };
        } else {
            console.error('Error checking auth status:', response.status);
            return { authenticated: false };
        }
    } catch (error) {
        console.error('Error checking authentication:', error);
        return { authenticated: false };
    }
}

function updateAuthMenu(authData) {
    const loginButton = document.getElementById('login');
    const createAddButton = document.getElementById('createAdd');
    const forBusinessButton = document.getElementById('forBussines');

    if (!loginButton || !createAddButton) {
        console.warn('Auth menu buttons not found');
        return;
    }

    if (!authData.authenticated) {
        // User is guest
        updateButton(loginButton, 'Увійти', '/login');
        updateButton(createAddButton, 'Зареєструватись', '/registration');
        if (forBusinessButton) {
            updateButton(forBusinessButton, 'Інформація для бізнесу', '/forBusiness');
            //resetButtonStyle(forBusinessButton);
        }
    } else {
        const userRoles = authData.roles || [];
        const isBusiness = userRoles.includes('ROLE_BUSINESS') || authData.businessVerified;
        const isAdmin = userRoles.includes('ROLE_ADMIN');

        if (isAdmin) {
            // Admin
            updateButton(loginButton, 'Панель адміна', '/admin/dashboard');
            updateButton(createAddButton, 'Всі оголошення', '/catalog');
            if (forBusinessButton) {
                updateButton(forBusinessButton, 'Перегляд скарг', '/complaints');
                //highlightAdminButton(forBusinessButton);
            }
        } else if (isBusiness) {
            // Business represent
            updateButton(loginButton, 'Створити пропозицію', '/advertisements/create');
            updateButton(createAddButton, 'Мої пропозиції', '/api/advertisements/my');
            if (forBusinessButton) {
                updateButton(forBusinessButton, 'Інформація для бізнесу', '/forBusiness');
                //resetButtonStyle(forBusinessButton);
            }
        } else {
            // Tourist
            updateButton(loginButton, 'Профіль', '/profile');
            updateButton(createAddButton, 'Всі пропозиції', '/catalog');
            if (forBusinessButton) {
                updateButton(forBusinessButton, 'Інформація для бізнесу', '/forBusiness');
                //resetButtonStyle(forBusinessButton);
            }
        }
    }
}

function updateButton(buttonElement, text, href) {
    const linkElement = buttonElement.querySelector('a');
    if (linkElement) {
        linkElement.textContent = text;
        linkElement.href = href;
    }
}

async function logout(event) {
    event.preventDefault();

    try {
        const response = await fetch('/logout', {
            method: 'POST',
            credentials: 'same-origin'
        });

        if (response.ok || response.redirected) {
            window.location.href = '/main';
        } else {
            console.error('Logout failed:', response.status);
            // Принудительное перенаправление в случае ошибки
            window.location.href = '/logout';
        }
    } catch (error) {
        console.error('Logout error:', error);
        // Принудительное перенаправление
        window.location.href = '/logout';
    }
}

async function checkAdvertisementAccess() {
    try {
        const authData = await checkAuthStatus();

        if (!authData.authenticated) {
            return { canCreate: false, message: 'Необхідна аутентифікація' };
        }

        const userRoles = authData.roles || [];
        const isBusiness = userRoles.includes('ROLE_BUSINESS') || authData.businessVerified;

        if (!isBusiness) {
            return {
                canCreate: false,
                message: 'Тільки верифіковані представники бізнесу можуть створювати оголошення'
            };
        }

        return { canCreate: true };

    } catch (error) {
        console.error('Error checking advertisement access:', error);
        return { canCreate: false, message: 'Помилка перевірки доступу' };
    }
}

async function checkAdminAccess() {
    try {
        const authData = await checkAuthStatus();

        if (!authData.authenticated) {
            return { isAdmin: false, message: 'Необхідна аутентифікація' };
        }

        const userRoles = authData.roles || [];
        const isAdmin = userRoles.includes('ROLE_ADMIN');

        if (!isAdmin) {
            return {
                isAdmin: false,
                message: 'Доступ тільки для адміністраторів'
            };
        }

        return { isAdmin: true };

    } catch (error) {
        console.error('Error checking admin access:', error);
        return { isAdmin: false, message: 'Помилка перевірки доступу' };
    }
}

document.addEventListener('DOMContentLoaded', async function() {
    try {
        const authData = await checkAuthStatus();
        updateAuthMenu(authData);

        if (window.location.pathname.includes('/advertisements/create') ||
            document.getElementById('advertForm')) {

            const accessCheck = await checkAdvertisementAccess();
            if (!accessCheck.canCreate) {
                alert(accessCheck.message);
                window.location.href = '/main';
                return;
            }
        }
        // Admin role check
        if (window.location.pathname.includes('/admin/')) {
            const adminCheck = await checkAdminAccess();
            if (!adminCheck.isAdmin) {
                alert(adminCheck.message);
                window.location.href = '/main';
                return;
            }
        }

    } catch (error) {
        console.error('Error initializing auth menu:', error);
    }
});

document.addEventListener('click', function(event) {
    const target = event.target;

    if (target.href && target.href.includes('/api/advertisements/my')) {
        event.preventDefault();
        loadMyAdvertisements();
    }
});

async function loadMyAdvertisements() {
    try {
        const response = await fetch('/api/advertisements/my');

        if (!response.ok) {
            if (response.status === 401) {
                alert('Необхідна аутентифікація');
                window.location.href = '/login';
                return;
            } else if (response.status === 403) {
                alert('Доступ заборонений');
                return;
            }
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const advertisements = await response.json();
        displayMyAdvertisements(advertisements);

    } catch (error) {
        console.error('Error loading my advertisements:', error);
        alert('Помилка завантаження ваших оголошень');
    }
}

function displayMyAdvertisements(advertisements) {
    const content = `
        <div class="center-global-container">
            <h2 class="advertTitle">Мої оголошення</h2>
            <div class="advertisements-list">
                ${advertisements.length === 0 ?
        '<p>У вас поки немає оголошень. <a href="/advertisements/create">Створити перше оголошення</a></p>' :
        advertisements.map(ad => `
                        <div class="advertisement-item" style="border: 1px solid #ddd; margin: 10px 0; padding: 15px; border-radius: 5px;">
                            <h3>${ad.title}</h3>
                            <p><strong>Категорія:</strong> ${getCategoryDisplayName(ad.category)}</p>
                            <p><strong>Опис:</strong> ${ad.description || 'Без опису'}</p>
                            <p><strong>Контакти:</strong> ${ad.contacts}</p>
                            ${ad.website ? `<p><strong>Веб-сайт:</strong> <a href="${ad.website}" target="_blank">${ad.website}</a></p>` : ''}
                            <div style="margin-top: 10px;">
                                <button onclick="editAdvertisement(${ad.id})" style="margin-right: 10px;">Редагувати</button>
                                <button onclick="deleteAdvertisement(${ad.id})" style="background-color: #d32f2f; color: white;">Видалити</button>
                            </div>
                        </div>
                    `).join('')
    }
            </div>
            <div style="margin-top: 20px;">
                <a href="/advertisements/create" style="background-color: #333; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">Створити нове оголошення</a>
            </div>
        </div>
    `;

    document.body.innerHTML = content;

    const header = `
        <header>
            <ul class="menu">
                <div class="leftPart">
                    <li id="forBussines"><a href="/forBussines">Інформація для бізесу</a></li>
                    <li><a href="/aboutProject">Про проєкт</a></li>
                    <li><a href="/Tourists">Військовий туризм</a></li>
                    <li><a href="/help">FAQ</a></li>
                </div>
                <div class="rightPart">
                    <li id="login"><a href="/advertisements/create">Створити пропозицію</a></li>
                    <li id="createAdd"><a href="/api/advertisements/my">Мої пропозиції</a></li>
             
                </div>
            </ul>
        </header>
    `;

    document.body.insertAdjacentHTML('afterbegin', header);
}

function getCategoryDisplayName(category) {
    const categoryNames = {
        'restaurant': 'Заклад харчування',
        'tour': 'Тур',
        'accomodation': 'Житло',
        'entertainment': 'Розваги',
        'public_attraction': 'Пам\'ятки'
    };
    return categoryNames[category] || category;
}

// TODO: Realise this functions
function editAdvertisement(id) {
    alert(`Редагування оголошення ${id} буде реалізовано пізніше`);
}

function deleteAdvertisement(id) {
    if (confirm('Ви впевнені, що хочете видалити це оголошення?')) {
        alert(`Видалення оголошення ${id} буде реалізовано пізніше`);
    }
}