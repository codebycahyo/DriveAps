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

class AddDocumentFragment : ExpandedBottomSheetDialogFragment() {

    private var _binding: FragmentAddDocumentBinding? = null
    private val binding get() = _binding!!

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
            binding.etDays.setText(arguments?.getInt(ARG_DAYS).toString())
            binding.saveButton.text = "Simpan Perubahan"
        }

        binding.saveButton.setOnClickListener {
            val typeStr = binding.etType.text.toString().trim()
            val numberStr = binding.etNumber.text.toString().trim()
            val daysStr = binding.etDays.text.toString().trim()
            
            if (typeStr.isEmpty() || numberStr.isEmpty() || daysStr.isEmpty()) {
                Toast.makeText(context, "Harap lengkapi semua data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val days = daysStr.toIntOrNull() ?: 0
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
                    validDays = days,
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
                    validDays = days,
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
        private const val ARG_DAYS = "arg_days"
        private const val ARG_ISSUED_DATE = "arg_issued_date"

        fun newInstance(
            id: Long,
            type: String,
            number: String,
            validDays: Int,
            issuedDate: Long
        ): AddDocumentFragment {
            val fragment = AddDocumentFragment()
            val args = Bundle().apply {
                putLong(ARG_ID, id)
                putString(ARG_TYPE, type)
                putString(ARG_NUMBER, number)
                putInt(ARG_DAYS, validDays)
                putLong(ARG_ISSUED_DATE, issuedDate)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
