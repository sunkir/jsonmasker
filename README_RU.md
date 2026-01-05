# 🎭 JSON Masker

Утилита для автоматизации маскирования конфиденциальных данных в процессе сериализации через Jackson. Инструмент предназначен для реализации требований по защите данных путем настройки правил скрытия или замены значений в POJO-объектах или сырых JSON-строках.

## 🏗 Архитектура интеграции

Утилита интегрируется напрямую в Jackson через кастомный модуль. Поля для маскирования определяются с помощью аннотаций или динамической конфигурации.

### 🧩 Основные компоненты

1.  **`MaskingJacksonModule`**: Модуль Jackson, добавляющий `BeanSerializerModifier`, который перехватывает процесс сериализации и применяет логику маскирования.
2.  **`JsonMaskingUtil`**: Основная точка входа. Может использоваться как самостоятельный экземпляр или быть базовым классом для предоставления специфичного контекста `ObjectMapper`.
3.  **Аннотации**: Метаданные для маркировки классов и полей, требующих обработки.

## 🛠 Детали реализации

### 1. 🏷 Маскирование на основе аннотаций

Для активации маскирования класс должен быть помечен аннотацией `@Masked`. Конкретные поля помечаются аннотациями стратегий:

*   `@SymbolMask`: Заменяет каждый символ на маску (по умолчанию `*`).
*   `@FullMask`: Заменяет всё значение поля статической строкой (по умолчанию `[masked]`).
*   `@PartialMask`: Применяет маскирование на основе позиционного паттерна (например, `***cccc`).
*   `@RegexpMask`: Использует регулярное выражение для поиска и замены частей строки.

**Пример реализации:**

```kotlin
@Masked
data class User(
    val id: Long,
    @SymbolMask
    val name: String,
    @FullMask(mask = "CONFIDENTIAL")
    val email: String,
    @RegexpMask(pattern = "(?<=.).+(?=.@)", mask = "********")
    val internalEmail: String, // mytestmail@gmail.com -> m********l@gmail.com
    @PartialMask(pattern = "****cccc")
    val phoneNumber: String
)
```

### 2. 🧰 Использование утилиты

`JsonMaskingUtil` предоставляет два основных метода для работы с объектами:
*   `toMaskedJson(obj)`: Сериализация с применением маскирования. 🔒
*   `toJson(obj)`: Обычная сериализация (подавляет логику модуля маскирования). 🔓

```kotlin
val util = JsonMaskingUtil()
val maskedJson = util.toMaskedJson(user)
```

### 3. 📄 Маскирование сырых JSON-строк

В случаях, когда данные представлены в виде строки, а не POJO, можно использовать метод `maskJsonString` с передачей `MaskingConfiguration`. Поддерживается поиск по точным ключам и по паттернам с использованием wildcard (например, `*_id`).

```kotlin
val config = MaskingConfiguration().apply {
    addKeyConfig("password", KeyMaskingConfig(strategyClass = FullMask::class, mask = "***"))
    addKeyConfig("*_id", KeyMaskingConfig(strategyClass = SymbolMask::class))
}

val result = util.maskJsonString(jsonString, config)
```

### 4. ⚙️ Настройка ObjectMapper

`JsonMaskingUtil` спроектирован для расширения. Если в проекте используется специфичная конфигурация `ObjectMapper` (форматы дат, стратегии именования и т.д.), его следует передать в конструктор или переопределить в наследнике.

```kotlin
class MyMaskingService(customMapper: ObjectMapper) : JsonMaskingUtil(customMapper) {
    // Использует предоставленный маппер, сохраняя возможности маскирования
}
```

### 5. 📡 Логирование ошибок (Events)

Утилита поддерживает отслеживание ошибок маскирования через систему событий. Это позволяет логировать сбои или обрабатывать исключения без прямой зависимости от конкретной библиотеки логирования.

```kotlin
MaskingEventDispatcher.addListener { event ->
    if (event.type == MaskingEventType.ERROR) {
        println("Ошибка: ${event.message} - ${event.throwable}")
    }
}
```

## 🔌 Ручная регистрация модуля

Если вы не используете `JsonMaskingUtil` и хотите зарегистрировать логику маскирования напрямую в свой экземпляр Jackson:

```kotlin
val mapper = ObjectMapper().registerModule(MaskingJacksonModule())
```
