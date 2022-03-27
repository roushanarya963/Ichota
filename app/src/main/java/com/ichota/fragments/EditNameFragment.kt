package com.ichota.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.ichota.R
import com.ichota.databinding.FragmentEditNameBinding
import com.ichota.interfaces.IMainActivity
import com.ichota.utils.Global
import com.ichota.viewModel.ProfileViewModel


private const val TAG = "EditNameFragment"

class EditNameFragment : Fragment() {
    private lateinit var binding: FragmentEditNameBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private val navArgs: EditNameFragmentArgs by navArgs()
    private var mIMainActivity: IMainActivity? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEditNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etName.setText(navArgs.name)
        setupObserver()
        setupListener()
    }

    private fun setupObserver() {
        profileViewModel.getProgressObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showProgress(it)

        }
        profileViewModel.getMessageObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.showMessage(it)

        }
        profileViewModel.getUpdateUserDetailObserver.observe(viewLifecycleOwner) {
            mIMainActivity?.getPreference()?.saveCurrentUser(it)


        }
    }

    private fun setupListener() {

        binding.fabBack.setOnClickListener {
            it.findNavController().navigateUp()
        }

        binding.btSave.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        val name = binding.etName.text.toString().trim()
        if (name.isEmpty()) {
            Global.showMessage(binding.root, getString(R.string.messageNameRequired))
            return
        }

        val request = HashMap<String,String>()
        request["user_id"] = mIMainActivity?.getPreference()?.getCurrentUser()?.userId?:""
        request["name"] =name
        mIMainActivity?.hideSoftKeyboard(binding.root)
        profileViewModel.updateUserDetail(request)




    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = EditNameFragment()

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