package com.ubaya.protectcare68

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_check_out.view.*
import org.json.JSONObject

private const val ARG_USERNAME = "username"
private const val ARG_PLACE = "place"
private const val ARG_VACCINE = "vaccine"
private const val ARG_DATETIME = "datetime"
private const val ARG_CODE = "code"

/**
 * A simple [Fragment] subclass.
 * Use the [CheckOutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CheckOutFragment : Fragment() {
    var username = ""
    var place = ""
    var vaccCount = 0
    var datetime = ""
    var code = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username = it.getString(ARG_USERNAME).toString()
            place = it.getString(ARG_PLACE).toString()
            vaccCount = it.getInt(ARG_VACCINE)
            datetime = it.getString(ARG_DATETIME).toString()
            code = it.getString(ARG_CODE).toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_check_out, container, false)

        view.txtLocation.text = place
        view.txtCheckIn.text = "Check in time: $datetime"

        if(vaccCount == 1) {
            view.cardCheckOut.setCardBackgroundColor(Color.parseColor("#FFFF00"))
        } else if(vaccCount > 1) {
            view.cardCheckOut.setCardBackgroundColor(Color.parseColor("#4DCB63"))
        }

        view.btnCheckOut.setOnClickListener {
            val queue = Volley.newRequestQueue(context)
            val url = "https://ubaya.fun/native/160419038/check_out.php"
            val stringRequest = object : StringRequest(
                Method.POST,
                url,
                Response.Listener {
                    Log.d("checkout_success", it)
                    val obj = JSONObject(it)
                    if (obj.getString("result") == "OK") {
                        Toast.makeText(context, "Check out successful!", Toast.LENGTH_SHORT).show()
                        var mainActivity: MainActivity = activity as MainActivity
                        mainActivity.checkOut(username)
                    } else {
                        Log.e("checkout_failed", obj.getString("message"))
                    }
                },
                Response.ErrorListener {
                    Log.d("checkout_error", it.message.toString())
                }
            ) {
                override fun getParams() = hashMapOf(
                    "username" to username,
                    "code" to code
                )
            }
            queue.add(stringRequest)
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param username Parameter 1.
         * @param place Parameter 2.
         * @param vaccineCount Parameter 3.
         * @param datetime Parameter 4.
         * @param code Parameter 5.
         * @return A new instance of fragment CheckOutFragment.
         */
        @JvmStatic
        fun newInstance(username: String, place: String, vaccineCount: Int, datetime: String, code: String) =
            CheckOutFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_USERNAME, username)
                    putString(ARG_PLACE, place)
                    putInt(ARG_VACCINE, vaccineCount)
                    putString(ARG_DATETIME, datetime)
                    putString(ARG_CODE, code)
                }
            }
    }
}