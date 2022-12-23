package ctec.app_fac_banos.ListView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ctec.app_fac_banos.Clases.Puesto;
import ctec.app_fac_banos.R;

public class ListViewAdapterPuestos extends BaseAdapter {
    private ArrayList<Puesto> listItems;
    private Context context;

    public ListViewAdapterPuestos(Context context,ArrayList<Puesto> listItems) {
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Puesto item = (Puesto) getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.listview_pasajero,null);
        TextView txtPasajero = convertView.findViewById(R.id.spnConcept);
        TextView txtInfo = convertView.findViewById(R.id.txtInfo);
        txtPasajero.setText(item.getIntIdPuesto().toString() );
        if (item.getEstadoPuesto()==true)
            txtInfo.setText("Puesto Libre");
        else
            txtInfo.setText(item.getInfPuesto().toString());

        return convertView;
    }

}
