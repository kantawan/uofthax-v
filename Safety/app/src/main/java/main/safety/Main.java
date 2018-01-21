package main.safety;

import android.content.Context;
import android.database.Cursor;
import android.os.DropBoxManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.String;
import java.util.ArrayList;

import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.widget.Toast;

import org.json.JSONObject;


public class Main extends AppCompatActivity {
    private EditText countDownText;
    private Button countDownButton;
    DatabaseHandler db;


    private CountDownTimer countDownTimer;
    private long timeLeft = 600000;

    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DatabaseHandler(this);


        // contact button
        Button toContact =  findViewById(R.id.contact_list);
        toContact.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent contactIntent = new Intent(Main.this, ContactList.class);
                startActivity(contactIntent);

//                // deserialize
//                try {
//                    FileInputStream fis = openFileInput("./contacts.txt");
//                    ObjectInputStream is = new ObjectInputStream(fis);
//                    // AppCompatActivity simpleClass = (AppCompatActivity) is.readObject();
//                    is.close();
//                    fis.close();
//                } catch (IOException e) {
//                    System.out.println("I do not like exceptions");
//                }

            }

        });

//        Button close_button =  findViewById(R.id.close_button);
//        close_button.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                Intent contactIntent = new Intent(Main.this, ContactList.class);
//                startActivity(contactIntent);
//                close();
//            }
//        });

        //timer button
        countDownText = findViewById(R.id.time_min);
        countDownButton = findViewById(R.id.start_button);

        //String textD = timeView.getText().toString() + ":00";
        //countDownText.setText(textD);

        countDownButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view){

                startStopTimer();

            }

        });

    }

    public void startStopTimer(){
        if (flag){
            stopTime();
        } else {
            countTime();
        }
    }

    public void countTime(){

        EditText timeView = findViewById(R.id.time_min);
        String timeViewString = timeView.getText().toString();
        String timeViewCut;



        if (!timeViewString.contains(":")){
            timeViewCut = timeViewString;
        }
        else {
            timeViewCut = timeViewString.substring(0,timeViewString.indexOf(":"));
        }


        timeLeft =  Integer.parseInt(timeViewCut) * 60000;

        countDownTimer = new CountDownTimer(timeLeft, 1000){
            @Override
            public void onTick(long mLeft){
                timeLeft = mLeft;
                updaterTimer();
            }

            @Override
            public void onFinish(){
                // Call trigger() method!!
                trigger();
            }
        }.start();

        countDownButton.setText("Cancel");
        flag = true;


    }

    public void stopTime(){
        timeLeft = 0;
        countDownText.setText("DONE");
        countDownTimer.cancel();
        countDownButton.setText("Start");
        flag = false;
    }

    public void updaterTimer(){
        int minutes = (int) timeLeft / 60000 ;
        int seconds = (int) timeLeft % 60000 / 1000;

        String timeLeftText;
        timeLeftText = "" + minutes;
        timeLeftText += ":";
        if (seconds < 10) timeLeftText += "0";
        timeLeftText += seconds;

        countDownText.setText(timeLeftText);

    }

    public void trigger(){

        Cursor data = db.getData();

//        String[] contacts = ContactList.getContacts();
//        Toast.makeText(this, contacts[0], Toast.LENGTH_SHORT).show();
//        countDownText.setText("FUCK");

        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()){
            //get the value from the database in column 1
            //then add it to the ArrayList
            listData.add(data.getString(2));
        }

        Toast.makeText(this, listData.get(0), Toast.LENGTH_SHORT).show();

        for (int i =0; i<3; i++){
            if (listData.get(i) != "0"){
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("+1" + listData.get(i), null, "HELLOOOO!!", null, null);
                    Toast.makeText(this, "Your emergency contacts have been contacted.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    System.out.println("damn");
                }

            }
        }

//        for (int i =0; i<3; i++){
//            if (contacts[i] != null){
//                try {
//                    SmsManager smsManager = SmsManager.getDefault();
//                    smsManager.sendTextMessage("+1" + contacts[i], null, "HELLOOOO!!", null, null);
//                    Toast.makeText(this, "Your emergency contacts have been contacted.", Toast.LENGTH_SHORT).show();
//                } catch (Exception e) {
//                    System.out.println("damn");
//                }
//
//            }
//        }

    }

    public void close() {
        // serialize here
        try {
            FileOutputStream fos = new FileOutputStream("./contacts.txt");
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(ContactList.getContacts());
            os.close();
            fos.close();
            System.out.println("Serialization successful");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("shit");
        }
    }
}
