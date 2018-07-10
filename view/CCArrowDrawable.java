package cc.sdkutil.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Created by wangcong on 15-4-3.
 * 可与 android.support.v4.widget.DrawerLayout
 * 代码中：
 *      private ArrowDrawable arrowDrawable;
        private boolean flipped = false;
        private ImageView imageView;
        private DrawerLayout drawer;

         @Override
         protected void onCreate(Bundle savedInstanceState) {
             super.onCreate(savedInstanceState);
             setContentView(R.layout.activity_main);

             drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
             imageView = (ImageView) findViewById(R.id.drawer_indicator);

             arrowDrawable = new ArrowDrawable();
             imageView.setImageDrawable(arrowDrawable);

             drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                     if (slideOffset >= .995) {
                         flipped = true;
                         arrowDrawable.setFlip(flipped);
                     } else if (slideOffset <= .005) {
                         flipped = false;
                         arrowDrawable.setFlip(flipped);
                     }

                    arrowDrawable.setParameter(slideOffset);
                 }
             });

             imageView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     if (drawer.isDrawerVisible(START)) {
                        drawer.closeDrawer(START);
                     } else {
                        drawer.openDrawer(START);
                     }
                 }
             });
         }
    Res中：
         <FrameLayout
         android:layout_width="match_parent"
         android:layout_height="@dimen/actionbar_dimen"
         android:background="@color/dark_gray"
         >

         <ImageView
         android:id="@+id/drawer_indicator"
         android:layout_width="@dimen/actionbar_dimen"
         android:layout_height="@dimen/actionbar_dimen"
         android:scaleType="centerInside"
         android:layout_gravity="start"
         />

         </FrameLayout>

         <android.support.v4.widget.DrawerLayout
         android:id="@+id/drawer_layout"
         android:layout_width="match_parent"
         android:layout_height="0dp"
         android:layout_weight="1"
         />
 */
public class CCArrowDrawable extends Drawable {

    private float parameter;
    private float lineLength = 50;
    private float strokeWidth = 5;
    private float gap = (float) (25 - 1.6 * strokeWidth);

    private boolean flip;
    private int strokeColor;
    private int strokeAlpha;

    private float rotation;
    private float rotation2;

    public CCArrowDrawable() {

        this.strokeColor = Color.WHITE;
        this.strokeAlpha = 255;
    }

    @Override
    public void draw(Canvas canvas) {
//        Log.i("width;height:", "[" + width + ";" + height + "]");

        rotation = parameter * 180;
        if (flip) {
            rotation = 360 - rotation;
        }
        rotation2 = parameter * 45;

        float seniline = (float) ((lineLength / 2.0f) * Math.cos(rotation2 * Math.PI / 180));

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(strokeColor);
        paint.setAlpha(strokeAlpha);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);

        canvas.save();
        canvas.translate(40, 40);
        canvas.rotate(rotation);
        canvas.drawLine(-25, 0, 25, 0, paint);

        canvas.save();
        canvas.rotate(rotation2);
        canvas.drawLine(-seniline, -gap, seniline, -gap, paint);
        canvas.restore();

        canvas.save();
        canvas.rotate(-rotation2);
        canvas.drawLine(-seniline, gap, seniline, gap, paint);
        canvas.restore();

        canvas.restore();

    }

    public void setParameter(float parameter) {
        if (parameter > 1 || parameter < 0) {
            throw new IllegalArgumentException("Value must be between 1 and zero inclusive!");
        }
        this.parameter = parameter;
        Log.i("parameter =", "" + parameter);
        invalidateSelf();
    }

    public void setFlip(boolean flip) {
        this.flip = flip;
        invalidateSelf();
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        invalidateSelf();
    }

    public void setStrokeAlpha(int strokeAlpha) {
        this.strokeAlpha = strokeAlpha;
        invalidateSelf();
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
