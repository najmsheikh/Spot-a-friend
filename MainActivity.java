package com.example.raimunoz.spotter;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
//import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.graphics.drawable.Drawable;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;
import android.widget.ListView;
import android.view.View.OnClickListener;

import java.net.URISyntaxException;
import java.util.Locale;
import android.content.Intent;


import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;

import java.util.List;

public class MainActivity extends Activity {

    //Socket mSocket;
    ImageView image;
    private static final int SERVERPORT = 5000;
    private static final String SERVER_IP = "http://dc151082.ngrok.io";
    //public String message_key = "send_message";
    TextToSpeech t1;
    Bitmap images;

    public ListView mList;
    private SpeechRecognizer speech;
    public Button speakButton;
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    String message;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(SERVER_IP);
        } catch (URISyntaxException e) {
            Log.e("s", "it did not work");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Intent intent = getIntent();
        String filepath = intent.getStringExtra("key"); //if it's a string you stored.
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        images = BitmapFactory.decodeFile(filepath, bmOptions);
        //new Thread(new ClientThread()).start();
        image = (ImageView) findViewById(R.id.imageView1);

        images = Bitmap.createScaledBitmap(images,640,400,true);

        image.setImageBitmap(images);
        mSocket.connect();


        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);

                }
            }
        });

        startVoiceRecognitionActivity();
        new speaktome().execute();
        Toast.makeText(MainActivity.this, "Message Received!"+message, Toast.LENGTH_SHORT).show();
        //Warning message
        //String message = "send_message";
        //mSocket.emit("new message", message);

        Button presser = (Button) findViewById(R.id.button);

        presser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ImageView imageView = (ImageView) findViewById(R.id.imageView1);//EditText et = (EditText) findViewById(R.id.EditText01);
              /*  try {
                Bitmap bmp = ((BitmapDrawable) image.getDrawable()).getBitmap(); //String str = et.getText().toString();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bmp.compress(CompressFormat.PNG, 0 ignored for PNG, bos);
                byte[] array = bos.toByteArray();

                OutputStream out = socket.getOutputStream();
                DataOutputStream dos = new DataOutputStream(out);
                dos.writeInt(array.length);
                dos.write(array, 0, array.length);
                */

                //Drawable myDrawable = getResources().getDrawable(R.drawable.group_pic);
               // Bitmap bmp = images;//((BitmapDrawable) myDrawable).getBitmap();
                //Bitmap bmp = ((BitmapDrawable) image.getDrawable()).getBitmap();
               // ByteArrayOutputStream bos = new ByteArrayOutputStream();
               // bmp.compress(CompressFormat.PNG, 0, bos);
               // byte[] array = bos.toByteArray();
               // mSocket.emit("send_image", array);
                //String toSpeak = "Hi my name is Jessica";
                //t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                //mSocket.emit("send_message", "Trapped in the Closet");
               // Toast.makeText(MainActivity.this, "Sent", Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, "Waiting for Response", Toast.LENGTH_SHORT).show();

               // mSocket.on("new_message", onNewMessage);

            }
        });
    }
    public void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            String spokenSpeech = matches.get(0);
            Toast.makeText(MainActivity.this, spokenSpeech, Toast.LENGTH_LONG).show();
            if (spokenSpeech.contains("spotafriend") || spokenSpeech.contains("spot a friend")) {
                Bitmap bmp = images;//((BitmapDrawable) myDrawable).getBitmap();
                //Bitmap bmp = ((BitmapDrawable) image.getDrawable()).getBitmap();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bmp.compress(CompressFormat.PNG, 0, bos);
                byte[] array = bos.toByteArray();
                mSocket.emit("send_image", array);
                //String toSpeak = "Hi my name is Jessica";
                //t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                //mSocket.emit("send_message", "Trapped in the Closet");
                Toast.makeText(MainActivity.this, "Sent", Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "Waiting for Response", Toast.LENGTH_SHORT).show();
                mSocket.on("new_message", onNewMessage);
            }
        }
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    //String message;
                    // username = data.getString("username");
                    message = data.toString();
                    //t1.speak(message, TextToSpeech.QUEUE_FLUSH, null);
                   // Toast.makeText(MainActivity.this, "Message Received!"+message, Toast.LENGTH_SHORT).show();

                    //finish();
                    // add the message to view
                   // addMessage(username, message);
                }
            });
        }
    };


    private class speaktome extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            t1.speak(message, TextToSpeech.QUEUE_FLUSH, null);
            while(t1.isSpeaking() == true)
            {

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }


            return message;

        }

        @Override
        protected void onPostExecute(String b) {
           // finished();
           // t1.speak(message, TextToSpeech.QUEUE_FLUSH, null);
            //Toast.makeText(MainActivity.this, "Message Received!"+message, Toast.LENGTH_SHORT).show();
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }


    public void onPause()
    {
        if(t1!=null)
        {
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("new_message", onNewMessage);
        //mSocket.off("new message", onNewMessage);
    }
}
