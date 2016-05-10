package com.context.kroket.escapeapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    GameClient tcpClient;
    ArrayList<String> list;

    /**
     * Method that makes the calls necessary to connect the players to the server.
     *
     * @param view is the view that was clicked.
     */
    public void connectButton(View view) {
        boolean connect = false;
        //connect to server, if this succeeds set connect boolean to true
        //for now, just set boolean to true
//        Intent intent = new Intent(this, clientActivity.class);
//        startActivity(intent);

        list = new ArrayList<String>();
        new connectTask().execute("");

        String message = "hallohallo";
        list.add("client: " + message);

        if (tcpClient != null) {
            tcpClient.sendMessage(message);
        }

        connect = true;

        TextView connectMessage = (TextView) findViewById(R.id.connectionMessage);
        Button start = (Button) findViewById(R.id.startButton);
        if (connect) {
            if (connectMessage != null) {
                connectMessage.setText("connected");
            }
            if (start != null) {
                start.setEnabled(true);
            }
        }
    }

    /**
     * Method that starts the game.
     *
     * @param view is the view that was clicked.
     */
    public void startButton(View view) {
        Intent intent = new Intent(this, Game_AA_Activity.class);
        startActivity(intent);
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        Button start = (Button) findViewById(R.id.startButton);
        if (start != null) {
            start.setEnabled(false);
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.context.kroket.escapeapp/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.context.kroket.escapeapp/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    public class connectTask extends AsyncTask<String, String, GameClient> {

        @Override
        protected GameClient doInBackground(String... message) {
            tcpClient = new GameClient(new GameClient.OnMessageReceived() {
                @Override
                public void messageReceived(String mes) {
                    publishProgress(mes);
                }
            });
            tcpClient.run();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            list.add(values[0]);
        }
    }
}
