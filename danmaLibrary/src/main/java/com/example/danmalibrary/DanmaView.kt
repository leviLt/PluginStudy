package com.example.danmalibrary

import android.animation.Animator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author : Levi
 * @date   : 2020/7/2 8:07 PM
 * @desc   : 弹幕
 */
class DanmaView : FrameLayout {

    /** 弹幕适配器  简单的适配器 主要是为了防止产品动不动就改动逻辑
     *  里面包含了View的缓存 */
    private var danmaAdapter: DanmaAdapter<*>? = null

    /**
     * 目前项目只有单行和双行之分
     * 单行：一行显示
     * 双行：当前在上方，下一条则在下方，以此类推
     */
    private var isSingleLine = true

    /** 是否已经触发handler */
    private var isSendMessage = AtomicBoolean(false)

    /**
     * 上一次View显示的位置
     */
    private val lastView: LastView by lazy { LastView(0, 1) }

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, default: Int) : super(
        context,
        attributeSet,
        default
    )

    init {
        //不限制子View的位置
        clipChildren = false
    }

    /**
     * 添加弹幕的View
     */
    @Synchronized
    private fun addDanmaView(targetView: TextView, entity: String) {
        //view 绑定entity
        targetView.text = entity
        targetView.setTextColor(Color.BLACK)
        targetView.setBackgroundColor(Color.TRANSPARENT)
        targetView.setPadding(8, 4, 8, 4)
        //量测宽高
        val widthSpec: Int = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        val heightSpec: Int = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        targetView.measure(widthSpec, heightSpec)
        val measuredHeight: Int = targetView.measuredHeight //测量得到的textview的高
        val measuredWidth: Int = targetView.measuredWidth //测量得到的textview的宽
        //设置宽高
        val layoutParams = LayoutParams(measuredWidth, measuredHeight)
        layoutParams.leftMargin = this.measuredWidth
        danmaAdapter?.apply {
            if (isLandscape) {
                //上一个弹幕显示的位置
                if (lastView.position == 0) {
                    // 当前显示在下方
                    layoutParams.topMargin = measuredHeight + 10
                    lastView.position = 1
                } else {
                    //当前显示上方
                    lastView.position = 0
                    lastView.height = measuredHeight
                }
            } else {
                layoutParams.gravity = Gravity.CENTER_VERTICAL
            }
        }
        //添加到ViewGroup中
        addView(targetView, layoutParams)
        //开启动画
        targetView.post {
            Log.e(
                "DanmaView",
                "targetView: width=${targetView.measuredWidth}  height=${targetView.measuredHeight} x=${targetView.x}"
            )
            Log.e(
                "DanmaView",
                "targetView: left=${targetView.left}  top=${targetView.top} right=${targetView.right}"
            )
            Log.e(
                "DanmaView",
                "targetView: x=${targetView.x}  y=${targetView.y}"
            )
            //添加动画
            val translateAnim =
                DanmaAdapter.createTranslateAnim(
                    targetView,
                    fromX = 0f,
                    toX = -this.measuredWidth.toFloat() - measuredWidth
                )
            translateAnim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    Log.e(
                        "DanmaView",
                        "onAnimationEnd===>targetView: left=${targetView.left}  top=${targetView.top} right=${targetView.right}"
                    )
                    Log.e(
                        "DanmaView",
                        "onAnimationEnd====>targetView: x=${targetView.x}  y=${targetView.y}"
                    )
                    removeView(targetView)
                    danmaAdapter?.addViewToCache(targetView)
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {

                }
            })
            translateAnim.start()
        }
    }


    /**
     * 弹幕适配器
     */
    fun <T> setAdapter(adapter: DanmaAdapter<T>) {
        this.danmaAdapter = adapter
        //弹幕队列中出来的数据
        this.danmaAdapter?.setQueueExecute { view, entity ->
            if (view is TextView) {
                //添加View
                addDanmaView(view, entity = entity as String)
            }
        }
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        //清空缓存的View
        this.danmaAdapter?.removeAllView()
    }

}

data class LastView(
    //自身的高度
    var height: Int,
    //是显示在上方还是下方 0 上方  1 下方
    var position: Int
)