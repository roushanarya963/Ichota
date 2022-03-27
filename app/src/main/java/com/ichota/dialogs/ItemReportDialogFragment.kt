package com.ichota.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.ichota.R
import com.ichota.databinding.DialogFragmentItemReportBinding
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.utils.ReportType
import com.ichota.viewModel.ReportViewModel

private const val TAG = "ItemReportDialogFragment"


class ItemReportDialogFragment : DialogFragment() {
    private lateinit var binding: DialogFragmentItemReportBinding
    private val reportViewModel: ReportViewModel by viewModels()
    private var reportType = ReportType.REPORT_TYPE_OTHER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFragmentItemReportBinding.inflate(inflater, container, false)
        dialog?.let {
            val back = ColorDrawable(Color.TRANSPARENT)
            val inset = InsetDrawable(back, Global.getPxFromDp(32).toInt())
            it.window?.setBackgroundDrawable(inset)
            it.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupObserver()
    }


    private fun setupListeners() {
        binding.btCancel.setOnClickListener { dismiss() }
        binding.btSend.setOnClickListener { sendReport() }
        binding.rgReport.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_offensive_content -> reportType = ReportType.REPORT_TYPE_OFFENSIVE_CONTENT
                R.id.rb_fraud -> reportType = ReportType.REPORT_TYPE_FRAUD
                R.id.rb_duplicate_ad -> reportType = ReportType.REPORT_TYPE_DUPLICATE_AD
                R.id.rb_product_already_sold -> reportType =
                    ReportType.REPORT_TYPE_PRODUCT_ALREADY_SOLD
                R.id.rb_other -> reportType = ReportType.REPORT_TYPE_OTHER
            }
        }
    }

    private fun sendReport() {
        val comments = binding.etComments.text.toString().trim()
        if (TextUtils.isEmpty(comments)) Global.showMessage(
            binding.root,
            getString(R.string.messageCommentsRequired)
        ) else {

            arguments?.let {
                val postType = it.getString(Constants.KEY_POST_TYPE)
                reportViewModel.reportPost(
                    it.getString(Constants.KEY_USER_ID, ""),
                    if (postType == Constants.CATEGORY_SERVICE)it.getString(Constants.KEY_POST_ID)?:return else "",
                    if (postType == Constants.CATEGORY_SALE)it.getString(Constants.KEY_POST_ID)?:return else "",
                    reportType,
                    comments

                )
            }


        }


    }

    private fun setupObserver() {

        reportViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            Global.showMessage(binding.root, it)
        }

        reportViewModel.getReportObserver.observe(viewLifecycleOwner) {
            if (it.success == Constants.RESPONSE_SUCCESS) {
                Global.showMessage(binding.root, it.message)
                Handler().postDelayed(
                    {
                        dismiss()
                    },
                    1000
                )

            } else {
                Global.showMessage(binding.root, it.message)
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
        fun newInstance(
            postId: String,
            userId: String,
            postType: String
        ) = ItemReportDialogFragment().apply {
            arguments = Bundle().apply {
                putString(Constants.KEY_POST_ID, postId)
                putString(Constants.KEY_USER_ID, userId)
                putString(Constants.KEY_POST_TYPE, postType)
            }

        }
    }
}