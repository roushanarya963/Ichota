package com.ichota.auth

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.SpannableString
import android.text.method.HideReturnsTransformationMethod
import android.text.method.TransformationMethod
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.AccessToken
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.ichota.activities.MainActivity
import com.ichota.R
import com.ichota.databinding.ActivityLoginBinding
import com.ichota.preferences.PrefKeys
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.viewModel.AuthViewModel


private val TransformationMethod.DoNothingTransformation: Unit
    get() {}
private const val RC_SIGN_IN_GOOGLE = 101
private const val RC_SIGN_IN_FACEBOOK = 102
private const val EMAIL = "email"
private const val TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()
    private var helper: PreferenceHelper? = null
    private var mProgressDialog: Dialog? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mGoogleSignInAccount: GoogleSignInAccount? = null
    private var mCallbackManager: CallbackManager? = null
    private var mDeviceToken: String? = null
    private var mDeviceUDID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etPassword.setOnClickListener {
            binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
        }

       // binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({
            helper = PreferenceHelper.getPreferences(this)
            mProgressDialog = Global.getProgressDialog(this)
            init()
            getAppToken()
            forgotPasswordSpan()
            signUpSpan()
            setupObservers()
            setupListeners()

        }, 500)


    }


    private fun setupObservers() {

        authViewModel.getMessage.observe(this) {
            Global.showMessage(binding.root, it)
        }
        authViewModel.getProgress.observe(this) {
            if (it) mProgressDialog?.show() else mProgressDialog?.dismiss()
        }
        authViewModel.getRegisterResponse.observe(this) {
            if (it.success == Constants.RESPONSE_SUCCESS) {

                try {
                    helper?.saveCurrentUser(it.data[0])  
                }catch (e:IndexOutOfBoundsException){
                    Log.d(TAG, "setupObservers: current user detail = $it")
                   
                }
               
                helper?.saveBoolean(PrefKeys.KEY_IS_USER_LOG_IN, true)
                Handler().postDelayed({
                    Intent(this, MainActivity::class.java).also {
                        startActivity(it)
                        finishAffinity()
                    }
                }, 300)

            }
        }

    }





    private fun init() {
        mCallbackManager = CallbackManager.Factory.create()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(getString(R.string.server_client_id))
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .requestProfile().requestScopes(Scope(Scopes.PROFILE))
            .requestProfile().requestScopes(Scope(Scopes.PLUS_ME))
            .requestProfile().requestScopes(Scope(Scopes.EMAIL))
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

    }

    @SuppressLint("HardwareIds")
    private fun getAppToken() {
        try {
            if (!helper!!.getStringValue(PrefKeys.KEY_DEVICE_TOKEN).isNullOrEmpty()) {
                mDeviceToken = helper!!.getStringValue(PrefKeys.KEY_DEVICE_TOKEN)
                Log.d(TAG, "GSM Registeration Token: $mDeviceToken")
            } else {


                FirebaseMessaging.getInstance().token.addOnCompleteListener {
                    if (!it.isSuccessful){
                        Log.d(TAG, "Fetching firebase registration token failed => ${it.exception}")
                        return@addOnCompleteListener
                    }

                    mDeviceToken = it.result

                    Log.d(TAG, "Firebase registration token = $mDeviceToken")

                    helper?.saveStringValue(
                        PrefKeys.KEY_DEVICE_TOKEN,
                        mDeviceToken?:""
                    )

                }

            }

        } catch (e: Exception) {
            mDeviceToken = ""
            Log.d(TAG, "Failed to complete token refresh:: ")
        }
        try {
            mDeviceUDID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            helper?.saveStringValue(PrefKeys.KEY_DEVICE_UDID, mDeviceUDID.toString())
            Log.d(TAG, "Device UDID: $mDeviceUDID")
        } catch (e: java.lang.Exception) {
            mDeviceUDID = ""
            e.printStackTrace()
            Log.d(TAG, "Failed to complete device UDID: ")
        }


    }

    private fun setupListeners() {

        binding.btLoginToAccount.setOnClickListener {

            authViewModel.loginUser(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString(),
                helper?.getStringValue(PrefKeys.KEY_DEVICE_UDID) ?: "",
                helper?.getStringValue(PrefKeys.KEY_DEVICE_TOKEN)?:""
            )

        }
        binding.btGotoSignUp.setOnClickListener { moveTo(SignUpActivity()) }

        binding.btForgotPassword.setOnClickListener { moveTo(ForgetPasswordActivity()) }
        binding.btGoogle.setOnClickListener { signInGoogle() }
        binding.btFacebook.setOnClickListener {
            initFacebookLogin()

        }
    }

    private fun initFacebookLogin() {

        val loginManager: LoginManager = LoginManager.getInstance()
        disconnectFromFacebook()
        loginManager.loginBehavior = LoginBehavior.WEB_ONLY

        loginManager.logInWithReadPermissions(
            this,
            listOf("public_profile","email")
        )

        loginManager.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                Log.d(TAG, "onSuccess: $result")
                result?.let {
                    val request = GraphRequest.newMeRequest(
                        it.accessToken
                    ) { jsonObject, response ->
                        jsonObject?.let { it ->
                            try {
                                val fbId = it.getString("id")
                                val name =
                                    "${it.getString("first_name")} ${it.optString("last_name")}"
                                var email = it.optString("email")
                                if (email.isNullOrEmpty()) email = "$fbId@facebook.com"
                                val url = "http://graph.facebook.com/$fbId/picture?type=large"

                                Log.d(TAG, "onSuccess: $jsonObject")
                                disconnectFromFacebook()

                                authViewModel.socialLogin(
                                    email,
                                    mDeviceUDID?:"",
                                    mDeviceToken?:"",
                                    name,
                                    "",
                                    fbId,
                                    "",
                                    "Facebook",
                                    url
                                )

                            } catch (e: Exception) {
                                Log.d(TAG, "onSuccess: ")
                                Global.showMessage(binding.root, e.localizedMessage)
                            }

                        }

                    }

                    val params = Bundle()
                    params.putString(
                        "fields",
                        "id, first_name, last_name, email,gender, birthday, location"
                    )


                    request.parameters = params
                    request.executeAsync()

                }


                /*  helper?.saveBoolean(PrefKeys.KEY_IS_USER_LOG_IN, true)
                  Toast.makeText(this@LoginActivity, "Success", Toast.LENGTH_LONG).show()
                  moveTo(MainActivity())*/

            }

            override fun onCancel() {
                Toast.makeText(this@LoginActivity, "cancel", Toast.LENGTH_LONG).show()

            }

            override fun onError(error: FacebookException?) {
                Toast.makeText(this@LoginActivity, error.toString() , Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun forgotPasswordSpan() {
        val ss = SpannableString(getString(R.string.forgotyourPassword))
        ss.setSpan(UnderlineSpan(), 0, ss.length, 0)
        binding.btForgotPassword.text = ss
    }

    private fun signUpSpan() {
        val ss = SpannableString(getString(R.string.dontHaveAccount))
        ss.setSpan(UnderlineSpan(), 27, ss.length, 0)
        binding.btGotoSignUp.text = ss

    }

    private fun signInGoogle() {
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE)
    }

    override fun onStart() {
        super.onStart()
        mGoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
    }

    private fun moveTo(destination: AppCompatActivity) {

        Intent(this, destination::class.java).also {
            startActivity(it)
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: $resultCode   $requestCode")

        if (resultCode == RESULT_OK) {

            if (requestCode == RC_SIGN_IN_GOOGLE) {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                googleSignIn(task)
            } else {
                mCallbackManager?.onActivityResult(requestCode, resultCode, data)
            }

        } else {
            Log.e(TAG, "error while signing In")
        }
    }

    private fun googleSignIn(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            Log.w(TAG, "signInResult:success=$account")
            Log.w(TAG, "signInResult:token" + account.serverAuthCode)

            authViewModel.socialLogin(
                account.email?:"",
                mDeviceUDID?:"",
                mDeviceToken?:"",
                account.displayName?:"",
                "",
                "",
                account.id?:"",
                "Google",
                account.photoUrl.toString()
            )

        }catch (e: java.lang.Exception){
            Log.w(TAG, "signInResult:failed code= ${e.localizedMessage}" )
        }

    }


    fun disconnectFromFacebook() {
        Log.d(TAG, "disconnectFromFacebook: ${AccessToken.getCurrentAccessToken()}")
        if (AccessToken.getCurrentAccessToken() == null) {
            return  // already logged out
        }
        GraphRequest(
            AccessToken.getCurrentAccessToken(),
            "/me/permissions/",
            null,
            HttpMethod.DELETE
        ) { LoginManager.getInstance().logOut() }
            .executeAsync()
    }

}