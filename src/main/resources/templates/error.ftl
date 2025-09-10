<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Ошибка</title>
    <style>
        body { font-family: sans-serif; padding: 2em; }
        .container { max-width: 600px; margin: 0 auto; }
        h1 { color: #c00; }
    </style>
</head>
<body>
<div class="container">
    <h1>Произошла ошибка</h1>
    <p>К сожалению, что‑то пошло не так.</p>
    <#if status??>
        <p>Код ошибки: ${status}</p>
    </#if>
    <#if message??>
        <p>Сообщение: ${message}</p>
    </#if>
    <p><a href="/">Вернуться на главную</a></p>
</div>
</body>
</html>