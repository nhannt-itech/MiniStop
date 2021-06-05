package hcmute.edu.vn.mssv18110328.area_admin;

import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.util.List;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import hcmute.edu.vn.mssv18110328.DatabaseHelper;
import hcmute.edu.vn.mssv18110328.R;
import hcmute.edu.vn.mssv18110328.models.Category;

public class CategoryListActivity extends AppCompatActivity {
    private final static String TAG = "CategoryListActivity";
    DatabaseHelper dbHelper= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        countRecords();
        readRecords();

        Button buttonCreateCategory = (Button) findViewById(R.id.buttonCreateCategory);
        buttonCreateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Context context = view.getRootView().getContext();
                dbHelper = new DatabaseHelper(context, context.getFilesDir().getAbsolutePath());

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View formElementsView = inflater.inflate(R.layout.category_input_form, null, false);

                final EditText editTextCategoryName = (EditText) formElementsView.findViewById(R.id.editTextCategoryName);
                final EditText editTextCategoryContent = (EditText) formElementsView.findViewById(R.id.editTextCategoryContent);

                new AlertDialog.Builder(context)
                        .setView(formElementsView)
                        .setTitle("TẠO DANH SÁCH")
                        .setPositiveButton("Add",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String categoryName = editTextCategoryName.getText().toString();
                                        String categoryContent = editTextCategoryContent.getText().toString();

                                        Category category = new Category(categoryName,categoryContent,null);
                                        boolean createSuccessful = dbHelper.addCategory(category);
                                        List<Category> count = dbHelper.getCategories();
                                        if(createSuccessful){
                                            Toast.makeText(context, "Student information was saved.", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(context, "Unable to save student information.", Toast.LENGTH_SHORT).show();
                                        }
                                        countRecords();
                                        readRecords();
                                        dialog.cancel();
                                    }
                                }).show();
            }
        });

        dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
        try {
            dbHelper.prepareDatabase();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }
    public void countRecords() {
        dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
        int recordCount = dbHelper.countCategory();
        TextView textViewRecordCount = (TextView) findViewById(R.id.textViewRecordCount);
        textViewRecordCount.setTextSize(30);
        textViewRecordCount.setText("Số lượng: "+recordCount);
    }

    public void readRecords() {

        LinearLayout linearLayoutRecords = (LinearLayout) findViewById(R.id.linearLayoutRecords);
        linearLayoutRecords.removeAllViews();

        List<Category> categories = dbHelper.getCategories();
        if (categories.size() > 0) {

            for (Category obj : categories) {

                int id = obj.getId();
                String name = obj.getName();
                String content = obj.getContent();

                String textViewContents = "Tên: " + name + " - Nội dung: " + content;

                TextView textViewCategoryItem= new TextView(this);
                textViewCategoryItem.setPadding(0, 10, 0, 10);
                textViewCategoryItem.setTextSize(20);
                textViewCategoryItem.setText(textViewContents);
                textViewCategoryItem.setTag(Integer.toString(id));
                textViewCategoryItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        String id = view.getTag().toString();
                        Context context = view.getContext();

                        final CharSequence[] items = { "Edit", "Delete" };

                        new AlertDialog.Builder(context).setTitle("Category Record")
                                .setItems(items, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int item) {
                                        if (item == 0) {
                                            editRecord(Integer.parseInt(id), view);
                                        }
                                        else if (item == 1) {

                                            boolean deleteSuccessful = dbHelper.deleteCategory(Integer.parseInt(id));

                                            if (deleteSuccessful){
                                                Toast.makeText(context, "Student record was deleted.", Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(context, "Unable to delete student record.", Toast.LENGTH_SHORT).show();
                                            }

                                            countRecords();
                                            readRecords();

                                        }
                                        dialog.dismiss();
                                    }
                                }).show();
                    }
                });

                linearLayoutRecords.addView(textViewCategoryItem);
            }

        }

        else {

            TextView locationItem = new TextView(this);
            locationItem.setPadding(8, 8, 8, 8);
            locationItem.setText("Không có dữ liệu trong danh sách");
            locationItem.setTextSize(20);
            linearLayoutRecords.addView(locationItem);
        }

    }

    public void editRecord(final int id, View view) {
        Context context = view.getRootView().getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.category_input_form, null, false);

        final EditText editTextStudentFirstname = (EditText) formElementsView.findViewById(R.id.editTextCategoryName);
        final EditText editTextStudentEmail = (EditText) formElementsView.findViewById(R.id.editTextCategoryContent);

        Category category = dbHelper.getCategory(id);

        editTextStudentFirstname.setText(category.getName());
        editTextStudentEmail.setText(category.getContent());

        new AlertDialog.Builder(context)
                .setView(formElementsView)
                .setTitle("Edit Record")
                .setPositiveButton("Save Changes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                                Category newCategory = new Category(category.getId(),
                                        ((EditText) formElementsView.findViewById(R.id.editTextCategoryName)).getText().toString(),
                                        ((EditText) formElementsView.findViewById(R.id.editTextCategoryContent)).getText().toString(),
                                        null)    ;

                                boolean updateSuccessful = dbHelper.updateCategory(newCategory);

                                if(updateSuccessful){
                                    Toast.makeText(context, "Student record was updated.", Toast.LENGTH_SHORT).show();
                                    readRecords();
                                }else{
                                    Toast.makeText(context, "Unable to update student record.", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }).show();
    }
}

