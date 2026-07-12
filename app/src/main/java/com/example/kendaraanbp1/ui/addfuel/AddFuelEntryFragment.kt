package com.example.kendaraanbp1.ui.addfuel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.kendaraanbp1.databinding.FragmentAddFuelEntryBinding
import com.example.kendaraanbp1.ui.common.viewmodel.SharedVehicleViewModel
import com.example.kendaraanbp1.ui.common.viewmodel.ViewModelFactory
import com.example.kendaraanbp1.ui.scan.ScannerActivity
import com.example.kendaraanbp1.util.ReceiptParser
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.activity.result.contract.ActivityResultContracts
import android.app.Activity
import android.content.Intent

class AddFuelEntryFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAddFuelEntryBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedVehicleViewModel by activityViewModels {
        ViewModelFactory(requireContext().applicationContext)
    }

    private val addFuelViewModel: AddFuelViewModel by viewModels {
        ViewModelFactory(requireContext().applicationContext)
    }

    private val scanLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val rawText = result.data?.getStringExtra(ScannerActivity.EXTRA_SCAN_RESULT)
            if (!rawText.isNullOrEmpty()) {
                val parsedData = ReceiptParser.parseFuelReceipt(rawText)
                if (parsedData.total != null) {
                    binding.etPrice.setText(parsedData.total.toInt().toString())
                }
                if (parsedData.stationName != null) {
                    binding.etStation.setText(parsedData.stationName)
                }
                Toast.makeText(context, "Berhasil memindai struk", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddFuelEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeButton.setOnClickListener { dismiss() }
        
        binding.uploadReceiptButton.setOnClickListener {
            scanLauncher.launch(Intent(requireContext(), ScannerActivity::class.java))
        }

        binding.saveButton.setOnClickListener {
            val litersStr = binding.etLiter.text.toString()
            val priceStr = binding.etPrice.text.toString()
            val odometerStr = binding.etOdometer.text.toString()
            val stationStr = binding.etStation.text.toString()
            
            if (litersStr.isEmpty() || priceStr.isEmpty() || odometerStr.isEmpty() || stationStr.isEmpty()) {
                Toast.makeText(context, "Harap lengkapi semua data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val liters = litersStr.toDoubleOrNull() ?: 0.0
            val price = priceStr.toDoubleOrNull() ?: 0.0
            val odometer = odometerStr.toIntOrNull() ?: 0
            val vehicleId = sharedViewModel.selectedVehicleId.value
            
            if (vehicleId == null) {
                Toast.makeText(context, "Tidak ada kendaraan aktif", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            addFuelViewModel.saveFuelEntry(
                vehicleId = vehicleId,
                liters = liters,
                pricePerLiter = price,
                odometer = odometer,
                stationName = stationStr,
                onSuccess = {
                    dismiss()
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddFuelEntryFragment"
    }
}
