package com.bhax.app.ui.explorer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bhax.app.data.LocationData
import com.bhax.app.databinding.FragmentExplorerBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.Marker

class ExplorerFragment : Fragment() {
    private var _binding: FragmentExplorerBinding? = null
    private val binding get() = _binding!!
    private lateinit var mapView: MapView
    private val viewModel: ExplorerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExplorerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMap()
        setupControls()
        setupSearch()
        setupObservers()
    }

    private fun setupMap() {
        mapView = binding.mapView
        Configuration.getInstance().userAgentValue = requireContext().packageName
        
        mapView.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(5.0)
            controller.setCenter(GeoPoint(20.5937, 78.9629)) // India's center
            overlays.add(CopyrightOverlay(context))
            
            // Update locations when map moves
            addMapListener { _, _, _ ->
                updateLocationsInView()
                true
            }
        }
    }

    private fun setupControls() {
        binding.apply {
            btnZoomIn.setOnClickListener { mapView.controller.zoomIn() }
            btnZoomOut.setOnClickListener { mapView.controller.zoomOut() }
            
            btnLayerToggle.setOnClickListener {
                // TODO: Implement layer switching
            }
        }
    }

    private fun setupSearch() {
        binding.searchInput.apply {
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(text.toString())
                    true
                } else {
                    false
                }
            }

            addTextChangedListener { text ->
                text?.toString()?.let { query ->
                    if (query.length >= 3) {
                        if (query.matches(Regex("\\d+"))) {
                            viewModel.searchByPincode(query)
                        } else {
                            viewModel.searchByAreaName(query)
                        }
                    }
                }
            }
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.searchResults.collectLatest { locations ->
                        updateMapMarkers(locations)
                    }
                }

                launch {
                    viewModel.selectedLocation.collectLatest { location ->
                        location?.let { showLocationOnMap(it) }
                    }
                }

                launch {
                    viewModel.error.collectLatest { error ->
                        error?.let {
                            showError(it)
                            viewModel.clearError()
                        }
                    }
                }
            }
        }
    }

    private fun performSearch(query: String) {
        if (query.matches(Regex("\\d+"))) {
            viewModel.searchByPincode(query)
        } else {
            viewModel.searchByAreaName(query)
        }
    }

    private fun updateLocationsInView() {
        val bounds = mapView.boundingBox
        viewModel.observeLocationsInBounds(
            bounds.latSouth,
            bounds.latNorth,
            bounds.lonWest,
            bounds.lonEast
        )
    }

    private fun updateMapMarkers(locations: List<LocationData>) {
        // Clear existing markers
        mapView.overlays.removeAll { it is Marker }
        
        // Add new markers
        locations.forEach { location ->
            val marker = Marker(mapView).apply {
                position = GeoPoint(location.latitude, location.longitude)
                title = location.areaName
                snippet = "Pincode: ${location.pincode}"
                setOnMarkerClickListener { _, _ ->
                    viewModel.selectLocation(location)
                    true
                }
            }
            mapView.overlays.add(marker)
        }
        
        mapView.invalidate()
    }

    private fun showLocationOnMap(location: LocationData) {
        // Center map on location
        mapView.controller.animateTo(
            GeoPoint(location.latitude, location.longitude),
            15.0,
            1000L
        )
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        updateLocationsInView()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
