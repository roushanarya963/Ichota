package com.ichota.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.ichota.adapter.PaymentMethodAdapter
import com.ichota.databinding.FragmentPaymentMethodsBinding
import com.ichota.dialogs.AddPaymentMethodDialogFragment
import com.ichota.interfaces.IMainActivity
import com.ichota.model.PaymentMethod
import com.ichota.viewModel.ProfileViewModel

private const val TAG = "PaymentMethodsFragment"

class PaymentMethodsFragment : Fragment(), PaymentMethodAdapter.IPaymentMethods,
    AddPaymentMethodDialogFragment.IAddPaymentMethod {
    private lateinit var binding: FragmentPaymentMethodsBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private var mIMainActivity: IMainActivity? = null
    private var mPaymentMethodAdapter : PaymentMethodAdapter? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPaymentMethodAdapter = PaymentMethodAdapter(requireContext())
        mPaymentMethodAdapter?.setOnPaymentMethodClickListener(this)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPaymentMethodsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvPaymentMethods.setHasFixedSize(true)
        binding.rvPaymentMethods.adapter = mPaymentMethodAdapter
        profileViewModel.getPaymentMethods(
            mIMainActivity?.getPreference()?.getCurrentUser()?.userId?:return
        )
        setupObserver()
        setupListener()
    }

    private fun setupObserver() {
        profileViewModel.getProgressObserver.observe(viewLifecycleOwner){
            mIMainActivity?.showProgress(it)
        }

        profileViewModel.getMessageObserver.observe(viewLifecycleOwner){
            mIMainActivity?.showMessage(it)
        }

        profileViewModel.getPaymentMethodsObserver.observe(viewLifecycleOwner){
            binding.emptyFile.root.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            mPaymentMethodAdapter?.setData(it)
        }



    }

    private fun setupListener() {
        binding.fabBack.setOnClickListener {
            it.findNavController().navigateUp()
        }
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            PaymentMethodsFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIMainActivity = context as IMainActivity
    }

    override fun onDetach() {
        super.onDetach()
        mIMainActivity = null
    }

    override fun onPaymentMethodClick(paymentMethod: PaymentMethod) {
        val addPaymentMethodDialog = AddPaymentMethodDialogFragment.newInstance(paymentMethod)
        addPaymentMethodDialog.setOnAddPaymentMethodStatusListener(this)
        addPaymentMethodDialog.show(childFragmentManager,TAG)

    }

    override fun onAddPaymentMethodSuccess() {
        profileViewModel.getPaymentMethods(
            mIMainActivity?.getPreference()?.getCurrentUser()?.userId?:return
        )
    }
}