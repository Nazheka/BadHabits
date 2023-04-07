plugins {
    id("convention.android.library")
    id("convention.android.compose")
}

android {
    namespace = "breakbadhabits.foundation.uikit"
}

dependencies {
    api(projects.foundation.controllers)
    api(projects.foundation.datetime)
    api("androidx.core:core-ktx:1.9.0")
    api("androidx.activity:activity-compose:1.7.0")
    api("androidx.appcompat:appcompat:1.6.1")
    api("androidx.compose.foundation:foundation:1.4.1")
    api("androidx.navigation:navigation-compose:2.5.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("androidx.compose.material3:material3:1.0.1")
}