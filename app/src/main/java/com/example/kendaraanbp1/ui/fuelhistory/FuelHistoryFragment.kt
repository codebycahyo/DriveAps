package com.example.kendaraanbp1.ui.fuelhistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kendaraanbp1.R
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import com.example.kendaraanbp1.data.repository.Resource
import com.example.kendaraanbp1.ui.fuelhistory.FuelHistoryViewModel
import com.example.kendaraanbp1.ui.common.viewmodel.SharedVehicleViewModel
import com.example.kendaraanbp1.ui.common.viewmodel.ViewModelFactory
import com.example.kendaraanbp1.data.model.FuelLogItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.kendaraanbp1.databinding.FragmentFuelHistoryBinding
import com.example.kendaraanbp1.ui.addfuel.AddFuelEntryFragment
import com.example.kendaraanbp1.ui.util.BottomNavTab
import com.example.kendaraanbp1.ui.util.VerticalSpaceItemDecoration
import com.example.kendaraanbp1.ui.util.applyBottomSystemBarMargin
import com.example.kendaraanbp1.ui.util.applyTopSystemBarPadding
import com.example.kendaraanbp1.ui.util.bind
import com.example.kendaraanbp1.ui.util.setActiveTab
import com.example.kendaraanbp1.ui.util.setEmptyState
import com.example.kendaraanbp1.ui.util.setupNavigation
import androidx.navigation.fragment.findNavController

class FuelHistoryFragment : Fragment() {

    private var _binding: FragmentFuelHistoryBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedVehicleViewModel by activityViewModels {
        ViewModelFactory(requireContext().applicationContext)
    }

    private val fuelViewModel: FuelHistoryViewModel by viewModels {
        ViewModelFactory(requireContext().applicationContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFuelHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.applyTopSystemBarPadding()
        binding.bottomNavInclude.root.applyBottomSystemBarMargin()
        binding.bottomNavInclude.setActiveTab(BottomNavTab.VEHICLES)
        binding.bottomNavInclude.setupNavigation(findNavController())

        val menuButton = binding.topAppBar.findViewById<android.view.View>(R.id.menuButton)
        menuButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.addFuelFab.setOnClickListener {
            AddFuelEntryFragment().show(childFragmentManager, AddFuelEntryFragment.TAG)
        }

        val adapter = FuelLogAdapter()
        binding.fuelLogList.layoutManager = LinearLayoutManager(requireContext())
        binding.fuelLogList.adapter = adapter
        binding.fuelLogList.addItemDecoration(
            VerticalSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.spacing_16))
        )
        
        setUpEmptyState()
        observeViewModels(adapter)
    }

    private fun setUpEmptyState() {
        binding.fuelLogEmptyState.bind(
            R.drawable.ic_inbox,
            R.string.empty_fuel_log_title,
            R.string.empty_fuel_log_subtitle,
        )
    }
    
    private fun observeViewModels(adapter: FuelLogAdapter) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    sharedViewModel.selectedVehicleId.collect { id ->
                        fuelViewModel.setVehicleId(id)
                    }
                }
                
                launch {
                    fuelViewModel.fuelLogs.collect { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                val logs = resource.data ?: emptyList()
                                val items = logs.map { log ->
                                    FuelLogItem(
                                        id = log.id,
                                        stationName = log.stationName,
                                        date = dateFormat.format(Date(log.date)),
                                        amountLabel = "Rp ${log.totalCost.toLong()}",
                                        litersLabel = "${log.liters} L",
                                        odometerLabel = "Odometer: ${log.odometer} km"
                                    )
                                }
                                adapter.submitList(items)
                                binding.fuelLogEmptyState.setEmptyState(items.isEmpty(), binding.fuelLogList)
                            }
                            is Resource.Error -> {
                                binding.fuelLogEmptyState.setEmptyState(true, binding.fuelLogList)
                            }
                            else -> {}
                        }
                    }
                }
                
                launch {
                    fuelViewModel.totalExpense.collect { resource ->
                        if (resource is Resource.Success) {
                            binding.totalExpenseValue.text = "Rp ${resource.data?.toLong() ?: 0}"
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
