package com.example.study

import android.content.Context
import android.os.Environment
import dalvik.system.DexClassLoader

/**
 * @author : Levi
 * @date   : 2020/6/30 9:47 AM
 * @desc   :
 */
object PluginUtils {

    private val apkPath =
        Environment.getExternalStorageDirectory().absolutePath + "/pluginmodel.apk"

    @JvmStatic
    fun loadPlugin(context: Context) {

        try {
            /** 获取PathList  */
            val baseDexClassLoaderClass = Class.forName("dalvik.system.BaseDexClassLoader")
            val pathListField = baseDexClassLoaderClass.getDeclaredField("pathList")
            pathListField.isAccessible = true


            /**
             * 获取插件的dexElements
             */

            /** 获取插件的pathList */
            val pluginClassLoader =
                DexClassLoader(apkPath, context.cacheDir.absolutePath, null, context.classLoader)
            val pluginPathList = pathListField.get(pluginClassLoader)

            /** 获取pathList 中的dexElements */
            val pluginPathListClass = pluginPathList.javaClass
            val pluginDexElementsField = pluginPathListClass.getDeclaredField("dexElements")
            pluginDexElementsField.isAccessible = true
            val pluginDexElements: Array<Any> =
                pluginDexElementsField.get(pluginPathList) as Array<Any>


            /**
             * 获取宿主的dexElements
             */
            val hostClassLoader = context.classLoader
            val hostPathList = pathListField.get(hostClassLoader)

            val hostPathListClass = hostPathList.javaClass
            val hostDexElementsField = hostPathListClass.getDeclaredField("dexElements")
            hostDexElementsField.isAccessible = true
            val hostDexElements: Array<Any> = hostDexElementsField.get(hostPathList) as Array<Any>

            /**
             * 合并宿主的dexElements和插件dexElements
             */

            val dexElements = java.lang.reflect.Array.newInstance(
                hostDexElements.javaClass.componentType,
                hostDexElements.size + pluginDexElements.size
            )
            System.arraycopy(hostDexElements, 0, dexElements, 0, hostDexElements.size)
            System.arraycopy(
                pluginDexElements,
                0,
                dexElements,
                hostDexElements.size,
                pluginDexElements.size
            )

            /**
             * 设置到数组的dexElements中
             */

            hostDexElementsField.set(hostPathList, dexElements)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
