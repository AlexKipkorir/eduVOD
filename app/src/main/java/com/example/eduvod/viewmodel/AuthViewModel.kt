package com.example.eduvod.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eduvod.datastore.UserPreferences
import com.example.eduvod.repositories.LoginRepository
import com.example.eduvod.retrofit.ApiClient
import com.example.eduvod.retrofit.request.LoginRequest
import com.example.eduvod.retrofit.response.ApiResponse
import com.example.eduvod.retrofit.response.LoginResponseData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Response

sealed class LoginState {
    data object Idle : LoginState()
    data object Loading : LoginState()
    data class Success(val data: LoginResponseData?) : LoginState()
    data class Error(val message: String) : LoginState()
    data object LoggedIn : LoginState()
    data object LoggedOut : LoginState()
}

open class AuthViewModel(
    context: Context,
    private val repository: LoginRepository = LoginRepository()
) : ViewModel() {

    private val userPrefs = UserPreferences(context)

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    open val loginState: StateFlow<LoginState> = _loginState

    private val savedToken = userPrefs.authToken
    val savedEmail = userPrefs.userEmail
    val savedUserId = userPrefs.userId

    open fun loginUser(email: String, password: String) {
        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            try {
                val response: Response<ApiResponse<LoginResponseData>> =
                    repository.loginUser(LoginRequest(email, password))

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!.data
                    if (data != null) {
                        val token = data.token ?: ""

                        userPrefs.saveUserSession(
                            token = token,
                            email = email,
                            userId = -1
                        )

                        ApiClient.setAuthToken(token)
                    }
                    _loginState.value = LoginState.Success(response.body()!!.data)
                } else {
                    _loginState.value = LoginState.Error(
                        response.body()?.message ?: "Unknown error occurred"
                    )
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }

    fun logout() {
        viewModelScope.launch {
            userPrefs.clearSession()
            _loginState.value = LoginState.LoggedOut
        }
    }

    fun checkIfLoggedIn() {
        viewModelScope.launch {
            val token = savedToken.first()
            if (!token.isNullOrBlank()) {
                // Re-inject token when user is already logged in
                ApiClient.setAuthToken(token)
                _loginState.value = LoginState.LoggedIn
            } else {
                _loginState.value = LoginState.LoggedOut
            }
        }
    }
}
