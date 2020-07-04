package com.example.danmalibrary

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.util.Log
import android.util.SparseIntArray
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.TextView
import java.util.*

/**
 * @author : Levi
 * @date   : 2020/7/2 8:25 PM
 * @desc   : 弹幕适配器
 */
class DanmaAdapter<T>(private val context: Context) {

    /** key 标识View的类型
     *  value 标识缓存的View
     *  目前都当做一种类型处理文字 */
    private val cacheViewsSparse: SparseIntArray by lazy { SparseIntArray() }

    /**
     * 现在项目只有一种
     * 数组  目前缓存最大值设置为20
     */
    private val cacheViews by lazy { Stack<TextView>() }

    /**
     * 队列管理
     */
    private val queueManager by lazy { QueueManager<T>() }
    private var queueExecute: ((K: View, T) -> Unit)? = null

    /**
     * 默认竖屏
     */
    var isLandscape = false
        set(value) {
            field = value
            queueManager.setLandscape(value)
        }


    init {
        /** 弹幕栈 出来一个一个 */
        queueManager.setQueueExecute {
            queueExecute?.invoke(getViewByType(entity = it), it)
        }
    }

    /** 添加View到缓存 */
    @Synchronized
    internal fun addViewToCache(view: TextView) {
        cacheViews.push(view)
        //控制缓存的大小
        if (cacheViews.size > 20) {
            cacheViews.removeAt(0)
        }
    }


    /** 移除View从缓存 */
    internal fun removeViewFromCache(view: View) {
        cacheViews.remove(view)
    }

    /** 移除所有的View */
    internal fun removeAllView() {
        cacheViews.removeAllElements()
    }

    /**
     * 获取缓存池View的大小
     */
    internal fun getCacheSize() = cacheViews.size

    /**
     * 通过类型获取View
     * @param entity 通过entity来区分是哪种类型的View
     */
    private fun getViewByType(entity: T): TextView {
        //将View转换之后返回
        return if (cacheViews.size > 0) {
            Log.e("DanmaView", "获取的缓存View")
            cacheViews.pop()
        } else {
            Log.e("DanmaView", "创建新的View")
            TextView(context)
        }
    }


    internal fun setQueueExecute(execute: (K: View, entity: T) -> Unit) {
        this.queueExecute = execute
    }


    /**
     * 添加弹幕的View，通过适配器
     */
    fun addData(entity: T) {
        queueManager.addQueueMessage(entity)
    }

    /**
     * 添加列表
     */
    fun setDataList(list: List<T>?) {
        queueManager.addQueueList(list)
    }


    companion object {
        /**
         * 创建位移动画
         */
        @JvmStatic
        fun createTranslateAnim(targetView: View, fromX: Float, toX: Float): Animator {
            val animation = ObjectAnimator.ofFloat(targetView, "translationX", fromX, toX)
            targetView.pivotX = 0f
            targetView.pivotY = 0f
            animation.duration = 3000
            animation.interpolator = LinearInterpolator()
            return animation
        }
    }
}


/**
 * 弹幕的类型
 */
enum class DanmaType(type: Int) {
    //文字
    TXT(1),

    //图片
    IMG(2),

    //礼物
    GIFT(3)
}