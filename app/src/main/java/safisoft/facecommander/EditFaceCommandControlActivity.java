package safisoft.facecommander;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.io.IOException;

public class EditFaceCommandControlActivity extends AppCompatActivity {


    DbConnction db ;
    Cursor c = null;
    boolean IS_EDITING ;
    EditDialog editDialog ;


    String PROJECT_NAME ;
    String PROJECT_CONTROL_DESCRIPTION ;
    String PROJECT_STATE ;
    String COLUMN_FOR_COMMAND_FIRST ;
    String COLUMN_FOR_COMMAND_SECOND ;


    ImageView CURRENT_IC_DONE_ED_BTN ;
    String AD_STATE ;
    TextView BTN_NAME ;
    TextView COM_DOWN ;
    TextView COM_UP ;

    TextView txtv_project_name ;
    TextView txtv_project_description ;

    TextView txtv_edit_or_new_project ;

    ImageButton btn_right_eye ;
    ImageButton btn_smile ;
    ImageButton btn_left_eye ;
    ImageButton btn_look_right ;
    ImageButton btn_look_left ;
    ImageButton btn_rotate_right ;
    ImageButton btn_rotate_left ;
    ImageButton btn_look_down ;
    ImageButton btn_look_up ;

    ImageView ic_done_right_eye ;
    ImageView ic_done_smile ;
    ImageView ic_done_left_eye ;
    ImageView ic_done_look_right ;
    ImageView ic_done_look_left ;
    ImageView ic_done_rotate_right ;
    ImageView ic_done_rotate_left ;
    ImageView ic_done_look_down ;
    ImageView ic_done_look_up ;

