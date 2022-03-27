package com.ichota.fragments

import SwipeHelper
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ichota.NavGraphDirections
import com.ichota.R
import com.ichota.adapter.NotificationAdapter
import com.ichota.databinding.FragmentNotificationBinding
import com.ichota.interfaces.IMainActivity
import com.ichota.model.Notification
import com.ichota.preferences.PrefKeys
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Constants
import com.ichota.utils.Global
import com.ichota.viewModel.NotificationsViewModel

private const val TAG = "NotificationFragment"

class NotificationFragment : Fragment(), NotificationAdapter.INotification {

    private lateinit var binding: FragmentNotificationBinding
    private val notificationViewModel: NotificationsViewModel by viewModels()
    private var mNotificationAdapter: NotificationAdapter? = null
    private var mIMainActivity: IMainActivity? = null
    private var isUserLogIn = false
    private var toast: Toast? = null


    companion object {
        @JvmStatic
        fun newInstance() = NotificationFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isUserLogIn = PreferenceHelper.getPreferences(requireContext()).getBoolean(PrefKeys.KEY_IS_USER_LOG_IN)
        mNotificationAdapter = NotificationAdapter(requireContext())
        mNotificationAdapter?.setupOnNotificationClickListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // val decoration = DividerItemDecoration(requireActivity(), RecyclerView.VERTICAL)
        if(isUserLogIn==false){
          //  Global.showLoginAlertMessage(requireActivity())
            binding.rvNotifications.visibility=View.GONE
            binding.defaultAnim.visibility=View.VISIBLE
        }else{
            setupListener()
            setupObserver()
        }

    }


    private fun setupObserver() {

        notificationViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showMessage(it)

            if(it==context?.getString(R.string.messageCheckInternet)){
                binding.layouInternetConnection.root.visibility=View.VISIBLE
            }else{
                binding.layouInternetConnection.root.visibility=View.GONE
            }

        }

        notificationViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            if (it)
                binding.progressBar.root.visibility = View.VISIBLE
            else
                binding.progressBar.root.visibility = View.GONE
        }


        notificationViewModel.getNotificationObserver.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.defaultAnim.visibility = View.VISIBLE
                return@observe
            }
            mNotificationAdapter?.setData(it)
        }

    }


    private fun setupListener() {

        val decoration = DividerItemDecoration(requireActivity(), RecyclerView.VERTICAL)

        binding.fabBack.setOnClickListener {
            it.findNavController().popBackStack()
        }

        binding.rvNotifications.addItemDecoration(decoration)
        binding.rvNotifications.setHasFixedSize(true)
        //  binding.rvNotifications.adapter = NotificationAdapter()
    // Add by Arya
        val itemTouchHelper = ItemTouchHelper(object : SwipeHelper(binding.rvNotifications) {

          override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {
              var buttons = listOf<UnderlayButton>()
              val deleteButton = deleteButton(position)
              when (position) {
                  1 -> buttons = listOf(deleteButton)
                  else -> Unit
              }
              return buttons
          }


      })
        itemTouchHelper.attachToRecyclerView(binding.rvNotifications) // Add by Arya

        binding.rvNotifications.adapter = mNotificationAdapter
        notificationViewModel.getNotifications(mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: "")

    }
// Add by Arya
    private fun toast(text: String) {
        toast?.cancel()
        toast = Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT)
        toast?.show()
    }

    private fun deleteButton(position: Int) : SwipeHelper.UnderlayButton {
        return SwipeHelper.UnderlayButton(
            requireContext(),
            "Delete",
            14.0f,
            android.R.color.holo_red_light,
            object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    toast("Deleted item $position")
                }
            })
    }

    /*----------------*/

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIMainActivity = context as IMainActivity
    }

    override fun onDetach() {
        super.onDetach()
        mIMainActivity = null
    }



    override fun onNotificationClick(position: Int) {
        moveToPostDetail(position)
    }

    private fun moveToPostDetail(position: Int) {

        val notification : Notification?  = mNotificationAdapter?.getItemAt(position)

        notification?.let { notificationData ->

            if(notificationData.postType == Constants.CATEGORY_SALE){
                if (notificationData.BuyingOption.equals("Bid", true)) {
                    val direction =
                        NavGraphDirections.actionGlobalNavPostDetailBidFragment(
                            Constants.CATEGORY_SALE, notificationData.productId, notificationData.userId
                        )
                    findNavController().navigate(direction)
                } else {

                    val direction =
                        NavGraphDirections.actionGlobalNavPostDetailFragment(
                            Constants.CATEGORY_SALE, notificationData.productId ?: "", notificationData.userId
                        )
                    findNavController().navigate(direction)
                }

            }else{
                val directions =
                    NavGraphDirections.actionGlobalNavPostDetailFragment(
                        Constants.CATEGORY_SERVICE,
                        notificationData.productId,
                        notificationData.userId
                    )
                findNavController()
                    .navigate(directions)
            }
        }
    }


}