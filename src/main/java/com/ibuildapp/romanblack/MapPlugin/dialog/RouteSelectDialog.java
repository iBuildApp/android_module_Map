package com.ibuildapp.romanblack.MapPlugin.dialog;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ibuildapp.romanblack.MapPlugin.R;

import java.util.List;

public class RouteSelectDialog extends Dialog{
    public interface RouteDialogListener{
        void itemClick(int position);
    }

    private List<String> items;
    private ListView list;

    public RouteSelectDialog(Context context, List<String> items, final RouteDialogListener listener) {
        super(context);
        this.items = items;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(true);

        setContentView(R.layout.map_route_select_dialog);
        list = (ListView) findViewById(R.id.map_route_select_dialog_list);
        list.setAdapter(new DialogAdapter());

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null)
                    listener.itemClick(position);

                dismiss();
            }
        });

    }

    public class DialogAdapter extends BaseAdapter{

        public  class ViewHolder{
            public TextView routeName;
        }
        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public String getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final String item = getItem(position);
            ViewHolder holder;

            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.map_route_select_dialog_item, null);
                holder = new ViewHolder();
                holder.routeName = (TextView) convertView.findViewById(R.id.map_route_select_dialog_item_text);
                convertView.setTag(holder);
            }
            else holder = (ViewHolder) convertView.getTag();

            holder.routeName.setText(item);

            return convertView;
        }
    }
}
