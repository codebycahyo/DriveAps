package com.example.kendaraanbp1.ui.addfuel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.kendaraanbp1.databinding.FragmentAddFuelEntryBinding
import com.example.kendaraanbp1.ui.common.ExpandedBottomSheetDialogFragment
import com.example.kendaraanbp1.ui.common.viewmodel.SharedVehicleViewModel
import com.example.kendaraanbp1.ui.common.viewmodel.ViewModelFactory
import com.example.kendaraanbp1.ui.scan.ScannerActivity
import com.example.kendaraanbp1.util.ReceiptParser
import androidx.core.content.ContextCompat
import com.example.kendaraanbp1.R
import androidx.activity.result.contract.ActivityResultContracts
import android.app.Activity
import android.content.Intent

class AddFuelEntryFragment : ExpandedBottomSheetDialogFragment() {

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
                    binding.etPrice.setTextColor(ContextCompat.getColor(requireContext(), R.color.brand_blue))
                }
                if (parsedData.stationName != null) {
                    binding.etStation.setText(parsedData.stationName)
                    binding.etStation.setTextColor(ContextCompat.getColor(requireContext(), R.color.brand_blue))
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

        val editId = arguments?.getLong(ARG_ID, -1L) ?: -1L
        if (editId != -1L) {
            binding.etLiter.setText(arguments?.getDouble(ARG_LITERS).toString())
            binding.etPrice.setText(arguments?.getDouble(ARG_PRICE).toString())
            binding.etOdometer.setText(arguments?.getInt(ARG_ODOMETER).toString())
            binding.etStation.setText(arguments?.getString(ARG_STATION))
            binding.saveButton.text = "Simpan Perubahan"
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
            
            if (editId != -1L) {
                val originalDate = arguments?.getLong(ARG_DATE) ?: System.currentTimeMillis()
                addFuelViewModel.updateFuelEntry(
                    id = editId,
                    vehicleId = vehicleId,
                    originalDate = originalDate,
                    liters = liters,
                    pricePerLiter = price,
                    odometer = odometer,
                    stationName = stationStr,
                    onSuccess = { dismiss() },
                    onError = { errorMessage ->
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    }
                )
            } else {
                addFuelViewModel.saveFuelEntry(
                    vehicleId = vehicleId,
                    liters = liters,
                    pricePerLiter = price,
                    odometer = odometer,
                    stationName = stationStr,
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
        const val TAG = "AddFuelEntryFragment"
        private const val ARG_ID = "arg_id"
        private const val ARG_DATE = "arg_date"
        private const val ARG_LITERS = "arg_liters"
        private const val ARG_PRICE = "arg_price"
        private const val ARG_ODOMETER = "arg_odometer"
        private const val ARG_STATION = "arg_station"

        fun newInstance(
            id: Long,
            date: Long,
            liters: Double,
            pricePerLiter: Double,
            odometer: Int,
            stationName: String
        ): AddFuelEntryFragment {
            val fragment = AddFuelEntryFragment()
            val args = Bundle().apply {
                putLong(ARG_ID, id)
                putLong(ARG_DATE, date)
                putDouble(ARG_LITERS, liters)
                putDouble(ARG_PRICE, pricePerLiter)
                putInt(ARG_ODOMETER, odometer)
                putString(ARG_STATION, stationName)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
