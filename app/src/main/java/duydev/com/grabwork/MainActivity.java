package duydev.com.grabwork;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private LoginFragment fragmentLogin;
    private ImageView imLogin;
    private ImageView imRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //view
        imLogin = (ImageView)findViewById(R.id.imLogin);
        imRegister = (ImageView)findViewById(R.id.imRegister);

        //add listener
        imLogin.setOnClickListener(this);
        imRegister.setOnClickListener(this);

        //setup fragment manager
        fragmentManager = getSupportFragmentManager();
        replaceFragment(new LoginFragment(), "login");
    }
    private void replaceFragment(Fragment fragment, String name){
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContentLogin, fragment,name);
        fragmentTransaction.commit();
    }
    @Override
    public void onClick(View view) {
        if(view == imLogin){
            replaceFragment(new LoginFragment(), "login");
        }
        if(view == imRegister){
            replaceFragment(new RegisterFragment(), "register");
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = (Fragment) getSupportFragmentManager().findFragmentByTag("register");
        if(fragment != null && fragment.isVisible()){
            replaceFragment(new LoginFragment(), "login");
            return;
        }
        super.onBackPressed();
    }
}
