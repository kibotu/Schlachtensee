package net.kibotu.schlachtensee.ui.custom

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.graphics.drawable.shapes.RectShape
import android.os.Build
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import net.kibotu.logger.Logger.v
import net.kibotu.resourceextension.px
import net.kibotu.schlachtensee.R
import java.util.*

/**
 * Created by [Jan Rabe](https://about.me/janrabe).
 */
class ParallaxScrollingView : SurfaceView, SurfaceHolder.Callback {
    private val uuid = UUID.randomUUID()
    var loggingEnabled = false
    private val _clipBounds = Rect()
    private var maxBitmapHeight = 0
    private var shader: BitmapShader? = null
    private val paint: Paint by lazy {
        Paint().apply {
            isDither = true
            isAntiAlias = true
            isFilterBitmap = true
            style = Paint.Style.FILL
        }
    }
    private var shape: RectShape? = null
    private var translateX = 0f
    private var translateY = 0f
    private var _matrix: Matrix? = null
    private var image: Bitmap? = null
    private var drawable = 0
    private fun log(message: String) {
        if (loggingEnabled) v(message)
    }

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        if (isInEditMode) return
        log("[init] $uuid")
        setWillNotDraw(false)
        holder.addCallback(this)
        holder.setFormat(PixelFormat.TRANSLUCENT)
        setZOrderOnTop(true)
        setBackgroundResource(android.R.color.transparent)

        _matrix = Matrix()
        shape = RectShape()
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ParallaxScrollingView, 0, 0)
        try {
            _speed = ta.getDimension(R.styleable.ParallaxScrollingView_speed, 0f)
            drawable = ta.getResourceId(R.styleable.ParallaxScrollingView_src, 0)
            create()
            freeResources()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            ta.recycle()
        }
    }

    private fun create() {
        log("[create] $uuid")
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val image = BitmapFactory.decodeResource(resources, drawable, options)
        this.image = image
        maxBitmapHeight = image.height.coerceAtLeast(maxBitmapHeight)
        shader = BitmapShader(
            image,
            Shader.TileMode.REPEAT,
            Shader.TileMode.REPEAT
        )
        shape?.resize(width.toFloat(), image.height.toFloat())
        paint.shader = shader
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        log("[surfaceCreated] $uuid")
        if (image == null) create()
        start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        log("[surfaceChanged] $uuid")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        log("[surfaceDestroyed] $uuid")
        stop()
        freeResources()
    }

    private fun freeResources() {

        if (image != null && image?.isRecycled == false) {
            log("[freeResources] $uuid recycle bitmap")
            try {
                image?.recycle()
            } catch (e: Exception) {
                if (loggingEnabled) e.printStackTrace()
            } finally {
                image = null
            }
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (isInEditMode) return
        canvas.getClipBounds(_clipBounds)
        matrix?.reset()
        matrix?.preTranslate(translateX + dX, translateY)
        shader?.setLocalMatrix(matrix)
        shape?.draw(canvas, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(width, maxBitmapHeight)
    }

    fun setOffset(dX: Float, dY: Float) {
        translateX = dX
        translateY = dY
        postInvalidate()
    }

    private var isRunning = false

    fun start() {
        if (isRunning) return
        log("[start] $uuid")
        isRunning = true
        if (offsetScrolling == null) offsetScrolling = createOffsetScrolling()
        removeCallbacks(offsetScrolling)
        post(offsetScrolling)
    }

    fun stop() {
        log("[stop] $uuid")
        removeCallbacks(offsetScrolling)
        isRunning = false
    }

    private var _speed = 0f

    var dX = 0f

    private var offsetScrolling: Runnable? = null

    private fun createOffsetScrolling(): Runnable = Runnable {
        dX += _speed
        post(offsetScrolling)
        postInvalidate()
    }

    /**
     * @param speed Dp per frame.
     */
    fun setSpeed(speed: Float) {
        _speed = speed.toInt().px.toFloat()
    }
}