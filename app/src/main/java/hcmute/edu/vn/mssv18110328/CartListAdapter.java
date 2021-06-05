package hcmute.edu.vn.mssv18110328;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import hcmute.edu.vn.mssv18110328.models.BillDetail;
import hcmute.edu.vn.mssv18110328.models.Product;

import static hcmute.edu.vn.mssv18110328.utils.Utility.FormatPrice;
import static hcmute.edu.vn.mssv18110328.utils.Utility.convertCompressedByteArrayToBitmap;

public class CartListAdapter  extends BaseAdapter {
    private List<BillDetail> listData;
    private List<Product> listProductWithData;
    private LayoutInflater layoutInflater;
    private Context context;


    public CartListAdapter(Context aContext,  List<BillDetail> listData, List<Product> listProductWithData) {
        this.context = aContext;
        this.listData = listData;
        this.listProductWithData = listProductWithData;
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
            convertView = layoutInflater.inflate(R.layout.cart_item_layout, null);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.ivImage);
            holder.name = (TextView) convertView.findViewById(R.id.tvName);
            holder.quantity = (TextView) convertView.findViewById(R.id.tvQuantity);
            holder.price = (TextView) convertView.findViewById(R.id.tvPrice);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BillDetail billDetail = this.listData.get(position);
        Product product = this.listProductWithData.get(position);

        holder.image.setImageBitmap(convertCompressedByteArrayToBitmap(product.getImage()));
        holder.name.setText(product.getName());
        holder.price.setText("Giá: " + FormatPrice(billDetail.getPrice()));
        holder.quantity.setText("Số lượng: " + String.valueOf(billDetail.getQuantity()));
        return convertView;
    }

    static class ViewHolder {
        ImageView image;
        TextView  name;
        TextView price;
        TextView quantity;
    }

}
