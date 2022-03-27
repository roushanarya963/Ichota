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
import com.ichota.databinding.DialogFragmentUserReportBinding
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.utils.ReportType
import com.ichota.viewModel.ReportViewModel

class UserReportDialogFragment : DialogFragment() {

    private lateinit var binding: DialogFragmentUserReportBinding
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
        binding = DialogFragmentUserReportBinding.inflate(inflater, container, false)

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
            reportType = when (checkedId) {
                R.id.rb_inappropriate_profile_picture -> ReportType.REPORT_TYPE_INAPPROPRIATE_PROFILE_PICTURE
                R.id.rb_threatening_me -> ReportType.REPORT_TYPE_USER_THREATENING_ME
                R.id.rb_insulting_me -> ReportType.REPORT_TYPE_USER_INSULTING_ME
                R.id.rb_spam -> ReportType.REPORT_TYPE_SPAM
                R.id.rb_Userfraud -> ReportType.REPORT_TYPE_FRAUD
                else -> ReportType.REPORT_TYPE_OTHER
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
                reportViewModel.reportUser(
                    it.getString(Constants.KEY_USER_ID, ""),
                    it.getString(Constants.KEY_REMOTE_USER_ID, ""),
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

        reportViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
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
            userId: String,
            remoteUserId: String
        ) = UserReportDialogFragment().apply {
            arguments = Bundle().apply {
                putString(Constants.KEY_USER_ID, userId)
                putString(Constants.KEY_REMOTE_USER_ID, remoteUserId)
            }

        }
    }
}