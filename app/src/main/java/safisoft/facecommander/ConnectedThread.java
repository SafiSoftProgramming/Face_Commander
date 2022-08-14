package safisoft.facecommander;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread{
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    public static final int RESPONSE_MESSAGE = 10;
    Handler uih;
    InputStream tmpIn = null;
    OutputStream tmpOut = null;

    public ConnectedThread(BluetoothSocket socket, Handler uih){
        mmSocket = socket;

        this.uih = uih;
        Log.i("[THREAD-CT]","Creating thread");
        try{
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();

        } catch(IOException e) {
            Log.e("[THREAD-CT]","Error:"+ e.getMessage());
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;

        try {
            mmOutStream.flush();
        } catch (IOException e) {
            return;
        }
        Log.i("[THREAD-CT]","IO's obtained");

    }

    public void run(){

           byte[] buffer = new byte[256];  // buffer store for the stream
           int bytes; // bytes returned from read()


          while(true) {
            try {

                DataInputStream mmInStream = new DataInputStream(tmpIn);
              //  DataOutputStream mmOutStream = new DataOutputStream(tmpOut);

                // Read from the InputStream
                bytes = mmInStream.read(buffer);
                String readMessage = new String(buffer, 0, bytes);

                Message msg = new Message();
                msg.what = RESPONSE_MESSAGE;
                msg.obj = readMessage;
                uih.sendMessage(msg);
            } catch (Exception e) {
                Log.i("[EX]", e.getMessage());
                break;
            }
        }

    }

    public void write(byte[] bytes){
        try{
            Log.i("[THREAD-CT]", "Writting bytes");
            mmOutStream.write(bytes);

        }catch(IOException e){}


    }

    public void cancel(){
        try{
            mmSocket.close();
        }catch(IOException e){}
    }
}



//   byte[] buffer = new byte[256];  // buffer store for the stream
//   int bytes; // bytes returned from read()
//       try {
//               //  Log.d((String) this.getTitle(), "Closing Server Socket.....");
//               //   mmServerSocket.close();
//
//               InputStream tmpIn = null;
//               OutputStream tmpOut = null;
//
//               // Get the BluetoothSocket input and output streams
//
//
//
//               DataInputStream mmInStream = new DataInputStream(tmpIn);
//               DataOutputStream mmOutStream = new DataOutputStream(tmpOut);
//               // here you can use the Input Stream to take the string from the client  whoever is connecting
//               //similarly use the output stream to send the data to the client
//
//               // Read from the InputStream
//               bytes = mmInStream.read(buffer);
//               String readMessage = new String(buffer, 0, bytes);
//               // Send the obtained bytes to the UI Activity
//
//               System.out.println(readMessage);
//
//               System.out.println("ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
//
//               //text.setText(readMessage);
//               } catch (Exception e) {
//               //catch your exception here
//               }



//   //byte[] buffer = new byte[1024];
//   //int bytes;
//
//   BufferedReader br = new BufferedReader(new InputStreamReader(mmInStream));
//   Log.i("[THREAD-CT]","Starting thread");
//   while(true){
//       System.out.println("mmmmmmmmmmmmmmmmmmmmmmmmmmmm");
//       try{
//           // bytes = mmInStream.read(buffer);
//           String resp = br.readLine();
//           //Transfer these data to the UI thread
//           Message msg = new Message();
//           msg.what = RESPONSE_MESSAGE;
//           msg.obj = resp;
//           uih.sendMessage(msg);
//           System.out.println("ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg");
//           System.out.println(msg);
//       }catch(IOException e){
//           Log.i("[ererererere]",e.getMessage());
//           break;
//       }
//   }
//   Log.i("[THREAD-CT]","While loop ended");