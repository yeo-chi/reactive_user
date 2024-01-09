package yeo.chi.reactive_user.user.configuration.jwt

enum class Role(val securityRole: String) {
    ANONYMOUS("ROLE_ANONYMOUS"),

    USER("ROLE_USER"),
}
