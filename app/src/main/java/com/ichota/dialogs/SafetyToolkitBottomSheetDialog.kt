package com.ichota.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ichota.databinding.DialogBottomSheetSafetyToolkitBinding
import com.ichota.utils.Extensions.addOnClickListener

private const val TAG= "SafetyToolkitBottomSheetDialog"
class SafetyToolkitBottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var binding: DialogBottomSheetSafetyToolkitBinding

    private var mCallback: ISafetyToolkitSheet? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBottomSheetSafetyToolkitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListener()
    }

    private fun setupClickListener() {

        binding.btGotIt.setOnClickListener {
            dismiss()
        }

        binding.gpSafetyCenter.addOnClickListener {
            Log.d(TAG, "setupClickListener: ")
            dismiss()
            mCallback?.onClickSafetyCenter()
        }
        binding.gpShareLocation.addOnClickListener {
            dismiss()
            mCallback?.onClickShareLocation()
        }
        binding.gpReportSafetyIssue.addOnClickListener {
            dismiss()
            mCallback?.onClickReportSafetyIssue()
        }

        binding.gpCallAssistance.addOnClickListener {
            dismiss()
            mCallback?.onClickCallAssistance()
        }

    }


    fun setOnSafetyToolkitClickListener(callback: ISafetyToolkitSheet) {
        this.mCallback = callback
    }

    interface  ISafetyToolkitSheet {
        fun onClickSafetyCenter()
        fun onClickShareLocation()
        fun onClickReportSafetyIssue()
        fun onClickCallAssistance()

    }

}