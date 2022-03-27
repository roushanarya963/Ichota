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
import com.ichota.adapter.SaleCategoriesAdapter
import com.ichota.databinding.FragmentSalesBinding
import com.ichota.interfaces.ICategoryClickListener
import com.ichota.interfaces.IMainActivity
import com.ichota.utils.Constants
import com.ichota.viewModel.SaleViewModel


private const val TAG = "SalesFragment"

class SalesFragment : Fragment(), ICategoryClickListener {

    private lateinit var binding: FragmentSalesBinding
    private val saleViewModel: SaleViewModel by activityViewModels()
    private var mSaleCategoriesAdapter: SaleCategoriesAdapter? = null
    private var mIMainActivity: IMainActivity? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mSaleCategoriesAdapter = SaleCategoriesAdapter()
        mSaleCategoriesAdapter?.onCategoryClickListener(this)

        Handler(Looper.getMainLooper()).postDelayed({
            saleViewModel.getSaleCategories()
        }, 1000)


    }

    override fun onResume() {

        binding.rvSales.layoutAnimation = AnimationUtils.loadLayoutAnimation(
            requireActivity(),
            R.anim.layout_anim_down_to_up
        )

        mSaleCategoriesAdapter?.notifyDataSetChanged()
        binding.rvSales.visibility = View.VISIBLE
        binding.rvSales.scheduleLayoutAnimation()

        super.onResume()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSalesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
    }

    private fun setupObserver() {
        saleViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showProgress(it)

        }
        saleViewModel.getSaleResObserver.observe(viewLifecycleOwner) {
            binding.rvSales.adapter = mSaleCategoriesAdapter
            mSaleCategoriesAdapter?.loadData(it)
        }
        saleViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showMessage(it)
        }
    }

    override fun onPause() {
        binding.rvSales.visibility = View.GONE
        super.onPause()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIMainActivity = context as IMainActivity
    }

    override fun onDetach() {
        super.onDetach()
        mIMainActivity = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = SalesFragment()


    }

    override fun onCategoryClick(catId: String, categoryType: String) {

        val direction = HomeFragmentDirections.actionNavGraphHomeToPostsListFragment(
            Constants.CATEGORY_SALE,
            catId
        )
        findNavController().navigate(direction)
    }


}