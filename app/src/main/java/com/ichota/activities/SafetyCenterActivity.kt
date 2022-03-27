package com.ichota.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.ichota.R
import com.ichota.adapter.SafetyBannersAdapter
import com.ichota.databinding.ActivitySafetyCenterBinding
import com.ichota.preferences.PreferenceHelper
import com.ichota.utils.Global
import com.ichota.viewModel.SafetyCenterViewModel

private const val TAG ="SafetyCenterActivity"
class SafetyCenterActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySafetyCenterBinding
    private val safetyCenterViewModel : SafetyCenterViewModel by viewModels()
    private lateinit var helper : PreferenceHelper
    private var mSafetyBannersAdapter : SafetyBannersAdapter?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySafetyCenterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        helper = PreferenceHelper.getPreferences(this)

        setupAdapter()
        setupObserver()
        getBanners()
        setupClickListener()
    }



    private fun getBanners(){
        safetyCenterViewModel.getSafetyBanners(
            helper.getCurrentUser()?.userId
        )
    }

    private fun setupAdapter() {
        mSafetyBannersAdapter = SafetyBannersAdapter()
        binding.rvSafetyTips.setHasFixedSize(true)
        binding.rvSafetyTips.adapter = mSafetyBannersAdapter
    }

    private fun setupObserver(){
        safetyCenterViewModel.getProgressObserver.observe(this){
            binding.progressBar.root.visibility = if(it) View.VISIBLE else View.GONE
        }
        safetyCenterViewModel.getMessageObserver.observe(this){
            Global.showMessage(binding.root,it)
        }

        safetyCenterViewModel.getSafetyBannersObserver.observe(this){
            mSafetyBannersAdapter?.setData(it)
        }


    }

    private fun setupClickListener() {
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

    }
}