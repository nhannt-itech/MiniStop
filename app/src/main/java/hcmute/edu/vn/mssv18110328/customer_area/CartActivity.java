package hcmute.edu.vn.mssv18110328.customer_area;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hcmute.edu.vn.mssv18110328.adapter.CartListAdapter;
import hcmute.edu.vn.mssv18110328.DatabaseHelper;
import hcmute.edu.vn.mssv18110328.R;
import hcmute.edu.vn.mssv18110328.models.Bill;
import hcmute.edu.vn.mssv18110328.models.BillDetail;
import hcmute.edu.vn.mssv18110328.models.Product;
import hcmute.edu.vn.mssv18110328.utils.SharedPrefs;

import static hcmute.edu.vn.mssv18110328.utils.Utility.CURRENT_ID;
import static hcmute.edu.vn.mssv18110328.utils.Utility.FormatPrice;
import static hcmute.edu.vn.mssv18110328.utils.Utility.convertCompressedByteArrayToBitmap;


public class CartActivity extends AppCompatActivity {
    DatabaseHelper dbHelper = null;
    int curUserId;
    SimpleDateFormat formatterDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
        formatterDate = new SimpleDateFormat("dd-MM-yyyy");


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
                    case R.id.bill:
                        startActivity(new Intent(getApplicationContext(),BillListCustomerActivity.class));
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
                                    Date date = new Date(System.currentTimeMillis());
                                    String dateBill = formatterDate.format(date);

                                    Bill curCart = dbHelper.getIncompleteBillByUserId(curUserId);
                                    curCart.setAddress(address);
                                    curCart.setPhone(phone);
                                    curCart.setStatus("complete");
                                    curCart.setDate(dateBill);

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

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    int curBillDetailId = Integer.parseInt(String.valueOf(parent.getItemIdAtPosition(position)));
                    BillDetail curBillDetail = dbHelper.getBillDetail(curBillDetailId);
                    showDialogUpdateBill(curBillDetail);
                }
            });
            tvTotal.setText("Tổng cộng: " + FormatPrice(curCart.getTotalPrice()));
        }
        else
        {
            listView.setVisibility(View.GONE);
            btnOrderNow.setVisibility(View.GONE);
            cvBillDetail.setVisibility(View.GONE);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

            tvTotal.setText("Giỏ hàng trống!");
            tvTotal.setTextSize(20);
            tvTotal.setLayoutParams(layoutParams);
        }
    }

    public void showDialogUpdateBill(BillDetail billDetail) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.cart_input_form, null, false);

        TextView tvName =  (TextView) formElementsView.findViewById(R.id.tvName);
        TextView tvPrice = (TextView) formElementsView.findViewById(R.id.tvPrice);
        TextView tvQuantity = (TextView) formElementsView.findViewById(R.id.tvQuantity);
        ImageView ivImage =  (ImageView) formElementsView.findViewById(R.id.ivImage);
        Button btnMinus = (Button) formElementsView.findViewById(R.id. btnMinus);
        Button btnPlus = (Button) formElementsView.findViewById(R.id. btnPlus);

        Product product = dbHelper.getProduct(billDetail.getProductId());

        tvName.setText(product.getName());
        tvPrice.setText(FormatPrice(billDetail.getPrice()));
        tvQuantity.setText(String.valueOf(billDetail.getQuantity()));
        ivImage.setImageBitmap(convertCompressedByteArrayToBitmap(product.getImage()));

        btnMinus.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if(Integer.parseInt(tvQuantity.getText().toString()) > 0) {
                    tvQuantity.setText( String.valueOf(Integer.parseInt(tvQuantity.getText().toString()) - 1) );
                    tvPrice.setText(FormatPrice(product.getPrice()* Integer.parseInt(tvQuantity.getText().toString())));
                }
            }
        });
        btnPlus.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if(Integer.parseInt(tvQuantity.getText().toString()) < 10) {
                    tvQuantity.setText( String.valueOf(Integer.parseInt(tvQuantity.getText().toString()) + 1) );
                    tvPrice.setText(FormatPrice(product.getPrice()* Integer.parseInt(tvQuantity.getText().toString())));
                }
            }
        });

        new AlertDialog.Builder(this)
                .setView(formElementsView)
                .setTitle("Cập nhật giỏ hàng")
                .setPositiveButton("Cập nhật",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                int curUserId = Integer.parseInt(SharedPrefs.getInstance().get(CURRENT_ID, String.class));
                                Bill curBill = dbHelper.getIncompleteBillByUserId(curUserId); //Lấy giỏ hàng
                                if (Integer.parseInt(tvQuantity.getText().toString()) == 0)
                                {
                                    if (dbHelper.deleteBillDetail(billDetail.getId()))
                                        Toast.makeText(getApplicationContext(), "Bạn vừa xóa sản phẩm khỏi đơn hàng.", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    billDetail.setQuantity(Integer.parseInt(tvQuantity.getText().toString()));
                                    billDetail.setPrice(product.getPrice()*billDetail.getQuantity());
                                    if (dbHelper.updateBillDetail(billDetail))
                                        Toast.makeText(getApplicationContext(), "Bạn vừa cập nhật đơn hàng.", Toast.LENGTH_SHORT).show();
                                }
                                //------------CẬP NHẬT LẠI TỔNG GIÁ TRỊ ĐƠN HÀNG-----------
                                if (dbHelper.countBillDetailsInBill(curBill.getId()) > 0)
                                {
                                    double totalPrice = 0;
                                    List<BillDetail> lBillDetail = dbHelper.getBillDetailByBillId(curBill.getId());
                                    for ( BillDetail object : lBillDetail) {
                                        totalPrice = totalPrice + object.getPrice();
                                    }
                                    curBill.setTotalPrice(totalPrice);
                                    dbHelper.updateBill(curBill);
                                }
                                else{
                                    dbHelper.deleteBill(curBill.getId());
                                }
                                loadData();
                                //-----------KẾT THÚC CẬP NHẬT TỔNG GIÁ TRỊ ĐƠN HÀNG--------
                                dialog.cancel();
                            }
                        }).show();
    }
}