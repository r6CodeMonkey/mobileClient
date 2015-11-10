package oddymobstar.graphics;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by timmytime on 08/08/15.
 */
public class GridGLSurfaceView extends GLSurfaceView {

    private GridGLRenderer renderer;

    private float mPreviousX;
    private float mPreviousY;

    private float mDensity;

    public GridGLSurfaceView(Context context) {
        super(context, null);
        //     init(context);
    }

    public GridGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //   init(context);


    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
      /*  if (event != null)
        {
            float x = event.getX();
            float y = event.getY();

            if (event.getAction() == MotionEvent.ACTION_MOVE)
            {
                if (renderer != null)
                {
                    float deltaX = (x - mPreviousX) / mDensity / 2f;
                    float deltaY = (y - mPreviousY) / mDensity / 2f;

                    renderer.mDeltaX += deltaX;
                    renderer.mDeltaY += deltaY;
                }
            }

            mPreviousX = x;
            mPreviousY = y;

            return true;
        }
        else
        {*/
        return super.onTouchEvent(event);
        //}
    }


    public void init(Context context, float density) {
        setEGLContextClientVersion(2);

        renderer = new GridGLRenderer(context);
        mDensity = density;

        setRenderer(renderer);

//        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }


}