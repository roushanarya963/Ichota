package com.ichota.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ichota.adapter.CategoryDialogAdapter
import com.ichota.databinding.DialogFragmentCategoryBinding
import com.ichota.model.SaleCategory

class CategoryDialogFragment : BottomSheetDialogFragment(),
    CategoryDialogAdapter.CategoryClickInterface {

    private lateinit var binding: DialogFragmentCategoryBinding
    private var mCategoryAdapter: CategoryDialogAdapter? = null
    private var mCallback: CategorySelectedInterface? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvCategory.adapter = mCategoryAdapter

    }


    fun setCategoryData(data: ArrayList<Any>) {
        Log.d("TAG", "setCategoryData: size = ${data.size} ")
        mCategoryAdapter = CategoryDialogAdapter()
        mCategoryAdapter?.setData(data)
        mCategoryAdapter?.onCategoryClickListener(this)
    }

    override fun onCategoryClick(catId: Any) {
        mCallback?.getSelectedCategory(catId)
        dismiss()
    }

    fun onCategorySelectedListener(callback: CategorySelectedInterface) {
        this.mCallback = callback
    }

    interface CategorySelectedInterface {
        fun getSelectedCategory(cateId: Any)
    }

    companion object{
        fun newInstance() = CategoryDialogFragment()
    }

}