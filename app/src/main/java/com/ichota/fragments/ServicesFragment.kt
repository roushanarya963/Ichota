package com.ichota.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ichota.R
import com.ichota.adapter.ServiceCategoriesAdapter
import com.ichota.databinding.FragmentServicesBinding
import com.ichota.interfaces.ICategoryClickListener
import com.ichota.interfaces.IMainActivity
import com.ichota.utils.Constants
import com.ichota.viewModel.ServiceViewModel

private const val TAG = "ServicesFragment"

class ServicesFragment : Fragment(), ICategoryClickListener {
    private lateinit var binding: FragmentServicesBinding
    private val serviceViewModel: ServiceViewModel by activityViewModels()
    private var mServiceCategoriesAdapter: ServiceCategoriesAdapter? = null
    private var mIMainActivity: IMainActivity? = null


    companion object {
        @JvmStatic
        fun newInstance() = ServicesFragment()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mServiceCategoriesAdapter = ServiceCategoriesAdapter()
        mServiceCategoriesAdapter?.onCategoryClickListener(this)
        Handler(Looper.getMainLooper()).postDelayed({
            serviceViewModel.getServiceCategories()
        }, 200)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentServicesBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()



    }


    override fun onResume() {

        binding.rvServices.layoutAnimation = AnimationUtils.loadLayoutAnimation(
            requireActivity(),
            R.anim.layout_anim_down_to_up
        )

        mServiceCategoriesAdapter?.notifyDataSetChanged()
        binding.rvServices.visibility = View.VISIBLE
        binding.rvServices.scheduleLayoutAnimation()
        super.onResume()


    }

    private fun setupObservers() {
        serviceViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showMessage(it)
        }
        serviceViewModel.getProgressObserver.observe(viewLifecycleOwner) {
              mIMainActivity?.showProgress(it)
        }

        serviceViewModel.getServiceCategoriesObserver.observe(viewLifecycleOwner) {

            /* binding.rvServices.layoutAnimation = AnimationUtils.loadLayoutAnimation(
                 requireActivity(),
                 R.anim.layout_anim_down_to_up
             )*/
            binding.rvServices.adapter = mServiceCategoriesAdapter
            mServiceCategoriesAdapter?.setData(it)

        }
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIMainActivity = context as IMainActivity
    }

    override fun onDetach() {
        super.onDetach()
      //  mIMainActivity = null
        mIMainActivity?.showProgress(false)
    }

    override fun onCategoryClick(catId: String, categoryType: String) {
        val direction = HomeFragmentDirections.actionNavGraphHomeToPostsListFragment(
            Constants.CATEGORY_SERVICE,
            catId
        )
        findNavController().navigate(direction)

    }
}