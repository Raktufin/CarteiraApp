package com.ocp.carteiraapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import modelo.Evento;

//Classe que define o comportamento e dados de cada um dos ITENS da lista de eventos
public class ItemListaEvento extends ArrayAdapter<Evento> {
    private Context contextoPai;
    private ArrayList<Evento> eventos;

    private static class ViewHolder {
        private TextView nomeItem;
        private TextView valorItem;
        private TextView dataItem;
        private TextView repeteItem;
        private TextView fotoItem;


    }

    public ItemListaEvento(Context contexto, ArrayList<Evento> dados){
        super(contexto, R.layout.item_lista_eventos, dados);

        this.contextoPai = contexto;
        this.eventos = dados;
    }

    @NonNull
    @Override
    public View getView(int indice, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);

        Evento eventoAtual = eventos.get(indice);
        ViewHolder novaView;

        final View resultado;

        //1º caso: Quando a lista e criada pela primeira vez
        if(convertView == null){
            novaView = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_lista_eventos, parent, false);

            novaView.dataItem = (TextView) convertView.findViewById(R.id.dataItem);
            novaView.fotoItem = (TextView) convertView.findViewById(R.id.fotoItem);
            novaView.nomeItem = (TextView) convertView.findViewById(R.id.nomeItem);
            novaView.repeteItem = (TextView) convertView.findViewById(R.id.repeteItem);
            novaView.valorItem = (TextView) convertView.findViewById(R.id.valorItem);

            resultado = convertView;
            convertView.setTag(novaView);
        } else {
            //2º caso: Item modificado
            novaView = (ViewHolder) convertView.getTag();
            return convertView;
        }

        //Setar os valores de cada campo
        novaView.nomeItem.setText(eventoAtual.getNome());
        novaView.valorItem.setText(eventoAtual.getValor() + "");
        novaView.fotoItem.setText(eventoAtual.getCaminhoFt() == null ? "Não" : "Sim");
        SimpleDateFormat formataData = new SimpleDateFormat("dd/MM/yyyy");
        novaView.dataItem.setText(formataData.format(eventoAtual.getOcorreu()));


        //Verificando se o evento repete
        Calendar data1 = Calendar.getInstance();
        data1.setTime(eventoAtual.getOcorreu());

        Calendar data2 = Calendar.getInstance();
        data2.setTime(eventoAtual.getValida());

        if(data1.get(Calendar.MONTH) != data2.get(Calendar.MONTH)){
            novaView.repeteItem.setText("Sim");
        } else {
            novaView.repeteItem.setText("Não");
        }

        return resultado;
    }
}
