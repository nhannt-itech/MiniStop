package hcmute.edu.vn.mssv18110328.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import hcmute.edu.vn.mssv18110328.R;
import hcmute.edu.vn.mssv18110328.models.Category;
import hcmute.edu.vn.mssv18110328.models.Product;
import static hcmute.edu.vn.mssv18110328.utils.Utility.convertCompressedByteArrayToBitmap;

public class    CategoryListAdapter  extends BaseAdapter {
    private List<Category> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public CategoryListAdapter(Context aContext,  List<Category> listData) {
        this.context = aContext;
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listData.get(position).getId();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.category_item_customer_layout, null);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.ivImage);
            holder.name = (TextView) convertView.findViewById(R.id.tvName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Category category = this.listData.get(position);

        holder.image.setImageBitmap(convertCompressedByteArrayToBitmap(category.getImage()));
        holder.name.setText(category.getName());

        return convertView;
    }

    static class ViewHolder {
        ImageView image;
        TextView  name;
    }
}
