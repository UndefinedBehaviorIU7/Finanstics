// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
    id("vkid.manifest.placeholders") version "1.1.0" apply true
}

vkidManifestPlaceholders {
    init(
        clientId = "53581972",
        clientSecret = "DLSE8CqpEBcsd16y6gRo"
    )

    vkidRedirectHost = "vk.com"
    vkidRedirectScheme = "vk53581972"
    vkidClientId = "53581972"
    vkidClientSecret = "DLSE8CqpEBcsd16y6gRo"
}

