package com.ichota.fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ichota.R
import com.ichota.databinding.SosCallLongPressedEventFragmentBinding
import com.ichota.dialogs.SafetyToolkitBottomSheetDialog


class SosCallLongPressedEventFragment(context: Context) : BottomSheetDialogFragment() {

    private lateinit var binding : SosCallLongPressedEventFragmentBinding
    private lateinit var mContext:Context



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= SosCallLongPressedEventFragmentBinding.inflate(inflater,container,false)
        setupClickListener()
        return binding.root
      //  return inflater.inflate(R.layout.sos_call_long_pressed_event_fragment, container, false)
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

    override fun onStart() {
        super.onStart()
        dialog?.let {
            it.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.bg_inset_16_all_side))
        }
    }
    private fun setupClickListener() {
        binding.btnCall.setOnClickListener{
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:911")
            startActivity(intent)
        }
        binding.btnCancel.setOnClickListener{
            dismiss()
        }
    }

    fun setOnSoscallEventFragmentBottom(callback: Context) {
        mContext=callback
    }


}