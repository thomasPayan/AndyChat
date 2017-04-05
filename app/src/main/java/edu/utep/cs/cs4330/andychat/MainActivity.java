package edu.utep.cs.cs4330.andychat;

import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import static android.R.attr.host;
import static java.lang.System.in;

public class MainActivity extends AppCompatActivity {

    //private static String LOCAL_HOST = "10.0.2.2"; //not 127.0.0.1!
    private static String LOCAL_HOST = "opuntia.cs.utep.edu";
    private static String CHAT_SERVER = LOCAL_HOST;
    private static final int PORT_NUMBER = 8000;

    /** Socket connected to the chat server. */
    private Socket socket;

    /** Pane to display all received/sent messages. */
    private ListView msgView;

    /** List adapter associated with the msgView view. */
    private ArrayAdapter<String> msgList;

    private EditText msgEdit;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();

        Button button = (Button) findViewById(R.id.connectButton);
        button.setOnClickListener(view -> connectToServer(CHAT_SERVER, PORT_NUMBER));

        msgView = (ListView) findViewById(R.id.msgView);
        msgList = new ArrayAdapter<String>(this, R.layout.msg_text);
        //android.R.layout.simple_list_item_1);
        msgView.setAdapter(msgList);

        msgEdit = (EditText) findViewById(R.id.msgEdit);
        button = (Button) findViewById(R.id.sendButton);
        button.setOnClickListener(view -> sendMessage(msgEdit.getText().toString()));
    }

    /** Connect to the specified chat server. */
    private void connectToServer(String host, int port) {
        new Thread(() -> {
            socket = createSocket(host, port);
            if (socket != null) {
                // WRITE YOUR CODE HERE ...
                if(msgEdit.getText().toString()!= null) {
                    sendMessage(msgEdit.getText().toString());
                }
            }

            handler.post(() -> showToast(socket != null ? "Connected." : "Failed to connect!"));
        }).start();
    }

    /** Creates a sock with the given host and port. */
    private Socket createSocket(String host, int port) {
        try {
            return new Socket(host, port);
        } catch (Exception e) {
            Log.d("TAG---", e.toString());
        }
        return null;
    }

    /** Send the given message to the chat server. */
    private void sendMessage(String msg) {
        // WRITE YOUR CODE HERE ...

        PrintWriter out = null;
        try {
            out = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        out.println(msg);
        out.flush();
        //displayMessage(msg);
    }

    /** Display the given message in the message pane by appending it. */
    private void displayMessage(String msg) {
        msgList.add(msg);
        msgView.smoothScrollToPosition(msgList.getCount() - 1);
    }

    private void readMessage() throws IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        while(true){
            String msg = in.readLine();
            if(msg == null){
                break;
            }
            else handler.post(()->msgList.add(msg));
        }
    }

    /** Show a toast message. */
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
