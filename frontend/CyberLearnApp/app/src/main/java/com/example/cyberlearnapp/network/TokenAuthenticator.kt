
package com.example.cyberlearnapp.network

import com.example.cyberlearnapp.utils.AuthManager
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Evitar bucles infinitos
        if (response.responseCount() >= 3) {
            return null
        }

        val refreshToken = AuthManager.getRefreshToken() ?: return null

        try {
            val refreshCall = RetrofitInstance.api.refreshToken(
                mapOf("refresh_token" to refreshToken)
            )
            val refreshResponse = refreshCall.execute()

            if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                val newTokens = refreshResponse.body()!!

                AuthManager.saveToken(newTokens.accessToken)
                if (newTokens.refreshToken.isNotEmpty()) {
                    AuthManager.saveRefreshToken(newTokens.refreshToken)
                }

                return response.request.newBuilder()
                    .header("Authorization", "Bearer ${newTokens.accessToken}")
                    .build()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    // EXTENSIÓN QUE RESUELVE EL ERROR
    private fun Response.responseCount(): Int {
        var result = 1
        var res: Response? = this
        while (res?.priorResponse != null) {
            result++
            res = res.priorResponse
        }
        return result
    }
}