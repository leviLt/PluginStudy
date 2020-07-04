package com.example.danmalibrary

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import java.lang.Thread.sleep
import java.util.*
import java.util.concurrent.Executors

/**
 * @author : Levi
 * @date   : 2020/7/3 12:17 PM
 * @desc   : 弹幕队列管理
 */
class QueueManager<T> {

    /**
     * 弹幕实体类队列池
     */
    private val stackEntity: MutableList<T> by lazy { Collections.synchronizedList(mutableListOf<T>()) }

    private var queueExecute: ((T) -> Unit)? = null

    private var isRun = false

    /**
     * 获取队列的时间间隔
     */
    private var intervalTime = 1000L

    /**
     * 创建一个单线程池
     */
    private val singleThreadExecutor by lazy { Executors.newSingleThreadExecutor() }


    private var isLandscape = false

    private val mainHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == 1) {
                msg.obj?.apply {
                    queueExecute?.invoke(this as T)
                }
            }
        }
    }


    private fun startLoop() {
        if (isRun) {
            Log.e("QueueManager", "singleThreadExecutor正在执行")
            return
        }
        singleThreadExecutor.execute {
            Log.e("QueueManager", "singleThreadExecutor执行===>")
            isRun = true
            //开启循环获取弹幕实体
            while (stackEntity.size > 0) {
                try {
                    synchronized(stackEntity) {
                        val entity = stackEntity[0]
                        stackEntity.removeAt(0)
                        val message = Message()
                        message.obj = entity
                        message.what = 1
                        mainHandler.sendMessage(message)
                    }
                    //休眠1s
                    sleep(intervalTime)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            isRun = false
        }
    }

    /**
     * 设置队列出栈方法
     */
    fun setQueueExecute(execute: (T) -> Unit) {
        this.queueExecute = execute
    }

    @Synchronized
    fun addQueueMessage(entity: T) {
        stackEntity.add(entity)
        startLoop()
    }

    @Synchronized
    fun addQueueList(list: List<T>?) {
        list ?: return
        stackEntity.addAll(list)
        startLoop()
    }

    fun setLandscape(value: Boolean) {
        this.isLandscape = value
        intervalTime = if (isLandscape) 500L else 1000L
    }

}