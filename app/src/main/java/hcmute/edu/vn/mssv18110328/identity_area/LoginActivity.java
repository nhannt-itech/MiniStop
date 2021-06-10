package hcmute.edu.vn.mssv18110328.identity_area;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;


import hcmute.edu.vn.mssv18110328.adapter.DatabaseHelper;
import hcmute.edu.vn.mssv18110328.R;
import hcmute.edu.vn.mssv18110328.customer_area.HomeActivity;
import hcmute.edu.vn.mssv18110328.models.User;
import hcmute.edu.vn.mssv18110328.utils.SharedPrefs;

import static hcmute.edu.vn.mssv18110328.utils.Utility.CURRENT_ID;
import static hcmute.edu.vn.mssv18110328.utils.Utility.CURRENT_NAME;

public class LoginActivity extends AppCompatActivity {
    DatabaseHelper dbHelper= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_login);
        changeStatusBarColor();
        dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
    }
    public void onLoginClick(View View){
        startActivity(new Intent(this,RegisterActivity.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
    }
    public void onForgotClick(View View){
        startActivity(new Intent(this,ForgotPasswordActivity.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
    }
    public void doLogin(View view)
    {
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (dbHelper.loginIsSuccess(email,password))
        {
            User curUser = dbHelper.getUser(email);

            SharedPrefs.getInstance().put(CURRENT_ID, String.valueOf(curUser.getId()));
            SharedPrefs.getInstance().put(CURRENT_NAME, curUser.getName());

            startActivity(new Intent(this,HomeActivity.class));
            Toast.makeText(this, "Bạn đã đăng nhập vào tài khoản thành công!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Tài khoản hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }
}