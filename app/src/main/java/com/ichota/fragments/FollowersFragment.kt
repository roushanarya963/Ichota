package com.ichota.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.ichota.databinding.FragmentFollowersBinding
import com.ichota.interfaces.IMainActivity
import com.ichota.model.User
import com.ichota.viewModel.MyNetworkViewModel


private const val TAG = "FollowersFragment"
private const val KEY_USER_ID = "key_user_id"

class FollowersFragment : Fragment(), FollowersAdapter.FollowerClickInterface {

    private lateinit var binding: FragmentFollowersBinding
    private val myNetworkViewModel: MyNetworkViewModel by viewModels()
    private var mFollowersAdapter: FollowersAdapter? = null
    private var mClickedPosition: Int = -1
    private var mIMainActivity: IMainActivity? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFollowersAdapter = FollowersAdapter()
        mFollowersAdapter?.onFollowerItemClickListener(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFollowersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val userId = it.getString(KEY_USER_ID)
            myNetworkViewModel.getUserFollowers(
                userId?:""
            )
        }

        setupObserver()
    }

    private fun setupObserver() {
        myNetworkViewModel.getProgressObserver.observe(viewLifecycleOwner) {
          binding.progressBar.root.visibility = if (it) View.VISIBLE else View.GONE

        }
        myNetworkViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showMessage(it)
        }
        myNetworkViewModel.getFollowersObserver.observe(viewLifecycleOwner) {

            binding.emptyFile.tvNoFollowing.text=getString(R.string.nofollower)
            binding.emptyFile.tvNoFollowingDesc.text=getString(R.string.go_exploresearchsomething)
            binding.emptyFile.root.visibility =
                if (it.isEmpty())
                    View.VISIBLE
                else
                    View.GONE
            Log.d(TAG, "setupObserver: Followers = ${it.size}")
            mFollowersAdapter?.setData(it)
            binding.rvFollowers.adapter = mFollowersAdapter

        }


        myNetworkViewModel.getRemoveItemObserver.observe(viewLifecycleOwner) {
            if (it) {
                mFollowersAdapter?.removeItem(mClickedPosition)
                if (mClickedPosition ==0 &&mFollowersAdapter?.itemCount ==0){
                    binding.emptyFile.root.visibility = View.VISIBLE
                }
            }
        }


    }

    companion object {
        @JvmStatic
        fun newInstance(userId : String) = FollowersFragment().apply {
            arguments = Bundle().apply {
                putString(KEY_USER_ID,userId)
            }
        }

    }

    override fun onItemRemoveClick(position: Int, user: User) {
        mClickedPosition = position
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.alert))
            .setMessage("Do you want to remove ${user.name}?")
            .setPositiveButton(getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()

            }
            .setNegativeButton(getString(R.string.remove)) { dialog, which ->
                myNetworkViewModel.removeFollower(
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