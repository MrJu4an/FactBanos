package ctec.app_fac_banos.ListView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ctec.app_fac_banos.Clases.FactBanos;
import ctec.app_fac_banos.R;

public class ListVAdapDetCierre extends BaseAdapter {
    private Context context;
    private ArrayList<FactBanos> factBanos;

    public ListVAdapDetCierre(Context context, ArrayList<FactBanos> listItems) {
        this.context = context;
        this.factBanos = listItems;
    }

    @Override
    public int getCount() {
        return factBanos.size();
    }

    @Override
    public Object getItem(int position) {
        return factBanos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FactBanos item = (FactBanos) getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.listview_detcierre,null);
        TextView txtVConcepto = convertView.findViewById(R.id.txtVConcepto);
        TextView txtVUsos = convertView.findViewById(R.id.txtVUsos);
        TextView txtVImp = convertView.findViewById(R.id.txtVImp);
        TextView txtVCantFact = convertView.findViewById(R.id.txtVCantFact);
        TextView txtVValor = convertView.findViewById(R.id.txtVValor);

        txtVConcepto.setText(item.getRBCONCEPTO());
        txtVUsos.setText(item.getRBCANTUSOS().toString());
        txtVImp.setText(item.getRBIMPFACT());
        txtVCantFact.setText(item.getRBNUMFAC().toString());
        txtVValor.setText(NumberFormat.getInstance().format(item.getRBTOTFAC()));

        return convertView;
    }

}
