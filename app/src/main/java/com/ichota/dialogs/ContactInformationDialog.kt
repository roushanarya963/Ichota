package com.ichota.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import com.ichota.databinding.DialogContactInformationBinding

class ContactInformationDialog(context: Context) :
    Dialog(context, android.R.style.ThemeOverlay_Material_Dialog) {
    private lateinit var binding: DialogContactInformationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogContactInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()


    }

    private fun initView() {
        binding.btCancel.setOnClickListener { dismiss() }
    }


    override fun onStart() {
        super.onStart()
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }


}