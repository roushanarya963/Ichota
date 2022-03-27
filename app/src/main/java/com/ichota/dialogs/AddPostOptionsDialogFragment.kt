package com.ichota.dialogs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ichota.NavGraphDirections
import com.ichota.auth.LoginActivity
import com.ichota.databinding.FragmentAddPostOptionsDialogBinding
import com.ichota.preferences.PrefKeys
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Global

private const val TAG ="AddPostOptionsDialogFragment"
class AddPostOptionsDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding : FragmentAddPostOptionsDialogBinding
    private var isUserLogIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isUserLogIn = PreferenceHelper.getPreferences(requireContext()).getBoolean(PrefKeys.KEY_IS_USER_LOG_IN)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentAddPostOptionsDialogBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


           /* if (isUserLogIn == false) {
               Global.showLoginAlertMessage(requireActivity())
                binding.constraintOffering.visibility=View.GONE
            }else{
                binding.constraintOffering.visibility=View.VISIBLE
                binding.authRoot.root.visibility=View.GONE
            }*/

        binding.cardSellItem.setOnClickListener {

            if(isUserLogIn==false){
                Global.showLoginAlertMessage(requireActivity())
            }else{
                val direction = NavGraphDirections.actionGlobalNavGraphAddPost()
                findNavController().navigate(direction)
                dismiss()
            }

           /* val direction = NavGraphDirections.actionGlobalNavGraphAddPost()
            findNavController().navigate(direction)
            dismiss()*/
        }
        binding.cardOfferService.setOnClickListener {

            if(isUserLogIn==false){
                Global.showLoginAlertMessage(requireActivity())

            }else{
                val direction = NavGraphDirections.actionGlobalNavAddServiceFragment()
                findNavController().navigate(direction)
                dismiss()
            }

            /*val direction = NavGraphDirections.actionGlobalNavAddServiceFragment()
            findNavController().navigate(direction)
            dismiss()*/
        }
    }

    companion object {
        fun newInstance() = AddPostOptionsDialogFragment()
            }
    }


