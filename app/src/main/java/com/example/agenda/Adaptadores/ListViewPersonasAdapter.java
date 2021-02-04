package com.example.agenda.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.agenda.Models.Persona;
import com.example.agenda.R;

import java.util.ArrayList;

public class ListViewPersonasAdapter extends BaseAdapter {
    Context context;
    ArrayList <Persona> personaData;
    LayoutInflater layoutInflater;
    Persona personaModel;

    public ListViewPersonasAdapter(Context context, ArrayList<Persona> personaData) {
        this.context = context;
        this.personaData = personaData;
        layoutInflater=(LayoutInflater)context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
        );
    }

    @Override
    public int getCount() {
        return personaData.size();
    }

    @Override
    public Object getItem(int position) {
        return personaData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if(rowView==null){
          rowView = layoutInflater.inflate(R.layout.lista_personas,
                  null,
                  true);
        }
        //Enlazar vistas
        TextView nombres = rowView.findViewById(R.id.lblnombres);
        TextView telefono = rowView.findViewById(R.id.lbltelefono);
        TextView fecharegistro = rowView.findViewById(R.id.lblfecharegistro);

        personaModel = personaData.get(position);
        nombres.setText(personaModel.getNombres());
        telefono.setText(personaModel.getTelefono());
        fecharegistro.setText(personaModel.getFecharegistro());


        return rowView;
    }
}
