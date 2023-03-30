package com.example.movie_demo

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject


class DetailsPage : AppCompatActivity() {
    var id: String = ""
@SuppressLint("SetTextI18n")
override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details_page)
        //Details Page to view details of a movie
        val toolbar = findViewById(R.id.toolbar2) as Toolbar?
        val imgb = findViewById<ImageView>(R.id.imageViewDetail)
        val textViewtitle = findViewById<TextView>(R.id.textViewDetailName)
        val textViewyearrel = findViewById<TextView>(R.id.textViewDetailReleaseYear)
        val textViewreldate = findViewById<TextView>(R.id.textViewDetailreldate)
        setSupportActionBar(toolbar)
        toolbar?.title = "Movie Details"
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);
    val sharedPreference =  getSharedPreferences("ids", Context.MODE_PRIVATE)
    id = sharedPreference.getString("ids",null).toString()
    Log.d("detid",id)
    //StrictMode.setThreadPolicy( StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build())
    val thread = Thread {
        val aLogger = HttpLoggingInterceptor()
        aLogger.level = (HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient()
        OkHttpClient().newBuilder().addInterceptor(aLogger).build()
        val request: Request = Request.Builder()
            .url("https://moviesdatabase.p.rapidapi.com/titles/"+id)
            .get()
            .addHeader("X-RapidAPI-Key", "16295c5e63msh60f240562b18cddp1ee0dbjsndcbf63e4c4f2")
            .addHeader("X-RapidAPI-Host", "moviesdatabase.p.rapidapi.com")
            .build()
        val response: Response = client.newCall(request).execute()
        val res2 = response.body?.string()
        val ki: JSONObject? = res2?.let { JSONObject(it) }
        val ki2 = ki?.getString("results")?.let { JSONObject(it) }
        var ki3: String = ""
        if(ki2?.getString("primaryImage") != "null") {
             ki3 = ki2?.getJSONObject("primaryImage")?.getString("url").toString()
        }
        runOnUiThread{
            if(ki3 != "null") {
                Picasso.with(this).load(ki3).into(imgb)
            }else{
                Picasso.with(this).load("https://m.media-amazon.com/images/M/MV5BZDI4ZDgwMWQtMjA3ZS00NmU5LTk5MGQtZTMyMGFlMjYyZmFlXkEyXkFqcGdeQXVyNTIzOTk5ODM@._V1_.jpg").into(imgb)
            }
            textViewtitle.text = ki2?.getJSONObject("titleText")?.getString("text")
            textViewyearrel.text = "Release Year: "+ki2?.getJSONObject("releaseYear")?.getString("year")
            textViewreldate.text = "Release Date: "+ki2?.getJSONObject("releaseDate")?.getString("day") +"/"+ki2?.getJSONObject("releaseDate")?.getString("month") +"/"+ ki2?.getJSONObject("releaseDate")?.getString("year")
        }
    }
    thread.start()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish() // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item)
    }
}
