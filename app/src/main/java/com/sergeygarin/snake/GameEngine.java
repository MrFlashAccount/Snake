package com.sergeygarin.snake;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import android.graphics.Bitmap;
import android.os.Handler;

/*Основной класс, движок игры. просчитывает шаги, создает окно*/
public class GameEngine
{
    private ArrayList<Float> walls;
    private ArrayList<Float> snake;
    private ArrayList<Integer> direction;
    private float[] apple;

    private int width;
    private int height;
    private int tileWidth;
    private int tileHeight;
    private int length;
    private int currentd;

    private Timer snakeMover;
    private TimerTask task;
    private Handler timerHandle;

    private int score;

    boolean playing;
    boolean lost;
    boolean paused;

    public GameEngine(int _width, int _height, Bitmap tile)
    {
        paused=false;
        walls = new ArrayList<>();
        snake = new ArrayList<>();
        direction = new ArrayList<>();
        width=_width;
        height=_height;
        tileWidth = tile.getWidth();
        tileHeight= tile.getHeight();
        setWalls();
        direction.add(3);
        length=5;
        timerHandle = new Handler();
        score=0;
        apple = new float[2];
        generateApple();
        playing = false;
        lost=false;
    }

    /* Проверяет, съела ли змея яблоко. */
    public void checkCollide()
    {
        if(snake.get(0) == apple[0] && snake.get(1) == apple[1])
        {
            score++;
            generateApple();
            growSnake();

            if (score < 15) updateTimer(((10000 / (height / tileHeight))) / ((score/3) + 1));

            else updateTimer(((10000 / (height / tileHeight))) / 6);
        }
        /* Проверяем, не встретилась ли змея со стеной и сама с собой*/
        for(int i = 0; i < walls.size() - 1; i += 2)
            if(snake.get(0).equals(walls.get(i)) && snake.get(1).equals(walls.get(i + 1)))
            {
                lost=true;
                playing=false;
                snakeMover.cancel();
            }

        for(int i = 2; i < snake.size() - 1; i += 2)
            if(snake.get(0).equals(snake.get(i)) && snake.get(1).equals(snake.get(i + 1)))
            {
                lost=true;
                playing=false;
                snakeMover.cancel();
            }
    }

    public boolean getPlaying()
    {
        return playing;
    }

    public void setPlaying(boolean play)
    {
        playing = play;
    }

    public void updateTimer(double d)
    {
        if(snakeMover != null) snakeMover.cancel();

        snakeMover=new Timer();
        task = new TimerTask()
        {
            @Override
            public void run()
            {
                timerHandle.post(new Runnable()
                {
                    public void run()
                    {
                        if(playing) moveSnake();
                    }
                });
            }
        };
        snakeMover.schedule(task, 0, (long) d);
    }
    public void generateApple()
    {
        int max = width/tileWidth -3;
        int t = (int)((Math.random()*10000)%max) + 2;

        apple[0]=t*tileWidth;

        max = height/tileHeight -7;
        t=(int) ((Math.random()*10000)%max) + 6;

        apple[1]=t*tileHeight;
    }

    public void initSnake()
    {
        length=5;
        direction = new ArrayList<>();
        direction.add(3);
        lost=false;
        snake = new ArrayList<>();
        score=0;
        for(int i = 0; i < length; i++)
        {
            snake.add((float)(tileWidth * (20)) + (tileWidth * i));
            snake.add((float)(tileHeight * (20)));
        }
        updateTimer(10000/(height/tileHeight));
    }

    public void setWalls()
    {
        for(int x = 0; x <= width; x += tileWidth)
            for (int y = tileHeight * 5; y <= height; y += tileHeight)
                if (x == 0 || x == width - tileWidth || y == tileHeight * 5 || y >= height - tileHeight)
                {
                    walls.add((float) x);
                    walls.add((float) y);
                }
    }

    public ArrayList<Float> getWalls()
    {
        return walls;
    }

    public ArrayList<Float> getSnake()
    {
        return snake;
    }

    public void growSnake()
    {
        snake.add(snake.get(snake.size() - 2));
        snake.add(snake.get(snake.size() - 2));
    }

    public void setDirection(int d)
    {
        direction.add(d);
        sanitizeDirection();
    }

    public void sanitizeDirection()
    {
        if (direction.size() <= 0) return;

        if(direction.get(0) == currentd || direction.get(0) == (currentd + 2) % 4)
            direction.remove(0);

        for(int i = 1; i < direction.size(); i++)
            //if(i < direction.size())
            if(direction.get(i) == direction.get(i - 1) || direction.get(i) == (direction.get(i - 1) + 2) % 4)
                direction.remove(i);
    }

    public void moveSnake()
    {
        for(int i = snake.size()-1; i >= 0; i -= 2)
        {
            if(i > 2)
            {
                snake.set(i, snake.get(i - 2));
                snake.set(i - 1, snake.get(i - 3));
            }
            else
            {
                if(direction.size() > 0)
                    currentd = direction.remove(0);

                switch(currentd)
                {
                    case 0:
                        snake.set(1, snake.get(1) + tileHeight);
                        break;
                    case 1:
                        snake.set(0, snake.get(0) + tileWidth);
                        break;
                    case 2:
                        snake.set(1, snake.get(1) - tileHeight);
                        break;
                    case 3:
                        snake.set(0, snake.get(0) - tileWidth);
                        break;
                }
            }
        }

        checkCollide();
    }

    public void pause()
    {
        if(!paused)
        {
            snakeMover.cancel();
            paused=true;
        }
        else
        {
            paused=false;
            updateTimer((10000/(height/tileHeight))/((score/3)+1));
        }
    }

    public int getScore()
    {
        return score;
    }
    public boolean lost()
    {
        return lost;
    }
    public float[] getApple()
    {
        return apple;
    }
}