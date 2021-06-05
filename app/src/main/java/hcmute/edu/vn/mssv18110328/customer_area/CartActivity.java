package hcmute.edu.vn.mssv18110328.customer_area;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hcmute.edu.vn.mssv18110328.CartListAdapter;
import hcmute.edu.vn.mssv18110328.DatabaseHelper;
import hcmute.edu.vn.mssv18110328.ProductListAdapter;
import hcmute.edu.vn.mssv18110328.R;
import hcmute.edu.vn.mssv18110328.models.Bill;
import hcmute.edu.vn.mssv18110328.models.BillDetail;
import hcmute.edu.vn.mssv18110328.models.Category;
import hcmute.edu.vn.mssv18110328.models.Product;
import hcmute.edu.vn.mssv18110328.utils.SharedPrefs;

import static hcmute.edu.vn.mssv18110328.utils.Utility.COMPLETE_ORDER_STATUS;
import static hcmute.edu.vn.mssv18110328.utils.Utility.CURRENT_ID;
import static hcmute.edu.vn.mssv18110328.utils.Utility.CURRENT_PRODUCT_ID;
import static hcmute.edu.vn.mssv18110328.utils.Utility.INCOMPLETE_ORDER_STATUS;
import static hcmute.edu.vn.mssv18110328.utils.Utility.FormatPrice;


public class CartActivity extends AppCompatActivity {
    DatabaseHelper dbHelper = null;
    int curUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());

        //-----------KHỞI TẠO GIÁ TRỊ CẦN THIẾT----------
        curUserId = Integer.parseInt(SharedPrefs.getInstance().get(CURRENT_ID, String.class));
        //---------KẾT THÚC KHỞI TẠO GIÁ THIẾT----------

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.cart);

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

        Button button = findViewById(R.id.btnOrderNow);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Context context = view.getRootView().getContext();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View formElementsView = inflater.inflate(R.layout.bill_input_form, null, false);

                EditText etAddress =  (EditText) formElementsView.findViewById(R.id.etAddress);
                EditText etPhone =  (EditText) formElementsView.findViewById(R.id.etPhone);

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setView(formElementsView)
                        .setTitle("Xác nhận đơn hàng")
                        .setPositiveButton("Hoàn tất", null).create();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {

                        Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String address = etAddress.getText().toString();
                                String phone = etPhone.getText().toString();
                                if(TextUtils.isEmpty(address))
                                {
                                    etAddress.requestFocus();
                                    etAddress.setError("Bạn phải nhập địa chỉ!");
                                }
                                else if(TextUtils.isEmpty(phone))
                                {
                                    etPhone.requestFocus();
                                    etPhone.setError("Bạn phải nhập số điện thoại!");
                                }
                                else{
                                    Bill curCart = dbHelper.getIncompleteBillByUserId(curUserId);
                                    curCart.setAddress(address);
                                    curCart.setPhone(phone);
                                    curCart.setStatus(COMPLETE_ORDER_STATUS);
                                    dbHelper.updateBill(curCart);
                                    Toast.makeText(getApplicationContext(), "Bạn đã đặt hành thành công!", Toast.LENGTH_SHORT).show();
                                    loadData();
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                });
                dialog.show();
            }
        });
    }

    private void loadData()
    {
        ListView listView = (ListView) findViewById(R.id.lvBillDetail);
        TextView tvTotal = findViewById(R.id.tvTotal);
        Button btnOrderNow = findViewById(R.id.btnOrderNow);
        androidx.cardview.widget.CardView cvBillDetail = findViewById(R.id.cvBillDetail);

        if (dbHelper.isUserHasCart(curUserId)){//User đã có giỏ hàng chưa
            Bill curCart = dbHelper.getIncompleteBillByUserId(curUserId);
            List<BillDetail> lBillDetail = dbHelper.getBillDetailByBillId(curCart.getId());

            List<Product> lProduct =  new ArrayList<Product>();;

            for ( BillDetail object : lBillDetail) {
                lProduct.add(dbHelper.getProduct(object.getProductId()));
            }

            listView.setAdapter(new CartListAdapter(this, lBillDetail,lProduct));
            tvTotal.setText("Tổng cộng: " + FormatPrice(curCart.getTotalPrice()));
        }
        else
        {
            listView.setVisibility(View.GONE);
            btnOrderNow.setVisibility(View.GONE);
            cvBillDetail.setVisibility(View.GONE);

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)tvTotal.getLayoutParams();
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

            tvTotal.setText("Giỏ hàng trống!");
            tvTotal.setTextSize(20);
            tvTotal.setPadding(10,500, 10,10);
            tvTotal.setLayoutParams(layoutParams);
        }
    }
}