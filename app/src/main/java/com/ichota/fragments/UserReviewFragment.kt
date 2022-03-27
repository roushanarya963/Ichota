package com.ichota.fragments

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.ColorFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HeaderViewListAdapter
import androidx.core.graphics.toColorFilter
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ichota.adapter.UserReviewsAdapter
import com.ichota.databinding.FragmentReviewPublicSubBinding
import com.ichota.interfaces.IMainActivity
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.viewModel.ProfileViewModel
import com.ichota.viewModel.TotalRatingItemViewMOdel
import okhttp3.MediaType.Companion.toMediaType
import okio.utf8Size
import java.text.DecimalFormat
import kotlin.math.roundToInt


class UserReviewFragment : Fragment()  {

    private lateinit var binding: FragmentReviewPublicSubBinding
    private var mIMainActivity: IMainActivity? = null
    private val totalReviewItemViewMOdel: TotalRatingItemViewMOdel by viewModels()
    private var mUserId: String? = null


    private var mUserReviewAdapter: UserReviewsAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUserReviewAdapter= UserReviewsAdapter()

        arguments?.let {
            mUserId = it.getString(Constants.KEY_USER_ID)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentReviewPublicSubBinding.inflate(inflater,container,false)
        return binding.root
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvReviews.itemAnimator = DefaultItemAnimator()

       // binding.rvReviews.setHasFixedSize(true)

        binding.rvReviews.adapter = mUserReviewAdapter

       /* totalReviewItemViewMOdel.getTotalReviewItem(
            mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: ""
        )*/

        totalReviewItemViewMOdel.getTotalReviewItem(
            mUserId ?: " "
        )

        setupObserver()

    }


    private fun setupObserver() {

        totalReviewItemViewMOdel.getMessageObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showMessage(it)
        }

        totalReviewItemViewMOdel.getProgressObserver.observe(viewLifecycleOwner) {

            if (it)
                binding.progressBar.visibility = View.VISIBLE
            else
                binding.progressBar.visibility = View.GONE
        }

        totalReviewItemViewMOdel.getNotificationObserver.observe(viewLifecycleOwner) { it ->



            binding.tvTotalRwview.text=it.totalUserRating

            binding.pbComnict.max=10
            binding.pbItmAsDesc.max=10
            binding.pbOnTmDlvPgb.max=10
            binding.pgReliable.max=10

            val currentprogress_comnictPgb: Float =it.totalCommunicationRating.toString().toFloat()
            val currentprogress_itmAsDescPgb: Float=it.totalItemAsDescribedRating.toString().toFloat()
            val currentprogress_onTmDlvPgb: Float =it.totalOnTimeDeliveryRating.toString().toFloat()
            val currentprogress_reliablePgb: Float=it.totalReliableRating.toString().toFloat()

            val numbers: FloatArray = floatArrayOf(currentprogress_comnictPgb, currentprogress_itmAsDescPgb, currentprogress_onTmDlvPgb, currentprogress_reliablePgb)
            val average: Double = numbers.average()

            binding.tvReviewByUser.text= DecimalFormat("##.#").format(average)
            binding.ratingBarByUser.rating= DecimalFormat("##.#").format(average).toFloat()

            binding.pbComnict.progressTintList=ColorStateList.valueOf(Color.parseColor("#11FA00"))
            binding.pbItmAsDesc.progressTintList=ColorStateList.valueOf(Color.parseColor("#03DAC5"))
            binding.pbOnTmDlvPgb.progressTintList=ColorStateList.valueOf(Color.parseColor("#FA9623"))
            binding.pgReliable.progressTintList=ColorStateList.valueOf(Color.RED)

            binding.pbComnict.setProgress(it.totalCommunicationRating.toFloat().roundToInt())
            binding.pbItmAsDesc.setProgress(it.totalItemAsDescribedRating.toFloat().roundToInt())
            binding.pbOnTmDlvPgb.setProgress(it.totalOnTimeDeliveryRating.toFloat().roundToInt())
            binding.pgReliable.setProgress(it.totalReliableRating.toFloat().roundToInt())
            mUserReviewAdapter?.setData(it.records)

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIMainActivity = context as IMainActivity
    }
    override fun onDetach() {
        super.onDetach()
        mIMainActivity = null
    }

    companion object{
        @JvmStatic
        fun newInstance(userId: String)= UserReviewFragment ().apply {
            arguments = Bundle().apply {
                putString(Constants.KEY_USER_ID,userId)
            }
        }
    }

}