package oddymobstar.graphics;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

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
    }

    public GridGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void init(Context context, float density) {
        setEGLContextClientVersion(2);

        renderer = new GridGLRenderer(context);
        mDensity = density;

        setOnTouchListener(new View.OnTouchListener() {

            float previousX, previousY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null) {

                    final float normalizedX = (event.getX() / (float) v.getWidth()) * 2 - 1;
                    //android doesnt like c style - as it turns out when not wrapped around all calcs.
                    final float normalizedY = ((event.getY() / (float) v.getHeight()) * 2 - 1)*-1;

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        previousX = event.getX();
                        previousY = event.getY();

                        queueEvent(new Runnable() {
                            @Override
                            public void run() {

                                renderer.handleTouchPress(normalizedX, normalizedY);

                            }
                        });

                        //handle touch

                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

                        final float deltaX = event.getX() - previousX;
                        final float deltaY = event.getY() - previousY;

                        //handle drag
                        queueEvent(new Runnable() {
                            @Override
                            public void run() {

                                renderer.handleTouchDrag(normalizedX,normalizedY);
                                renderer.handleCamera(deltaX, deltaY);
                            }
                        });
                    }

                    return true;
                } else {
                    return false;
                }
            }

        });

        setRenderer(renderer);

    }


}