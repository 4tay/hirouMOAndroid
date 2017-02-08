package com.littlehouse_design.jsonparsing.Utils;

/**
 * Created by johnkonderla on 1/16/17.
 */

import android.util.Log;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPClient {

    private String serverMessage;
    private String serverIP;
    private int serverPort;
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;

    PrintWriter out;
    BufferedReader in;
    InputStream inputStream = null;

    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(OnMessageReceived listener, String serverIP, int serverPort) {
        mMessageListener = listener;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }
    //public

    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void sendMessage(String message){
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
        }
    }

    /**
     * Stop the connection between the client and the server.
     * Prolly gonna use this at some point
     */
    public void stopClient(){


        mRun = false;
    }

    public void run(String message) {

        mRun = true;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(serverIP);

            Log.d("TCP Client", "C: Connecting...");

            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, serverPort);

            try {

                //send the message to the server
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                Log.d("TCP Client", "C: Sent.");

                Log.d("TCP Client", "C: Done.");
                sendMessage(message);

                //receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //inputStream = socket.getInputStream();

                //in this while the client listens for the messages sent by the server
                while (mRun) {
                    //serverMessage = readFromStream(inputStream);
                    serverMessage = in.readLine();


                    if (serverMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MainActivity class
                        mMessageListener.messageReceived(0, serverMessage);
                        mRun = false;
                        Log.d("Closing the stream", "closed it");
                    }
                    /*//serverMessage = null;
                    if(socket.isConnected()) {

                    }*/

                }

                //Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + serverMessage + "'");

            } catch (Exception e) {



                Log.e("TCP", "S: Error", e);
                mMessageListener.messageReceived(1, "The connection failed: " + e);

            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e("TCP CLIENT", "Something wrong with the close statement", e);
                    mMessageListener.messageReceived(1, "The connection failed: " + e);
                }
            }

        }

        catch (Exception e) {

            Log.e("TCP", "C: Error", e);
            mMessageListener.messageReceived(1, "The connection failed: " + e);

        }

    }



    public void requestToken(String message) {

        mRun = true;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(serverIP);

            Log.d("TCP Client", "C: Connecting...");

            //create a socket to make the connection with the server
            Socket socket = new Socket();


            try {
                //Set my timeout to 10 seconds
                socket.connect(new InetSocketAddress(serverAddr, serverPort),10000);

                //send the message to the server
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                Log.d("TCP Client", "C: Sent.");

                Log.d("TCP Client", "C: Done.");
                sendMessage(message);

                //receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //inputStream = socket.getInputStream();

                //in this while the client listens for the messages sent by the server
                while (mRun) {
                    //serverMessage = readFromStream(inputStream);
                    serverMessage = in.readLine();


                    if (serverMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MainActivity class
                        mMessageListener.messageReceived(0, serverMessage);
                        mRun = false;
                        Log.d("Closing the stream", "closed it");
                    }
                    /*//serverMessage = null;
                    if(socket.isConnected()) {

                    }*/

                }

                //Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + serverMessage + "'");

            } catch (Exception e) {



                Log.e("TCP", "S: Error", e);
                mMessageListener.messageReceived(1, "The connection failed: " + e);

            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e("TCP CLIENT", "Something wrong with the close statement", e);
                    mMessageListener.messageReceived(1, "The connection failed: " + e);
                }
            }

        }

        catch (Exception e) {

            Log.e("TCP", "C: Error", e);
            mMessageListener.messageReceived(1, "The connection failed: " + e);

        }

    }




    //Declare the interface. The method messageReceived(String message) must be implemented in the MainActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        void messageReceived(int type, String message);
    }

    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder stringBuilder = new StringBuilder();

        if(inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String singleLine = bufferedReader.readLine();
            while (singleLine != null) {
                stringBuilder.append(singleLine);
                singleLine = bufferedReader.readLine();
            }
        }
        return stringBuilder.toString();
    }
}
