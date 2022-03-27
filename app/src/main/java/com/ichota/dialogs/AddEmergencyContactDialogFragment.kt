package com.ichota.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.DialogFragment
import com.ichota.R
import com.ichota.databinding.FragmentAddEmergencyContactDialogBinding
import com.ichota.preferences.PrefKeys
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Global
import com.ichota.utils.Global.getPxFromDp

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AddEmergencyContactDialogFragment : DialogFragment() {

    private  lateinit var binding : FragmentAddEmergencyContactDialogBinding

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var helpers:PreferenceHelper?=null
    private var phonenumber:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       /* arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }*/

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAddEmergencyContactDialogBinding.inflate(inflater,container,false)

        dialog?.let {
            val back= ColorDrawable(Color.TRANSPARENT)
            val inset = InsetDrawable(back, Global.getPxFromDp(32).toInt())
            it.window?.setBackgroundDrawable(inset)
            it.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }
        return binding.root
       // return inflater.inflate(R.layout.fragment_add_emergency_contact_dialog, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        helpers=PreferenceHelper.getPreferences(requireContext())
     //   phonenumber = binding.etPhone.text.toString().trim()
        var savedphonenumber= helpers?.getStringValue(PrefKeys.KEY_ADD_EMERGENCY_NUMBER)
        if (savedphonenumber != null) {
           binding.etPhone.setText(savedphonenumber)
            binding.ccp.textView_selectedCountry.setText(helpers?.getStringValue(PrefKeys.KEY_SELECT_COUNTRY_CODE))

        }
        setListener()

    }

    private fun setListener() {

        binding.btCancel.setOnClickListener {
            dismiss()
        }

        binding.btSubmit.setOnClickListener {

          if(binding.etPhone.text.toString().trim().isEmpty()){
              Toast.makeText(requireContext(),getString(R.string.pleaseenteremergencycontactnumber),Toast.LENGTH_LONG).show()
          }else{
              helpers?.saveStringValue(PrefKeys.KEY_ADD_EMERGENCY_NUMBER,binding.etPhone.text.toString().trim())
              helpers?.saveStringValue(PrefKeys.KEY_SELECT_COUNTRY_CODE,binding.ccp.selectedCountryCodeWithPlus.toString().trim())
              Toast.makeText(requireContext(),getString(R.string.emergencynumberupdatesuccessfully),Toast.LENGTH_LONG).show()
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

        fun newInstance(/*param1: String, param2: String*/) =
            AddEmergencyContactDialogFragment().apply {
                /*arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }*/
            }
    }



}