package com.ichota.auth

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.ichota.R
import com.ichota.activities.OtpVerification
import com.ichota.databinding.ActivitySignUpBinding
import com.ichota.network.APIFactory
import com.ichota.preferences.PrefKeys
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.viewModel.AuthViewModel
import java.util.regex.Pattern

private const val TAG = "SignUpActivity"
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val authViewModel: AuthViewModel by viewModels()
    private var helper: PreferenceHelper? = null
    private lateinit var mProgressDialog: Dialog
    lateinit var activity: Activity
    private var mOTP : String? = null
    private var edit_phone_number:String?=null

    var fullnm :String ? = null
    var emailid   :String ? = null
    var deviceid :String ? = null
    var phonenum :String ? = null
    var countrycode :String ? = null
    var password :String ? = null
    var cmfpassword :String ? = null
    var devicetoken :String ? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        helper = PreferenceHelper.getPreferences(this)
        mProgressDialog = Global.getProgressDialog(this)
        edit_phone_number=helper?.getStringValue(PrefKeys.KEY_EDIT_MOBILE_NUMBER)
        init()
        initListeners()
        setupObservers()

    }

    private fun setupObservers() {

        authViewModel.getMessage.observe(this) {
            Global.showMessage(binding.root, it)
        }

        authViewModel.getProgress.observe(this) {
            if (it) mProgressDialog.show() else mProgressDialog.dismiss()
        }

        authViewModel.getVerifyOtpObserver.observe(this) {
            if (it.success == Constants.RESPONSE_SUCCESS) {

              //  Global.showMessage(binding.root, it.messageText)

                 mOTP= it.data?.otp.toString()
                 fullnm=  binding.etName.text.toString().trim()
                 emailid=   binding.etEmail.text.toString().trim()
                 deviceid=  helper?.getStringValue(PrefKeys.KEY_DEVICE_UDID)
                 phonenum= binding.etPhone.text.toString().trim()
                 countrycode=  binding.ccp.selectedCountryCodeWithPlus.toString().trim()
                 password=  binding.etPassword.text.toString().trim()
                 cmfpassword= binding.etConfirmPassword.text.toString().trim()
                 devicetoken=  helper?.getStringValue(PrefKeys.KEY_DEVICE_TOKEN)
                 intent = Intent(this@SignUpActivity, OtpVerification::class.java)

                intent.putExtra("name",fullnm)
                intent.putExtra("email",emailid)
                intent.putExtra("device_id",deviceid)
                intent.putExtra("phone_number",phonenum)
                intent.putExtra("country_code",countrycode)
                intent.putExtra("password",password)
                intent.putExtra("confirm_password",cmfpassword)
                intent.putExtra("device_token",devicetoken)
                intent.putExtra("otp",mOTP)

                startActivity(intent)

            }
        }

        /*authViewModel.getRegisterResponse.observe(this){
            if (it.success == Constants.RESPONSE_SUCCESS) {
                Global.showMessage(binding.root, it.messageText)
            }
        }*/

    }





    private fun init() {
        setupAlreadyAccountSpan()
        setupTermsSpan()
    }


    private fun setupAlreadyAccountSpan() {

        val ss = SpannableString(getString(R.string.alreadyHaveAcc))
        ss.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                onBackPressed()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(this@SignUpActivity, R.color.colorPrimary)
                ds.isUnderlineText = false
            }
        }, 22, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.btGotoLogin.text = ss
        binding.btGotoLogin.highlightColor = Color.TRANSPARENT
        binding.btGotoLogin.movementMethod = LinkMovementMethod.getInstance()

    }

    private fun setupTermsSpan() {
        val ss = SpannableString(getString(R.string.registerTermsDescription))
        ss.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    moveToBrowser(APIFactory.URL_TERM_AND_CONDITIONS)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = ContextCompat.getColor(this@SignUpActivity, R.color.colorPrimary)
                    ds.isUnderlineText = false
                }
            }, 32, 52, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        ss.setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    moveToBrowser(APIFactory.URL_PRIVACY_POLICY)

                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = ContextCompat.getColor(this@SignUpActivity, R.color.colorPrimary)
                    ds.isUnderlineText = false
                }
            }, 68, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvTermsPrivacy.text = ss
        binding.tvTermsPrivacy.movementMethod = LinkMovementMethod.getInstance()
        binding.tvTermsPrivacy.highlightColor = Color.TRANSPARENT

    }

    private fun initListeners() {

        binding.fabBack.setOnClickListener {this.finish() }

        binding.btRegister.setOnClickListener {

            Global.hideKeyboard(this,binding.root)

             if(binding.etName.text.toString().trim().isEmpty()){
                 Toast.makeText(this,getString(R.string.messageNameRequired),Toast.LENGTH_LONG).show()
             }else if(binding.etEmail.text.toString().trim().isEmpty()){
                 Toast.makeText(this,getString(R.string.messageEmailRequired),Toast.LENGTH_LONG).show()
             }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString().trim()).matches()){
                 Toast.makeText(this,getString(R.string.messageValidEmailAddress),Toast.LENGTH_LONG).show()
             }else if(binding.etPhone.text.toString().trim().isEmpty()){
                 Toast.makeText(this,getString(R.string.phonenumbervalidation),Toast.LENGTH_LONG).show()
             }else if(binding.etPassword.text.toString().trim().isEmpty()){
                 Toast.makeText(this,getString(R.string.messagePasswordRequired),Toast.LENGTH_LONG).show()
             }else if(binding.etPassword.text.toString().trim().length<8){
                 Toast.makeText(this,getString(R.string.messageMinimumPasswordLenght),Toast.LENGTH_LONG).show()
             }else if(binding.etPassword.text.toString().trim()!=binding.etConfirmPassword.text.toString().trim()){
                 Toast.makeText(this ,getString(R.string.messagePasswordDoesNotMatch),Toast.LENGTH_LONG).show()
             }else {
                 authViewModel.verifyMobile( binding.ccp.selectedCountryCodeWithPlus.toString().trim()+ binding.etPhone.text.toString().trim())
             }

         //   authViewModel.verifyMobile( binding.ccp.selectedCountryCodeWithPlus.toString().trim()+ binding.etPhone.text.toString().trim())

        }

    }

    private fun moveToBrowser(link : String){
       val browserIntent =  Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(browserIntent)
    }





}