package com.ichota.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.ichota.R
import com.ichota.adapter.MarkAndSoldAdapter
import com.ichota.databinding.FragmentMarkAsSoldDialogBinding
import com.ichota.interfaces.IMainActivity
import com.ichota.model.UserChatData
import com.ichota.utils.Global
import com.ichota.viewModel.MyListingViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"


class MarkAsSoldDialogFragment : DialogFragment() , MarkAndSoldAdapter.UserChatListClickInterface {

    private var userId: String? = null
    private var productId: String? = null
    private var postId:String?=null
    private val myListingViewModel : MyListingViewModel by viewModels()
    private lateinit var binding : FragmentMarkAsSoldDialogBinding
    private var mMarkAndSoldAdapter : MarkAndSoldAdapter ? = null
    private var mIMainActivity: IMainActivity? = null
    private var status = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        mMarkAndSoldAdapter= MarkAndSoldAdapter()
        mMarkAndSoldAdapter?.onItemlistClickListener(this)
        arguments?.let {
            userId = it.getString(ARG_PARAM1)
            productId = it.getString(ARG_PARAM2)
            postId = it.getString(ARG_PARAM3)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMarkAsSoldDialogBinding.inflate(inflater,container,false)

        dialog?.let {
            val back = ColorDrawable(Color.TRANSPARENT)
            val inset = InsetDrawable(back, Global.getPxFromDp(32).toInt())
            it.window?.setBackgroundDrawable(inset)
            it.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            userId?.let { userId ->
                productId?.let { productId ->
                    myListingViewModel.markAndSold(userId, productId)
                }
            }

        setupListeners()
        setupObserver()
        setMakAsSoldObeserver()
    }

    private fun setMakAsSoldObeserver() {

        myListingViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showMessage(it)
            dismiss()
            findNavController().navigateUp()
        }
        myListingViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showProgress(it)
        }

        myListingViewModel.getMarkAsSoldObserver.observe(viewLifecycleOwner) {
            it?.let {

            }
        }
    }


    private fun setupListeners() {
        binding.fabBack.setOnClickListener { dismiss() }


           binding.tvMarkAsSold.setOnClickListener {
               if(status){

                   postId?.let { postId -> userId?.let { userId ->
                       myListingViewModel.markAsSold(postId, userId) }
                   }
                   dismiss()
                   /*Toast.makeText(requireContext(),"HelloBuddy",Toast.LENGTH_LONG).show()
                   Log.d("TAG", "setupListeners: ${5}")*/
               }
           }



    }
    private fun setupObserver() {
        myListingViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showProgress(it)
        }
        myListingViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showMessage(it)
        }
        myListingViewModel.getMarkAndSoldObserver.observe(viewLifecycleOwner) {
            binding.rvUserChatList.adapter = mMarkAndSoldAdapter
            mMarkAndSoldAdapter?.setData(it)
        }


       /* myListingViewModel.getMarkAsSoldObserver.observe(viewLifecycleOwner){
            mActiveAdsAdapter?.removeItem(mClickedPosition)
        }*/


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

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(userId: String, productId: String,postId:String) =
            MarkAsSoldDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, userId)
                    putString(ARG_PARAM2, productId)
                    putString(ARG_PARAM3,postId)
                }
            }
    }

    override fun onItemClick(userChatData: UserChatData) {
       binding.tvMarkAsSold.setTextColor(Color.parseColor("#1167B1"))
        status=true
    }
}