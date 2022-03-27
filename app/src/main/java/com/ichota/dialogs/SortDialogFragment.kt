package com.ichota.dialogs

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ichota.R
import com.ichota.databinding.DialogFragmentSortBinding
import com.ichota.interfaces.IMainActivity
import com.ichota.preferences.PrefKeys
import com.ichota.utils.Sort

private const val TAG = "SortDialogFragment"

class SortDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: DialogFragmentSortBinding
    private var mListener: SortOptionClickListener? = null
    private var mIMainActivity: IMainActivity? = null

    private var newest_post_item: String? = null
    private var closest_post_item:String? = null
    private var low_to_high:String?= null
    private var high_to_low:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFragmentSortBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when(mIMainActivity?.getPreference()?.getStringValue(PrefKeys.KEY_SORT_BY)){
            Sort.NEWEST ->binding.rbNewest.isChecked = true
            Sort.CLOSEST ->binding.rbClosest.isChecked = true
            Sort.LOW_TO_HIGH ->binding.rbLowToHigh.isChecked = true
            Sort.HIGH_TO_LOW ->binding.rbHighToLow.isChecked = true
        }


        binding.btBack.setOnClickListener { dismiss() }
        binding.rgSort.setOnCheckedChangeListener { group, checkedId ->
            Log.d(TAG, "onViewCreated: $checkedId")
            when (checkedId) {

                R.id.rb_newest -> {
                    mListener?.onSortOptionClick(Sort.NEWEST)
                   // dismiss()
                    mIMainActivity?.getPreference()?.saveStringValue(PrefKeys.KEY_SORT_BY, Sort.NEWEST)

                    newest_post_item= mIMainActivity?.getPreference()?.getStringValue(PrefKeys.KEY_SORT_BY)


                    dismiss()
                }
                R.id.rb_closest -> {
                    mListener?.onSortOptionClick(Sort.CLOSEST)
                  //  dismiss()
                    mIMainActivity?.getPreference()?.saveStringValue(PrefKeys.KEY_SORT_BY, Sort.CLOSEST)

                    closest_post_item= mIMainActivity?.getPreference()?.getStringValue(PrefKeys.KEY_SORT_BY).toString()

                    dismiss()
                }
                R.id.rb_low_to_high -> {
                    mListener?.onSortOptionClick(Sort.LOW_TO_HIGH)
                        //  dismiss()
                    mIMainActivity?.getPreference()?.saveStringValue(PrefKeys.KEY_SORT_BY, Sort.LOW_TO_HIGH)
                    low_to_high= mIMainActivity?.getPreference()?.getStringValue(PrefKeys.KEY_SORT_BY).toString()

                    dismiss()
                }
                R.id.rb_high_to_low -> {
                    mListener?.onSortOptionClick(Sort.HIGH_TO_LOW)
                  //  dismiss()
                    mIMainActivity?.getPreference()?.saveStringValue(PrefKeys.KEY_SORT_BY, Sort.HIGH_TO_LOW)

                    high_to_low= mIMainActivity?.getPreference()?.getStringValue(PrefKeys.KEY_SORT_BY).toString()


                    dismiss()
                }
                else -> Toast.makeText(requireContext(), "else part", Toast.LENGTH_SHORT).show()

            }



        }

    }

    override fun onResume() {
        super.onResume()
        /*setCheck(
            mIMainActivity?.getPreference()?.getStringValue(PrefKeys.KEY_SORT_BY) ?: Sort.NEWEST
        )*/
    }

    fun setListener(mListener: SortOptionClickListener) {
        this.mListener = mListener
    }

    interface SortOptionClickListener {
        fun onSortOptionClick(sort: String)
    }

    companion object {
        @JvmStatic
        fun newInstance(): SortDialogFragment = SortDialogFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mIMainActivity = context as IMainActivity
    }

    override fun onDetach() {
        super.onDetach()
        mIMainActivity = null
    }

    private fun setCheck(value: String) {
        when (value) {
            Sort.NEWEST -> binding.rbNewest.isChecked = true
            Sort.CLOSEST -> binding.rbClosest.isChecked = true
            Sort.LOW_TO_HIGH -> binding.rbLowToHigh.isChecked = true
            Sort.HIGH_TO_LOW -> binding.rbHighToLow.isChecked = true
        }

    }
}