package com.ubaya.protectcare68

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_check_in.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import org.json.JSONObject

private const val  ARG_USERNAME = "username"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    var user: User? = null
    var username = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username = it.getString(ARG_USERNAME).toString()
        }

        val queue = Volley.newRequestQueue(context)
        var url = "https://ubaya.fun/native/160419038/get_user.php"
        var stringRequest = object : StringRequest(
            Method.POST,
            url,
            Response.Listener {
                Log.d("getuser_success", it)
                val obj = JSONObject(it)
                if (obj.getString("result") == "OK") {
                    var data= obj.getJSONArray("data")
                    for(i in 0 until data.length()) {
                        var playObj = data.getJSONObject(i)
                        with(playObj){
                            user = User(
                                getString("username"),
                                getString("name"),
                                getInt("vaccine_count")
                            )
                        }
                    }
                    txtNameProfile.text = "Name: ${user?.name.toString()}"
                    txtVaccinate.text = "Number of vaccination doses: ${user?.num_vaccine.toString()}"
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

        url = "https://ubaya.fun/native/160419038/get_most_visited.php"
        var stringRequest2 = object : StringRequest(
            Method.POST,
            url,
            Response.Listener {
                Log.d("getmostvisited_success", it)
                val obj = JSONObject(it)
                if (obj.getString("result") == "OK") {
                    var data= obj.getJSONObject("data")

                    txtTimes.text = "The most visited place: (${data.getString("count")} times visited)"
                    txtMostVisited.text = data.getString("place")
                    Picasso.get().load(data.getString("image")).into(imageViewPlaceProfile)
                } else if(obj.getString("result") == "ERROR") {
                    txtTimes.text = "The most visited place: -"
                    txtMostVisited.text = ""
                } else {
                    Log.e("getmostvisited_failed", obj.getString("message"))
                }
            },
            Response.ErrorListener {
                Log.d("getmostvisited_error", it.message.toString())
            }
        ) {
            override fun getParams(): MutableMap<String, String>? {
                return hashMapOf("username" to username)
            }
        }
        queue.add(stringRequest2)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)

        view.fabLogout.setOnClickListener {
            AlertDialog.Builder(requireActivity()).apply {
                setTitle("Confirmation")
                setMessage("Are you sure want to log out?")
                setPositiveButton("Yes") { _, _ ->
                    var sharedName = activity?.packageName
                    var shared = activity?.getSharedPreferences(sharedName, Context.MODE_PRIVATE)
                    shared?.edit()?.clear()?.commit()

                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                setNegativeButton("No", null)
                setCancelable(false)
                create().show()
            }
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param username Parameter 1.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(username: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_USERNAME, username)
                }
            }
    }
}