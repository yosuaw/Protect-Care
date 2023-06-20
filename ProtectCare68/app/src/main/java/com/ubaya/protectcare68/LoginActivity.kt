package com.ubaya.protectcare68

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    companion object {
        val SHARED_USERNAME = "SHARED_USERNAME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var sharedName = packageName
        var shared = getSharedPreferences(sharedName, Context.MODE_PRIVATE)
        var username = shared.getString(SHARED_USERNAME, null)
        if (username != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnLogin.setOnClickListener {
            val queue = Volley.newRequestQueue(this)
            val url = "https://ubaya.fun/native/160419038/login.php"
            val stringRequest = object : StringRequest(
                Method.POST,
                url,
                Response.Listener {
                    Log.d("apiresult", it)
                    val obj = JSONObject(it)
                    if(obj.getString("result") == "OK") {
                        Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show()

                        var editor = shared.edit()
                        editor.putString(SHARED_USERNAME, txtUsername.text.toString())
                        editor.apply()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                },
                Response.ErrorListener {
                    Log.e("apierror", it.message.toString())
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    return hashMapOf("username" to txtUsername.text.toString(),
                                     "password" to txtPassword.text.toString())
                }
            }
            queue.add(stringRequest)
        }
    }
}