    ImageButton btn_save_Project ;

    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_face_command_control);



        txtv_project_name = findViewById(R.id.txtv_project_name);
        txtv_project_description = findViewById(R.id.txtv_project_description);
        txtv_edit_or_new_project = findViewById(R.id.txtv_edit_or_new_project);

        btn_right_eye = findViewById(R.id.btn_right_eye);
        btn_smile = findViewById(R.id.btn_smile);
        btn_left_eye = findViewById(R.id.btn_left_eye);
        btn_look_right = findViewById(R.id.btn_look_right);
        btn_look_left = findViewById(R.id.btn_look_left);
        btn_rotate_right = findViewById(R.id.btn_rotate_right);
        btn_rotate_left = findViewById(R.id.btn_rotate_left);
        btn_look_down = findViewById(R.id.btn_look_down);
        btn_look_up = findViewById(R.id.btn_look_up);

        ic_done_right_eye = findViewById(R.id.ic_done_right_eye);
        ic_done_smile = findViewById(R.id.ic_done_smile);
        ic_done_left_eye = findViewById(R.id.ic_done_left_eye);
        ic_done_look_right = findViewById(R.id.ic_done_look_right);
        ic_done_look_left = findViewById(R.id.ic_done_look_left);
        ic_done_rotate_right = findViewById(R.id.ic_done_rotate_right);
        ic_done_rotate_left = findViewById(R.id.ic_done_rotate_left);
        ic_done_look_down = findViewById(R.id.ic_done_look_down);
        ic_done_look_up = findViewById(R.id.ic_done_look_up);

        btn_save_Project = findViewById(R.id.btn_save_Project);






        PROJECT_NAME = getIntent().getStringExtra("PROJECT_NAME");
        PROJECT_CONTROL_DESCRIPTION = getIntent().getStringExtra("PROJECT_CONTROL_DESCRIPTION");
        PROJECT_STATE = getIntent().getStringExtra("PROJECT_STATE");
        AD_STATE = getIntent().getStringExtra("AD_STATE");



        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



        AdRequest adRequest_interstitial = new AdRequest.Builder().build();

        InterstitialAd.load(this,"ca-app-pub-5637187199850424/9384235892", adRequest_interstitial,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });

        if(AD_STATE.equals("true")) {


            int min = 2*1000;
            new CountDownTimer(min, 1000) {
                public void onTick(long millisUntilFinished) { }
                public void onFinish() {

                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(EditFaceCommandControlActivity.this);
                    } else {
                        Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    }

                }
            }.start();
        }


        db = new DbConnction(EditFaceCommandControlActivity.this);
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

        txtv_project_name.setText(PROJECT_NAME);
        txtv_project_description.setText(PROJECT_CONTROL_DESCRIPTION);


        if(PROJECT_STATE.equals("NEW")) {

            db.InsertNewProjectFaceControl(PROJECT_NAME, PROJECT_CONTROL_DESCRIPTION
                    , "Empty", "Empty", "Empty", "Empty"
                    , "Empty", "Empty", "Empty", "Empty"
                    , "Empty", "Empty", "Empty", "Empty", "Empty"
                    , "Empty", "Empty", "Empty", "Empty", "Empty");


            db.InsertNewProjectDetails(PROJECT_NAME, PROJECT_CONTROL_DESCRIPTION);

        }
        else if (PROJECT_STATE.equals("EDIT")){
            txtv_edit_or_new_project.setText("Edit");
            SnackBarInfoControl snackBarInfoControl = new SnackBarInfoControl();
            snackBarInfoControl.SnackBarInfoControlView(EditFaceCommandControlActivity.this, findViewById(android.R.id.content).getRootView(), EditFaceCommandControlActivity.this,"Edit Your Project");
        }




        btn_right_eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               HANDLE_COMMAND_DATA("right_eye");
            }
        });

        btn_smile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HANDLE_COMMAND_DATA("smile");
            }
        });

        btn_left_eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HANDLE_COMMAND_DATA("left_eye");
            }
        });

        btn_look_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HANDLE_COMMAND_DATA("look_right");
            }
        });

        btn_look_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HANDLE_COMMAND_DATA("look_left");
            }
        });

        btn_rotate_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HANDLE_COMMAND_DATA("rotate_right");
            }
        });

        btn_rotate_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HANDLE_COMMAND_DATA("rotate_left");
            }
        });

        btn_look_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HANDLE_COMMAND_DATA("look_down");
            }
        });

        btn_look_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HANDLE_COMMAND_DATA("look_up");
            }
        });

        c = db.Row_Query("Project_Face_Control", "project_name", PROJECT_NAME);
        c.moveToFirst();

        if(!c.getString(c.getColumnIndexOrThrow("right_eye")).equals("Empty") || !c.getString(c.getColumnIndexOrThrow("right_eye_not")).equals("Empty")){
            ic_done_right_eye.setVisibility(View.VISIBLE);
        }

        if(!c.getString(c.getColumnIndexOrThrow("smile")).equals("Empty") || !c.getString(c.getColumnIndexOrThrow("smile_not")).equals("Empty")){
            ic_done_smile.setVisibility(View.VISIBLE);
        }

        if(!c.getString(c.getColumnIndexOrThrow("left_eye")).equals("Empty") || !c.getString(c.getColumnIndexOrThrow("left_eye_not")).equals("Empty")){
            ic_done_left_eye.setVisibility(View.VISIBLE);
        }

        if(!c.getString(c.getColumnIndexOrThrow("look_right")).equals("Empty") || !c.getString(c.getColumnIndexOrThrow("look_right_not")).equals("Empty")){
            ic_done_look_right.setVisibility(View.VISIBLE);
        }

        if(!c.getString(c.getColumnIndexOrThrow("look_left")).equals("Empty") || !c.getString(c.getColumnIndexOrThrow("look_left_not")).equals("Empty")){
            ic_done_look_left.setVisibility(View.VISIBLE);
        }

        if(!c.getString(c.getColumnIndexOrThrow("rotate_right")).equals("Empty") || !c.getString(c.getColumnIndexOrThrow("rotate_right_not")).equals("Empty")){
            ic_done_rotate_right.setVisibility(View.VISIBLE);
        }

        if(!c.getString(c.getColumnIndexOrThrow("rotate_left")).equals("Empty") || !c.getString(c.getColumnIndexOrThrow("rotate_left_not")).equals("Empty")){
            ic_done_rotate_left.setVisibility(View.VISIBLE);
        }

        if(!c.getString(c.getColumnIndexOrThrow("look_down")).equals("Empty") || !c.getString(c.getColumnIndexOrThrow("look_down_not")).equals("Empty")){
            ic_done_look_down.setVisibility(View.VISIBLE);
        }

        if(!c.getString(c.getColumnIndexOrThrow("look_up")).equals("Empty") || !c.getString(c.getColumnIndexOrThrow("look_up_not")).equals("Empty")){
            ic_done_look_up.setVisibility(View.VISIBLE);
        }

        btn_save_Project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(EditFaceCommandControlActivity.this, FindBluetoothActivity.class);
                intent.putExtra("PROJECT_NAME",PROJECT_NAME);
                intent.putExtra("PROJECT_DESCRIPTION",PROJECT_CONTROL_DESCRIPTION);
                startActivity(intent);
                finish();


            }
        });



    }



    private void HANDLE_COMMAND_DATA(String PRESSED_BUTTON){


        editDialog = new EditDialog(EditFaceCommandControlActivity.this);
        editDialog.show();
        editDialog.setCanceledOnTouchOutside(false);
        editDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        c = db.Row_Query("Project_Face_Control", "project_name", PROJECT_NAME);
        c.moveToFirst();

        switch(PRESSED_BUTTON) {
            case "right_eye":
                editDialog.txtv_command_name.setText("Right Eye Commands");
                editDialog.imgv_command_pic.setBackgroundResource(R.drawable.ic_btn_right_eye);
                editDialog.txtv_command_first_head.setText("When Right Eye Closed");
                editDialog.txtv_command_first_des.setText("Send this Command to Arduino when you close your Right eye.");
                editDialog.txtv_command_second_head.setText("When Right Eye Opened");
                editDialog.txtv_command_second_des.setText("Send this Command to Arduino when you open your Right eye.");
                editDialog.edtxt_command_first.setText(c.getString(c.getColumnIndexOrThrow("right_eye")));
                editDialog.edtxt_command_second.setText(c.getString(c.getColumnIndexOrThrow("right_eye_not")));
                COLUMN_FOR_COMMAND_FIRST = "right_eye";
                COLUMN_FOR_COMMAND_SECOND = "right_eye_not";
                CURRENT_IC_DONE_ED_BTN = ic_done_right_eye ;
                break;

            case "smile":
                editDialog.txtv_command_name.setText("Smile Commands");
                editDialog.imgv_command_pic.setBackgroundResource(R.drawable.ic_btn_smile);
                editDialog.txtv_command_first_head.setText("When you Smile");
                editDialog.txtv_command_first_des.setText("Send this Command to Arduino when you smile.");
                editDialog.txtv_command_second_head.setText("When you not Smiling");
                editDialog.txtv_command_second_des.setText("Send this Command to Arduino when you not Smiling.");
                editDialog.edtxt_command_first.setText(c.getString(c.getColumnIndexOrThrow("smile")));
                editDialog.edtxt_command_second.setText(c.getString(c.getColumnIndexOrThrow("smile_not")));
                COLUMN_FOR_COMMAND_FIRST = "smile";
                COLUMN_FOR_COMMAND_SECOND = "smile_not";
                CURRENT_IC_DONE_ED_BTN = ic_done_smile ;
                break;

            case "left_eye":
                editDialog.txtv_command_name.setText("Left Eye Commands");
                editDialog.imgv_command_pic.setBackgroundResource(R.drawable.ic_btn_left_eye);
                editDialog.txtv_command_first_head.setText("When Left Eye Closed");
                editDialog.txtv_command_first_des.setText("Send this Command to Arduino when you close your left eye.");
                editDialog.txtv_command_second_head.setText("When Left Eye Opened");
                editDialog.txtv_command_second_des.setText("Send this Command to Arduino when you open your left eye.");
                editDialog.edtxt_command_first.setText(c.getString(c.getColumnIndexOrThrow("left_eye")));
                editDialog.edtxt_command_second.setText(c.getString(c.getColumnIndexOrThrow("left_eye_not")));
                COLUMN_FOR_COMMAND_FIRST = "left_eye";
                COLUMN_FOR_COMMAND_SECOND = "left_eye_not";
                CURRENT_IC_DONE_ED_BTN = ic_done_left_eye ;
                break;

            case "look_right":
                editDialog.txtv_command_name.setText("Look Right Commands");
                editDialog.imgv_command_pic.setBackgroundResource(R.drawable.ic_btn_look_right);
                editDialog.txtv_command_first_head.setText("When you Look Right");
                editDialog.txtv_command_first_des.setText("Send this Command to Arduino when you look right.");
                editDialog.txtv_command_second_head.setText("When you not looking right");
                editDialog.txtv_command_second_des.setText("Send this Command to Arduino when you not looking right.");
                editDialog.edtxt_command_first.setText(c.getString(c.getColumnIndexOrThrow("look_right")));
                editDialog.edtxt_command_second.setText(c.getString(c.getColumnIndexOrThrow("look_right_not")));
                COLUMN_FOR_COMMAND_FIRST = "look_right";
                COLUMN_FOR_COMMAND_SECOND = "look_right_not";
                CURRENT_IC_DONE_ED_BTN = ic_done_look_right ;
                break;

            case "look_left":
                editDialog.txtv_command_name.setText("Look Left Commands");
                editDialog.imgv_command_pic.setBackgroundResource(R.drawable.ic_btn_look_left);
                editDialog.txtv_command_first_head.setText("When you Look Left");
                editDialog.txtv_command_first_des.setText("Send this Command to Arduino when you look Left.");
                editDialog.txtv_command_second_head.setText("When you not looking Left");
                editDialog.txtv_command_second_des.setText("Send this Command to Arduino when you not looking left.");
                editDialog.edtxt_command_first.setText(c.getString(c.getColumnIndexOrThrow("look_left")));
                editDialog.edtxt_command_second.setText(c.getString(c.getColumnIndexOrThrow("look_left_not")));
                COLUMN_FOR_COMMAND_FIRST = "look_left";
                COLUMN_FOR_COMMAND_SECOND = "look_left_not";
                CURRENT_IC_DONE_ED_BTN = ic_done_look_left ;
                break;

            case "rotate_right":
                editDialog.txtv_command_name.setText("Rotate Head Right Commands");
                editDialog.imgv_command_pic.setBackgroundResource(R.drawable.ic_btn_rotate_right);
                editDialog.txtv_command_first_head.setText("When you Rotate your Head Right");
                editDialog.txtv_command_first_des.setText("Send this Command to Arduino when you rotate your head right.");
                editDialog.txtv_command_second_head.setText("When you not Rotate Head Right");
                editDialog.txtv_command_second_des.setText("Send this Command to Arduino when you not rotate your head right.");
                editDialog.edtxt_command_first.setText(c.getString(c.getColumnIndexOrThrow("rotate_right")));
                editDialog.edtxt_command_second.setText(c.getString(c.getColumnIndexOrThrow("rotate_right_not")));
                COLUMN_FOR_COMMAND_FIRST = "rotate_right";
                COLUMN_FOR_COMMAND_SECOND = "rotate_right_not";
                CURRENT_IC_DONE_ED_BTN = ic_done_rotate_right ;
                break;

            case "rotate_left":
                editDialog.txtv_command_name.setText("Rotate Head Left Commands");
                editDialog.imgv_command_pic.setBackgroundResource(R.drawable.ic_btn_rotate_left);
                editDialog.txtv_command_first_head.setText("When you Rotate your Head Left");
                editDialog.txtv_command_first_des.setText("Send this Command to Arduino when you rotate your head left.");
                editDialog.txtv_command_second_head.setText("When you not Rotate Head Left");
                editDialog.txtv_command_second_des.setText("Send this Command to Arduino when you not rotate your head left.");
                editDialog.edtxt_command_first.setText(c.getString(c.getColumnIndexOrThrow("rotate_left")));
                editDialog.edtxt_command_second.setText(c.getString(c.getColumnIndexOrThrow("rotate_left_not")));
                COLUMN_FOR_COMMAND_FIRST = "rotate_left";
                COLUMN_FOR_COMMAND_SECOND = "rotate_left_not";
                CURRENT_IC_DONE_ED_BTN = ic_done_rotate_left ;
                break;

            case "look_down":
                editDialog.txtv_command_name.setText("Look Down Commands");
                editDialog.imgv_command_pic.setBackgroundResource(R.drawable.ic_btn_look_down);
                editDialog.txtv_command_first_head.setText("When you Look Down");
                editDialog.txtv_command_first_des.setText("Send this Command to Arduino when you look down.");
                editDialog.txtv_command_second_head.setText("When you not look down");
                editDialog.txtv_command_second_des.setText("Send this Command to Arduino when you not not look down.");
                editDialog.edtxt_command_first.setText(c.getString(c.getColumnIndexOrThrow("look_down")));
                editDialog.edtxt_command_second.setText(c.getString(c.getColumnIndexOrThrow("look_down_not")));
                COLUMN_FOR_COMMAND_FIRST = "look_down";
                COLUMN_FOR_COMMAND_SECOND = "look_down_not";
                CURRENT_IC_DONE_ED_BTN = ic_done_look_down ;
                break;

            case "look_up":
                editDialog.txtv_command_name.setText("Look Up Commands");
                editDialog.imgv_command_pic.setBackgroundResource(R.drawable.ic_btn_look_up);
                editDialog.txtv_command_first_head.setText("When you Look Up");
                editDialog.txtv_command_first_des.setText("Send this Command to Arduino when you look Up.");
                editDialog.txtv_command_second_head.setText("When you not look Up");
                editDialog.txtv_command_second_des.setText("Send this Command to Arduino when you not not look up.");
                editDialog.edtxt_command_first.setText(c.getString(c.getColumnIndexOrThrow("look_up")));
                editDialog.edtxt_command_second.setText(c.getString(c.getColumnIndexOrThrow("look_up_not")));
                COLUMN_FOR_COMMAND_FIRST = "look_up";
                COLUMN_FOR_COMMAND_SECOND = "look_up_not";
                CURRENT_IC_DONE_ED_BTN = ic_done_look_up ;
                break;

            default:
                ////
        }


        editDialog.btn_ok_in_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UPDATE_FACE_COMMANDS(COLUMN_FOR_COMMAND_FIRST, editDialog.edtxt_command_first,COLUMN_FOR_COMMAND_SECOND,editDialog.edtxt_command_second,CURRENT_IC_DONE_ED_BTN);

                SnackBarInfoControl snackBarInfoControl = new SnackBarInfoControl();
                snackBarInfoControl.SnackBarInfoControlView(EditFaceCommandControlActivity.this, findViewById(android.R.id.content).getRootView(), EditFaceCommandControlActivity.this,"Done.");

                editDialog.dismiss();

            }
        });

        editDialog.btn_cancel_in_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDialog.dismiss();
            }
        });

    }


    private void UPDATE_FACE_COMMANDS(String COLUMN_FOR_COMMAND_FIRST, EditText EDITTEXT_COMMAND_FIRST, String COLUMN_FOR_COMMAND_SECOND,EditText EDITTEXT_COMMAND_SECOND
                                     , ImageView IMAGEVIEW_TARGET_DONE_EDITING){


        db.UpdateFaceControlRecord(COLUMN_FOR_COMMAND_FIRST,EDITTEXT_COMMAND_FIRST.getText().toString(), PROJECT_NAME);
        db.UpdateFaceControlRecord(COLUMN_FOR_COMMAND_SECOND,EDITTEXT_COMMAND_SECOND.getText().toString(), PROJECT_NAME);

        if(EDITTEXT_COMMAND_FIRST.getText().toString().equals("") && EDITTEXT_COMMAND_SECOND.getText().toString().equals("")
                || EDITTEXT_COMMAND_FIRST.getText().toString().equals("Empty") && EDITTEXT_COMMAND_SECOND.getText().toString().equals("Empty")
                || EDITTEXT_COMMAND_FIRST.getText().toString().equals("") && EDITTEXT_COMMAND_SECOND.getText().toString().equals("Empty")
                || EDITTEXT_COMMAND_FIRST.getText().toString().equals("Empty") && EDITTEXT_COMMAND_SECOND.getText().toString().equals("")){

            IMAGEVIEW_TARGET_DONE_EDITING.setVisibility(View.INVISIBLE);
        }
        else {
            IMAGEVIEW_TARGET_DONE_EDITING.setVisibility(View.VISIBLE);
        }

        if(EDITTEXT_COMMAND_FIRST.getText().toString().equals("")){
            db.UpdateFaceControlRecord(COLUMN_FOR_COMMAND_FIRST, "Empty", PROJECT_NAME);
        }
        if(EDITTEXT_COMMAND_SECOND.getText().toString().equals("")){
            db.UpdateFaceControlRecord(COLUMN_FOR_COMMAND_SECOND,"Empty", PROJECT_NAME);
        }


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditFaceCommandControlActivity.this, NewProjectChooseActivity.class);
        intent.putExtra("AD_STATE","false" );
        startActivity(intent);
        finish();
    }


}