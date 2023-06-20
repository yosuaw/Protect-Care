package com.ubaya.protectcare68

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    val fragments: ArrayList<Fragment> = ArrayList()
    var username = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var sharedName = packageName
        var shared = getSharedPreferences(sharedName, Context.MODE_PRIVATE)
        username = shared.getString(LoginActivity.SHARED_USERNAME, null).toString()
        
        val queue = Volley.newRequestQueue(this)
        val url = "https://ubaya.fun/native/160419038/get_last_check_in.php"
        val stringRequest = object : StringRequest(
            Method.POST,
            url,
            Response.Listener {
                Log.d("success", it)
                val obj = JSONObject(it)
                if (obj.getString("result") == "OK") {
                    val data = obj.getJSONObject("data")
                    val parser = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val formatter = java.text.SimpleDateFormat("EEEE, d MMMM yyyy, HH:mm")
                    val formattedTime = formatter.format(parser.parse(data.getString("check_in")))

                    fragments.add(CheckOutFragment.newInstance(username, data.getString("place"), data.getInt("vaccine_count"), formattedTime, data.getString("code")))
                } else {
                    username?.let { CheckInFragment.newInstance(it) }?.let { fragments.add(it) }
                }

                username?.let { HistoryFragment.newInstance(it) }?.let { fragments.add(it) }
                username?.let { ProfileFragment.newInstance(it) }?.let { fragments.add(it) }
                viewPager.adapter = MyAdapter(this, fragments)
            },
            Response.ErrorListener {
                Log.d("error", it.message.toString())
            }
        ) {
            override fun getParams(): MutableMap<String, String>? {
                return hashMapOf("username" to username)
            }
        }
        queue.add(stringRequest)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                bottomNav.selectedItemId = bottomNav.menu.getItem(position).itemId
            }
        })

        bottomNav.setOnItemSelectedListener {
            viewPager.currentItem = when(it.itemId) {
                R.id.itemCheckIn -> 0
                R.id.itemHistory -> 1
                R.id.itemProfile -> 2
                else -> 0
            }
            true
        }
    }

    fun checkIn(username: String, place: String, vaccineCount: Int, dateTime: String, code: String) {
        fragments[0] = CheckOutFragment.newInstance(username, place, vaccineCount, dateTime, code)
        viewPager.adapter = MyAdapter(this, fragments)
    }

    fun checkOut(username: String) {
        fragments[0] = CheckInFragment.newInstance(username)
        viewPager.adapter = MyAdapter(this, fragments)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }
}