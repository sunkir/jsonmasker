# 🎭 JSON Masker

Utility for automating the masking of sensitive data during Jackson serialization. This tool is designed to help developers implement data privacy requirements by providing a configurable way to redact or obscure specific fields in POJOs or raw JSON strings.

## 🏗 Integration Overview

The utility integrates directly with Jackson via a custom module. It identifies fields for masking through annotations or dynamic configuration.

### 🧩 Core Components

1.  **`MaskingJacksonModule`**: A Jackson module that adds a `BeanSerializerModifier` to intercept serialization and apply masking.
2.  **`JsonMaskingUtil`**: The primary entry point. It can be used as a standalone instance or inherited to provide a custom `ObjectMapper` context.
3.  **Annotations**: Metadata used to mark classes and fields that require processing.

## 🛠 Implementation Details

### 1. 🏷 Annotation-Based Masking

To use annotation-based masking, the class must be marked with `@Masked`. Individual fields are then marked with specific strategy annotations.

*   `@SymbolMask`: Replaces characters with a mask (default is `*`).
*   `@FullMask`: Replaces the entire field value with a static string (default is `[masked]`).
*   `@PartialMask`: Applies masking based on a positional pattern (e.g., `***cccc`).
*   `@RegexpMask`: Uses a regular expression to identify and replace parts of the string.

**Example Implementation:**

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

### 2. 🧰 Manual Utility Usage

`JsonMaskingUtil` provides two main methods for handling objects:
*   `toMaskedJson(obj)`: Serializes with masking enabled. 🔒
*   `toJson(obj)`: Serializes without masking (suppresses the module logic). 🔓

```kotlin
val util = JsonMaskingUtil()
val maskedJson = util.toMaskedJson(user)
```

### 3. 📄 Masking Raw JSON Strings

For scenarios where you have a JSON string rather than a POJO, you can use `maskJsonString` with a `MaskingConfiguration`. This supports exact key matches and wildcard patterns (e.g., `*_id`).

```kotlin
val config = MaskingConfiguration().apply {
    addKeyConfig("password", KeyMaskingConfig(strategyClass = FullMask::class, mask = "***"))
    addKeyConfig("*_id", KeyMaskingConfig(strategyClass = SymbolMask::class))
}

val result = util.maskJsonString(jsonString, config)
```

### 4. ⚙️ Customizing the ObjectMapper

The `JsonMaskingUtil` is designed to be extensible. If your project uses a specific `ObjectMapper` configuration (e.g., custom date formats, naming strategies), you should pass it to the constructor or inherit from the utility.

```kotlin
class MyMaskingService(customMapper: ObjectMapper) : JsonMaskingUtil(customMapper) {
    // Uses the provided mapper while maintaining masking capabilities
}
```

### 5. 📡 Event-Based Logging (Errors)

The utility supports tracing masking errors through an event system. This allows you to log failures or handle errors without direct dependencies on a logging framework.

```kotlin
MaskingEventDispatcher.addListener { event ->
    println("Error: ${event.message} - ${event.throwable}")
}
```