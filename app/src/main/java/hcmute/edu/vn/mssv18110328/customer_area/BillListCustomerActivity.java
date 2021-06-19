package hcmute.edu.vn.mssv18110328.customer_area;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import hcmute.edu.vn.mssv18110328.adapter.BillListCustomerAdapter;
import hcmute.edu.vn.mssv18110328.DatabaseHelper;
import hcmute.edu.vn.mssv18110328.R;
import hcmute.edu.vn.mssv18110328.utils.SharedPrefs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import static hcmute.edu.vn.mssv18110328.utils.Utility.CURRENT_ID;

public class BillListCustomerActivity extends AppCompatActivity {
    DatabaseHelper dbHelper= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_list_customer);
        dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.bill);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.bill:
                        return true;
                    case R.id.cart:
                        startActivity(new Intent(getApplicationContext(),CartActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.info:
                        startActivity(new Intent(getApplicationContext(),InfoActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        loadData();
    }

    private void loadData()
    {
        ListView listView = (ListView) findViewById(R.id.lvBill);
        androidx.cardview.widget.CardView cvBill = findViewById(R.id.cvBill);

        int curUserId = Integer.parseInt(SharedPrefs.getInstance().get(CURRENT_ID, String.class));

        if ( dbHelper.countBillsInUser(curUserId) > 0 ){//User đã có giỏ hàng chưa
            listView.setAdapter(new BillListCustomerAdapter(this, dbHelper.getBillsByUserId(curUserId)));
        }
        else
        {//User chưa có giỏ hàng
            RelativeLayout mainLayout = findViewById(R.id.mainLayout);

            RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lparams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

            listView.setVisibility(View.GONE);
            cvBill.setVisibility(View.GONE);

            TextView tvNone = new TextView(this);
            tvNone.setTextSize(20);
            tvNone.setText("Hiện tại bạn chưa có đơn hàng!");
            tvNone.setLayoutParams(lparams);
            mainLayout.addView(tvNone);
        }
    }


}