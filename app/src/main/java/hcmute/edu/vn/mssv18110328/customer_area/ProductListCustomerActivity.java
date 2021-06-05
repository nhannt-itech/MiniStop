package hcmute.edu.vn.mssv18110328.customer_area;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import java.util.List;

import hcmute.edu.vn.mssv18110328.DatabaseHelper;
import hcmute.edu.vn.mssv18110328.ProductListCustomerAdapter;
import hcmute.edu.vn.mssv18110328.R;
import hcmute.edu.vn.mssv18110328.models.Product;

import static hcmute.edu.vn.mssv18110328.utils.Utility.CURRENT_CATEGORY_ID;
import static hcmute.edu.vn.mssv18110328.utils.Utility.CURRENT_PRODUCT_ID;

public class ProductListCustomerActivity extends AppCompatActivity {
    int categoryId;
    DatabaseHelper dbHelper= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list_customer);
        categoryId = Integer.parseInt(getIntent().getStringExtra(CURRENT_CATEGORY_ID));
        dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());

        loadData();
    }

    private void loadData()
    {
        List<Product> lProduct = dbHelper.getProductsByCategoryId(categoryId);
        final ListView listView = (ListView) findViewById(R.id.lvProduct);
        listView.setAdapter(new ProductListCustomerAdapter(getApplicationContext(), lProduct));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                long productId = parent.getItemIdAtPosition(position);
                startActivity(new Intent(ProductListCustomerActivity.this , ProductActivity.class).putExtra(CURRENT_PRODUCT_ID, String.valueOf(productId)));
            }
        });
    }
}