package com.ichota.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ichota.R
import com.ichota.databinding.AdapterVehicleTypeBinding
import com.ichota.model.VehicleType

class VehicleTypeAdapter : RecyclerView.Adapter<VehicleTypeAdapter.VehicleTypeViewHolder>() {

    private val vehicleTypes = arrayOf(
        VehicleType("Van", R.drawable.ic_van),
        VehicleType("Minivan", R.drawable.ic_mini_van),
        VehicleType("Car", R.drawable.ic_car, true),
        VehicleType("SUV", R.drawable.ic_suv),
        VehicleType("Truck", R.drawable.ic_truck),
        VehicleType("Others", R.drawable.ic_more),
    )


    inner class VehicleTypeViewHolder(private val binding: AdapterVehicleTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(vehicleType: VehicleType) {

            binding.ivVehicle.setImageResource(vehicleType.image)
            binding.tvVehicleName.text = vehicleType.name
            binding.root.isChecked = vehicleType.isSelected

            binding.root.setOnClickListener {
                for ((index, value) in vehicleTypes.withIndex()) {
                    value.isSelected = index == absoluteAdapterPosition
                }
                notifyItemRangeChanged(0, vehicleTypes.size - 1)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleTypeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterVehicleTypeBinding.inflate(inflater, parent, false)
        return VehicleTypeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VehicleTypeViewHolder, position: Int) {
        holder.bindView(vehicleTypes[position])
    }

    override fun getItemCount(): Int {
        return vehicleTypes.size
    }
}