package com.ichota.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ichota.databinding.ShareLocationBottomSheetDialogFragmentBinding


class ShareLocationBottomSheetDialog : BottomSheetDialogFragment() {

   private lateinit var binding: ShareLocationBottomSheetDialogFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return  super.onCreateDialog(savedInstanceState).apply {
            // window?.setDimAmount(0.2f) // Set dim amount here
            setOnShowListener {
                val bottomSheet = findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                bottomSheet.setBackgroundResource(android.R.color.transparent)
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= ShareLocationBottomSheetDialogFragmentBinding.inflate(inflater,container,false)
        return binding.root
     //   return inflater.inflate(R.layout.share_location_bottom_sheet_dialog_fragment, container, false)
    }


}