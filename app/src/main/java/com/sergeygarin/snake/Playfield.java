package com.sergeygarin.snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/* Игровое поле. Рисует, обрабатывает события, агрегирует другие классы.*/
public class Playfield extends SurfaceView implements SurfaceHolder.Callback
{
    private GameThread _thread;
    private GameEngine engine;

    private Bitmap tile;
    private Paint wall;
    private Paint snake;
    private Paint apple;

    private double initX;
    private double finalX;

    private double initY;
    private double finalY;

    public Playfield(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        getHolder().addCallback(this);
        setFocusable(true);
        _thread = new GameThread(getHolder(),this);
        tile = BitmapFactory.decodeResource(getResources(),
                R.drawable.tiles);
        setupPaints();
    }

    public void setupPaints()
    {
        wall = new Paint();
        snake = new Paint();
        apple = new Paint();
        apple.setColorFilter(new LightingColorFilter(Color.RED, 1));
        wall.setColorFilter(new LightingColorFilter(Color.WHITE, 1));
        snake.setColorFilter(new LightingColorFilter(Color.GREEN, 1));
    }

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {}

    public void surfaceCreated(SurfaceHolder arg0) {
        if (_thread.getState() == Thread.State.TERMINATED)
        {
            _thread = new GameThread(getHolder(),this);
            _thread.setRunning(true);
            _thread.start();
        }
        else
        {
            _thread.setRunning(true);
            _thread.start();
        }

        engine = new GameEngine(getWidth(),getHeight(),tile);
        engine.initSnake();
    }
    public void surfaceDestroyed(SurfaceHolder arg0)
    {
        boolean retry = true;
        _thread.setRunning(false);
        while (retry)
        {
            try
            {
                _thread.join();
                retry = false;
            }
            catch (InterruptedException e)
            {
                //игнорируем ошибки
            }
        }
    }
    public void onDraw(Canvas canvas)
    {
        canvas.drawColor(Color.BLACK);

        if (engine == null) return;

        for(int i = 0; i < engine.getWalls().size() - 1; i += 2)
            canvas.drawBitmap(tile, engine.getWalls().get(i), engine.getWalls().get(i + 1), wall);

        for(int i = 0; i < engine.getSnake().size() - 1; i += 2)
            canvas.drawBitmap(tile, engine.getSnake().get(i), engine.getSnake().get(i + 1), snake);

        canvas.drawBitmap(tile, engine.getApple()[0], engine.getApple()[1] , apple);
    }

    public boolean onTouchEvent(MotionEvent event)
    {
        if(engine.getPlaying())
        {
            if(event.getAction() == MotionEvent.ACTION_DOWN)
            {
                initX=event.getX();
                initY=event.getY();
            }

            double totY;
            double totX;

            if(event.getAction() == MotionEvent.ACTION_MOVE)
            {
                finalX = event.getX();
                finalY = event.getY();

                totY = Math.abs(Math.abs(finalY) - Math.abs(initY));
                totX = Math.abs(Math.abs(finalX) - Math.abs(initX));

                float threshold=20;

                if(totY > threshold || totX > threshold)
                {
                    if(totX > totY)
                    {
                        if(finalX > initX) engine.setDirection(1);
                        else if (finalX < initX) engine.setDirection(3);
                    }
                    else if(totY > totX)
                    {
                        if(finalY > initY) engine.setDirection(0);
                        else if (finalY < initY) engine.setDirection(2);
                    }

                    initX = finalX;
                    initY = finalY;
                }
            }
        }
        return true;
    }
    public void startGame()
    {
        engine.setPlaying(true);
        engine.initSnake();
    }
    public boolean gameOn()
    {
        return (engine != null);
    }
    public int getScore()
    {
        return engine.getScore();
    }
    public boolean lost()
    {
        return engine.lost();
    }
    public void pause()
    {
        engine.pause();
    }
}