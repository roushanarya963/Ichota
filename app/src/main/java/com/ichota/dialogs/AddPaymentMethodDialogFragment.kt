package com.ichota.dialogs

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.URLUtil
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.ichota.R
import com.ichota.databinding.FragmentAddPaymentMethodDialogBinding
import com.ichota.interfaces.IMainActivity
import com.ichota.model.PaymentMethod
import com.ichota.model.User
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.viewModel.ProfileViewModel

private const val TAG = "AddPaymentMethodDialogFragment"

private const val KEY_PAYMENT_METHOD = "payment_method"

class AddPaymentMethodDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentAddPaymentMethodDialogBinding
    private val profileViewModel : ProfileViewModel by viewModels()
    private var mIMainActivity : IMainActivity?=null
    private var mPaymentMethod:PaymentMethod?=null
    private var mCallback:IAddPaymentMethod?=null
    private var mUser: User? = null
    private var mShareLink: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUser = mIMainActivity?.getPreference()?.getCurrentUser()
        mPaymentMethod = arguments?.getParcelable(KEY_PAYMENT_METHOD)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentAddPaymentMethodDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            it.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            it.window?.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.bg_inset_dialog_16
                )
            )
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPaymentData()
        setupObserver()
        setupOnClickListener()
    }

    private fun setupObserver() {
        profileViewModel.getProgressObserver.observe(viewLifecycleOwner){
            binding.progressBar.visibility = if(it) View.VISIBLE else View.GONE


        }
        profileViewModel.getMessageObserver.observe(viewLifecycleOwner){
            Global.showMessage(binding.root,it)

        }
        profileViewModel.getUpdatePaymentMethodObserver.observe(viewLifecycleOwner){

            if(it.success==Constants.RESPONSE_SUCCESS) {


            }

            dismiss()
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            mCallback?.onAddPaymentMethodSuccess()

        }
    }

    private fun setupOnClickListener() {
        binding.btContinue.setOnClickListener {
            validateData()

        }
    }

    private fun validateData() {
        val paymentUrl = binding.etPaymentUrl.text.toString().trim()
        if(paymentUrl.isEmpty()){
            Global.showMessage(binding.root,getString(R.string.messageEnterPaymentUrl))
            return
        }
        if(!URLUtil.isValidUrl(paymentUrl)){
            Global.showMessage(binding.root,getString(R.string.messageEnterValidUrl))
            return
        }


        Global.hideKeyboard(requireContext(),binding.root)
        mPaymentMethod?.let {
            profileViewModel.updatePaymentMethod(
                mIMainActivity?.getPreference()?.getCurrentUser()?.userId?:return,
                if(it.status =="0") "1" else "0",
                it.id,
                paymentUrl
            )
        }



    }

    private fun setupPaymentData() {
        mPaymentMethod?.let {
            binding.tvPaymentName.text = it.title
            binding.etPaymentUrl.setText(it.userUrl)
        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIMainActivity = context as IMainActivity
    }

    override fun onDetach() {
        super.onDetach()
        mIMainActivity = null
    }

    fun setOnAddPaymentMethodStatusListener(callback:IAddPaymentMethod){
        mCallback  =callback
    }

    interface IAddPaymentMethod{
        fun onAddPaymentMethodSuccess()
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(paymentMethod : PaymentMethod) =
            AddPaymentMethodDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_PAYMENT_METHOD, paymentMethod)

                }
            }
    }

}