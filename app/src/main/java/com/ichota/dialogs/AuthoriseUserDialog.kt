package com.ichota.dialogs

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.ichota.auth.LoginActivity
import com.ichota.databinding.DialogAuthoriseUserBinding

class AuthoriseUserDialog : DialogFragment() {
    private lateinit var binding: DialogAuthoriseUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog?.window?.requestFeature(
            Window.FEATURE_NO_TITLE
        )
       // setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogAuthoriseUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
       // dialog?.setCancelable(false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btMoveToLogin.setOnClickListener {
            Intent(requireActivity(), LoginActivity::class.java).also {
                startActivity(it)
                requireActivity().finishAffinity()
            }
        }
    }

    companion object {
        fun newInstance() = AuthoriseUserDialog()
    }
}