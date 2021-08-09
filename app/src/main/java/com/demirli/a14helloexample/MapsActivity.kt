package com.demirli.a14helloexample

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    private lateinit var helloInNative: String
    private lateinit var userLocation: LatLng

    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        username = intent.getStringExtra(Constants.INTENT_EXTRA_NAME)

        helloInNative = ""


        map_activity_logout_btn.setOnClickListener{

            Toast.makeText(this,"Goodbye " + username, Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object: LocationListener{
            override fun onLocationChanged(location: Location?) {
                if(location != null){

                    val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
                    val addressList = geocoder.getFromLocation(location.latitude,location.longitude,1)
                    if (addressList != null && addressList.size > 0){
                        val downloadHelloInNative = DownloadHelloInNative()
                        val url = "https://fourtonfish.com/hellosalut/?cc=${addressList[0].countryCode}"

                        downloadHelloInNative.execute(url)
                    }

                    mMap.clear()
                    userLocation = LatLng(location.latitude,location.longitude)
                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String?) {
            }

            override fun onProviderDisabled(provider: String?) {
            }

        }
        getPermission()
    }

    inner class DownloadHelloInNative(): AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String {
            var result = ""

            var url: URL

            var httpURLConnection: HttpURLConnection

            url = URL(params[0])

            httpURLConnection = url.openConnection() as HttpURLConnection
            val inputStream = httpURLConnection.inputStream
            val inputStreamReader = InputStreamReader(inputStream)
            var data = inputStreamReader.read()

            while(data > 0){
                val character = data.toChar()
                result += character

                data = inputStreamReader.read()
            }

            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            val jsonObject = JSONObject(result)
            helloInNative = jsonObject.getString("hello")
            addMarker()
        }
    }

    fun addMarker(){
        mMap.addMarker(MarkerOptions().position(userLocation).title(helloInNative + "!!! " + username)).showInfoWindow()
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,5f))
    }

    fun getPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),Constants.ACCESS_FINE_LOCATION_REQUEST_CODE)
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.ACCESS_FINE_LOCATION_REQUEST_CODE && grantResults.size > 0){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)
            }
        }
    }
}
