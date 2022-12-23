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

import ctec.app_fac_banos.Clases.DetalleCaja;
import ctec.app_fac_banos.Clases.FactBanos;
import ctec.app_fac_banos.FacBanosActivity;
import ctec.app_fac_banos.R;

public class ListVAdapDetCaja extends BaseAdapter {
    private Context context;
    private ArrayList<FactBanos> factBanos;

    public ListVAdapDetCaja(Context context, ArrayList<FactBanos> listItems) {
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

        convertView = LayoutInflater.from(context).inflate(R.layout.listview_detalle,null);
        TextView txtVFact = convertView.findViewById(R.id.txtVFact);
        TextView txtVCant = convertView.findViewById(R.id.txtVCant);
        TextView txtVTotal = convertView.findViewById(R.id.txtVTotal);
        TextView txtVFecha = convertView.findViewById(R.id.txtVFecha);
        TextView txtCliente = convertView.findViewById(R.id.txtCliente);

        txtVFact.setText(item.getRBPREFACT().toString() + "-" + item.getRBNUMFAC().toString());
        txtVCant.setText(item.getRBCANTUSOS().toString());
        txtVTotal.setText(NumberFormat.getInstance().format(item.getRBTOTFAC()));

        txtVFecha.setText(item.getRBFECHA());
        txtCliente.setText(item.getRBCEDCLIENTE() + " " + item.getRBNOMCLIENTE());

        return convertView;
    }

    private Date convertDate(String date,String Format){
        Date dateResult = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(Format);
            String dateInString = date;
            dateResult = formatter.parse(dateInString);

        } catch (ParseException ex) {

        }
        return  dateResult;
    }

}
