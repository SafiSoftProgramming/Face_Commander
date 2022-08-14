package safisoft.facecommander;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewProjectChooseActivity extends AppCompatActivity {

    Cursor c = null;
    DbConnction db ;
    List<Recent_Projects> Recent_Projects_List;
    RecyclerView recyclerView;
    Recent_Projects_Adapter recentProjectsAdapter;
    ImageButton btn_new_project ;
    LinearLayout lay_no_data ;


    ReviewManager reviewManager ;
    ReviewInfo reviewInfo = null;
    private InterstitialAd mInterstitialAd;

    String AD_STATE ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project_choose);

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

    InterstitialAd.load(this,"ca-app-pub-5637187199850424/8093896605", adRequest_interstitial,
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


            int min = 3*1000;
            new CountDownTimer(min, 1000) {
                public void onTick(long millisUntilFinished) { }
                public void onFinish() {

             if (mInterstitialAd != null) {
                 mInterstitialAd.show(NewProjectChooseActivity.this);
             } else {
                 Log.d("TAG", "The interstitial ad wasn't ready yet.");
             }

                }
            }.start();


        }


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btn_new_project = findViewById(R.id.btn_new_project);
        lay_no_data = findViewById(R.id.lay_no_data);

        db = new DbConnction(NewProjectChooseActivity.this);
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


        btn_new_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               Intent intent = new Intent(NewProjectChooseActivity.this, StartNewProjectActivity.class);
               startActivity(intent);
               finish();



            }
        });





        Recent_Projects_List = new ArrayList<>();
        c = db.Query_Data("Projects_List", null, null, null, null, null, null);
        c =  db.sortRecord_id();
        c.moveToFirst();

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Recent_Projects_List.add(new Recent_Projects(c.getInt(0),c.getString(1),c.getString(2)));
            }while (c.moveToNext());
        }


        recentProjectsAdapter = new Recent_Projects_Adapter(NewProjectChooseActivity.this,Recent_Projects_List);
        recyclerView.setAdapter(recentProjectsAdapter);

        if(Recent_Projects_List.isEmpty()){
            lay_no_data.setVisibility(View.VISIBLE);
        }
        else {
            lay_no_data.setVisibility(View.GONE);
        }



        if(PROJECT_COUNT() > 0){

            int min = 3*1000;
            new CountDownTimer(min, 1000) {
                public void onTick(long millisUntilFinished) { }
                public void onFinish() {
                    StartInAppReview();
                }
            }.start();
        }



    }



    private int PROJECT_COUNT(){
        c = db.Query_Data("Projects_List", null, null, null, null, null, null);
        c.moveToLast();
        return c.getCount();
    }





    public void StartInAppReview(){

        ReviewManager manager = ReviewManagerFactory.create(NewProjectChooseActivity.this);

        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            try {
                if (task.isSuccessful()) {
                    // We can get the ReviewInfo object
                    ReviewInfo reviewInfo = task.getResult();
                    Task<Void> flow = manager.launchReviewFlow(NewProjectChooseActivity.this, reviewInfo);
                    flow.addOnCompleteListener(task2 -> {
                        // The flow has finished. The API does not indicate whether the user
                        // reviewed or not, or even whether the review dialog was shown. Thus, no
                        // matter the result, we continue our app flow.
                        //   Toast.makeText(getApplicationContext(), "In-app review returned.", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    // There was some problem, continue regardless of the result.
                    // goToAppPage(activity);
                }
            } catch (Exception ex) {
                //   Toast.makeText(getApplicationContext(), "Exception from openReview():", Toast.LENGTH_SHORT).show();
            }
        });

    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        SnackBarInfoControl snackBarInfoControl = new SnackBarInfoControl();
        snackBarInfoControl.SnackBarInfoControlView(getApplicationContext(),findViewById(android.R.id.content).getRootView(),NewProjectChooseActivity.this,"Press Twice to Exit");

    }


}