package com.ichota.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.ichota.NavGraphDirections
import com.ichota.R
import com.ichota.auth.LoginActivity
import com.ichota.databinding.ActivityOtpVerificationBinding
import com.ichota.interfaces.IMainActivity
import com.ichota.preferences.PrefKeys
import com.ichota.preferences.PreferenceHelper
import com.ichota.profile.AccountFragment
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.viewModel.AuthViewModel

private const val FULLNAME = "fullname"
private const val EMAILID = "emailid"
private const val DEVICEID = "deviceid"
private const val MOBILENUMBER = "mobile"
private const val COUNTRYCODEPLUS = "countrycodeplus"
private const val ENTERPASSWORD ="password"
private const val CONFIRMPASSWORD ="comfirmpassword"
private const val DEVICETOKEN = "devicetoken"
private const val TAG = "OtpVerification"
class OtpVerification : AppCompatActivity() {


    private lateinit var fullname : String
    private lateinit var emailId : String
    private lateinit var deviceuid : String
    private lateinit var mobilenumber : String
    private lateinit var countrycodeplus : String
    private lateinit var enterpassword : String
    private lateinit var confirmpassword : String
    private lateinit var devicetokenid : String
    private  var otp : String ? = null

    private var mIMainActivity: IMainActivity? = null
    private var mOTP : String? = null
    private lateinit var binding: ActivityOtpVerificationBinding
    private val authViewModel: AuthViewModel by viewModels()
    private var helper: PreferenceHelper? = null
    private lateinit var mProgressDialog: Dialog
    private var edit_phone_number:String?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mProgressDialog = Global.getProgressDialog(this)
        helper = PreferenceHelper.getPreferences(this)



        edit_phone_number=helper?.getStringValue(PrefKeys.KEY_EDIT_MOBILE_NUMBER)

       fullname= intent.getStringExtra("name").toString()
       emailId= intent.getStringExtra("email").toString()
       deviceuid=  intent.getStringExtra("device_id").toString()
       mobilenumber=  intent.getStringExtra("phone_number").toString()
       countrycodeplus= intent.getStringExtra("country_code").toString()
       enterpassword= intent.getStringExtra("password").toString()
       confirmpassword=  intent.getStringExtra("confirm_password").toString()
       devicetokenid = intent.getStringExtra("device_token").toString()



       otp = intent.getStringExtra("otp").toString()

        setupListener()
        setupObservers()
    }



    private fun setupListener() {

        binding.otpView.setOtpCompletionListener {
            mOTP  = it
        }


        binding.fabBack.setOnClickListener {
            finish()
        }

        binding.btValidate.setOnClickListener {

            if (mOTP.isNullOrEmpty()){
                Global.showMessage(binding.root,getString(R.string.messageOptRequired))
                return@setOnClickListener
            }

            if(mOTP==otp){
                authViewModel.registerUser(
                    fullname.toString(),
                    emailId.toString(),
                    deviceuid.toString(),
                    mobilenumber.toString(),
                    countrycodeplus.toString(),
                    enterpassword.toString(),
                    confirmpassword.toString(),
                    devicetokenid.toString()
                )
            }else{
                Toast.makeText(this,getString(R.string.pleaseentercorrectpin),Toast.LENGTH_LONG).show()
            }

            mIMainActivity?.hideSoftKeyboard(binding.root)
        }


    }


    private fun setupObservers() {
        authViewModel.getMessage.observe(this) {
            Global.showMessage(binding.root, it)

            Handler(Looper.getMainLooper()).postDelayed({
                  finish()
            },3000)

          //  finish()

        }

        authViewModel.getProgress.observe(this) {
            if (it) mProgressDialog.show() else mProgressDialog.dismiss()
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

                Toast.makeText(this,getString(R.string.youhavesuccessfulylogin),Toast.LENGTH_LONG).show()

            }
        }
    }


}