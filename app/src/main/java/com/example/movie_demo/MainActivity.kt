package com.example.movie_demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.allViews
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.example.movie_demo.databinding.ActivityMainBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    var isLoading = false
    var recyclerView: RecyclerView? = null
    var recyclerViewAdapter: RecyclerViewAdapter? = null
    var swipeRefreshLayout: SwipeRefreshLayout? = null
var page =0
    val data = ArrayList<String>()
    //var lastscroll = 0
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        setSupportActionBar(binding.toolbar)

        // this creates a vertical layout Manager
        swipeRefreshLayout!!.setOnRefreshListener(OnRefreshListener {
            swipeRefreshLayout!!.setRefreshing(false)
            recyclerView!!.recycledViewPool.clear()
            data.clear()
            try {
                populateData()
                recyclerViewAdapter?.notifyDataSetChanged()
            } catch (_: IndexOutOfBoundsException) {

            }
        })
        recyclerView?.layoutManager = GridLayoutManager(this,2)

        populateData()
        initScrollListener();




    }
    private fun populateData(){
        StrictMode.setThreadPolicy( StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build())
        val thread = Thread {
            try {
                val aLogger = HttpLoggingInterceptor()
                aLogger.level = (HttpLoggingInterceptor.Level.BODY)
                val client: OkHttpClient =
                    OkHttpClient().newBuilder().build()
                val mediaType: MediaType? = "text/plain".toMediaTypeOrNull()
                val body: RequestBody = RequestBody.create(mediaType, "")
                val request: Request = Request.Builder()
                    .url("https://moviesdatabase.p.rapidapi.com/titles")
                    .method("GET", null)
                    .addHeader(
                        "X-RapidAPI-Key",
                        "16295c5e63msh60f240562b18cddp1ee0dbjsndcbf63e4c4f2"
                    )
                    .addHeader("X-RapidAPI-Host", "moviesdatabase.p.rapidapi.com")
                    .build()

                val response: Response = client.newCall(request).execute()
                val res2 = response.body?.string()
                val ki: JSONObject? = res2?.let { JSONObject(it) }
                val ki2 = JSONArray(ki?.getString("results"))
                val kilength = ki2.length()
                if (ki != null) {
                    page = ki.getString("page").toInt()
                }
                runOnUiThread {
                    for (i in 0 until kilength) {
                        val ki3 = ki2.getJSONObject(i)
                        val ki4 = ki3.getJSONObject("titleText").getString("text")
                        var ki6:String = "null"
                        if(ki3.getString("primaryImage") != "null") {
                            ki6 = ki3.getJSONObject("primaryImage").getString("url")
                        }
                        data.add(ki4)
                    }

                    // This will pass the ArrayList to our Adapter
                    val recyclerViewAdapter = RecyclerViewAdapter(data)

                    // Setting the Adapter with the recyclerview
                    recyclerView?.adapter = recyclerViewAdapter
                }



            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
    }
    private fun initScrollListener() {
        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as GridLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == data.size - 1) {
                        //bottom of list!
                        //lastscroll = linearLayoutManager.getPosition(recyclerView)
                        loadMore()
                        isLoading = true
                    }
                }
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadMore() {
        //data.add(null.toString())
        //recyclerViewAdapter?.notifyItemInserted(data.size - 1)
        val handler = Handler()
        handler.postDelayed({
           //data.removeAt(data.size - 1)
            val scrollPosition: Int = data.size
            recyclerViewAdapter?.notifyItemRemoved(scrollPosition)
            var currentSize = scrollPosition
            val nextLimit = currentSize + 10
            page += 1
            while (currentSize - 1 < nextLimit) {
                StrictMode.setThreadPolicy( StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build())
                val thread2 = Thread {
                    try {
                        val aLogger = HttpLoggingInterceptor()
                        aLogger.level = (HttpLoggingInterceptor.Level.BODY)
                        val client: OkHttpClient =
                            OkHttpClient().newBuilder().addInterceptor(aLogger).build()
                        val mediaType: MediaType? = "text/plain".toMediaTypeOrNull()
                        val body: RequestBody = RequestBody.create(mediaType, "")
                        val request: Request = Request.Builder()
                            .url("https://moviesdatabase.p.rapidapi.com/titles?page="+page.toString())
                            .method("GET", null)
                            .addHeader(
                                "X-RapidAPI-Key",
                                "16295c5e63msh60f240562b18cddp1ee0dbjsndcbf63e4c4f2"
                            )
                            .addHeader("X-RapidAPI-Host", "moviesdatabase.p.rapidapi.com")
                            .build()

                        val response: Response = client.newCall(request).execute()
                        val res2 = response.body?.string()
                        val ki: JSONObject? = res2?.let { JSONObject(it) }
                        val ki2 = JSONArray(ki?.getString("results"))
                        val kilength = ki2.length()
                        runOnUiThread {
                            for (i in 0 until kilength) {
                                val ki3 = ki2.getJSONObject(i)
                                val ki4 = ki3.getJSONObject("titleText").getString("text")
                                var ki6: String = "null"
                                if (ki3.getString("primaryImage") != "null") {
                                    ki6 = ki3.getJSONObject("primaryImage").getString("url")
                                }
                                if (!data.contains(ki4) && ki4 != "null"){
                                data.add(ki4)
                                    //recyclerView?.clearAnimation()
                                    //recyclerView?.invalidate()
                                    //recyclerView?.clearFocus()
//                                    data.removeAt(i);
//                                    recyclerView?.removeViewAt(i);
//                                    recyclerViewAdapter?.notifyItemRemoved(i)
//                                    recyclerViewAdapter?.notifyItemRangeChanged(i, data.size);

                                }

                            }
                            recyclerView?.removeAllViews()
                            recyclerViewAdapter = RecyclerViewAdapter(data)
                            recyclerView?.scrollToPosition(data.size - 10);
                            recyclerViewAdapter?.notifyDataSetChanged()
                            //recyclerView?.removeAllViews()
                           // recyclerView?.scrollToPosition(data.size - 1);
                            //recyclerView?.removeAllViews()
                            //recyclerView?.scrollToPosition(lastscroll)
                            //recyclerViewAdapter = RecyclerViewAdapter(data)
                         //   recyclerViewAdapter?.notifyDataSetChanged()
                            //Log.d("within",data.toString())

                            // This will pass the ArrayList to our Adapter
                           // val recyclerViewAdapter = RecyclerViewAdapter(data)

                            // Setting the Adapter with the recyclerview
                            //recyclerView?.adapter = recyclerViewAdapter
                        }

                        //Log.d("array", ki2.toString())

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                thread2.start()

                currentSize++
            }
            isLoading = false
        }, 500)
        //runOnUiThread {

//            recyclerView?.clearAnimation()
//            recyclerView?.invalidate()
//            recyclerView?.clearFocus()
//            //recyclerViewAdapter = RecyclerViewAdapter(data)
//            recyclerViewAdapter?.notifyItemRangeInserted(currentSize,kilength)
        //}

    }


}