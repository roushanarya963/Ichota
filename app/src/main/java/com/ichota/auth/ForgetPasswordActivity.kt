package com.ichota.auth

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ichota.R
import com.ichota.databinding.ActivityForgetPasswordBinding
import com.ichota.utils.Global
import com.ichota.viewModel.AuthViewModel


private const val TAG = "ForgetPasswordActivity"

class ForgetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgetPasswordBinding
    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListeners()
        setupObserver()
    }


    private fun setupListeners() {
        binding.ivBack.setOnClickListener { finish() }
        binding.btSubmit.setOnClickListener {
            authViewModel.forgotPassword(
                binding.etEmail.text.toString().trim()
            )
            Global.hideKeyboard(this,binding.root)
        }


    }

    private fun setupObserver() {
        authViewModel.getMessage.observe(this) {
            Global.showMessage(binding.root, it)
        }
        authViewModel.getProgress.observe(this) {
            binding.progressBar.root.visibility = if (it) View.VISIBLE else View.GONE
        }
        authViewModel.getForgotPasswordRes.observe(this) {
            showAlert(it.message)

        }
    }

    private fun showAlert(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.forgotPassword)
            .setMessage(message)
            .setPositiveButton(R.string.ok) { dialog, which ->
                dialog.dismiss()
                finish()

            }
            .show()


    }




}