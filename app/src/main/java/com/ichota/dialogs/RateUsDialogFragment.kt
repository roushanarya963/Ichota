package com.ichota.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ichota.R
import com.ichota.databinding.DialogPostedSuccessfullyBinding
import com.ichota.databinding.FragmentRateUsDialogBinding


class RateUsDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentRateUsDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rate_us_dialog, container, false)
        binding = FragmentRateUsDialogBinding.inflate(inflater,container,false)
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            RateUsDialogFragment().apply {

            }
    }
}