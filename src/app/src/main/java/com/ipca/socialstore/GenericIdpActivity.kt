package com.ipca.socialstore

import com.google.firebase.auth.OAuthProvider

class GenericIdpActivity {
    fun microsoft() {
        // [START auth_microsoft_provider_create]
        val provider = OAuthProvider.newBuilder("microsoft.com")
        // [END auth_microsoft_provider_create]

        // [START auth_microsoft_provider_params]
        // Target specific email with login hint.
        // Force re-consent.
        provider.addCustomParameter("prompt", "consent")

        // Target specific email with login hint.
        provider.addCustomParameter("login_hint", "user@alunos.ipca.pt")
        // [END auth_microsoft_provider_params]

        // [START auth_microsoft_provider_scopes]
        // Request read access to a user's email addresses.
        // This must be preconfigured in the app's API permissions.
        provider.scopes = listOf("mail.read", "calendars.read")
        // [END auth_microsoft_provider_scopes]
    }
}