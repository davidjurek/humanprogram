package app.humanprogram.android.core.export

data class HprgmAppState(
    val appearance: String = "system",
    val dateFormat: String = "month_day_year",
    val fontChoice: String = "serif",
    val metadataVisibleByDefault: Boolean = false,
    val showProjectBucket: Boolean = false,
    val showTaskSource: Boolean = true,
    val calendarViewMode: String = "month",
    val onboardingComplete: Boolean = true,
    val selectedCalendarIdsCsv: String = "",
    val appLockEnabled: Boolean = false,
    val biometricUnlockEnabled: Boolean = false,
    val appLockTimeoutMinutes: Int = 0,
    val appLockCredentialType: String = "",
    val appLockVerifierScheme: String = "",
    val appLockPinSaltBase64: String = "",
    val appLockPinHashBase64: String = "",
    val recoveryPhraseEncryptionScheme: String = "",
    val recoveryPhraseKeyAlias: String = "",
    val recoveryPhraseNonceBase64: String = "",
    val recoveryPhraseCiphertextBase64: String = "",
    val recoveryPhraseFormat: String = "",
    val recoveryPhraseVerifierScheme: String = "",
    val recoveryPhraseSaltBase64: String = "",
    val recoveryPhraseHashBase64: String = "",
    val recoveryPhrasePlainText: String = "",
    val backlogView: String = "tasks",
    val backlogSort: String = "creation"
) {
    fun toJson(): String {
        return listOf(
            "\"schemaVersion\":1",
            "\"appearance\":${appearance.toJsonString()}",
            "\"dateFormat\":${dateFormat.toJsonString()}",
            "\"fontChoice\":${fontChoice.toJsonString()}",
            "\"metadataVisibleByDefault\":$metadataVisibleByDefault",
            "\"showProjectBucket\":$showProjectBucket",
            "\"showTaskSource\":$showTaskSource",
            "\"calendarViewMode\":${calendarViewMode.toJsonString()}",
            "\"onboardingComplete\":$onboardingComplete",
            "\"selectedCalendarIdsCsv\":${selectedCalendarIdsCsv.toJsonString()}",
            "\"appLockEnabled\":$appLockEnabled",
            "\"biometricUnlockEnabled\":$biometricUnlockEnabled",
            "\"appLockTimeoutMinutes\":$appLockTimeoutMinutes",
            "\"appLockCredentialType\":${appLockCredentialType.toJsonString()}",
            "\"appLockVerifierScheme\":${appLockVerifierScheme.toJsonString()}",
            "\"appLockPinSaltBase64\":${appLockPinSaltBase64.toJsonString()}",
            "\"appLockPinHashBase64\":${appLockPinHashBase64.toJsonString()}",
            "\"recoveryPhraseEncryptionScheme\":${recoveryPhraseEncryptionScheme.toJsonString()}",
            "\"recoveryPhraseKeyAlias\":${recoveryPhraseKeyAlias.toJsonString()}",
            "\"recoveryPhraseNonceBase64\":${recoveryPhraseNonceBase64.toJsonString()}",
            "\"recoveryPhraseCiphertextBase64\":${recoveryPhraseCiphertextBase64.toJsonString()}",
            "\"recoveryPhraseFormat\":${recoveryPhraseFormat.toJsonString()}",
            "\"recoveryPhraseVerifierScheme\":${recoveryPhraseVerifierScheme.toJsonString()}",
            "\"recoveryPhraseSaltBase64\":${recoveryPhraseSaltBase64.toJsonString()}",
            "\"recoveryPhraseHashBase64\":${recoveryPhraseHashBase64.toJsonString()}",
            "\"recoveryPhrasePlainText\":${recoveryPhrasePlainText.toJsonString()}",
            "\"backlogView\":${backlogView.toJsonString()}",
            "\"backlogSort\":${backlogSort.toJsonString()}"
        ).joinToString(prefix = "{", separator = ",", postfix = "}")
    }

    companion object {
        fun fromJson(json: String): HprgmAppState {
            val item = json.parseJsonValueMap()
            return HprgmAppState(
                appearance = item.string("appearance", "system"),
                dateFormat = item.string("dateFormat", "month_day_year"),
                fontChoice = item.string("fontChoice", "serif"),
                metadataVisibleByDefault = item.boolean("metadataVisibleByDefault", false),
                showProjectBucket = item.boolean("showProjectBucket", false),
                showTaskSource = item.boolean("showTaskSource", true),
                calendarViewMode = item.string("calendarViewMode", "month"),
                onboardingComplete = item.boolean("onboardingComplete", true),
                selectedCalendarIdsCsv = item.string("selectedCalendarIdsCsv", ""),
                appLockEnabled = item.boolean("appLockEnabled", false),
                biometricUnlockEnabled = item.boolean("biometricUnlockEnabled", false),
                appLockTimeoutMinutes = item.int("appLockTimeoutMinutes", 0),
                appLockCredentialType = item.string("appLockCredentialType", ""),
                appLockVerifierScheme = item.string("appLockVerifierScheme", ""),
                appLockPinSaltBase64 = item.string("appLockPinSaltBase64", ""),
                appLockPinHashBase64 = item.string("appLockPinHashBase64", ""),
                recoveryPhraseEncryptionScheme = item.string("recoveryPhraseEncryptionScheme", ""),
                recoveryPhraseKeyAlias = item.string("recoveryPhraseKeyAlias", ""),
                recoveryPhraseNonceBase64 = item.string("recoveryPhraseNonceBase64", ""),
                recoveryPhraseCiphertextBase64 = item.string("recoveryPhraseCiphertextBase64", ""),
                recoveryPhraseFormat = item.string("recoveryPhraseFormat", ""),
                recoveryPhraseVerifierScheme = item.string("recoveryPhraseVerifierScheme", ""),
                recoveryPhraseSaltBase64 = item.string("recoveryPhraseSaltBase64", ""),
                recoveryPhraseHashBase64 = item.string("recoveryPhraseHashBase64", ""),
                recoveryPhrasePlainText = item.string("recoveryPhrasePlainText", ""),
                backlogView = item.string("backlogView", "tasks"),
                backlogSort = item.string("backlogSort", "creation")
            )
        }
    }
}

