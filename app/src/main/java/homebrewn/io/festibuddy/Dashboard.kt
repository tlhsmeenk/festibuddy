package homebrewn.io.festibuddy

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.View
import android.widget.Toast
import com.mapbox.mapboxsdk.Mapbox
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.activity_dashboard.*

class Dashboard : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* init the mapbox instance, set the content view and call onSaveInstanceState */
        Mapbox.getInstance(this, getString(R.string.access_token))
        setContentView(R.layout.activity_dashboard)
        savedInstanceState?.let { mapView.onSaveInstanceState(it) }
        setSupportActionBar(search_bar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        System.err.println("ADDING STUFF")
        val search = menu?.findItem(R.id.action_search)
        when(search){
            is SearchView -> setOptions(search)
        }

        return super.onCreateOptionsMenu(menu)
    }

    private fun setOptions(search: SearchView) {
        search.isSubmitButtonEnabled=true
        search_bar.setOnClickListener {
            Toast.makeText(this, ":P", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.let {mapView.onSaveInstanceState(it) }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

}
