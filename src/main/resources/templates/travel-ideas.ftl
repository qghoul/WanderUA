<#ftl encoding='UTF-8'>
<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="/css/styles.css">
    <title>Мої ідеї подорожей</title>
    <style>
        .travel-ideas-container {
            max-width: 1200px;
            margin: 40px auto;
            padding: 0 20px;
        }

        .page-header {
            margin-bottom: 40px;
            text-align: center;
        }

        .page-title {
            font-size: 36px;
            font-weight: 700;
            color: #333;
            margin-bottom: 10px;
        }

        .page-subtitle {
            font-size: 16px;
            color: #666;
        }

        .create-idea-btn {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            padding: 14px 28px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
            margin: 20px 0;
        }

        .create-idea-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 20px rgba(102, 126, 234, 0.4);
        }

        .travel-ideas-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
            gap: 25px;
            margin-top: 30px;
        }

        .travel-idea-card {
            background: white;
            border-radius: 15px;
            overflow: hidden;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
            transition: all 0.3s;
            cursor: pointer;
            position: relative;
        }

        .travel-idea-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
        }

        .travel-idea-card-header {
            padding: 25px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            position: relative;
        }

        .travel-idea-card-title {
            font-size: 20px;
            font-weight: 600;
            margin-bottom: 8px;
            line-height: 1.4;
        }

        .travel-idea-card-count {
            font-size: 14px;
            opacity: 0.9;
            display: flex;
            align-items: center;
            gap: 5px;
        }

        .travel-idea-card-body {
            padding: 20px 25px;
        }

        .travel-idea-card-description {
            color: #666;
            font-size: 14px;
            line-height: 1.6;
            margin-bottom: 15px;
            display: -webkit-box;
            -webkit-line-clamp: 3;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }

        .travel-idea-card-footer {
            display: flex;
            gap: 10px;
            padding-top: 15px;
            border-top: 1px solid #f0f0f0;
        }

        .card-action-btn {
            flex: 1;
            padding: 10px;
            border: none;
            border-radius: 8px;
            font-size: 14px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s;
        }

        .btn-view {
            background-color: #667eea;
            color: white;
        }

        .btn-view:hover {
            background-color: #5568d3;
        }

        .btn-delete {
            background-color: #f0f0f0;
            color: #666;
        }

        .btn-delete:hover {
            background-color: #ffebee;
            color: #d32f2f;
        }

        .empty-state {
            text-align: center;
            padding: 80px 20px;
        }

        .empty-state-icon {
            font-size: 80px;
            margin-bottom: 20px;
        }

        .empty-state-title {
            font-size: 24px;
            font-weight: 600;
            color: #333;
            margin-bottom: 10px;
        }

        .empty-state-text {
            font-size: 16px;
            color: #666;
            margin-bottom: 30px;
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

        @media (max-width: 768px) {
            .travel-ideas-grid {
                grid-template-columns: 1fr;
            }

            .page-title {
                font-size: 28px;
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
            <li id="faq"><a href="/help">FAQ</a></li>
        </div>
        <div class="rightPart">
            <li id="login"><a href="/login">Увійти</a></li>
            <li id="createAdd"><a href="/registration">Зареєструватись</a></li>
        </div>
    </ul>
</header>

<div class="content">
    <div class="travel-ideas-container">
        <div class="page-header">
            <h1 class="page-title">Мої ідеї подорожей</h1>
            <p class="page-subtitle">Організуйте свої улюблені місця в тематичні колекції</p>
            <button class="create-idea-btn" onclick="openCreateModal()">
                <span>➕</span>
                <span>Створити нову ідею</span>
            </button>
        </div>

        <div id="loadingContainer" class="loading-container">
            <div class="spinner"></div>
            <p>Завантаження ідей подорожей...</p>
        </div>

        <div id="travelIdeasGrid" class="travel-ideas-grid" style="display: none;">
            <!-- Cards will be populated by JavaScript -->
        </div>

        <div id="emptyState" class="empty-state" style="display: none;">
            <div class="empty-state-icon">🗺️</div>
            <h2 class="empty-state-title">У вас ще немає ідей подорожі</h2>
            <p class="empty-state-text">Почніть створювати колекції цікавих місць для ваших майбутніх подорожей</p>
            <button class="create-idea-btn" onclick="openCreateModal()">
                <span>➕</span>
                <span>Створити першу ідею</span>
            </button>
        </div>
    </div>
</div>

<!-- Create Modal -->
<div id="createModal" class="travel-idea-modal" style="display: none;">
    <div class="travel-idea-modal-content">
        <div class="travel-idea-modal-header">
            <h2>Створити ідею подорожі</h2>
            <button class="travel-idea-close" onclick="closeCreateModal()">&times;</button>
        </div>

        <div class="travel-idea-modal-body">
            <div class="form-group">
                <label for="createTitle">Назва <span style="color: #ff4444;">*</span></label>
                <input type="text"
                       id="createTitle"
                       placeholder="Наприклад: Вихідні у Львові"
                       maxlength="100"
                       required>
            </div>

            <div class="form-group">
                <label for="createDescription">Опис</label>
                <textarea id="createDescription"
                          placeholder="Опишіть вашу ідею подорожі..."
                          maxlength="1000"
                          style="min-height: 120px;"></textarea>
            </div>
        </div>

        <div class="travel-idea-modal-footer">
            <button class="btn-travel-idea btn-cancel" onclick="closeCreateModal()">
                Скасувати
            </button>
            <button class="btn-travel-idea btn-save" onclick="createTravelIdea()">
                Створити
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
    let travelIdeas = [];

    // Load travel ideas on page load
    document.addEventListener('DOMContentLoaded', function() {
        loadTravelIdeas();
    });

    async function loadTravelIdeas() {
        const loadingContainer = document.getElementById('loadingContainer');
        const grid = document.getElementById('travelIdeasGrid');
        const emptyState = document.getElementById('emptyState');

        loadingContainer.style.display = 'block';
        grid.style.display = 'none';
        emptyState.style.display = 'none';

        try {
            const response = await fetch('/api/travel-ideas');

            if (!response.ok) {
                throw new Error('Failed to load travel ideas');
            }

            travelIdeas = await response.json();

            loadingContainer.style.display = 'none';

            if (travelIdeas.length === 0) {
                emptyState.style.display = 'block';
            } else {
                grid.style.display = 'grid';
                displayTravelIdeas();
            }
        } catch (error) {
            console.error('Error loading travel ideas:', error);
            loadingContainer.innerHTML = '<p style="color: #ff4444;">Помилка завантаження ідей подорожі</p>';
        }
    }
    <#noparse>
    function displayTravelIdeas() {
        const grid = document.getElementById('travelIdeasGrid');

        grid.innerHTML = travelIdeas.map(idea => {
            const description = idea.description || 'Немає опису';
            const truncatedDesc = description.length > 120 ? description.substring(0, 120) + '...' : description;

            return `
                <div class="travel-idea-card">
                    <div class="travel-idea-card-header">
                        <div class="travel-idea-card-title">${escapeHtml(idea.title)}</div>
                        <div class="travel-idea-card-count">
                            <span>📍</span>
                            <span>${idea.advertisementCount || 0} ${getPlacesWord(idea.advertisementCount || 0)}</span>
                        </div>
                    </div>
                    <div class="travel-idea-card-body">
                        <div class="travel-idea-card-description">${escapeHtml(truncatedDesc)}</div>
                        <div class="travel-idea-card-footer">
                            <button class="card-action-btn btn-view" onclick="viewTravelIdea(${idea.id})">
                                Переглянути
                            </button>
                            <button class="card-action-btn btn-delete" onclick="deleteTravelIdea(${idea.id}, event)">
                                Видалити
                            </button>
                        </div>
                    </div>
                </div>
            `;
        }).join('');
    }
    </#noparse>

    function viewTravelIdea(id) {
        window.location.href = `/travel-ideas/` + id;
    }

    async function deleteTravelIdea(id, event) {
        event.stopPropagation();

        if (!confirm('Ви впевнені, що хочете видалити цю ідею подорожі? Усі збережені місця будуть втрачені.')) {
            return;
        }

        try {
            const response = await fetch(`/api/travel-ideas/` + id, {
                method: 'DELETE'
            });

            if (!response.ok) {
                throw new Error('Failed to delete travel idea');
            }

            // Reload the list
            await loadTravelIdeas();

        } catch (error) {
            console.error('Error deleting travel idea:', error);
            alert('Помилка видалення ідеї подорожі');
        }
    }

    function openCreateModal() {
        document.getElementById('createModal').style.display = 'block';
        document.body.style.overflow = 'hidden';
    }

    function closeCreateModal() {
        document.getElementById('createModal').style.display = 'none';
        document.body.style.overflow = 'auto';
        document.getElementById('createTitle').value = '';
        document.getElementById('createDescription').value = '';
    }

    async function createTravelIdea() {
        const title = document.getElementById('createTitle').value.trim();
        const description = document.getElementById('createDescription').value.trim();

        if (!title) {
            alert('Будь ласка, вкажіть назву ідеї подорожі');
            return;
        }

        try {
            const response = await fetch('/api/travel-ideas', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    title: title,
                    description: description
                })
            });

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.error || 'Помилка створення ідеї');
            }

            closeCreateModal();
            await loadTravelIdeas();

        } catch (error) {
            console.error('Error creating travel idea:', error);
            alert(error.message || 'Помилка створення ідеї подорожі');
        }
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

    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    // Close modal on outside click
    window.onclick = function(event) {
        const modal = document.getElementById('createModal');
        if (event.target === modal) {
            closeCreateModal();
        }
    }
</script>

</body>
</html>