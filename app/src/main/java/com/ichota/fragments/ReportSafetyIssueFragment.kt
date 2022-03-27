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
import com.ichota.R
import com.ichota.databinding.FragmentReportSafetyIssueBinding
import com.ichota.interfaces.IMainActivity
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.viewModel.SafetyCenterViewModel

private const val TAG = "ReportSafetyIssueActivity"

class ReportSafetyIssueFragment : Fragment() {
    private lateinit var binding: FragmentReportSafetyIssueBinding
    private val safetyCenterViewModel: SafetyCenterViewModel by viewModels()
    private val navArgs: ReportSafetyIssueFragmentArgs by navArgs()
    private var mIMainActivity: IMainActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportSafetyIssueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListener()
        setupObserver()
    }


    private fun setupObserver() {
        safetyCenterViewModel.getProgressObserver.observe(this) {
            binding.progressBar.root.visibility = if (it) View.VISIBLE else View.GONE
        }
        safetyCenterViewModel.getMessageObserver.observe(this) {
            Global.showMessage(binding.root, it)
        }

        safetyCenterViewModel.getReportSafetyObserver.observe(viewLifecycleOwner) {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(it.message)
                .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                    dialog.dismiss()
                    findNavController().navigateUp()
                }.show()
        }
    }

    private fun setupClickListener() {
        binding.topAppbAr.setNavigationOnClickListener {
            it.findNavController().navigateUp()
        }

        binding.btSubmit.setOnClickListener {
            sendSafetyReport()
        }

    }

    private fun sendSafetyReport() {
        val comments = binding.etReportSafetyDescription.text.toString().trim()

        if (comments.isEmpty()) {
            mIMainActivity?.showMessage(getString(R.string.messageAddComments))
            return
        }

        safetyCenterViewModel.reportSafetyIssue(
            mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: return,
            navArgs.remoteUserId,
            if (navArgs.postType == Constants.CATEGORY_SALE) navArgs.postId else "",
            if (navArgs.postType == Constants.CATEGORY_SERVICE) navArgs.postId else "",
            comments
        )
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