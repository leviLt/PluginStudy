package com.example.study

import android.app.Application

/**
 * @author : Levi
 * @date   : 2020/6/30 9:42 AM
 * @desc   :
 */
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        //加载插件dex
        PluginUtils.loadPlugin(this)
    }
}