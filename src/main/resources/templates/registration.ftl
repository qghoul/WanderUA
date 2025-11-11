<#ftl encoding='UTF-8'>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Реєстрація</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" type="text/css" href="/static/css/auth_styles.css">
</head>
<body>

<form action="/registration" method="post">
  <div class="container">
    <h1>Реєстрація</h1>
    <p>Будь-ласка заповніть форму для реєстрації.</p>

    <!--<div style="background-color: #e8f5e8; padding: 12px; border-radius: 4px; margin-bottom: 15px; border-left: 4px solid #4caf50;">
      <strong>🏢 Автоматична реєстрація для бізнесу:</strong><br>
      Всі нові користувачі отримують статус представника бізнесу і можуть створювати оголошення.
    </div>-->

    <hr>

    <script>
      const urlParams = new URLSearchParams(window.location.search);
      if (urlParams.has('error')) {
        document.write('<div style="color: #d32f2f; background-color: #ffebee; padding: 10px; border-radius: 4px; margin-bottom: 15px; border-left: 4px solid #d32f2f;">Помилка при реєстрації. Користувач з таким іменем або email вже існує.</div>');
      }
    </script>

    <label for="username"><b>Ім'я користувача</b></label>
    <input type="text" placeholder="Введіть ім'я користувача (мінімум 5 символів)" name="username" id="username" minlength="5" required>

    <label for="email"><b>Email</b></label>
    <input type="email" placeholder="Введіть ваш email" name="email" id="email" required>

    <label for="fullName"><b>Повне ім'я (необов'язково)</b></label>
    <input type="text" placeholder="Ваше повне ім'я" name="fullName" id="fullName">

    <label for="password"><b>Пароль</b></label>
    <input type="password" placeholder="Введіть пароль (мінімум 5 символів)" name="password" id="password" minlength="5" required>

    <label for="passwordConfirm"><b>Підтвердження паролю</b></label>
    <input type="password" placeholder="Повторіть пароль" name="passwordConfirm" id="passwordConfirm" minlength="5" required>

    <p>Створюючи акаунт ви погоджуєтесь з нашими <a href="#">Terms & Privacy</a>.</p>

    <button type="submit" class="registerbtn">Зареєструвати акаунт</button>
  </div>

  <div class="container signin">
    <p>Вже маєте аккаунт? <a href="/login">Авторизація</a></p>
  </div>
</form>

<script>
  document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('form');
    const password = document.getElementById('password');
    const passwordConfirm = document.getElementById('passwordConfirm');

    form.addEventListener('submit', function(e) {
      if (password.value !== passwordConfirm.value) {
        e.preventDefault();
        alert('Паролі не співпадають');
        passwordConfirm.focus();
        return false;
      }

      if (password.value.length < 5) {
        e.preventDefault();
        alert('Пароль повинен містити мінімум 5 символів');
        password.focus();
        return false;
      }
    });
  });
</script>

</body>
</html>