private fun Map<String, String>.string(key: String, default: String): String = this[key] ?: default

private fun Map<String, String>.boolean(key: String, default: Boolean): Boolean = this[key]?.toBooleanStrictOrNull() ?: default

private fun Map<String, String>.int(key: String, default: Int): Int = this[key]?.toIntOrNull() ?: default

private fun String.toJsonString(): String {
    return buildString {
        append('"')
        this@toJsonString.forEach { char ->
            when (char) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> append(char)
            }
        }
        append('"')
    }
}

private fun String.parseJsonValueMap(): Map<String, String> {
    val result = mutableMapOf<String, String>()
    var index = skipWhitespace(0)
    require(this[index] == '{') { "Expected JSON object." }
    index += 1
    while (true) {
        index = skipWhitespace(index)
        if (this[index] == '}') return result
        val keyResult = readJsonString(index)
        index = skipWhitespace(keyResult.nextIndex)
        require(this[index] == ':') { "Expected JSON separator." }
        index = skipWhitespace(index + 1)
        if (this[index] == '"') {
            val valueResult = readJsonString(index)
            result[keyResult.value] = valueResult.value
            index = skipWhitespace(valueResult.nextIndex)
        } else {
            val valueStart = index
            index = skipScalarValue(index)
            result[keyResult.value] = substring(valueStart, index).trim()
        }
        when (this[index]) {
            ',' -> index += 1
            '}' -> return result
            else -> error("Expected JSON comma or end.")
        }
    }
}

private fun String.skipScalarValue(startIndex: Int): Int {
    var index = startIndex
    while (index < length && this[index] != ',' && this[index] != '}') {
        index += 1
    }
    return skipWhitespace(index)
}

private fun String.skipWhitespace(startIndex: Int): Int {
    var index = startIndex
    while (index < length && this[index].isWhitespace()) {
        index += 1
    }
    return index
}

private data class JsonStringResult(
    val value: String,
    val nextIndex: Int
)

private fun String.readJsonString(startIndex: Int): JsonStringResult {
    require(this[startIndex] == '"') { "Expected JSON string." }
    val output = StringBuilder()
    var index = startIndex + 1
    while (index < length) {
        val char = this[index]
        when (char) {
            '"' -> return JsonStringResult(output.toString(), index + 1)
            '\\' -> {
                val escaped = this[index + 1]
                output.append(
                    when (escaped) {
                        '"' -> '"'
                        '\\' -> '\\'
                        'n' -> '\n'
                        'r' -> '\r'
                        't' -> '\t'
                        else -> escaped
                    }
                )
                index += 2
            }
            else -> {
                output.append(char)
                index += 1
            }
        }
    }
    error("Unclosed JSON string.")
}
