package duydev.com.grabwork.activity;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import duydev.com.grabwork.R;

public class LoginFragment extends AppCompatActivity implements View.OnClickListener{
    private EditText edtUser;
    private EditText edtPass;
    private Button btnLogin;
    private CheckBox cbReUser;
    private TextView tvForgotPass;
    private SharedPreferences sharedPreferences;
    private ImageView imLogin;
    private ImageView imRegister;
    private FragmentManager fragmentManager;
    private android.support.v4.app.FragmentTransaction fragmentTransaction;
    private LoginFragment fragmentLogin;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);

        imLogin = (ImageView)findViewById(R.id.imLogin);
        imRegister = (ImageView)findViewById(R.id.imRegister);

        //add listener
        imLogin.setOnClickListener(this);
        imRegister.setOnClickListener(this);
        edtUser = (EditText) findViewById(R.id.edt_user);
        edtPass = (EditText) findViewById(R.id.edtPass);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        cbReUser = (CheckBox) findViewById(R.id.cbRemember);
        tvForgotPass = (TextView) findViewById(R.id.tvForgotPass);

        btnLogin.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if(view == btnLogin){
            String user = edtUser.getText().toString();
            String pass = edtPass.getText().toString();

            if(!user.equals("") && !pass.equals("")){
                String url = "http://192.168.1.150:8080/GrabWorkService/rest/services/login;user="+user+";pass="+pass;
                new MyAsyncTask(this).execute(url);
            }else
                Toast.makeText(LoginFragment.this, "Tên đăng nhập và mật khẩu phải khác rỗng!", Toast.LENGTH_SHORT).show();
        }
        if(view == imRegister){
            Intent intent = new Intent(this, RegisterFragment.class);
            startActivity(intent);
        }
    }
    private void replaceFragment(Fragment fragment, String name){
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContentLogin, fragment,name);
        fragmentTransaction.commit();
    }
    public class MyAsyncTask extends AsyncTask<String, Integer, String>{

        private ProgressDialog pd;
        private Context context;

        public MyAsyncTask(Context ctx){
            this.context = ctx;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(context,"Vui lòng đợi...", "Đang kiểm tra..", false, true);
            pd.setCanceledOnTouchOutside(false);
        }

        @Override
        protected String doInBackground(String... strings) {
            String flag = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream is = httpURLConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line = br.readLine();

                JSONObject result = new JSONObject(line);
                flag = result.getString("status");
                httpURLConnection.disconnect();
                publishProgress(-1);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return flag;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(values[0] == -1)
                pd.dismiss();
        }

        @Override
        protected void onPostExecute(String flag) {
            super.onPostExecute(flag);

            if(flag != null) {
                if (flag.equals("Thành công")) {
                    //save user
                    if (cbReUser.isChecked()) {
                        edtUser.setText("");
                        edtPass.setText("");
                        sharedPreferences = context.getSharedPreferences("login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user", flag);
                        editor.commit();
                    }

                } else {
                    edtUser.setText("");
                    edtPass.setText("");
                    Toast.makeText(context, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_LONG).show();
                }
            }else{
                pd.dismiss();
                edtUser.setText("");
                edtPass.setText("");
                Toast.makeText(context, "Không thể kết nối tới server", Toast.LENGTH_LONG).show();
            }
        }
    }
}
