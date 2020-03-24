package com.www.lvhttp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.www.net.HttpCreator
import com.www.net.LvHttp

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        HttpCreator.init("https://www.nuli100.com/JSCM_PD/")
            .log(true)

        LvHttp.get("index.php")
            .addParam("m", "App")
            .addParam("c", "APIUserIndex")
            .addParam("a", "getStyles")
            .send {
                val style = it.format(GetStylesBean::class.java)
                Toast.makeText(this, style.data.changeBottleThums, Toast.LENGTH_LONG).show()
            }
    }
}
