package com.ichota.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ichota.NavGraphDirections
import com.ichota.R
import com.ichota.adapter.FollowersAdapter
import com.ichota.databinding.FragmentFollowingBinding
import com.ichota.databinding.FragmentMyNetworkPagerBinding
import com.ichota.interfaces.IMainActivity
import com.ichota.model.User
import com.ichota.viewModel.MyNetworkViewModel


private const val TAG = "FollowingFragment"
private const val KEY_USER_ID = "key_user_id"
class FollowingFragment : Fragment(), FollowersAdapter.FollowerClickInterface {
    private lateinit var binding: FragmentFollowingBinding
    private val myNetworkViewModel: MyNetworkViewModel by viewModels()
    private var mFollowerAdapter: FollowersAdapter? = null
    private var mClickedPosition: Int = -1
    private var mIMainActivity: IMainActivity? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFollowerAdapter = FollowersAdapter()
        mFollowerAdapter?.onFollowerItemClickListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFollowingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val userId = it.getString(KEY_USER_ID)
            Handler().postDelayed({
                myNetworkViewModel.getUserFollowings(
                    userId?: ""
                )
            }, 500)

        }


        setupObserver()
    }

    private fun setupObserver() {
        myNetworkViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showMessage(it)

        }
        myNetworkViewModel.getProgressObserver.observe(viewLifecycleOwner) {

            /*binding.emptyFile.tvNoFollowing.text=getString(R.string.nofollowing)
            binding.emptyFile.tvNoFollowingDesc.text=getString(R.string.thatisabummerlistsomethingyou)*/

            binding.progressBar.root.visibility = if (it) View.VISIBLE else View.GONE

        }

        myNetworkViewModel.getFollowingsObserver.observe(viewLifecycleOwner) {

            binding.emptyFile.tvNoFollowing.text=getString(R.string.nofollowing)
            binding.emptyFile.tvNoFollowingDesc.text=getString(R.string.thatisabummerlistsomethingyou)
            binding.emptyFile.root.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            mFollowerAdapter?.setData(it)
            binding.rvFollowing.adapter = mFollowerAdapter

        }
        myNetworkViewModel.getRemoveItemObserver.observe(viewLifecycleOwner) {
            if (it) {
                mFollowerAdapter?.removeItem(mClickedPosition)

                if (mClickedPosition == 0 && mFollowerAdapter?.itemCount == 0) {
                    binding.emptyFile.root.visibility = View.VISIBLE
                }


            }
        }
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(userId : String) = FollowingFragment().apply {
            arguments = Bundle().apply {
                putString(KEY_USER_ID,userId)
            }
        }
    }

    override fun onItemRemoveClick(position: Int, user: User) {
        mClickedPosition = position
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.alert))
            .setMessage("Do you want to unfollow ${user.name}?")
            .setPositiveButton(getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()

            }
            .setNegativeButton(getString(R.string.unfollow)) { dialog, which ->
                myNetworkViewModel.unFollowUser(
                    mIMainActivity?.getPreference()?.getCurrentUser()?.userId ?: "",
                    user.userId
                )
            }
            .show()


    }

    override fun onItemClick(userId: String) {
        val direction = NavGraphDirections.actionGlobalNavPublicProfileFragment(userId)
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