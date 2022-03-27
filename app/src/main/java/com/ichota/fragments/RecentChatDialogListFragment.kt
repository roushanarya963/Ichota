package com.ichota.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayout
import com.ichota.NavGraphDirections
import com.ichota.adapter.RecentChatDialogListAdapter
import com.ichota.adapterClasses.MyTabSelectorListener
import com.ichota.databinding.FragmentRecentChatDialogListBinding
import com.ichota.interfaces.IMainActivity
import com.ichota.model.GlobalResModel
import com.ichota.model.MessageType
import com.ichota.preferences.PrefKeys
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Global
import com.ichota.viewModel.ChatViewModel

class RecentChatDialogListFragment : Fragment(), RecentChatDialogListAdapter.IChatClick {
    internal lateinit var binding: FragmentRecentChatDialogListBinding
    internal var messageAdapterDialog: RecentChatDialogListAdapter? = null
    internal val chatViewModel: ChatViewModel by activityViewModels()
    private var mIMainActivity: IMainActivity? = null
    private var isUserLogIn = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isUserLogIn = PreferenceHelper.getPreferences(requireContext()).getBoolean(PrefKeys.KEY_IS_USER_LOG_IN)
        messageAdapterDialog = RecentChatDialogListAdapter(requireContext())
        messageAdapterDialog?.setOnChatClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        chatViewModel.getRecentChatDialogs(mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: "")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecentChatDialogListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tabLayoutChats.addOnTabSelectedListener(object : MyTabSelectorListener() {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                super.onTabSelected(tab)
                when (tab?.position) {
                    0 -> {
                        messageAdapterDialog?.setData(chatViewModel.getAllChatDialogs)
                        binding.emptyFile.visibility =
                            if (chatViewModel.getAllChatDialogs.isEmpty()) View.VISIBLE
                            else View.GONE
                    }
                    1 -> {
                        messageAdapterDialog?.setData(chatViewModel.getUnreadChatDialogs)
                        binding.emptyFile.visibility =
                            if (chatViewModel.getUnreadChatDialogs.isEmpty()) View.VISIBLE
                            else View.GONE
                    }
                }
            }
        })


        if(isUserLogIn==false){



           binding.constraintlayoutEmptyfile.visibility=View.VISIBLE
           binding.constraintlayoutRecentmessage.visibility=View.GONE

        }else{
            setupObserver()
            setupListeners()
        }

        /*setupObserver()
        setupListeners()*/

    }

    private fun setupListeners() {
        binding.tvMarkAsRead.setOnClickListener {
            chatViewModel.markAllAsRead(
                mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: ""
            )
        }

    }

    private fun setupObserver() {
        chatViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showMessage(it)

        }
        chatViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showProgress(it)
        }

        chatViewModel.getMarkAsReadObserver.observe(viewLifecycleOwner) {

            chatViewModel.getRecentChatDialogs(
                mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: "",
            )
        }

        chatViewModel.getRecentChatDialogObserver.observe(viewLifecycleOwner) {
            binding.emptyFile.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            messageAdapterDialog?.setData(it)
            binding.rvMessages.adapter = messageAdapterDialog
            binding.tabLayoutChats.getTabAt(0)?.select()

        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = RecentChatDialogListFragment()
    }



    override fun onOpenChat(position: Int,postType : String) {

        val direction = NavGraphDirections.actionGlobalNavChatFragment(
            messageAdapterDialog?.getItemAt(position), MessageType.TYPE_MESSAGE,postType
        )
        findNavController().navigate(direction)
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