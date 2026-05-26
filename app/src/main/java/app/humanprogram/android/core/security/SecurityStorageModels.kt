package app.humanprogram.android.core.security

enum class SecurityCredentialType {
    PIN,
    PASSWORD
}

data class SecurityCredentialRecord(
    val credentialType: SecurityCredentialType,
    val verifierSaltBase64: String,
    val verifierHashBase64: String,
    val verifierScheme: String
)

data class RecoveryPhraseRecord(
    val encryptedPhrase: EncryptedSecret,
    val verifierSaltBase64: String,
    val verifierHashBase64: String,
    val verifierScheme: String,
    val phraseFormat: String
)
