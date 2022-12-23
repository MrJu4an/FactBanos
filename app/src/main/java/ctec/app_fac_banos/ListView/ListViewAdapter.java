package ctec.app_fac_banos.ListView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ctec.app_fac_banos.R;

public class ListViewAdapter extends BaseAdapter {

    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<DatoListView> datoListViewlist = null;
    private ArrayList<DatoListView> arraylist;

    public ListViewAdapter(Context context, List<DatoListView> datoListViewlist) {
        mContext = context;
        this.datoListViewlist = datoListViewlist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<DatoListView>();
        this.arraylist.addAll(datoListViewlist);
    }

    public class ViewHolder {
        TextView rank;
        TextView country;
        TextView population;
    }

    @Override
    public int getCount() {
        return datoListViewlist.size();
    }

    @Override
    public DatoListView getItem(int position) {
        return datoListViewlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.listview_item, null);
            // Locate the TextViews in listview_item.xml
            holder.rank = (TextView) view.findViewById(R.id.txtVDato);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.rank.setText(datoListViewlist.get(position).getDato());

        /* Listen for ListView Item Click
        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Global.g_PosBuscar = position;
            }
        });*/

        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        datoListViewlist.clear();
        if (charText.length() == 0) {
            datoListViewlist.addAll(arraylist);
        }
        else
        {
            for (DatoListView wp : arraylist)
            {
                if (wp.getDato().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    datoListViewlist.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}
