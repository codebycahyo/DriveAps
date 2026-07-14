package com.example.kendaraanbp1.ui.addvehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.kendaraanbp1.R
import com.example.kendaraanbp1.databinding.FragmentAddVehicleBinding
import com.example.kendaraanbp1.ui.common.viewmodel.SharedVehicleViewModel
import com.example.kendaraanbp1.ui.common.viewmodel.ViewModelFactory

class AddVehicleFragment : Fragment() {

    private var _binding: FragmentAddVehicleBinding? = null
    private val binding get() = _binding!!

    private enum class VehicleType { CAR, MOTORCYCLE }

    private var selectedType = VehicleType.CAR

    private val sharedViewModel: SharedVehicleViewModel by activityViewModels {
        ViewModelFactory(requireContext().applicationContext)
    }

    private val addVehicleViewModel: AddVehicleViewModel by viewModels {
        ViewModelFactory(requireContext().applicationContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddVehicleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener { findNavController().popBackStack() }
        binding.cancelButton.setOnClickListener { findNavController().popBackStack() }

        binding.vehicleTypeCar.setOnClickListener { selectVehicleType(VehicleType.CAR) }
        binding.vehicleTypeMotorcycle.setOnClickListener { selectVehicleType(VehicleType.MOTORCYCLE) }

        binding.nextButton.setOnClickListener {
            val brand = binding.etBrand.text.toString().trim()
            val model = binding.etModel.text.toString().trim()
            val plate = binding.etPlate.text.toString().trim()
            val type = if (selectedType == VehicleType.CAR) "Mobil" else "Motor"
            
            if (brand.isEmpty() || model.isEmpty() || plate.isEmpty()) {
                Toast.makeText(context, "Harap lengkapi semua data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            addVehicleViewModel.saveVehicle(
                type = type,
                brand = brand,
                model = model,
                plateNumber = plate,
                year = 2024, // Assuming default or static for now since no UI field was requested specifically
                onSuccess = { id ->
                    sharedViewModel.selectVehicle(id)
                    findNavController().popBackStack()
                },
                onError = { errorMessage ->
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    private fun selectVehicleType(type: VehicleType) {
        if (selectedType == type) return
        selectedType = type

        val context = requireContext()
        val isCarSelected = type == VehicleType.CAR

        binding.vehicleTypeCar.setBackgroundResource(
            if (isCarSelected) R.drawable.bg_vehicle_type_selected else R.drawable.bg_vehicle_type_unselected
        )
        binding.vehicleTypeMotorcycle.setBackgroundResource(
            if (isCarSelected) R.drawable.bg_vehicle_type_unselected else R.drawable.bg_vehicle_type_selected
        )

        val selectedColor = ContextCompat.getColor(context, R.color.white)
        val unselectedColor = ContextCompat.getColor(context, R.color.text_secondary)

        binding.vehicleTypeCarIcon.imageTintList =
            ContextCompat.getColorStateList(context, if (isCarSelected) R.color.white else R.color.text_secondary)
        binding.vehicleTypeMotorcycleIcon.imageTintList =
            ContextCompat.getColorStateList(context, if (isCarSelected) R.color.text_secondary else R.color.white)

        binding.vehicleTypeCarLabel.setTextColor(if (isCarSelected) selectedColor else unselectedColor)
        binding.vehicleTypeMotorcycleLabel.setTextColor(if (isCarSelected) unselectedColor else selectedColor)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
