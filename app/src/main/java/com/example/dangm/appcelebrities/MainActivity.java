package com.example.dangm.appcelebrities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private Button btn0, btn1, btn2, btn3;
    private ImageView img;
    ArrayList<String> urlImage = new ArrayList<>();
    ArrayList<String> urlName = new ArrayList<>();
    int chosenCeleb = 0;
    int locationOfCorrectAnswer;
    int incorrectAnswer;
    String[] answers = new String[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        DownloadTask downloadTask = new DownloadTask();
        String result = "";
        try {
            result = downloadTask.execute("http://www.posh24.se/kandisar").get();
            String[] splitResutl = result.split("<div class=\"sidebarContainer\">");

            Pattern pattern = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = pattern.matcher(splitResutl[0]);

            while(m.find()) {
                urlImage.add(m.group(1));
            }

            pattern = Pattern.compile("alt=\"(.*?)\"");
            m = pattern.matcher(splitResutl[0]);

            while (m.find()) {
                urlName.add(m.group(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        generateAnswers();
    }

    public void generateAnswers() {

        ImageTask imageTask = new ImageTask();
        Bitmap bitmap;
        Random random = new Random();
        chosenCeleb = random.nextInt(urlImage.size());
        try {
            bitmap = imageTask.execute(urlImage.get(chosenCeleb)).get();
            img.setImageBitmap(bitmap);
            locationOfCorrectAnswer = random.nextInt(4);

            for(int i = 0; i < 4; i++) {
                if(i == locationOfCorrectAnswer) {
                    answers[i] = urlName.get(chosenCeleb);
                } else {
                    incorrectAnswer = random.nextInt(urlImage.size());
                    while(incorrectAnswer == chosenCeleb) {
                        incorrectAnswer = random.nextInt(urlImage.size());
                    }
                    answers[i] = urlName.get(incorrectAnswer);
                }
            }

            btn0.setText(answers[0]);
            btn1.setText(answers[1]);
            btn2.setText(answers[2]);
            btn3.setText(answers[3]);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void chooseCeleb(View view) {
        if (view.getTag().toString().equals(String.valueOf(locationOfCorrectAnswer))) {
            Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Wrong it was! " + urlName.get(chosenCeleb), Toast.LENGTH_SHORT).show();
        }
        generateAnswers();
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String result = "";

            try {
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = null;
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();

                while(data != -1) {
                    char current = (char) data;
                    result += current;
                    data = inputStreamReader.read();
                }

                return  result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    public class ImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {

            Bitmap bitmap;
            try {
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream input = urlConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

    }

    private void init() {
        btn0 = (Button) findViewById(R.id.button0);
        btn1 = (Button) findViewById(R.id.button1);
        btn2 = (Button) findViewById(R.id.button2);
        btn3 = (Button) findViewById(R.id.button3);
        img = (ImageView) findViewById(R.id.img);
    }
}
