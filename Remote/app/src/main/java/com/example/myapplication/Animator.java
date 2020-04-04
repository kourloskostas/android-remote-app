package com.example.myapplication;


import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class Animator implements Runnable {

    ImageView img ;

    Animator (ImageView img ){
        this.img = img;

    }

    public void run() {

            for (int i = 0 ; i <50; i++ )
            {

                img.getLayoutParams().width = 150 + i;
                img.getLayoutParams().height = 150 + i;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

    }

}
