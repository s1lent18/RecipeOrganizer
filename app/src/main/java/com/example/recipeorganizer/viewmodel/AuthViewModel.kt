package com.example.recipeorganizer.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeorganizer.models.api.LoginApi
import com.example.recipeorganizer.models.api.SignupApi
import com.example.recipeorganizer.models.model.LoginModel
import com.example.recipeorganizer.models.requests.LoginRequest
import com.example.recipeorganizer.models.requests.SignupRequest
import com.example.recipeorganizer.models.response.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginapi: LoginApi,
    private val signupapi: SignupApi
) : ViewModel()  {

    private val _loginresult = MutableLiveData<NetworkResponse<LoginModel>>()
    val loginresult: LiveData<NetworkResponse<LoginModel>> = _loginresult

    private val _signupresult = MutableLiveData<NetworkResponse<LoginModel>>()
    val signupresult: LiveData<NetworkResponse<LoginModel>> = _signupresult

    fun login(loginrequest: LoginRequest) {
        _loginresult.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val response = loginapi.loginUser(loginrequest)
                Log.d("LoginViewModel", "HTTP response code: ${response.code()}")
                if (response.isSuccessful && response.code() == 200) {
                    response.body()?.let {
                        _loginresult.value = NetworkResponse.Success(it)
                    }
                } else {
                    _loginresult.value = NetworkResponse.Failure("Wrong Username/Password")
                }
            } catch (e: Exception) {
                _loginresult.value = NetworkResponse.Failure("Server IrResponsive")
            }
        }
    }

    fun signup(signuprequest: SignupRequest) {
        _signupresult.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val response = signupapi.registerUser(signuprequest)
                Log.d("LoginViewModel", "HTTP response code: ${response.code()}")
                if (response.isSuccessful && response.code() == 200) {
                    response.body()?.let {
                        _signupresult.value = NetworkResponse.Success(it)
                    }
                } else {
                    _signupresult.value = NetworkResponse.Failure("Wrong Username/Password")
                }
            } catch (e: Exception) {
                _signupresult.value = NetworkResponse.Failure("Server IrResponsive")
            }
        }
    }
}