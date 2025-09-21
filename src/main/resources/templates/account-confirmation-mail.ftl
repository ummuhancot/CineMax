<#-- FTL Template -->
<html>
<head>
    <style>
        .container {
            font-family: Verdana, sans-serif;
            background-color: #fac769;
            border-radius: 10px;
            margin: 20px auto;
            box-shadow: 0 5px 10px rgba(0, 0, 0, 0.1), 0 2px 5px rgba(0, 0, 0, 0.3);
            width: 80%;
        }
        .content {
            background-color: #F8F8FF;
            border: 2px solid #fac769;
            padding: 20px;
            text-align: center;
        }
        .code {
            display: inline-block;
            font-size: 24px;
            background-color: #d2d2d2;
            padding: 10px;
            border-radius: 5px;
            margin: 20px 0;
            color: #000;
        }
    </style>
</head>
<body>

<div class="container">
    <div class="content">
        <img src="${imgSource}" alt="Logo" style="max-width: 100%;">
        <p>Here is your reset code:</p>
        <div class="code">${code}</div>
    </div>
</div>

</body>
</html>
