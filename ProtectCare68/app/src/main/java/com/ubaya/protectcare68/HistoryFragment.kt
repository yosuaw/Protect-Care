package com.ubaya.protectcare68

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_history.view.*
import org.json.JSONObject

private const val ARG_USERNAME = "username"

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryFragment : Fragment() {
    var username = ""
    var histories: ArrayList<History> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username = it.getString(ARG_USERNAME).toString()
        }

        histories.clear()
        val queue = Volley.newRequestQueue(context)
        val url = "https://ubaya.fun/native/160419038/get_history.php"
        val stringRequest = object : StringRequest(
            Method.POST,
            url,
            Response.Listener {
                Log.d("gethistory_success", it)
                val obj = JSONObject(it)
                if (obj.getString("result") == "OK") {
                    var data= obj.getJSONArray("data")
                    for(i in 0 until data.length()) {
                        var playObj = data.getJSONObject(i)
                        val parser = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        val formatter = java.text.SimpleDateFormat("EEEE, yyyy-MM-dd HH:mm")
                        val formattedCheckIn = formatter.format(parser.parse(playObj.getString("check_in")))
                        var formattedCheckOut = ""

                        if(!playObj.isNull("check_out")) {
                            formattedCheckOut = formatter.format(parser.parse(playObj.getString("check_out")))
                        }

                        with(playObj){
                            var history = History(
                                getString("username"),
                                getInt("vaccine_count"),
                                getString("location"),
                                formattedCheckIn,
                                formattedCheckOut
                            )
                            histories.add(history)
                        }
                    }
                    updateList()
                } else {
                    Log.e("gethistory_failed", obj.getString("message"))
                }
            },
            Response.ErrorListener {
                Log.d("gethistory_error", it.message.toString())
            }
        ) {
            override fun getParams(): MutableMap<String, String>? {
                return hashMapOf("username" to username)
            }
        }
        queue.add(stringRequest)
    }

    private fun updateList() {
        val linearLayoutManager = LinearLayoutManager(activity)
        view?.historyView?.let {
            it.layoutManager = linearLayoutManager
            it.setHasFixedSize(true)
            it.adapter = HistoryAdapter(histories)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param username Parameter 1.
         * @return A new instance of fragment HistoryFragment.
         */
        @JvmStatic
        fun newInstance(username: String) =
            HistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_USERNAME, username)
                }
            }
    }
}