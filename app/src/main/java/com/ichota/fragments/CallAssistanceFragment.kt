package com.ichota.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ichota.R
import com.ichota.databinding.CallAssistanceFragmentBinding


private const val TAG=""
class CallAssistanceFragment : Fragment() {

    private lateinit var binding: CallAssistanceFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= CallAssistanceFragmentBinding.inflate(inflater,container,false)
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListener()
    }

    private fun setupClickListener() {
        binding.topAppBar.setNavigationOnClickListener {
            it.findNavController().navigateUp()
        }



        binding.callAssisSosLottiAnim.setOnLongClickListener {

           MaterialAlertDialogBuilder(requireContext())
                .setMessage(getString(R.string.call911))
                .setPositiveButton(getString(R.string.Call)){dialog, _ ->
                    dialog.dismiss()
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:911")
                    startActivity(intent)
                }
                .setNegativeButton(getString(R.string.cancel)){dialog,_ ->
                    dialog.dismiss()

                }.show()

            return@setOnLongClickListener true

        }





        }

}