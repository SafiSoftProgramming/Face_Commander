package safisoft.facecommander;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.IOException;

public class StartNewProjectActivity extends AppCompatActivity {


    EditText edtxt_project_name ;
    EditText edtxt_project_description ;
    ImageButton btn_new_Project_next_step ;
    Cursor c = null;
    DbConnction db ;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_new_project);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        edtxt_project_name = findViewById(R.id.edtxt_project_name);
        edtxt_project_description = findViewById(R.id.edtxt_project_description);
        btn_new_Project_next_step = findViewById(R.id.btn_new_Project_next_step);


        db = new DbConnction(StartNewProjectActivity.this);
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



        btn_new_Project_next_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!edtxt_project_name.getText().toString().isEmpty() && !edtxt_project_description.getText().toString().isEmpty()) {


                        if(IS_DUPLICATED_NAME()){
                            SnackBarInfoControl snackBarInfoControl = new SnackBarInfoControl();
                            snackBarInfoControl.SnackBarInfoControlView(StartNewProjectActivity.this, v,StartNewProjectActivity.this,"You Have a Project With The Same Name Try another Name");
                        }
                        else {
                            Intent_Handel(EditFaceCommandControlActivity.class);
                        }


                }
                else if (edtxt_project_name.getText().toString().isEmpty()){
                    SnackBarInfoControl snackBarInfoControl = new SnackBarInfoControl();
                    snackBarInfoControl.SnackBarInfoControlView(StartNewProjectActivity.this, v,StartNewProjectActivity.this,"Insert Project Name");
                }
                else if (edtxt_project_description.getText().toString().isEmpty()){
                    SnackBarInfoControl snackBarInfoControl = new SnackBarInfoControl();
                    snackBarInfoControl.SnackBarInfoControlView(StartNewProjectActivity.this, v,StartNewProjectActivity.this,"Insert Project Description");
                }

            }
        });








    }






    private boolean IS_DUPLICATED_NAME(){
        c = db.Query_Data("Projects_List", null, null, null, null, null, null);
        c.moveToFirst();
        boolean state = false;
        if (c.moveToFirst()) {
            do {
                if(edtxt_project_name.getText().toString().equals(c.getString(1))){
                    state = true ;
                }
            }while (c.moveToNext());
        }
        return state ;
    }


    private void Intent_Handel(Class Activity){
        Intent intent = new Intent(StartNewProjectActivity.this, Activity);
        intent.putExtra("PROJECT_NAME",edtxt_project_name.getText().toString());
        intent.putExtra("PROJECT_CONTROL_DESCRIPTION", edtxt_project_description.getText().toString());
        intent.putExtra("PROJECT_STATE","NEW");
        intent.putExtra("AD_STATE","true" );
        startActivity(intent);
        finish();

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(StartNewProjectActivity.this, NewProjectChooseActivity.class);
        intent.putExtra("AD_STATE","false" );
        startActivity(intent);
        finish();
    }













}