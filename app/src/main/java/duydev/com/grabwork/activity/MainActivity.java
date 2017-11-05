package duydev.com.grabwork.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import duydev.com.grabwork.R;

public class MainActivity extends AppCompatActivity{


    private FragmentManager fragmentManager;
    private android.support.v4.app.FragmentTransaction fragmentTransaction;
    private LoginFragment fragmentLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("login",MODE_PRIVATE);
        String user = sharedPreferences.getString("user","");
        if(!user.equals("")){
            //go to main activity
            finish();
        }

        //view


        //setup fragment manager

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
