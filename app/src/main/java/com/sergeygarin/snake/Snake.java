package com.sergeygarin.snake;

import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.Window;

public class Snake extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.snake);

        final Playfield game =  (Playfield)findViewById(R.id.playField1);
        final Button start = (Button)findViewById(R.id.button1);
        final Button pause = (Button)findViewById(R.id.button2);
        final TextView score = (TextView)findViewById(R.id.textView1);
        final TextView scoreGame = (TextView)findViewById(R.id.textView2);
        final ImageView logo = (ImageView)findViewById(R.id.imageView1);
        final Button credit = (Button)findViewById(R.id.button3);
        final Builder credits = new AlertDialog.Builder(this);

        start.setBackgroundResource(R.drawable.button);

        credits.setTitle("О приложении");
        credits.setMessage("Приложение написано Гариным С.В. For fun and profit");
        credits.setPositiveButton("ok", null);

        score.setTextColor(Color.WHITE);
        score.setVisibility(View.GONE);
        score.setTextAppearance(getApplicationContext(), R.style.bold);

        pause.setVisibility(View.GONE);
        pause.setTextAppearance(getApplicationContext(), R.style.bold);
        pause.setText(R.string.pauseButton);

        credit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                credits.show();
            }
        });

        start.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                game.startGame();
                scoreGame.setVisibility(View.VISIBLE);
                scoreGame.setText(getString(R.string.score) + " 0");
                pause.setVisibility(View.VISIBLE);
                start.setVisibility(View.GONE);
                score.setVisibility(View.GONE);
                logo.setVisibility(View.GONE);
                credit.setVisibility(View.GONE);
            }
        });
        pause.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                game.pause();
            }
        });

        Timer timer=new Timer();
        TimerTask task;
        final Handler timerHandle = new Handler();

        task = new TimerTask()
        {
            @Override
            public void run()
            {
                timerHandle.post(new Runnable()
                {
                    public void run()
                    {
                        String gameText = getString(R.string.score) + " " + game.getScore();
                        scoreGame.setText(gameText);

                        if(game.gameOn() && game.lost())
                        {
                            String endText = getString(R.string.score) + " " + game.getScore();
                            start.setVisibility(View.VISIBLE);
                            scoreGame.setVisibility(View.GONE);
                            score.setVisibility(View.VISIBLE);
                            score.setText(endText);
                        }
                    }
                });
            }
        };
        timer.schedule(task, 1000, 500);
    }
}