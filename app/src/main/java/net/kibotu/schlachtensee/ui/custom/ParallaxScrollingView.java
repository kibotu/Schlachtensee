package net.kibotu.schlachtensee.ui.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import net.kibotu.logger.Logger;
import net.kibotu.schlachtensee.R;

import java.util.UUID;

import static com.exozet.android.core.extensions.ResourceExtensions.getPx;

/**
 * Created by <a href="https://about.me/janrabe">Jan Rabe</a>.
 */

public class ParallaxScrollingView extends SurfaceView implements SurfaceHolder.Callback {

    private UUID uuid = UUID.randomUUID();

    public boolean loggingEnabled = false;

    private Rect clipBounds = new Rect();
    private int maxBitmapHeight;
    private BitmapShader shader;
    private Paint paint;
    private RectShape shape;
    private float translateX;
    private float translateY;
    private Matrix matrix;
    private Bitmap image;
    private int drawable;

    private void log(String message) {
        if (loggingEnabled)
            Logger.v(message);
    }

    public ParallaxScrollingView(Context context) {
        super(context);
        init(null);
    }

    public ParallaxScrollingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ParallaxScrollingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ParallaxScrollingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (isInEditMode())
            return;

        log("[init] " + uuid);

        setWillNotDraw(false);
        getHolder().addCallback(this);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);
        setBackgroundResource(android.R.color.transparent);

        paint = new Paint();
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setStyle(Paint.Style.FILL);

        matrix = new Matrix();
        shape = new RectShape();

        final TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ParallaxScrollingView, 0, 0);
        try {
            speed = ta.getDimension(R.styleable.ParallaxScrollingView_speed, 0);
            drawable = ta.getResourceId(R.styleable.ParallaxScrollingView_src, 0);

            create();

            freeResources();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ta.recycle();
        }
    }

    private void create() {
        log("[create] " + uuid);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        image = BitmapFactory.decodeResource(getResources(), drawable, options);
        maxBitmapHeight = Math.max(image.getHeight(), maxBitmapHeight);

        shader = new BitmapShader(image,
                Shader.TileMode.REPEAT,
                Shader.TileMode.REPEAT);
        shape.resize(getWidth(), image.getHeight());
        paint.setShader(shader);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        log("[surfaceCreated] " + uuid);

        if (image == null)
            create();

        start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        log("[surfaceChanged] " + uuid);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        log("[surfaceDestroyed] " + uuid);

        stop();

        freeResources();
    }

    private void freeResources() {

//        if (SDK_INT >= HONEYCOMB && SDK_INT < JELLY_BEAN_MR2)
//            return;

        if (image != null && !image.isRecycled()) {
            log("[freeResources] " + uuid + " recycle bitmap");
            try {
                image.recycle();
            } catch (Exception e) {
                if (loggingEnabled)
                    e.printStackTrace();
            } finally {
                image = null;
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (isInEditMode())
            return;

        if (canvas == null)
            return;

        canvas.getClipBounds(clipBounds);
        matrix.reset();
        matrix.preTranslate(translateX + dX, translateY);
        shader.setLocalMatrix(matrix);
        shape.draw(canvas, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, maxBitmapHeight);
    }

    public void setOffset(float dX, float dY) {
        this.translateX = dX;
        this.translateY = dY;
        postInvalidate();
    }

    boolean isRunning;

    public void start() {
        if (isRunning)
            return;

        log("[start] " + uuid);

        isRunning = true;

        if (offsetScrolling == null)
            offsetScrolling = createOffsetScrolling();

        removeCallbacks(offsetScrolling);
        post(offsetScrolling);
    }

    public void stop() {
        log("[stop] " + uuid);
        removeCallbacks(offsetScrolling);
        isRunning = false;
    }

    float speed;
    float dX = 0;
    private Runnable offsetScrolling;

    @NonNull
    private Runnable createOffsetScrolling() {
        return () -> {
            dX += speed;
            post(offsetScrolling);
            postInvalidate();
        };
    }

    /**
     * @param speed Dp per frame.
     */
    public void setSpeed(float speed) {
        this.speed = getPx((int) speed);
    }
}
