package com.example.study

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try {
            val pluginTestClass = Class.forName("com.example.pluginmodel.PluginTest")
            val declaredMethod = pluginTestClass.getDeclaredMethod("printLog")
            declaredMethod.isAccessible = true
            declaredMethod.invoke(pluginTestClass.newInstance())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
