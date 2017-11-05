package duydev.com.grabwork.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import duydev.com.grabwork.R;

/**
 * Created by duy dev on 10/21/2017.
 */

public class RegisterFragment extends AppCompatActivity {

    private ImageView icon;
    private EditText edtName;
    private EditText edtPassReg;
    private EditText edtAddress;
    private EditText edtPhone;
    private EditText edtCardId;
    private Spinner spnRole;
    private Button btnRegister;
    private Uri url_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_register);

        icon = (ImageView) findViewById(R.id.imIcon);
        edtName = (EditText) findViewById(R.id.edtName);
        edtPassReg = (EditText) findViewById(R.id.edtPassRe);
        edtAddress = (EditText) findViewById(R.id.edtAddress);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtCardId = (EditText) findViewById(R.id.edtCardId);
        spnRole = (Spinner) findViewById(R.id.spnRole);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtName.getText().toString();
                String pass = edtPassReg.getText().toString();
                String address = edtAddress.getText().toString();
                String phone = edtPhone.getText().toString();
                String card = edtCardId.getText().toString();
                String role_id = spnRole.getSelectedItemPosition()+"";
                if(!name.equals("") && !pass.equals("") &&
                        !address.equals("") && !phone.equals("")
                        && !card.equals("")){
                    String []temp = {name, pass,address,phone,card,role_id};
                    new MyAsyncTask(RegisterFragment.this).execute(temp);
                }else
                    Toast.makeText(RegisterFragment.this, "Tên đăng nhập và mật khẩu phải khác rỗng!", Toast.LENGTH_SHORT).show();
            }
        });
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == 1)
            {
                url_image = data.getData();
                icon.setImageURI(url_image);
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    private class MyAsyncTask extends AsyncTask<String[], Integer, String> {

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
        protected String doInBackground(String[]... strings) {
            String flag = null;
            try {
                String path_image = "default";
                if(url_image != null){
                    InputStream is = getContentResolver().openInputStream(url_image);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,90,byteArray);
                    byte []array = byteArray.toByteArray();
                    path_image = Base64.encodeToString(array, Base64.DEFAULT);
                }

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("param1", strings[0][0]);
                postDataParams.put("param2", strings[0][1]);
                postDataParams.put("param3", strings[0][2]);
                postDataParams.put("param4", strings[0][3]);
                postDataParams.put("param5", Long.parseLong(strings[0][4]));
                postDataParams.put("param6", path_image);
                postDataParams.put("param7", 0);
                postDataParams.put("param8", 0);
                postDataParams.put("param9", Integer.parseInt(strings[0][5]));
                postDataParams.put("param10", 0);

                URL url = new URL("http://192.168.1.150:8080/GrabWorkService/rest/services/register");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();


                InputStream is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line = br.readLine();

                is.close();
                conn.disconnect();

                JSONObject result = new JSONObject(line);
                flag = result.getString("status");
                publishProgress(-1);

            } catch (MalformedURLException e) {

                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return flag;
        }
        public String getPostDataString(JSONObject params) throws Exception {

            StringBuilder result = new StringBuilder();
            boolean first = true;

            Iterator<String> itr = params.keys();

            while(itr.hasNext()){

                String key= itr.next();
                Object value = params.get(key);

                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));

            }
            return result.toString();
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
                    edtName.setText("");
                    edtPassReg.setText("");
                    edtAddress.setText("");
                    edtPhone.setText("");
                    edtCardId.setText("");
                    Toast.makeText(context, "thanh cong", Toast.LENGTH_LONG).show();

                } else {
                    edtName.setText("");
                    edtPassReg.setText("");
                    edtAddress.setText("");
                    edtPhone.setText("");
                    edtCardId.setText("");
                    Toast.makeText(context, "that bai", Toast.LENGTH_LONG).show();
                }
            }else{
                pd.dismiss();
                edtName.setText("");
                edtPassReg.setText("");
                edtAddress.setText("");
                edtPhone.setText("");
                edtCardId.setText("");
                Toast.makeText(context, "Không thể kết nối tới server", Toast.LENGTH_LONG).show();
            }
        }
    }
}
