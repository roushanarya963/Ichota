package com.ichota.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ichota.NavGraphDirections
import com.ichota.R
import com.ichota.databinding.FragmentEditEmailBinding
import com.ichota.interfaces.IMainActivity
import com.ichota.utils.Global
import com.ichota.viewModel.ProfileViewModel

private const val TAG = "EditEmailFragment"

class EditEmailFragment : Fragment() {
    private lateinit var binding: FragmentEditEmailBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private val navArgs: EditEmailFragmentArgs by navArgs()
    private var mIMainActivity: IMainActivity? = null
    private var mEmail : String? =  null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEditEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etEmail.setText(navArgs.email)
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

        profileViewModel.getSendOtpToEmailObserver.observe(viewLifecycleOwner){

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.info))
                .setMessage(getString(R.string.messageOtpSentToEmail))
                .setPositiveButton(getString(R.string.ok)){dialog,which->
                    dialog.dismiss()
                    val direction =
                        NavGraphDirections.actionGlobalOtpVerificationFragment(
                            mEmail!!,"email"
                        )
                    findNavController().navigate(direction)
                }
                .show()


        }
    }

    private fun setupListener() {

        binding.fabBack.setOnClickListener {
            it.findNavController().navigateUp()
        }

        binding.btSave.setOnClickListener {

             mEmail = binding.etEmail.text.toString().trim()
            if (mEmail.isNullOrEmpty()) {
                Global.showMessage(binding.root, getString(R.string.messageEmailRequired))
                return@setOnClickListener
            }
            val request = HashMap<String,String>()
            request["user_id"] = mIMainActivity?.getPreference()?.getCurrentUser()?.userId?:""
            request["email"] = mEmail!!

            profileViewModel.sendOtpToEmail(request)
            mIMainActivity?.hideSoftKeyboard(binding.root)


        }
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = EditEmailFragment()

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