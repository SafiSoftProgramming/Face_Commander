/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package safisoft.facecommander.java;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.common.annotation.KeepName;
import safisoft.facecommander.CameraSource;
import safisoft.facecommander.CameraSourcePreview;
import safisoft.facecommander.ConnectedThread;
import safisoft.facecommander.DbConnction;
import safisoft.facecommander.GraphicOverlay;
import safisoft.facecommander.NewProjectChooseActivity;
import safisoft.facecommander.R;
import safisoft.facecommander.SnackBarInfoControl;
import safisoft.facecommander.java.facedetector.FaceDetectorProcessor;
import safisoft.facecommander.preference.SettingsActivity;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/** Live preview demo for ML Kit APIs. */
@KeepName
public final class LivePreviewActivity extends AppCompatActivity
    implements OnItemSelectedListener, CompoundButton.OnCheckedChangeListener  {

  FaceDetectorProcessor faceDetectorProcessor ;
  private static final String OBJECT_DETECTION = "Object Detection";
  private static final String FACE_DETECTION = "Face Detection";

  private static final String TAG = "LivePreviewActivity";

  private CameraSource cameraSource = null;
  private CameraSourcePreview preview;
  private GraphicOverlay graphicOverlay;
  private String selectedModel = OBJECT_DETECTION;



  String bluetooth_name ;
  String bluetooth_mac ;
  String project_name ;
  String project_description ;

  String PROJECT_NAME ;

  String RIGHT_EYE ;
  String RIGHT_EYE_NOT ;
  String SMILE ;
  String SMILE_NOT ;
  String LEFT_EYE ;
  String LEFT_EYE_NOT ;
  String LOOK_RIGHT ;
  String LOOK_RIGHT_NOT ;
  String LOOK_LEFT ;
  String LOOK_LEFT_NOT ;
  String ROTATE_RIGHT ;
  String ROTATE_RIGHT_NOT ;
  String ROTATE_LEFT ;
  String ROTATE_LEFT_NOT ;
  String LOOK_DOWN ;
  String LOOK_DOWN_NOT ;
  String LOOK_UP ;
  String LOOK_UP_NOT ;

  String MODULE_MAC ;
  public final static int REQUEST_ENABLE_BT = 1;
  UUID MY_UUID ;

  BluetoothAdapter bta;
  BluetoothSocket mmSocket;
  BluetoothDevice mmDevice;
  ConnectedThread btt = null;
  //  TextView response;
  public Handler mHandler;

  private Handler Send_Handler;

  DbConnction db ;
  Cursor c = null;

  TextView txtv_bluetooth_name ;
  TextView txtv_bluetooth_mac ;

  TextView txtv_project_name ;
  TextView txtv_project_description ;

 // TextView txtv_command_history ;

  ImageButton btn_turn_off ;

  public static boolean isRecursionEnable = true;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");
    setContentView(R.layout.activity_vision_live_preview);



    MobileAds.initialize(this, new OnInitializationCompleteListener() {
      @Override
      public void onInitializationComplete(InitializationStatus initializationStatus) {
      }
    });
    AdView mAdView = findViewById(R.id.adView);
    AdRequest adRequest = new AdRequest.Builder().build();
    mAdView.loadAd(adRequest);



    faceDetectorProcessor = new FaceDetectorProcessor(LivePreviewActivity.this);
    PROJECT_NAME = getIntent().getStringExtra("PROJECT_NAME");

    bluetooth_name = getIntent().getStringExtra("bluetooth_name");
    bluetooth_mac = getIntent().getStringExtra("bluetooth_mac");

    project_name = getIntent().getStringExtra("PROJECT_NAME");
    project_description = getIntent().getStringExtra("PROJECT_DESCRIPTION");

    MODULE_MAC = bluetooth_mac ;
    MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    bta = BluetoothAdapter.getDefaultAdapter();

    //if bluetooth is not enabled then create Intent for user to turn it on
    if(!bta.isEnabled()){
      Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
    }else{
      initiateBluetoothProcess();
    }


    db = new DbConnction(LivePreviewActivity.this);
    try {
      db.createDataBase();
    } catch (IOException ioe) {
      throw new Error("Unable to create database");
    }
    try {
      db.openDataBase();
    } catch (SQLException sqle) {
      throw sqle;
    }

    c = db.Row_Query("Project_Face_Control", "project_name", PROJECT_NAME);
    c.moveToFirst();


     RIGHT_EYE = c.getString(c.getColumnIndexOrThrow("right_eye")) ;
     RIGHT_EYE_NOT =c.getString(c.getColumnIndexOrThrow("right_eye_not")) ;
     SMILE =c.getString(c.getColumnIndexOrThrow("smile")) ;
     SMILE_NOT = c.getString(c.getColumnIndexOrThrow("smile_not")) ;
     LEFT_EYE = c.getString(c.getColumnIndexOrThrow("left_eye")) ;
     LEFT_EYE_NOT = c.getString(c.getColumnIndexOrThrow("left_eye_not")) ;
     LOOK_RIGHT = c.getString(c.getColumnIndexOrThrow("look_right")) ;
     LOOK_RIGHT_NOT = c.getString(c.getColumnIndexOrThrow("look_right_not")) ;
     LOOK_LEFT = c.getString(c.getColumnIndexOrThrow("look_left")) ;
     LOOK_LEFT_NOT = c.getString(c.getColumnIndexOrThrow("look_left_not")) ;
     ROTATE_RIGHT = c.getString(c.getColumnIndexOrThrow("rotate_right")) ;
     ROTATE_RIGHT_NOT = c.getString(c.getColumnIndexOrThrow("rotate_right_not")) ;
     ROTATE_LEFT = c.getString(c.getColumnIndexOrThrow("rotate_left")) ;
     ROTATE_LEFT_NOT = c.getString(c.getColumnIndexOrThrow("rotate_left_not")) ;
     LOOK_DOWN = c.getString(c.getColumnIndexOrThrow("look_down")) ;
     LOOK_DOWN_NOT = c.getString(c.getColumnIndexOrThrow("look_down_not")) ;
     LOOK_UP = c.getString(c.getColumnIndexOrThrow("look_up")) ;
     LOOK_UP_NOT = c.getString(c.getColumnIndexOrThrow("look_up_not")) ;






    preview = findViewById(R.id.preview_view);
    if (preview == null) {
      Log.d(TAG, "Preview is null");
    }
    graphicOverlay = findViewById(R.id.graphic_overlay);
    if (graphicOverlay == null) {
      Log.d(TAG, "graphicOverlay is null");
    }

    Spinner spinner = findViewById(R.id.spinner);
    List<String> options = new ArrayList<>();

    options.add(FACE_DETECTION);


    // Creating adapter for spinner
    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_style, options);
    // Drop down layout style - list view with radio button
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // attaching data adapter to spinner
    spinner.setAdapter(dataAdapter);
    spinner.setOnItemSelectedListener(this);

    ToggleButton facingSwitch = findViewById(R.id.facing_switch);
    facingSwitch.setOnCheckedChangeListener(this);

    ImageView settingsButton = findViewById(R.id.settings_button);
    settingsButton.setOnClickListener(
        v -> {
          Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
          intent.putExtra(
              SettingsActivity.EXTRA_LAUNCH_SOURCE, SettingsActivity.LaunchSource.LIVE_PREVIEW);
          startActivity(intent);
        });

    createCameraSource(selectedModel);



  //  txtv_command_history = findViewById(R.id.txtv_command_history);

    txtv_bluetooth_name =findViewById(R.id.txtv_bluetooth_name);
    txtv_bluetooth_mac =findViewById(R.id.txtv_bluetooth_mac);

    txtv_project_name = findViewById(R.id.txtv_project_name);
    txtv_project_description = findViewById(R.id.txtv_project_description);

    btn_turn_off = findViewById(R.id.btn_turn_off);


    txtv_bluetooth_name.setText(bluetooth_name);
    txtv_bluetooth_mac.setText(bluetooth_mac);

    txtv_project_name.setText(project_name);
    txtv_project_description.setText(project_description);



    btn_turn_off.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        
      resetConnection();
      Intent intent = new Intent(LivePreviewActivity.this, NewProjectChooseActivity.class);
      intent.putExtra("AD_STATE","true" );
      startActivity(intent);
      finish();
      }
    });


 //
 //  this.executor_.scheduleWithFixedDelay(new Runnable() {
 //    @Override
 //    public void run() {
 //      startsend();
 //    }
 //  }, 0L,1000, TimeUnit.MILLISECONDS);
 //

    Send_Handler = new Handler();
        startRepeatingTask();
  }

 //
 // private final ScheduledThreadPoolExecutor executor_ =
 //         new ScheduledThreadPoolExecutor(1);
 //

  Runnable mStatusChecker = new Runnable() {
    @Override
    public void run() {
      try {
        HANDLE_START_SEND(); //this function can change value of mInterval.
      } finally {
        // 100% guarantee that this always happens, even if
        // your update method throws an exception
        Send_Handler.postDelayed(mStatusChecker, 100);
      }
    }
  };

  void startRepeatingTask() {
    mStatusChecker.run();
  }

  void stopRepeatingTask() {
    Send_Handler.removeCallbacks(mStatusChecker);
  }


  @Override
  public synchronized void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    // An item was selected. You can retrieve the selected item using
    // parent.getItemAtPosition(pos)
    selectedModel = parent.getItemAtPosition(pos).toString();
    Log.d(TAG, "Selected model: " + selectedModel);
    preview.stop();
    createCameraSource(selectedModel);
    startCameraSource();
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
    // Do nothing.
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    Log.d(TAG, "Set facing");
    if (cameraSource != null) {
      if (isChecked) {
        cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
      } else {
        cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
      }
    }
    preview.stop();
    startCameraSource();
  }

  private void createCameraSource(String model) {
    // If there's no existing cameraSource, create one.
    if (cameraSource == null) {
      cameraSource = new CameraSource(this, graphicOverlay);
    }

    try {
      switch (model) {

        case FACE_DETECTION:
          Log.i(TAG, "Using Face Detector Processor");
          cameraSource.setMachineLearningFrameProcessor(new FaceDetectorProcessor(this));

          break;

        default:
          Log.e(TAG, "Unknown model: " + model);
      }
    } catch (RuntimeException e) {
      Log.e(TAG, "Can not create image processor: " + model, e);
      Toast.makeText(
              getApplicationContext(),
              "Can not create image processor: " + e.getMessage(),
              Toast.LENGTH_LONG)
          .show();
    }
  }

  /**
   * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
   * (e.g., because onResume was called before the camera source was created), this will be called
   * again when the camera source is created.
   */
  private void startCameraSource() {
    if (cameraSource != null) {
      try {
        if (preview == null) {
          Log.d(TAG, "resume: Preview is null");
        }
        if (graphicOverlay == null) {
          Log.d(TAG, "resume: graphOverlay is null");
        }
        preview.start(cameraSource, graphicOverlay);
      } catch (IOException e) {
        Log.e(TAG, "Unable to start camera source.", e);
        cameraSource.release();
        cameraSource = null;
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(TAG, "onResume");
    createCameraSource(selectedModel);
    startCameraSource();
  }

  /** Stops the camera. */
  @Override
  protected void onPause() {
    super.onPause();
    preview.stop();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (cameraSource != null) {
      cameraSource.release();
    }
    stopRepeatingTask();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if(resultCode == RESULT_OK && requestCode == REQUEST_ENABLE_BT){
      initiateBluetoothProcess();
    }
  }

  public void initiateBluetoothProcess(){

    if(bta.isEnabled()){
      //attempt to connect to bluetooth module
      BluetoothSocket tmp = null;
      mmDevice = bta.getRemoteDevice(MODULE_MAC);

      //create socket
      try {
        tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
        mmSocket = tmp;
        mmSocket.connect();
        Log.i("[BLUETOOTH]","Connected to: "+mmDevice.getName());
      }catch(IOException e){
        try{mmSocket.close();}catch(IOException c){return;}
      }

    Log.i("[BLUETOOTH]", "Creating handler");
//   mHandler = new Handler(Looper.getMainLooper()){
//
//     @Override
//     public void handleMessage(Message msg) {
//       super.handleMessage(msg);
//       if(msg.what == ConnectedThread.RESPONSE_MESSAGE){
//         String txt = (String)msg.obj;
//
//         StringBuilder sb = new StringBuilder();
//         sb.append(txt);                                      // append string
//         String sbprint = sb.substring(0, sb.length());            // extract string
//         sb.delete(0, sb.length());
//         final String finalSbprint = sb.append(sbprint).toString();
//     //    txtv_command_history.append(finalSbprint);
//      //   Toast.makeText(LivePreviewActivity.this, finalSbprint, Toast.LENGTH_SHORT).show();
//
//       }
//     }
//   };

      Log.i("[BLUETOOTH]", "Creating and running Thread");
      btt = new ConnectedThread(mmSocket,mHandler);
      btt.start();


    }
  }

  private void resetConnection() {
    if (mmSocket != null) {
      try {mmSocket.close();} catch (Exception e) {}
      mmSocket = null;

    }
  }

  boolean CONNECTION_LOST = true ;
  boolean SOMETHING_WRONG = true ;

  public void SEND_COMMAND(String Commend){

    try {

      if(!isConnected(mmDevice)){

        if(CONNECTION_LOST) {
          SnackBarInfoControl snackBarInfoControl = new SnackBarInfoControl();
          snackBarInfoControl.SnackBarInfoControlView(LivePreviewActivity.this, findViewById(android.R.id.content).getRootView(), LivePreviewActivity.this, "Bluetooth Connection Lost!");
        }
        CONNECTION_LOST = false ;
      }

      if (mmSocket.isConnected() && btt != null) {
        btt.write(Commend.getBytes());

     //  txtv_command_history.append( "> "+Commend+"\n");
      } else {
        if (SOMETHING_WRONG) {
          SnackBarInfoControl snackBarInfoControl = new SnackBarInfoControl();
          snackBarInfoControl.SnackBarInfoControlView(LivePreviewActivity.this, findViewById(android.R.id.content).getRootView(), LivePreviewActivity.this, "Something Went Wrong");
        }
        SOMETHING_WRONG = false ;
      }

    }
    catch (Exception E){
      if (SOMETHING_WRONG) {
      SnackBarInfoControl snackBarInfoControl = new SnackBarInfoControl();
      snackBarInfoControl.SnackBarInfoControlView(LivePreviewActivity.this, findViewById(android.R.id.content).getRootView(), LivePreviewActivity.this,"Something Went Wrong");
      }
      SOMETHING_WRONG = false ;
    }


  }

  public static boolean isConnected(BluetoothDevice device) {
    try {
      Method m = device.getClass().getMethod("isConnected", (Class[]) null);
      boolean connected = (boolean) m.invoke(device, (Object[]) null);
      return connected;
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }


  @Override
  public void onBackPressed() {

   resetConnection();
   Intent intent = new Intent(LivePreviewActivity.this, NewProjectChooseActivity.class);
   intent.putExtra("AD_STATE","true" );
   startActivity(intent);
   finish();
  }



  public void HANDLE_START_SEND() {
    if (faceDetectorProcessor.FindRightEyeOpenProbability() != null) {


      if (faceDetectorProcessor.FindRightEyeOpenProbability() > 0.5000000) {
          if(!LEFT_EYE.equals("Empty")){
          SEND_COMMAND(LEFT_EYE);}
      } else {
        if(!LEFT_EYE_NOT.equals("Empty")){
          SEND_COMMAND(LEFT_EYE_NOT);}
      }

      if (faceDetectorProcessor.FindSmilingProbability() > 0.5000000) {
        if(!SMILE.equals("Empty")){
          SEND_COMMAND(SMILE);}
      } else {
        if(!SMILE_NOT.equals("Empty")){
          SEND_COMMAND(SMILE_NOT);}
      }

      if (faceDetectorProcessor.FindLeftEyeOpenProbability() > 0.5000000) {
        if(!RIGHT_EYE.equals("Empty")){
          SEND_COMMAND(RIGHT_EYE);}
      } else {
        if(!RIGHT_EYE_NOT.equals("Empty")){
          SEND_COMMAND(RIGHT_EYE_NOT);}
      }

      if (faceDetectorProcessor.FindHeadEulerAngle_X_LookUpAndDownMin() < -15) {
        if(!LOOK_DOWN.equals("Empty")){
        SEND_COMMAND(LOOK_DOWN);}
      } else {
        if(!LOOK_DOWN_NOT.equals("Empty")){
        SEND_COMMAND(LOOK_DOWN_NOT);}
      }

      if (faceDetectorProcessor.FindHeadEulerAngle_X_LookUpAndDownMin() > 15) {
        if(!LOOK_UP.equals("Empty")){
        SEND_COMMAND(LOOK_UP);}
      } else {
        if(!LOOK_UP_NOT.equals("Empty")){
        SEND_COMMAND(LOOK_UP_NOT);}
      }

      if (faceDetectorProcessor.FindHeadEulerAngle_Y_LookLeftAndRightMin() < -30 ) {
        if(!LOOK_RIGHT.equals("Empty")){
        SEND_COMMAND(LOOK_RIGHT);}
      } else {
        if(!LOOK_RIGHT_NOT.equals("Empty")){
        SEND_COMMAND(LOOK_RIGHT_NOT);}
      }

      if (faceDetectorProcessor.FindHeadEulerAngle_Y_LookLeftAndRightMin() > 30 ) {
        if(!LOOK_LEFT.equals("Empty")){
        SEND_COMMAND(LOOK_LEFT);}
      } else {
        if(!LOOK_LEFT_NOT.equals("Empty")){
        SEND_COMMAND(LOOK_LEFT_NOT);}
      }

      if (faceDetectorProcessor.FindHeadEulerAngle_Z_RotateLeftAndRightPlus() > 25) {
        if(!ROTATE_RIGHT.equals("Empty")){
        SEND_COMMAND(ROTATE_RIGHT);}
      } else {
        if(!ROTATE_RIGHT_NOT.equals("Empty")){
        SEND_COMMAND(ROTATE_RIGHT_NOT);}
      }

      if (faceDetectorProcessor.FindHeadEulerAngle_Z_RotateLeftAndRightPlus() < -25) {
        if(!ROTATE_LEFT.equals("Empty")){
        SEND_COMMAND(ROTATE_LEFT);}
      } else {
        if(!ROTATE_LEFT_NOT.equals("Empty")){
        SEND_COMMAND(ROTATE_LEFT_NOT);}

      }


    }

  }

}
