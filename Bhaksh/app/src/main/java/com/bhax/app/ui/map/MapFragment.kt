package com.bhax.app.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.gestures.generated.GesturesSettings
import com.mapbox.maps.plugin.attribution.generated.AttributionSettings
import com.mapbox.maps.plugin.compass.generated.CompassSettings
import com.mapbox.maps.plugin.scalebar.generated.ScaleBarSettings

class MapFragment : Fragment() {
    private var mapViewportState: MapViewportState? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                BhaxDweepMap()
            }
        }
    }

    @Composable
    private fun BhaxDweepMap() {
        // Center coordinates for Indian subcontinent
        val initialCenter = Point.fromLngLat(78.9629, 20.5937)
        val viewportState = rememberMapViewportState {
            setCameraOptions {
                zoom(4.0)
                center(initialCenter)
                pitch(0.0)
                bearing(0.0)
            }
        }
        mapViewportState = viewportState

        MapboxMap(
            Modifier.fillMaxSize(),
            mapViewportState = viewportState,
            gesturesSettings = GesturesSettings {
                // Restrict panning to Indian subcontinent bounds
                setBounds(
                    minLon = 68.1766,
                    minLat = 6.7535,
                    maxLon = 97.4025,
                    maxLat = 35.6745
                )
                setPinchToZoomEnabled(true)
                setDoubleTapToZoomInEnabled(true)
                setQuickZoomEnabled(true)
            },
            attributionSettings = AttributionSettings {
                setEnabled(false)
            },
            compassSettings = CompassSettings {
                setEnabled(false)
            },
            scaleBarSettings = ScaleBarSettings {
                setEnabled(false)
            }
        )
    }

    fun centerOnLocation(latitude: Double, longitude: Double) {
        mapViewportState?.let { viewport ->
            viewport.setCameraOptions(
                CameraOptions.Builder()
                    .center(Point.fromLngLat(longitude, latitude))
                    .zoom(12.0)
                    .build()
            )
        }
    }

    companion object {
        fun newInstance() = MapFragment()
    }
}