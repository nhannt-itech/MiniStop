package hcmute.edu.vn.mssv18110328.customer_area;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import hcmute.edu.vn.mssv18110328.adapter.CategoryListAdapter;
import hcmute.edu.vn.mssv18110328.adapter.DatabaseHelper;
import hcmute.edu.vn.mssv18110328.adapter.ProductListCustomerAdapter;
import hcmute.edu.vn.mssv18110328.R;
import hcmute.edu.vn.mssv18110328.models.Category;
import hcmute.edu.vn.mssv18110328.models.Product;
import hcmute.edu.vn.mssv18110328.utils.SharedPrefs;

public class HomeActivity extends AppCompatActivity {
    DatabaseHelper dbHelper= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());

        TextView tvHello = findViewById(R.id.tvHello);

        tvHello.setText("Xin chào " + SharedPrefs.getInstance().get("current_name", String.class) + "!");

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
                    case R.id.bill:
                        startActivity(new Intent(getApplicationContext(),BillListCustomerActivity.class));
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
                Intent intentProduct = new Intent(HomeActivity.this , ProductActivity.class);
                intentProduct.putExtra("current_product_id", String.valueOf(parent.getItemIdAtPosition(position)));
                startActivity(intentProduct);
            }
        });

        gridViewCategory.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                long categoryId = parent.getItemIdAtPosition(position);
                startActivity(new Intent(HomeActivity.this , ProductListCustomerActivity.class).putExtra("current_category_id", String.valueOf(categoryId)));
            }
        });
    }
}