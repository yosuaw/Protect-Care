package com.ubaya.protectcare68

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_check_in.*
import kotlinx.android.synthetic.main.fragment_check_in.view.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_USERNAME = "username"

/**
 * A simple [Fragment] subclass.
 * Use the [CheckInFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CheckInFragment : Fragment() {
    var username = ""
    var places: ArrayList<Place> = ArrayList()
    var vaccCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username = it.getString(ARG_USERNAME).toString()
        }

        places.add(Place("0", "Select a place...", "https://cdn-icons-png.flaticon.com/512/854/854878.png"))

        var queue = Volley.newRequestQueue(activity)
        var url = "https://ubaya.fun/native/160419038/get_place.php"
        var stringRequest = StringRequest(
            Request.Method.POST,
            url,
            Response.Listener {
                Log.d("apiresult", it)
                val obj = JSONObject(it)
                if(obj.getString("result") == "OK") {
                    val data = obj.getJSONArray("data")

                    for(i in 0 until data.length()) {
                        val placeObj = data.getJSONObject(i)
                        with(placeObj) {
                            var place = Place(getString("code"), getString("name"), getString("image"))
                            places.add(place)
                        }
                    }

                    //Create adapter for the Spinner
                    val adapter = ArrayAdapter(activity as Context, R.layout.myspinner_layout, places)
                    adapter.setDropDownViewResource(R.layout.myspinner_item_layout)
                    spinPlace.adapter = adapter
                }
            },
            Response.ErrorListener {
                Log.e("apierror", it.message.toString())
            }
        )
        queue.add(stringRequest)
    }

    private fun scanCode() {
        val integrator = IntentIntegrator(activity)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
        integrator.setBeepEnabled(false)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val result:IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(context, "cancelled", Toast.LENGTH_SHORT).show()
                } else {
                    txtCode.setText(result.contents)
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_check_in, container, false)

        view.txtCodeLayout.setEndIconOnClickListener {
            scanCode()
        }

        view.spinPlace.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) { }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Picasso.get().load(places[position].image).into(imageViewPlace)
            }
        }

        view.btnCheckIn.setOnClickListener {
            val queue = Volley.newRequestQueue(context)
            val url = "https://ubaya.fun/native/160419038/get_user.php"
            val stringRequest = object : StringRequest(
                Method.POST,
                url,
                Response.Listener {
                    Log.d("getuser_success", it)
                    val obj = JSONObject(it)
                    if (obj.getString("result") == "OK") {
                        var data = obj.getJSONArray("data")
                        var playObj = data.getJSONObject(0)
                        vaccCount = playObj.getInt("vaccine_count")

                        checkIn()
                    } else {
                        Log.e("getuser_failed", obj.getString("message"))
                    }
                },
                Response.ErrorListener {
                    Log.d("getuser_error", it.message.toString())
                }
            ) {
                override fun getParams(): MutableMap<String, String>? {
                    return hashMapOf("username" to username)
                }
            }
            queue.add(stringRequest)
        }

        view.btnPlaceLocation.setOnClickListener {
            if(places[spinPlace.selectedItemId.toInt()].code == "0") {
                Toast.makeText(context, "Please select a place first!", Toast.LENGTH_SHORT).show()
            } else {
                val gmmIntentUri = Uri.parse("geo:0,0?q=${places[spinPlace.selectedItemId.toInt()].name}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }
        }

        return view
    }

    fun checkIn() {
        if(places[spinPlace.selectedItemId.toInt()].code == "0") {
            Toast.makeText(context, "Please select a place first!", Toast.LENGTH_SHORT).show()
        }
        else if(txtCode.text.toString() != places[spinPlace.selectedItemId.toInt()].code) {
            Toast.makeText(context, "The Unique Code for this place is incorrect!", Toast.LENGTH_SHORT).show()
        }
        else if(vaccCount < 1) {
            Toast.makeText(context, "You must be vaccinated at least once to check in!", Toast.LENGTH_SHORT).show()
        }
        else if(txtCode.text.toString() == places[spinPlace.selectedItemId.toInt()].code && vaccCount >= 1) {
            val formatter = java.text.SimpleDateFormat("EEEE, d MMMM yyyy, HH:mm")
            val formattedTime = formatter.format(Date())

            val queue = Volley.newRequestQueue(context)
            val url = "https://ubaya.fun/native/160419038/check_in.php"
            val stringRequest = object : StringRequest(
                Method.POST,
                url,
                Response.Listener {
                    Log.d("checkin_success", it)
                    val obj = JSONObject(it)
                    if (obj.getString("result") == "OK") {
                        Toast.makeText(context, "Check in successful!", Toast.LENGTH_SHORT).show()
                        var mainActivity: MainActivity = activity as MainActivity
                        mainActivity.checkIn(username, places[spinPlace.selectedItemId.toInt()].name, vaccCount, formattedTime, places[spinPlace.selectedItemId.toInt()].code)
                    } else {
                        Log.e("checkin_failed", obj.getString("message"))
                    }
                },
                Response.ErrorListener {
                    Log.d("checkin_error", it.message.toString())
                }
            ) {
                override fun getParams() = hashMapOf(
                    "username" to username,
                    "code" to places[spinPlace.selectedItemId.toInt()].code,
                    "vaccine_count" to vaccCount.toString()
                )
            }
            queue.add(stringRequest)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param username Parameter 1.
         * @return A new instance of fragment CheckInFragment.
         */
        @JvmStatic
        fun newInstance(username: String) =
            CheckInFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_USERNAME, username)
                }
            }
    }
}