package com.example.kendaraanbp1.ui.editvehicle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.kendaraanbp1.R
import com.example.kendaraanbp1.databinding.FragmentEditVehicleBinding
import com.example.kendaraanbp1.ui.common.viewmodel.SharedVehicleViewModel
import com.example.kendaraanbp1.ui.common.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class EditVehicleFragment : Fragment() {

    private var _binding: FragmentEditVehicleBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedVehicleViewModel by activityViewModels {
        ViewModelFactory(requireContext().applicationContext)
    }

    private val editVehicleViewModel: EditVehicleViewModel by viewModels {
        ViewModelFactory(requireContext().applicationContext)
    }

    private enum class VehicleType { CAR, MOTORCYCLE }
    private var selectedType = VehicleType.CAR

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEditVehicleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        setUpTypeSelectors()
        
        // Load initial data
        val vehicleId = sharedViewModel.selectedVehicleId.value
        if (vehicleId != null) {
            editVehicleViewModel.loadVehicle(vehicleId)
        } else {
            Toast.makeText(context, "Tidak ada kendaraan yang dipilih", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                editVehicleViewModel.vehicle.collect { vehicle ->
                    vehicle?.let {
                        binding.etBrand.setText(it.brand)
                        binding.etModel.setText(it.model)
                        binding.etPlate.setText(it.plateNumber)
                        // Selection logic
                        if (it.vehicleType == "Motor") {
                            selectType(VehicleType.MOTORCYCLE)
                        } else {
                            selectType(VehicleType.CAR)
                        }
                    }
                }
            }
        }

        binding.nextButton.setOnClickListener {
            val brand = binding.etBrand.text.toString().trim()
            val model = binding.etModel.text.toString().trim()
            val plate = binding.etPlate.text.toString().trim()
            val type = if (selectedType == VehicleType.CAR) "Mobil" else "Motor"
            
            if (brand.isEmpty() || model.isEmpty() || plate.isEmpty()) {
                Toast.makeText(context, "Harap lengkapi semua data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            editVehicleViewModel.updateVehicle(
                type = type,
                brand = brand,
                model = model,
                plateNumber = plate,
                year = editVehicleViewModel.vehicle.value?.year ?: 2024,
                onSuccess = {
                    Toast.makeText(context, "Kendaraan berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                },
                onError = { errorMessage ->
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
            )
        }

        binding.deleteButton.setOnClickListener {
            com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Hapus Kendaraan")
                .setMessage("Apakah Anda yakin ingin menghapus kendaraan ini beserta seluruh riwayatnya (BBM, Servis, Dokumen)?")
                .setPositiveButton("Hapus") { _, _ ->
                    editVehicleViewModel.deleteVehicle(
                        onSuccess = {
                            Toast.makeText(context, "Kendaraan berhasil dihapus", Toast.LENGTH_SHORT).show()
                            // Clear selected vehicle
                            sharedViewModel.selectVehicle(null)
                            // Navigate back to home or pop back stack
                            findNavController().popBackStack(R.id.homeDashboardFragment, false)
                        },
                        onError = { errorMessage ->
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                        }
                    )
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    private fun setUpTypeSelectors() {
        binding.vehicleTypeCar.setOnClickListener { selectType(VehicleType.CAR) }
        binding.vehicleTypeMotorcycle.setOnClickListener { selectType(VehicleType.MOTORCYCLE) }
        selectType(VehicleType.CAR)
    }

    private fun selectType(type: VehicleType) {
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
