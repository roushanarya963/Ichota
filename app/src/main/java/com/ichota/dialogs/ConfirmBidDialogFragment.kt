package com.ichota.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.*
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ichota.NavGraphDirections
import com.ichota.R
import com.ichota.adapter.SalePostsAdapter
import com.ichota.databinding.DialogPostedSuccessfullyBinding
import com.ichota.databinding.FragmentConfirmBidDialogBinding
import com.ichota.fragments.PostDetailBidFragmentArgs
import com.ichota.preferences.PrefKeys
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.viewModel.PostDetailViewModel

class ConfirmBidDialogFragment : DialogFragment() {

     private lateinit var binding: FragmentConfirmBidDialogBinding
     private var helper: PreferenceHelper? = null
     private var val_postId: String? = null
     private var val_bidamount: String? = null
     private var mSalePostsAdapter: SalePostsAdapter? = null

     private var mCallBack : IsConfirmDialogBidFragment? = null

     var postId = "postid"
     var bidamountmax = "bidamtmax"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        helper=PreferenceHelper.getPreferences(requireContext())
        mSalePostsAdapter = SalePostsAdapter()
        arguments?.let {
            val_postId = it.getString(postId)
            val_bidamount = it.getString(bidamountmax)
        }
        Log.d("TAG", "onCreate:$val_bidamount+$val_postId ")
    }

    @SuppressLint("NewApi")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentConfirmBidDialogBinding.inflate(inflater, container, false)

        dialog?.let {
            val back = ColorDrawable(Color.TRANSPARENT)
            val inset = InsetDrawable(back, Global.getPxFromDp(32).toInt())
            it.window?.setBackgroundDrawable(inset)
            it.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        setupListeners()
        return binding.root

    }

    private fun setupListeners() {

      binding.tvBidAmount.text= getString(R.string.priceInDoller,val_bidamount)
      var userId=helper?.getCurrentUser()?.userId
      binding.btConfirmBid.setOnClickListener {


            mCallBack?.onConfirmDialogBidFragment(helper?.getCurrentUser()?.userId ?: "",
                val_postId.toString(), val_bidamount.toString()
            )

          Log.d("TAG", "setupListeners: $userId+$val_postId+$val_bidamount")

          dismiss()

        /*  val_bidamount?.let { val_bidamount ->
              val_postId?.let { val_postId ->
                  postDetailViewModel.addToBid(helper?.getCurrentUser()?.userId ?: "", val_postId, val_bidamount)
              }
          }*/


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
        @JvmStatic
        fun newInstance(postedId: String, bidamount: String) =
            ConfirmBidDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(postId, postedId)
                    putString(bidamountmax, bidamount)
                }
            }
    }

    fun setOnIsConfirmDialogBidFragment(callBack:IsConfirmDialogBidFragment){
        this.mCallBack=callBack
    }

    interface IsConfirmDialogBidFragment { fun onConfirmDialogBidFragment(userId:String,postId:String,bidAmount:String) }

}