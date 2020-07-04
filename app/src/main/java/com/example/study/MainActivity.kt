package com.example.study

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.danmalibrary.DanmaAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var adapter: DanmaAdapter<String>? = null
    private var count = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        try {
//            val pluginTestClass = Class.forName("com.example.pluginmodel.PluginTest")
//            val declaredMethod = pluginTestClass.getDeclaredMethod("printLog")
//            declaredMethod.isAccessible = true
//            declaredMethod.invoke(pluginTestClass.newInstance())
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

        inidata()

        btnAddDanma.setOnClickListener {
            count++
            adapter?.addData("测试数据===$count")
        }

        screenChange.setOnClickListener {
            adapter?.apply {
                isLandscape = !isLandscape
            }
        }
    }

    private fun inidata() {
        adapter = DanmaAdapter<String>(this)
        danma.setAdapter(adapter!!)

        adapter!!.setDataList(
            listOf(
                "1",
                "2",
                "3",
                "4",
                "5",
                "6"
            )
        )


    }
}
