<#ftl encoding='UTF-8'>
<!DOCTYPE html>
<html>
<script src="/static/js/auth-menu.js"></script>

<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" type="text/css" href="/css/styles.css">
    <title>WanderUA</title>
</head>
<body>
<header>
    <ul class="menu">
        <div class="leftPart">
            <li id="mainButton"><a href="/">WanderUA</a></li>
            <li id="aboutProject"><a href="/aboutProject">Про проєкт</a></li>
            <li id="businessInfo"><a href="/business_info">Інформація для бізнесу</a></li>
            <li id ="faq"><a href="/help">FAQ</a></li>
        </div>
        <div class="rightPart">
            <li id="login"><a href="/login">Увійти</a></li>
            <li id="createAdd"><a href="/registration">Зареєструватись</a></li>
        </div>
    </ul>
</header>

<div class="content">
    <div class="center-global-container">
        <h2 class="advertTitle">Інфомация для бізнесу та принципи взаємодії</h2>
        <!-- Add content here: -->
        <div class="info-text-business-page">
            <p>Туристична онлайн-платформа WanderUA надає українським підприємствам можливість безкоштовно розміщувати
                інформацію про власну діяльність, підвищувати впізнаваність бренду та залучати нових клієнтів.</p><br>
            <p>Якщо ви є представником бізнесу, ви можете отримати статус бізнес-акаунту та безкоштовно публікувати нформацію про
                свій бізнес на платформі WanderUA. Для цього необхідно пройти верифікацію та підтвердити право представляти компанію.</p><br>
            <p>Процедура верифікації є простою та зручною — ви можете заповнити онлайн-форму та надіслати відповідний запит, який буде
                розглянуто адміністратором платформи. Також є можливість надіслати необхідну інформацію та підтвердні документи на
                електронну адресу: WanderUA_business_verify@gmail.com .</p><br>
            <p>Платформа WanderUA підтримує Цілі сталого розвитку (Sustainable Development Goals) та прагне сприяти їх впровадженню
                у туристичній сфері України. Саме тому ми пропонуємо безкоштовне просування на платформі для бізнесів,
                діяльність яких відповідає принципам сталого розвитку.</p><br>
            <p>Сучасні дослідження підтверджують, що більшість туристів надають перевагу саме сталим пропозиціям і готові
                сплачувати більше за екологічні та соціально відповідальні послуги.</p><br>
            <p>Для підтвердження відповідності принципам сталого розвитку необхідно надати сертифікат про проходження
                незалежного аудиту через форму нижче. Для подачі запиту для ідентифікації бізнесу як сталий необхідно
                отримати підтвердження верифікації як представник бізнесу.</p>
        </div>
        <button class="submit-form" id="businessAccountBtn">Отримання статусу бізнес-акаунту</button>
        <button class="submit-form" id="sustainabilityBtn">Верифікація бізнесу як сталий</button>
    </div>
</div>

<footer>
    <div class="footer-content">
        <p>Copyright &copy; 2025 WanderUA. All rights reserved.</p>
    </div>
</footer>
<script>
    document.addEventListener("DOMContentLoaded", function () {
        const businessAccountBtn = document.getElementById("businessAccountBtn");
        const sustainabilityBtn = document.getElementById("sustainabilityBtn");

        if (businessAccountBtn) {
            businessAccountBtn.addEventListener("click", function () {
                window.location.href = "/business_verify";
            });
        }

        if (sustainabilityBtn) {
            sustainabilityBtn.addEventListener("click", function () {
                window.location.href = "/sustainability_verify";
            });
        }
    });
</script>
</body>
</html>