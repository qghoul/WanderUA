<#ftl encoding='UTF-8'>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="/static/css/styles.css">
    <title>Приклад оголошення</title>
</head>
<script src="/static/js/auth-menu.js"></script>
<script>
    document.addEventListener("DOMContentLoaded", () => {
        document.querySelectorAll(".stars").forEach(container => {
            const rating = parseFloat(container.dataset.rating);
            const fullStars = Math.floor(rating);
            const hasHalf = rating % 1 >= 0.25;
            const totalStars = 5;

            for (let i = 0; i < totalStars; i++) {
                const star = document.createElement("span");
                star.classList.add("star");

                if (i < fullStars) {
                    star.classList.add("filled");
                } else if (i === fullStars && hasHalf) {
                    star.classList.add("half");
                }

                container.appendChild(star);
            }
        });
    });
    document.addEventListener("DOMContentLoaded", function () {
        const starContainers = document.querySelectorAll(".review-stars");

        starContainers.forEach(container => {
            const rating = parseInt(container.dataset.rating, 10);
            const totalStars = 5;

            const starsDiv = document.createElement("div");
            starsDiv.classList.add("stars");

            for (let i = 1; i <= totalStars; i++) {
                const star = document.createElement("div");
                star.classList.add("star");
                if (i <= rating) {
                    star.classList.add("filled");
                }
                starsDiv.appendChild(star);
            }

            container.appendChild(starsDiv);
        });
    });
</script>
<body>
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
        <div class="advertTitle"><b>Mr.Grill Hotdogs & Burgers</b></div>
        <div class="addContainer">
            <div class="product-image">
                <img src="E:\5 курс\Online Tourism Platform\images\advertSample1.jpg">
            </div>
            <div class="advertDetails">
                <p id="rating"><b>Оцінка:</b> <span class="stars"data-rating="4.5"></span>4.5 (100 відгуків)</p>
                <p id="adress"><b>Адреса:</b> Khreschatyk St, 7/11, Kyiv 01001 Ukraine </p>
                <p id="workdays"><b>Години роботи у будні дні:</b> 09:00 - 21:00 </p>
                <p id="weekends"><b>Години роботи у вихідні дні:</b> 10:00 - 22:00 </p>
                <p id="price_category"><b>Цінова категорія:</b> $ Бюджетна</p> <!-- бюджетна середня преміум-->
                <p id="food_options"><b>Особливі пропозиції:</b> Vegan options</p>
                <p id="web-site"><b>Web-site:</b> mrgrill-restaurant.choiceqr.com</p>
                <p id="contact"><b>Контакти:</b> 099-777-01-01</p>
                <button class="submit-form">Зберегти в "Ідею подорожі"</button>
                <button class="submit-form">Залишити відгук</button>
            </div>
        </div>
        <div class="describe">
            <div id="headline"><b>Опис</b></div>
            <!--<div id="creator">Створено: Mr.Grill Hotdogs & Burgers</div>-->
            <p>Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu.</p>
            <div class="complaintButton">Поскаржитись</div>
            <div id="creationDate">Дата створення: 28.05.2025</div>
            <p></p>
        </div>

        <div class="reviews-section">
            <h2>Відгуки</h2>


        </div>
    </div>
</div>
<footer>
    <div class="footer-content">
        <p>Copyright &copy; 2025 WanderUA. All rights reserved.</p>
    </div>
</footer>
</body>
</html>