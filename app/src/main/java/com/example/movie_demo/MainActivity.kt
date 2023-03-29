package com.example.movie_demo

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.JsonReader
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.movie_demo.databinding.ActivityMainBinding
import org.json.JSONObject
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        val thread = Thread {
            try {
                val githubEndpoint = URL("https://moviesdatabase.p.rapidapi.com/titles")
                val myConnection: HttpsURLConnection = githubEndpoint.openConnection() as HttpsURLConnection
                myConnection.setRequestProperty("X-RapidAPI-Key","16295c5e63msh60f240562b18cddp1ee0dbjsndcbf63e4c4f2")
                myConnection.setRequestProperty("X-RapidAPI-Host","moviesdatabase.p.rapidapi.com")
                if (myConnection.getResponseCode() == 200) {
                    //Log.d("incoming","yes")
                    val responseBody: InputStream = myConnection.inputStream
                    val responseBodyReader = InputStreamReader(responseBody, "UTF-8")
                    //Log.d("incoming", myConnection.getInputStream().toString())
                    val jsonReader = JsonReader(responseBodyReader)
                    jsonReader.beginObject(); // Start processing the JSON object
                    while (jsonReader.hasNext()) { // Loop through all keys
                        val key: String = jsonReader.nextName(); // Fetch the next key
                        val jsonObject = JSONObject(responseBody.toString())
                        val jsonArray = jsonObject.optJSONArray("results")
                        if (jsonArray != null) {
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val id = jsonObject.optString("id")
                                Log.d("incoming", id)
                            }
                        }

//                        if (key.equals("result")) { // Check if desired key
//                            // Fetch the value as a String
//                            val value: Unit = jsonReader.beginArray()
//                            //value.jsonObject.optJSONArray("Employee")
//                            //Log.d("incoming", value)
//                            // Do something with the value
//                            // ...
//
//                            break; // Break out of the loop
//                        } else {
//                            jsonReader.skipValue(); // Skip values of other keys
//                        }
                    }


                } else {
                    Log.d("incoming","no")
                    // Error handling code goes here
                }
                //Your code goes here
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        thread.start()

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own actionz", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}