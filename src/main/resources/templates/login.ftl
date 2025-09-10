<#ftl encoding='UTF-8'>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Авторизація</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" type="text/css" href="/css/auth_styles.css">
</head>
<body>

<form action="/login" method="post">
  <div class="container">
    <h1>Авторизація</h1>
    <p>Будь-ласка введіть дані для авторизації у сервісі.</p>
    <hr>

    <script>
      const urlParams = new URLSearchParams(window.location.search);
      if (urlParams.has('error')) {
        document.write('<div style="color: #d32f2f; background-color: #ffebee; padding: 10px; border-radius: 4px; margin-bottom: 15px; border-left: 4px solid #d32f2f;">Невірний email або пароль</div>');
      }
      if (urlParams.has('registered')) {
        document.write('<div style="color: #2e7d32; background-color: #e8f5e8; padding: 10px; border-radius: 4px; margin-bottom: 15px; border-left: 4px solid #2e7d32;">Реєстрація успішна! Тепер ви можете увійти в систему</div>');
      }
      if (urlParams.has('logout')) {
        document.write('<div style="color: #1976d2; background-color: #e3f2fd; padding: 10px; border-radius: 4px; margin-bottom: 15px; border-left: 4px solid #1976d2;">Ви успішно вийшли з системи</div>');
      }
    </script>

    <label for="email"><b>Email</b></label>
    <input type="email" placeholder="Введіть ваш email" name="email" id="email" required>

    <label for="password"><b>Пароль</b></label>
    <input type="password" placeholder="Введіть пароль" name="password" id="password" required>

    <hr>
    <p>Забули пароль? <a href="#">Відновлення паролю</a>.</p>

    <button type="submit" class="registerbtn">Увійти</button>
  </div>

  <div class="container signin">
    <p>Не маєте акаунту? <a href="/registration">Реєстрація</a></p>
  </div>
</form>

</body>
</html>