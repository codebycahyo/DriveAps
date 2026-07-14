package com.example.kendaraanbp1.ui.addservice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.kendaraanbp1.databinding.FragmentAddServiceEntryBinding
import com.example.kendaraanbp1.ui.common.ExpandedBottomSheetDialogFragment
import com.example.kendaraanbp1.ui.common.viewmodel.SharedVehicleViewModel
import com.example.kendaraanbp1.ui.common.viewmodel.ViewModelFactory

class AddServiceEntryFragment : ExpandedBottomSheetDialogFragment() {

    private var _binding: FragmentAddServiceEntryBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedVehicleViewModel by activityViewModels {
        ViewModelFactory(requireContext().applicationContext)
    }

    private val addServiceViewModel: AddServiceViewModel by viewModels {
        ViewModelFactory(requireContext().applicationContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddServiceEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeButton.setOnClickListener { dismiss() }

        val editId = arguments?.getLong(ARG_ID, -1L) ?: -1L
        if (editId != -1L) {
            binding.etCategory.setText(arguments?.getString(ARG_CATEGORY))
            binding.etWorkshop.setText(arguments?.getString(ARG_WORKSHOP))
            binding.etOdometer.setText(arguments?.getInt(ARG_ODOMETER).toString())
            binding.etCost.setText(arguments?.getDouble(ARG_COST)?.toLong().toString())
            binding.saveButton.text = "Simpan Perubahan"
        }

        binding.saveButton.setOnClickListener {
            val categoryStr = binding.etCategory.text.toString().trim()
            val workshopStr = binding.etWorkshop.text.toString().trim()
            val odometerStr = binding.etOdometer.text.toString().trim()
            val costStr = binding.etCost.text.toString().trim()
            
            if (categoryStr.isEmpty() || workshopStr.isEmpty() || odometerStr.isEmpty() || costStr.isEmpty()) {
                Toast.makeText(context, "Harap lengkapi semua data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val odometer = odometerStr.toIntOrNull() ?: 0
            val cost = costStr.toDoubleOrNull() ?: 0.0
            val vehicleId = sharedViewModel.selectedVehicleId.value
            
            if (vehicleId == null) {
                Toast.makeText(context, "Tidak ada kendaraan aktif", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (editId != -1L) {
                val originalDate = arguments?.getLong(ARG_DATE) ?: System.currentTimeMillis()
                val nextServiceDate = arguments?.getLong(ARG_NEXT_DATE).takeIf { it != -1L }
                val nextServiceOdo = arguments?.getInt(ARG_NEXT_ODO).takeIf { it != -1 }

                addServiceViewModel.updateServiceEntry(
                    id = editId,
                    vehicleId = vehicleId,
                    originalDate = originalDate,
                    category = categoryStr,
                    workshopName = workshopStr,
                    odometer = odometer,
                    totalCost = cost,
                    originalNextServiceDate = nextServiceDate,
                    originalNextServiceOdo = nextServiceOdo,
                    onSuccess = { dismiss() },
                    onError = { errorMessage ->
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    }
                )
            } else {
                addServiceViewModel.saveServiceEntry(
                    vehicleId = vehicleId,
                    category = categoryStr,
                    workshopName = workshopStr,
                    odometer = odometer,
                    totalCost = cost,
                    onSuccess = { dismiss() },
                    onError = { errorMessage ->
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    }
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddServiceEntryFragment"
        private const val ARG_ID = "arg_id"
        private const val ARG_DATE = "arg_date"
        private const val ARG_CATEGORY = "arg_category"
        private const val ARG_WORKSHOP = "arg_workshop"
        private const val ARG_ODOMETER = "arg_odometer"
        private const val ARG_COST = "arg_cost"
        private const val ARG_NEXT_DATE = "arg_next_date"
        private const val ARG_NEXT_ODO = "arg_next_odo"

        fun newInstance(
            id: Long,
            date: Long,
            category: String,
            workshopName: String,
            odometer: Int,
            totalCost: Double,
            nextServiceDate: Long?,
            nextServiceOdometer: Int?
        ): AddServiceEntryFragment {
            val fragment = AddServiceEntryFragment()
            val args = Bundle().apply {
                putLong(ARG_ID, id)
                putLong(ARG_DATE, date)
                putString(ARG_CATEGORY, category)
                putString(ARG_WORKSHOP, workshopName)
                putInt(ARG_ODOMETER, odometer)
                putDouble(ARG_COST, totalCost)
                putLong(ARG_NEXT_DATE, nextServiceDate ?: -1L)
                putInt(ARG_NEXT_ODO, nextServiceOdometer ?: -1)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
