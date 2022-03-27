package com.ichota.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.common.base.MoreObjects
import com.ichota.R
import com.ichota.databinding.DialogFragmentFilterBinding


class FilterDialogFragment(callback: ApplyFilterInterface) : BottomSheetDialogFragment() {
    private lateinit var binding: DialogFragmentFilterBinding
    private val buyingOptions = listOf("Pickup", "Bid")
    private val conditions = listOf("New", "Like New", "Good", "Fair", "Bad")
    private var buyingOptionAdapter: ArrayAdapter<String>? = null
    private var conditionsAdapter: ArrayAdapter<String>? = null
    private var mCallback: ApplyFilterInterface? = null


    init {
        mCallback = callback
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buyingOptionAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, buyingOptions)
        conditionsAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, conditions)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFragmentFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.autocompleteBuyingOptions.setAdapter(buyingOptionAdapter)
        binding.autocompleteConditions.setAdapter(conditionsAdapter)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btApplyFilter.setOnClickListener {
            validateFilterData()
        }
    }

    private fun validateFilterData() {

        if(binding.etMinimumPrice.text.toString().trim().isEmpty()){
          Toast.makeText(requireContext(),getString(R.string.pleaseenterminimumprice),Toast.LENGTH_LONG).show()
        }else if(binding.etMaximumPrice.text.toString().trim().isEmpty()){
           Toast.makeText(requireContext(),getString(R.string.pleaseentermaximumprice),Toast.LENGTH_LONG).show()
        }else if(binding.autocompleteBuyingOptions.text.toString().trim().isEmpty()){
            Toast.makeText(requireContext(),getString(R.string.pleaseselecteanyone),Toast.LENGTH_LONG).show()
        }else if(binding.autocompleteConditions.text.toString().trim().isEmpty()){
            Toast.makeText(requireContext(),getString(R.string.pleaseselectcondition),Toast.LENGTH_LONG).show()
        }else{

            val minPrice = binding.etMinimumPrice.text.toString().trim()
            val maxPrice = binding.etMaximumPrice.text.toString().trim()
            val buyingOptions = binding.autocompleteBuyingOptions.text.toString().trim()
            val condition = binding.autocompleteConditions.text.toString().trim()

            try{
                if (minPrice.toInt() > maxPrice.toInt()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.minPriceLessThanMax),
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
            } catch(ex : NumberFormatException){ // handle your exception

            }

            /* if (minPrice.toInt() > maxPrice.toInt()) {
                 Toast.makeText(
                     requireContext(),
                     getString(R.string.minPriceLessThanMax),
                     Toast.LENGTH_SHORT
                 ).show()
                 return
             }*/

            var distance = ""
            if (binding.sliderDistance.value.toInt() <= 0) {
                distance = ""
                binding.tvDistanceFinalValue.text=distance

            }
            distance = binding.sliderDistance.value.toInt().toString()

            binding.tvDistanceFinalValue.text=distance

            mCallback?.onApplyFilterClick(
                minPrice,
                maxPrice,
                buyingOptions,
                condition,
                distance
            )
            dismiss()
        }


       /* val minPrice = binding.etMinimumPrice.text.toString().trim()
        val maxPrice = binding.etMaximumPrice.text.toString().trim()
        val buyingOptions = binding.autocompleteBuyingOptions.text.toString().trim()
        val condition = binding.autocompleteConditions.text.toString().trim()

        try{
            if (minPrice.toInt() > maxPrice.toInt()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.minPriceLessThanMax),
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        } catch(ex : NumberFormatException){ // handle your exception

        }

       *//* if (minPrice.toInt() > maxPrice.toInt()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.minPriceLessThanMax),
                Toast.LENGTH_SHORT
            ).show()
            return
        }*//*

        var distance = ""
        if (binding.sliderDistance.value.toInt() <= 0) {
            distance = ""
            binding.tvDistanceFinalValue.text=distance

        }
        distance = binding.sliderDistance.value.toInt().toString()

        binding.tvDistanceFinalValue.text=distance

        mCallback?.onApplyFilterClick(
            minPrice,
            maxPrice,
            buyingOptions,
            condition,
            distance
        )
        dismiss()
*/      //  dismiss()

    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(callback: ApplyFilterInterface) = FilterDialogFragment(callback)

    }


    interface ApplyFilterInterface {
        fun onApplyFilterClick(
            minPrice: String,
            maxPrice: String,
            buyingOption: String,
            condition: String,
            distance: String


        )
    }
}