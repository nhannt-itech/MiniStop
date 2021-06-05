package hcmute.edu.vn.mssv18110328.customer_area;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import hcmute.edu.vn.mssv18110328.DatabaseHelper;
import hcmute.edu.vn.mssv18110328.R;
import hcmute.edu.vn.mssv18110328.models.User;
import hcmute.edu.vn.mssv18110328.utils.SharedPrefs;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;

import static hcmute.edu.vn.mssv18110328.utils.Utility.CURRENT_ID;
import static hcmute.edu.vn.mssv18110328.utils.Utility.CURRENT_NAME;
import static hcmute.edu.vn.mssv18110328.utils.Utility.convertCompressedByteArrayToBitmap;
import static hcmute.edu.vn.mssv18110328.utils.Utility.getBitmapAsByteArray;

public class InfoActivity extends AppCompatActivity {
    ImageView mImageView;
    DatabaseHelper dbHelper= null;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSTION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.info);

        dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
        mImageView = findViewById(R.id.iv_avatar);

        EditText etName =  (EditText) findViewById(R.id.etName);
        EditText etUsername = (EditText) findViewById(R.id.etUsername);
        EditText etEmail = (EditText) findViewById(R.id.etEmail);

        int idUserIsLogin = Integer.parseInt(SharedPrefs.getInstance().get(CURRENT_ID, String.class));
        User userIsLogin = dbHelper.getUser(idUserIsLogin);

        mImageView.setImageBitmap(convertCompressedByteArrayToBitmap(userIsLogin.getImage()));
        etName.setText(userIsLogin.getName());
        etUsername.setText(userIsLogin.getUsername());
        etEmail.setText(userIsLogin.getEmail());

        etUsername.setEnabled(false);
        etEmail.setEnabled(false);

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

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.favorite:
                        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.cart:
                        startActivity(new Intent(getApplicationContext(),CartActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.info:
                        return true;
                }
                return false;
            }
        });
    }

    public void doUpdateInfo(View view)
    {
        EditText etName =  (EditText) findViewById(R.id.etName);
        EditText etUsername =  (EditText) findViewById(R.id.etUsername);
        EditText etEmail =  (EditText) findViewById(R.id.etEmail);
        Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();

        int idUserIsLogin = Integer.parseInt(SharedPrefs.getInstance().get(CURRENT_ID, String.class));
        User userIsLogin = dbHelper.getUser(idUserIsLogin);

        userIsLogin.setName(etName.getText().toString());
        userIsLogin.setUsername(etUsername.getText().toString());
        userIsLogin.setEmail(etEmail.getText().toString());
        userIsLogin.setImage(getBitmapAsByteArray(bitmap));

        if (dbHelper.updateUser(userIsLogin)){
            Toast.makeText(this, "Thông tin bạn đã được cập nhật", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Gặp lỗi khi cập nhật thông tin.", Toast.LENGTH_SHORT).show();
        }
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
}