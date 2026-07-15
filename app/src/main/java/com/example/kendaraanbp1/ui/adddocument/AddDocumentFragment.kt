package com.example.kendaraanbp1.ui.adddocument

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.kendaraanbp1.databinding.FragmentAddDocumentBinding
import com.example.kendaraanbp1.ui.common.ExpandedBottomSheetDialogFragment
import com.example.kendaraanbp1.ui.common.viewmodel.SharedVehicleViewModel
import com.example.kendaraanbp1.ui.common.viewmodel.ViewModelFactory
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddDocumentFragment : ExpandedBottomSheetDialogFragment() {

    private var _binding: FragmentAddDocumentBinding? = null
    private val binding get() = _binding!!

    private var selectedExpiryDate: Long? = null


    private val sharedViewModel: SharedVehicleViewModel by activityViewModels {
        ViewModelFactory(requireContext().applicationContext)
    }

    private val addDocumentViewModel: AddDocumentViewModel by viewModels {
        ViewModelFactory(requireContext().applicationContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddDocumentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeButton.setOnClickListener { dismiss() }

        val editId = arguments?.getLong(ARG_ID, -1L) ?: -1L
        if (editId != -1L) {
            binding.etType.setText(arguments?.getString(ARG_TYPE))
            binding.etNumber.setText(arguments?.getString(ARG_NUMBER))
            
            val expiry = arguments?.getLong(ARG_EXPIRY_DATE) ?: 0L
            if (expiry > 0) {
                selectedExpiryDate = expiry
                binding.etExpiryDate.setText(SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(Date(expiry)))
            }
            binding.saveButton.text = "Simpan Perubahan"
        }
        
        binding.etExpiryDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Masa Berlaku")
                .setSelection(selectedExpiryDate ?: MaterialDatePicker.todayInUtcMilliseconds())
                .build()
                
            datePicker.addOnPositiveButtonClickListener { selection ->
                selectedExpiryDate = selection
                binding.etExpiryDate.setText(SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(Date(selection)))
            }
            
            datePicker.show(childFragmentManager, "EXPIRY_DATE_PICKER")
        }

        binding.saveButton.setOnClickListener {
            val typeStr = binding.etType.text.toString().trim()
            val numberStr = binding.etNumber.text.toString().trim()
            val expiryDateMillis = selectedExpiryDate
            
            if (typeStr.isEmpty() || numberStr.isEmpty() || expiryDateMillis == null) {
                Toast.makeText(context, "Harap lengkapi semua data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val vehicleId = sharedViewModel.selectedVehicleId.value
            
            if (vehicleId == null) {
                Toast.makeText(context, "Tidak ada kendaraan aktif", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (editId != -1L) {
                val originalIssuedDate = arguments?.getLong(ARG_ISSUED_DATE) ?: System.currentTimeMillis()
                addDocumentViewModel.updateDocument(
                    id = editId,
                    vehicleId = vehicleId,
                    documentType = typeStr,
                    documentNumber = numberStr,
                    expiryDateMillis = expiryDateMillis,
                    originalIssuedDate = originalIssuedDate,
                    onSuccess = { dismiss() },
                    onError = { errorMessage ->
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    }
                )
            } else {
                addDocumentViewModel.saveDocument(
                    vehicleId = vehicleId,
                    documentType = typeStr,
                    documentNumber = numberStr,
                    expiryDateMillis = expiryDateMillis,
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
        const val TAG = "AddDocumentFragment"
        private const val ARG_ID = "arg_id"
        private const val ARG_TYPE = "arg_type"
        private const val ARG_NUMBER = "arg_number"
        private const val ARG_EXPIRY_DATE = "arg_expiry_date"
        private const val ARG_ISSUED_DATE = "arg_issued_date"

        fun newInstance(
            id: Long,
            type: String,
            number: String,
            expiryDate: Long,
            issuedDate: Long
        ): AddDocumentFragment {
            val fragment = AddDocumentFragment()
            val args = Bundle().apply {
                putLong(ARG_ID, id)
                putString(ARG_TYPE, type)
                putString(ARG_NUMBER, number)
                putLong(ARG_EXPIRY_DATE, expiryDate)
                putLong(ARG_ISSUED_DATE, issuedDate)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
