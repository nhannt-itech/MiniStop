package hcmute.edu.vn.mssv18110328.identity_area;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hcmute.edu.vn.mssv18110328.DatabaseHelper;
import hcmute.edu.vn.mssv18110328.R;
import hcmute.edu.vn.mssv18110328.models.User;

import static hcmute.edu.vn.mssv18110328.utils.Utility.getBitmapAsByteArray;

public class RegisterActivity extends AppCompatActivity {
    ImageView mImageView;
    DatabaseHelper dbHelper= null;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSTION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        changeStatusBarColor();

        setContentView(R.layout.activity_register);
        dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());

        mImageView = findViewById(R.id.iv_avatar);

        mImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED)
                    {
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSTION_CODE);
                    }
                    else {
                        pickImageFromGarelly();
                    }
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case PERMISSTION_CODE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGarelly();
                }
                else{
                    Toast.makeText(this, "Permission denide...!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private void pickImageFromGarelly() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE && data!=null) {
            Uri ImageUrl = data.getData();
            CropImage.activity(ImageUrl)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                Uri resultUri = result.getUri();
                mImageView.setImageURI(resultUri);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
            }
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
    public void onLoginClick(View view){
        startActivity(new Intent(this,LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
    }
    public void doRegis(View view) {

        final EditText etName =  (EditText) findViewById(R.id.etName);
        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);

        String name = etName.getText().toString();
        String username = etUsername.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
        byte[] byteivImage = getBitmapAsByteArray(bitmap);

        if(TextUtils.isEmpty(name))
        {
            etName.requestFocus();
            etName.setError("Ba??n pha??i nh????p t??n!");
        }
        else if(TextUtils.isEmpty(username))
        {
            etUsername.requestFocus();
            etUsername.setError("Ba??n pha??i nh????p t??n ng??????i du??ng!");
        }
        else if(TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            etEmail.requestFocus();
            etEmail.setError("Email kh??ng h????p l????!");
        }
        else if(!isValidPassword(password))
        {
            etPassword.requestFocus();
            etPassword.setError("M????t kh????u pha??i t????i thi????u 8 ky?? t????, bao g????m ch???? ca??i, s???? va?? ky?? t???? ??????c bi????t!");
        }
        else if (dbHelper.emailExists(email))
        {
            etEmail.requestFocus();
            etEmail.setError("Email ??a?? t????n ta??i??!");
        }
        else if (dbHelper.usernameExists(username))
        {
            etUsername.requestFocus();
            etUsername.setError("Username ??a?? t????n ta??i??!");
        }
        else{
            User user = new User(name,username,email,password,byteivImage);
            boolean createSuccessful = dbHelper.addUser(user);

            int i = 1;
            if (createSuccessful)
            {
                startActivity(new Intent(this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
                Toast.makeText(this, "Ba??n ??a?? ????ng ky?? ta??i khoa??n tha??nh c??ng!", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Co?? l????i khi ????ng ky??!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public boolean isValidPassword(String password) { //T????i thi????u 8 ky?? t????, 1 ch???? ca??i, 1 s???? va?? 1 ky?? t???? ??????c bi????t

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }
}