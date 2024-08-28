
# questionTab


Ну короче оно работает тут вроде как.

## Как запустить проект

Чтобы запустить проект, выполните следующие шаги:

1. **Установка зависимостей:**
   ```
   cd questionTab
   mvn install
   ```

2. **Запуск сервера разработки:**
   ```
   cd <module>
   mvn spring-boot:run
   ```

В проекте используется 4 отдельных независимых модуля user/comment/question/answer 

Каждый можно запускать отдельно друг от друга


## Документация

После того как вы установили все зависимости в модуле можно зайти в соответствующую документацию
   - [question docs](http://127.0.0.1:8090/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config)
   - [user docs](http://127.0.0.1:8091/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config)
   - [answer docs](http://127.0.0.1:8093/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config)
   - [comments docs](http://127.0.0.1:8094/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config)

Ну и так надо с каждым модулем


org.springframework.http.converter.HttpMessageNotReadableException: JSON parse error: Unexpected character ('Ð' (code 208)): was expecting comma to separate Object entries; nested exception is com.fasterxml.jackson.core.JsonParseException: Unexpected character ('Ð' (code 208)): was expecting comma to separate Object entries<EOL> at [Source: (PushbackInputStream); line: 11, column: 21]]



{
"question": "Я самый крутой чел на свете",
"subject": "LP",
"answers": [
{
"answer": "Нет, лучше изучай spring",
"bestAnswer": false
}
],
"labNumber": 2,
"description": ""Привет! Я работаю над простым проектом на Java и столкнулся с проблемой. Вот пример кода, который я использую:\n\n```java\n// Example Java Code\n\npublic class HelloWorld {\n    public static void main(String[] args) {\n        System.out.println(\"Hello, world!\");\n    }\n}\n```""
}