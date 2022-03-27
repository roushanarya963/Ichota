package com.ichota.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.nfc.Tag
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.ichota.R
import com.ichota.databinding.FragmentAddBidBinding
import com.ichota.dialogs.ConfirmBidDialogFragment
import com.ichota.dialogs.ConfirmBidDialogFragmentDirections
import com.ichota.preferences.PrefKeys
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.viewModel.PostDetailViewModel

private const val KEY_PRODUCT_PRICE = "key_product_price"
private const val KEY_MIN_BID_PRICE = "key_min_bid_price"
private const val KEY_MAX_BID_PRICE = "key_max_bid_price"
private const val KEY_POST_ID = "key_post_id"

class AddBidFragment : Fragment() , ConfirmBidDialogFragment.IsConfirmDialogBidFragment{

    private lateinit var binding: FragmentAddBidBinding
    private val navArgs: AddBidFragmentArgs by navArgs()
    private val postDetailViewModel: PostDetailViewModel by viewModels()
    private var helper: PreferenceHelper? = null
    private var mPostId = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        helper = PreferenceHelper.getPreferences(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddBidBinding.inflate(inflater, container, false)
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productPrice = navArgs.productPrice
        val maxBidPrice = navArgs.maxBidAmount
        val postCoverImg = navArgs.productImage
        mPostId = navArgs.postId

        binding.tvProductPrice.text = getString(R.string.priceInDoller, productPrice)
        binding.tvCurrentBid.text = getString(R.string.priceInDoller, maxBidPrice.toString())



       if(maxBidPrice==0 ){
           binding.etUpdatePrice.setText(productPrice)
       }else{
           binding.etUpdatePrice.setText(maxBidPrice.toString())
       }

        if(maxBidPrice!=0){
            binding.btPlaceBid.setText(getString(R.string.updateyourbID))
        }

        Glide.with(requireContext())
            .load(postCoverImg)
            .error(R.drawable.app_logo)
            .placeholder(R.drawable.app_logo)
            .into(binding.ivPost)

        setupListeners()
        setupObserver()

    }




    private fun setupListeners() {

        binding.fabBack.setOnClickListener { requireActivity().onBackPressed() }
        binding.btPlaceBid.setOnClickListener {

            if (binding.etUpdatePrice.text.toString().trim().isEmpty()) {
                Global.showMessage(binding.root, getString(R.string.messageBidAmountRequired))
            } else {
                showConfirmBidDialogFragmnet()
            }

        }

    }

    private fun showConfirmBidDialogFragmnet() {
        val confirmBidDialogFragment=ConfirmBidDialogFragment.newInstance(mPostId,binding.etUpdatePrice.text.toString().trim())
        confirmBidDialogFragment.setOnIsConfirmDialogBidFragment(this)
        confirmBidDialogFragment.show(childFragmentManager,"TAG")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupObserver() {

        postDetailViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            binding.progressBar.root.visibility = if (it) View.VISIBLE else View.GONE

        }

        postDetailViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            Global.showMessage(binding.root, it)
        }

        postDetailViewModel.getAddToBidObserver.observe(viewLifecycleOwner) {

            if (it.success == Constants.RESPONSE_SUCCESS) {

                Global.showMessage(binding.root, it.message)

                val vibrator = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
            //    findNavController().navigateUp()
                 showSuccessDialog()

            } else {
                Global.showMessage(binding.root, it.message)
            }


        }

    }

    @SuppressLint("StringFormatMatches")
    private fun showSuccessDialog() {

        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_posted_successfully)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val btOkay = dialog.findViewById<Button>(R.id.bt_okay)
        val tvmessage=dialog.findViewById<TextView>(R.id.tv_thank_you_description)
            tvmessage.setText(getString(R.string.yourbidhasbeensuccessfullypost))
        btOkay.setOnClickListener {
            binding.tvCurrentBid.text=getString(R.string.priceInDoller,binding.etUpdatePrice.text.toString().trim().toDouble())
            findNavController().navigateUp()
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onConfirmDialogBidFragment(userId: String, postId: String, bidAmount: String) {
        postDetailViewModel.addToBid(userId, postId, bidAmount)
    }


}