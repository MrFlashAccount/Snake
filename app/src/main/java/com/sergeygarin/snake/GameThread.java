package com.sergeygarin.snake;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
public class GameThread extends Thread
{
    private SurfaceHolder _surfaceHolder;
    private Playfield _playField;
    private boolean _run = false;

    public GameThread(SurfaceHolder surfaceHolder, Playfield playField)
    {
        _surfaceHolder = surfaceHolder;
        _playField = playField;
    }

    public void setRunning(boolean run)
    {
        _run = run;
    }

    @Override
    public void run()
    {
        Canvas c;
        while(_run)
        {
            c = null;
            try
            {
                c = _surfaceHolder.lockCanvas(null);
                synchronized (_surfaceHolder)
                {
                    _playField.onDraw(c);
                }
            }
            finally
            {
                if(c != null)
                {
                    _surfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }
    }
}