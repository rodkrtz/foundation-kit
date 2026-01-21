package com.rodkrtz.foundationkit.repository.json

/**
 * Interface for JSON serialization and deserialization.
 *
 * This abstraction allows the library to work with any JSON library
 * (Jackson, Gson, Kotlinx Serialization, Moshi, etc.) without coupling
 * to a specific implementation.
 *
 * Example implementations:
 * ```
 * // Using Jackson
 * class JacksonSerializer(private val objectMapper: ObjectMapper) : JsonSerializer {
 *     override fun <T> serialize(obj: T): String = 
 *         objectMapper.writeValueAsString(obj)
 *
 *     override fun <T> deserialize(json: String, clazz: Class<T>): T = 
 *         objectMapper.readValue(json, clazz)
 * }
 *
 * // Using Gson
 * class GsonSerializer(private val gson: Gson) : JsonSerializer {
 *     override fun <T> serialize(obj: T): String = 
 *         gson.toJson(obj)
 *
 *     override fun <T> deserialize(json: String, clazz: Class<T>): T = 
 *         gson.fromJson(json, clazz)
 * }
 * ```
 */
interface JsonSerializer {

    /**
     * Serializes an object to JSON string.
     *
     * @param T The type of object to serialize
     * @param obj The object to serialize
     * @return JSON string representation
     */
    fun <T> serialize(obj: T): String

    /**
     * Deserializes a JSON string to an object.
     *
     * @param T The type to deserialize to
     * @param json The JSON string
     * @param clazz The class to deserialize to
     * @return The deserialized object
     */
    fun <T> deserialize(json: String, clazz: Class<T>): T
}

/**
 * Extension function for easier deserialization with reified types.
 *
 * Allows calling deserialize without explicitly passing the class:
 * ```
 * val user: User = serializer.deserialize(json)
 * ```
 * instead of:
 * ```
 * val user = serializer.deserialize(json, User::class.java)
 * ```
 *
 * @param T The type to deserialize to
 * @param json The JSON string
 * @return The deserialized object
 */
inline fun <reified T> JsonSerializer.deserialize(json: String): T {
    return deserialize(json, T::class.java)
}
