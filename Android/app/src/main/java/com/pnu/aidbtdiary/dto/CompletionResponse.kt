import com.google.gson.annotations.SerializedName

data class CompletionResponse(
    @SerializedName("id") val id: String,
    @SerializedName("created") val created: Int,
    @SerializedName("model") val model: String,
    @SerializedName("choices") val choices: List<Choice>,
    @SerializedName("usage") val usage: Usage
)

data class Choice(
    @SerializedName("index") val index: Int,
    @SerializedName("message") val message: Message
)

data class Usage(
    @SerializedName("completion_tokens") val completionTokens: Int,
    @SerializedName("prompt_tokens") val promptTokens: Int,
    @SerializedName("total_tokens") val totalTokens: Int
)

data class Message(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String,
)


