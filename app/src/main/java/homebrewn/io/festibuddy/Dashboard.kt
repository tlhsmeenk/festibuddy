package homebrewn.io.festibuddy

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.view.Menu
import com.mapbox.mapboxsdk.Mapbox
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.activity_dashboard.*
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import android.location.Location;

import com.mapbox.mapboxsdk.geometry.LatLng;

import android.support.annotation.NonNull;

import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider




class Dashboard : AppCompatActivity(), PermissionsListener, LocationEngineListener {

    private val permissionsManager: PermissionsManager =  PermissionsManager(this)
    private var locationPlugin: LocationLayerPlugin? = null
    private var locationEngine: LocationEngine? = null
    private var originLocation: Location? = null
    private var map: MapboxMap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* init the mapbox instance, set the content view and call onSaveInstanceState */
        Mapbox.getInstance(this, getString(R.string.access_token))
        setContentView(R.layout.activity_dashboard)
        savedInstanceState?.let { mapView.onSaveInstanceState(it) }
        setSupportActionBar(search_bar)

        mapView.getMapAsync {
            mapboxMap ->
            this.map = mapboxMap
            enableLocationPlugin()
        }

        create_group_fab.setOnClickListener {
            startActivity(Intent(this, CreateGroup::class.java))
        }
    }

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            originLocation = location
            setCameraPosition(location)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onConnected() {
        locationEngine?.requestLocationUpdates()
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationPlugin()
        } else {
            finish()
        }
    }

    private fun enableLocationPlugin(){
        if(PermissionsManager.areLocationPermissionsGranted(this)){
            initalizeLocationEngine()
            map?.let { mapboxMap ->
                locationPlugin= LocationLayerPlugin(mapView, mapboxMap, locationEngine)
                locationPlugin?.cameraMode = RenderMode.COMPASS
            }
        }else {
            permissionsManager.requestLocationPermissions(this)
        }
    }

    @SuppressLint("MissingPermission")
    private fun initalizeLocationEngine() {
        val locationEngineProvider = LocationEngineProvider(this)
        locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable()
        locationEngine?.priority = LocationEnginePriority.HIGH_ACCURACY
        locationEngine?.activate()
        locationEngine?.addLocationEngineListener(this)

        val lastLocation = locationEngine?.lastLocation
        if (lastLocation != null) {
            originLocation = lastLocation
            setCameraPosition(lastLocation)
        }

    }

    private fun setCameraPosition(location: Location) {
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(
                LatLng(location.latitude, location.longitude), 13.0))
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val search = menu?.findItem(R.id.action_search)
        val actionView = search?.actionView

        when(actionView){
            is SearchView -> setOptions(actionView)
        }

        return super.onCreateOptionsMenu(menu)
    }

    private fun setOptions(search: SearchView) {
        search.isSubmitButtonEnabled=true
        search.setOnSearchClickListener {
            sliding_layout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.let {mapView.onSaveInstanceState(it) }
    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        mapView?.onStart()
        locationEngine?.requestLocationUpdates()
        locationPlugin?.onStart()
    }


    override fun onStop() {
        super.onStop()
        mapView.onStop()
        locationEngine?.removeLocationUpdates()
        locationPlugin?.onStop()
    }


    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        locationEngine?.deactivate()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

}
