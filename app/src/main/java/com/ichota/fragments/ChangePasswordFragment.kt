package com.ichota.fragments

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ichota.R
import com.ichota.databinding.FragmentChangePasswordBinding
import com.ichota.interfaces.IMainActivity
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.viewModel.AuthViewModel


private const val TAG = "ChangePasswordFragment"

class ChangePasswordFragment : Fragment() {
    private lateinit var binding: FragmentChangePasswordBinding
    private var mIMainActivity: IMainActivity? = null
    private val authViewModel: AuthViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {

        authViewModel.getMessage.observe(viewLifecycleOwner) {
            mIMainActivity?.showMessage(it)

        }
        authViewModel.getProgress.observe(viewLifecycleOwner) {
            mIMainActivity?.showProgress(it)
        }

        authViewModel.getChangePasswordObserver.observe(viewLifecycleOwner) {
            if (it.success == Constants.RESPONSE_SUCCESS) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.Success))
                    .setMessage(it.message)
                    .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                        dialog.dismiss()
                        findNavController().popBackStack()
                    }
                    .show()
            }
        }
    }

    private fun setupListeners() {
        binding.fabBack.setOnClickListener {
            it.findNavController().popBackStack()
        }

        binding.btChangePassword.setOnClickListener {

            validateData()
        }

    }

    private fun validateData() {
        val oldPassword = binding.etOldPassword.text.toString().trim()
        val newPassword = binding.etNewPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (TextUtils.isEmpty(oldPassword)) {
            mIMainActivity?.showMessage(getString(R.string.messageOldPasswordRequired))
            return
        }

        if (TextUtils.isEmpty(newPassword)) {
            mIMainActivity?.showMessage(getString(R.string.messageNewPasswordRequired))
            return
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            mIMainActivity?.showMessage(getString(R.string.messageConfirmPasswordRequired))
            return
        }

        if (newPassword != confirmPassword) {
            mIMainActivity?.showMessage(getString(R.string.messagePasswordConfirmationFailed))
            return
        }

        authViewModel.changePassword(
            mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: "",
            oldPassword,
            newPassword
        )

    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            ChangePasswordFragment()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIMainActivity = context as IMainActivity
    }

    override fun onDetach() {
        super.onDetach()
        mIMainActivity = null
    }
}