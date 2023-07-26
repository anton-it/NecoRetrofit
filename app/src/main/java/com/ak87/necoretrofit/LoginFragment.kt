package com.ak87.necoretrofit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ak87.necoretrofit.databinding.FragmentLoginBinding
import com.ak87.necoretrofit.retrofit.ApiFactory
import com.ak87.necoretrofit.retrofit.AuthRequest
import com.ak87.necoretrofit.retrofit.MainApi
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val mainApi = ApiFactory.mainApi
    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnNext.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_productsFragment)
            }
            btnSignin.setOnClickListener {
                auth(
                    AuthRequest(
                        etLogin.text.toString(),
                        etPassword.text.toString()
                    )
                )
            }
        }
    }

    private fun auth(authRequest: AuthRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = mainApi.auth(authRequest)
            val message = response.errorBody()?.string()?.let {
                JSONObject(it).getString("message") }
            requireActivity().runOnUiThread {
                binding.tvStatus.text = message
                val user = response.body()
                if (user != null) {
                    Picasso.get().load(user.image).into(binding.imageView)
                    binding.tvName.text = user.firstName
                    binding.btnNext.visibility = View.VISIBLE
                    viewModel.token.value = user.token
                }
            }
        }

    }
}