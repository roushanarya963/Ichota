package com.ichota.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.DialogFragment
import com.ichota.R
import com.ichota.databinding.FragmentAddAddressDialogBinding
import com.ichota.databinding.FragmentAddEmergencyContactDialogBinding
import com.ichota.preferences.PrefKeys
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Global

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AddAddressDialogFragment : DialogFragment() {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var helpers:PreferenceHelper?=null

    private lateinit var binding: FragmentAddAddressDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentAddAddressDialogBinding.inflate(inflater,container,false)
        dialog?.let {
            val back= ColorDrawable(Color.TRANSPARENT)
            val inset = InsetDrawable(back, Global.getPxFromDp(32).toInt())
            it.window?.setBackgroundDrawable(inset)
            it.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }
        return binding.root
       // return inflater.inflate(R.layout.fragment_add_address_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        helpers=PreferenceHelper.getPreferences(requireContext())

        var saveaddress= helpers?.getStringValue(PrefKeys.KEY_ADD_ADDRESS)
        if (saveaddress != null) {
            binding.etPhone.setText(saveaddress)

        }

        setListener()
    }

    private fun setListener() {

        binding.btCancel.setOnClickListener {
            dismiss()
        }

        binding.btSubmit.setOnClickListener {

            if(binding.etPhone.text.toString().trim().isEmpty()){
                Toast.makeText(requireContext(),getString(R.string.pleaseaddaddress),
                    Toast.LENGTH_LONG).show()
            }else{
                helpers?.saveStringValue(PrefKeys.KEY_ADD_ADDRESS,binding.etPhone.text.toString().trim())
                Toast.makeText(requireContext(),getString(R.string.addressaddedsuccessfully),
                    Toast.LENGTH_LONG).show()
                dismiss()
            }

        }

    }


    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCancelable(false)
    }

    companion object {
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
            AddAddressDialogFragment().apply {
               /* arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }*/
            }
    }


}