package com.example.myapplication;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {

    private final int REQ_CODE = 100;
    TextView temperView;
    TextView modeBtn;
    View parentLayout ;
    View sideLayout ;
    TextView modeView;
    MediaPlayer mp;
    String[] timestamps = { "15 min", "30 min", "1 hour", "1:30 hour", "2 hours"};
    String[] modes = { "Cool", "Heat", "Auto", "Fan", "Dehum"};
    int num_of_modes = modes.length;
    int mode_idx = 0;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parentLayout = findViewById(android.R.id.content);
        sideLayout = getLayoutInflater().inflate(R.layout.side_activity,null);
        temperView = findViewById(R.id.temperature);
        modeView = findViewById(R.id.mode);
        modeBtn = findViewById(R.id.modeBtn);
        mp = MediaPlayer.create(this,R.raw.flick);
        final ImageView outerCircle = findViewById(R.id.outerCircle);
        final TextView temperature = findViewById(R.id.temperature);
        int visibility;

        sideLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_main);
            }
        });

/*
        sideLayout.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            @Override
            public void onSwipeRight() {
                // Whatever
                setContentView(R.layout.activity_main);
            }
        });

 */
        parentLayout.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            @Override
            public void onSwipeLeft() {
                // Whatever
                setContentView(R.layout.side_activity);
            }
        });



        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        Spinner spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        //Creating the ArrayAdapter instance having the timestamp list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,timestamps);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        Snackbar.make(parentLayout, "Linked to A/C", Snackbar.LENGTH_LONG)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                .show();

        //Voice Recognition commands
        ImageView speak = findViewById(R.id.speak);
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
                try {
                    startActivityForResult(intent, REQ_CODE);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Sorry your device not supported",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });




        // ON/OFF Listener
        ImageView innerCircle = findViewById(R.id.innerCircle);
        innerCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               int  visibility = outerCircle.getVisibility();
                mp.start();

                if (visibility == View.VISIBLE)
                {
                    outerCircle.setVisibility(View.INVISIBLE);
                    temperature.setText("--");
                }
                else if (visibility == View.INVISIBLE)
                {
                    outerCircle.setVisibility(View.VISIBLE);
                    temperature.setText("22 C");
                }
                /*
                Animator animator = new Animator (outerCircle);
                new Thread(animator).start();

                 */
            }
        });

        // - Decr Listener
        ImageView decr = findViewById(R.id.dcrCrcl);
        decr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String tempertxt = (String) temperature.getText();

                int temp = Integer.parseInt(tempertxt.substring(0,2)) - 1;

                temperView.setText(temp + " C");
                outerCircle.setVisibility(View.VISIBLE);
                mp.start();    }
        });


        // + Incr Listener
        ImageView incr = findViewById(R.id.incCrcl);
        incr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tempertxt = (String) temperature.getText();
                if (tempertxt.equals("--")){return;}
                int temp = Integer.parseInt(tempertxt.substring(0,2)) + 1;

                temperView.setText(temp + " C");
                outerCircle.setVisibility(View.VISIBLE);
                mp.start();    }
        });

        //Mode Button Listener
        modeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                outerCircle.setVisibility(View.VISIBLE);
                mp.start();
                mode_idx++;
                mode_idx = mode_idx%num_of_modes;

                modeView.setText(modes[mode_idx]);

            }
        });

        //Help Popup

        final ImagePopup imagePopup = new ImagePopup(this);
        imagePopup.setBackgroundColor(getColor(R.color.colorPrimary));
        ImageView imageView = (ImageView) findViewById(R.id.Info);
        ImageView helpView = (ImageView) findViewById(R.id.help);
        imagePopup.initiatePopup(helpView.getDrawable()); // Load Image from Drawable

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /** Initiate Popup view **/
                imagePopup.viewPopup();

            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    String text = result.get(0).toLowerCase(); // Voice command given by user
                    String command_out = "";
                    ImageView outerCircle = findViewById(R.id.outerCircle);
                    String currTemp = (String) temperView.getText();

                    //Check for KeyWords
                    if(text.contains("open")||text.contains("on"))
                    {
                        command_out = "A/C ON";
                        temperView.setText("22 C");
                        outerCircle.setVisibility(View.VISIBLE);
                        mp.start();
                    }
                    else if(text.contains("mode"))
                    {
                        command_out = "Mode Set";
                        mp.start();
                    }
                    else if(text.contains("set")||text.contains("change"))
                    {
                        String Temperature = null;
                        String [] words = text.split(" ");
                        for (String str : words){
                            try
                            {
                                int tmp = (Integer.parseInt(str));
                                Temperature = String.valueOf(tmp);
                                break;
                            } catch (NumberFormatException nfe){}
                        }
                        if (Temperature == null) {command_out = "Sorry didn't catch that"; break;}
                        outerCircle.setVisibility(View.VISIBLE);
                        mp.start();
                        temperView.setText(Temperature + " C");
                        command_out = "Temperature Set";
                    }
                    else if(text.contains("close")||text.contains("off")){
                        command_out = "A/C OFF";
                        temperView.setText("--");
                        outerCircle.setVisibility(View.INVISIBLE);
                        mp.start();

                    }
                    else if(text.contains("hot")||text.contains("sweating")||text.contains("furnace")){

                        if (currTemp.equals("--"))
                        {
                            outerCircle.setVisibility(View.VISIBLE);
                            mp.start();
                            command_out = "Setting Optimal 22 C";
                            temperView.setText("22 C");
                        }else {
                            int temp = Integer.parseInt(currTemp.substring(0,2)) - 4;
                            outerCircle.setVisibility(View.VISIBLE);
                            mp.start();
                            command_out = "Reducing Temperature";
                            temperView.setText(temp + " C");
                        }
                    }
                    else if(text.contains("cold")||text.contains("chilling")||text.contains("freezing")){

                        if (currTemp.equals("--"))
                        {
                            command_out = "Setting Optimal 25 C";
                            temperView.setText("25 C");
                            outerCircle.setVisibility(View.VISIBLE);
                            mp.start();
                        }else {
                            int temp = Integer.parseInt(currTemp.substring(0,2)) + 4;

                            command_out = "Increasing Temperature";
                            temperView.setText(temp + " C");
                            outerCircle.setVisibility(View.VISIBLE);
                            mp.start();
                        }

                    }else{

                        command_out = "Sorry didn't catch that";
                    }





                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, command_out, Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                }
                break;
            }
        }
    }


    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {

        String temp =(String) temperView.getText();
        String message = "A/C off in" + timestamps[position];

        if (temp.equals("--")){message = "A/C already off";}

        Snackbar.make(parentLayout,message, Snackbar.LENGTH_LONG)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                .show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        String currTemp = (String) temperView.getText();

        mp.start();
        if (currTemp.equals("--")){

            temperView.setText("22 C");
            return true;
        }

        int action = event.getAction();
        int keyCode = event.getKeyCode();
        int  temp = Integer.parseInt(currTemp.substring(0, 2));
        switch (keyCode) {

            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    temp++;

                    if (temp > 34) {
                        temp = 34;
                    }
                    temperView.setText(temp + " C");
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    temp--;

                    if (temp < 14) {
                        temp = 14;
                    }
                    temperView.setText(temp + " C");
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);

        }

    }

}