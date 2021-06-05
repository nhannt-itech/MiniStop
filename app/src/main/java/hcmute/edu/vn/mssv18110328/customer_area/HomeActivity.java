package hcmute.edu.vn.mssv18110328.customer_area;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import hcmute.edu.vn.mssv18110328.CategoryListAdapter;
import hcmute.edu.vn.mssv18110328.DatabaseHelper;
import hcmute.edu.vn.mssv18110328.ProductListCustomerAdapter;
import hcmute.edu.vn.mssv18110328.R;
import hcmute.edu.vn.mssv18110328.identity_area.LoginActivity;
import hcmute.edu.vn.mssv18110328.identity_area.RegisterActivity;
import hcmute.edu.vn.mssv18110328.models.Category;
import hcmute.edu.vn.mssv18110328.models.Product;
import hcmute.edu.vn.mssv18110328.utils.SharedPrefs;

import static hcmute.edu.vn.mssv18110328.utils.Utility.CURRENT_CATEGORY_ID;
import static hcmute.edu.vn.mssv18110328.utils.Utility.CURRENT_NAME;
import static hcmute.edu.vn.mssv18110328.utils.Utility.CURRENT_PRODUCT_ID;

public class HomeActivity extends AppCompatActivity {
    DatabaseHelper dbHelper= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());

        TextView tvHello = findViewById(R.id.tvHello);

        tvHello.setText("Xin chào " + SharedPrefs.getInstance().get(CURRENT_NAME, String.class) + "!");

        ViewFlipper viewFlipper = findViewById(R.id.viewFlipperAdvertise);
        viewFlipper.setFlipInterval(2000);
        viewFlipper.startFlipping();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.home:
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
        List<Product> lProduct = dbHelper.getProducts();
        GridView gridView = (GridView) findViewById(R.id.gvProductListCustomer);
        gridView.setAdapter(new ProductListCustomerAdapter(getApplicationContext(), lProduct));

        List<Category> lCategory = dbHelper.getCategories();
        GridView gridViewCategory = (GridView) findViewById(R.id.gvCategoryListCustomer);
        gridViewCategory.setAdapter(new CategoryListAdapter(getApplicationContext(), lCategory));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                long productId = parent.getItemIdAtPosition(position);
                startActivity(new Intent(HomeActivity.this , ProductActivity.class).putExtra(CURRENT_PRODUCT_ID, String.valueOf(productId)));
            }
        });

        gridViewCategory.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                long categoryId = parent.getItemIdAtPosition(position);
                startActivity(new Intent(HomeActivity.this , ProductListCustomerActivity.class).putExtra(CURRENT_CATEGORY_ID, String.valueOf(categoryId)));
            }
        });
    }
}