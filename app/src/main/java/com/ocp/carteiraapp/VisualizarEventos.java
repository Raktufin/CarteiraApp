package com.ocp.carteiraapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import ferramentas.EventosDB;
import modelo.Evento;

public class VisualizarEventos extends AppCompatActivity {
    private TextView tituloEvento;
    private TextView totalEvento;

    private ListView listaEvento;

    private Button btNovoEvento;
    private Button btCancelarEvento;

    private ArrayList<Evento> eventos;

    private ItemListaEvento adapter;

    //Operacao == 0 indica entrada, == 1 indica saida
    private int op = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_eventos);

        this.tituloEvento = (TextView) findViewById(R.id.tituloEvento);
        this.totalEvento = (TextView) findViewById(R.id.totalEvento);

        this.listaEvento = (ListView) findViewById(R.id.listaEvento);

        this.btNovoEvento = (Button) findViewById(R.id.btnNovoEvento);
        this.btCancelarEvento = (Button) findViewById(R.id.btnCancelarEvento);

        cadastrarEventos();

        Intent intencao = getIntent();
        this.op = intencao.getIntExtra("acao", -1);
        //0 == entrada e 1 == saida

        ajustaOperacao();

        carregaEventosLista();
    }

    private void cadastrarEventos() {
        this.btNovoEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (op != -1) {
                    Intent trocaAct = new Intent(VisualizarEventos.this, CadastroEdicaoEventos.class);

                    if (op == 0) {
                        trocaAct.putExtra("acao", 0);
                        startActivityForResult(trocaAct, 0);
                    } else {
                        trocaAct.putExtra("acao", 1);
                        startActivityForResult(trocaAct, 1);
                    }
                }
            }
        });

        this.btCancelarEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void ajustaOperacao() {
        if(op == 0) {
            tituloEvento.setText("Entradas");
        } else if(op == 1) {
            tituloEvento.setText("Saídas");
        } else {
            //Erro na configuracao da intent
            Toast.makeText(VisualizarEventos.this, "[ERRO] Erro no parametro acao", Toast.LENGTH_LONG).show();
        }
    }

    //Vamos precisar realizar uma busca no BD sobre os eventos referentes a operacao e apresenta-los na lista
    private void carregaEventosLista() {
        //Aqui ocorre a busca dos eventos no BD
        /*eventos = new ArrayList<>();
        eventos.add(new Evento("Padaria", null, 10, new Date(), new Date(), new Date()));
        eventos.add(new Evento("Supermercado", null, 30.5, new Date(), new Date(), new Date()));*/

        EventosDB db = new EventosDB(VisualizarEventos.this);
        eventos = db.buscarEventos(op, MainActivity.dataApp);

        adapter = new ItemListaEvento(getApplicationContext(), eventos);
        listaEvento.setAdapter(adapter);

        //Soma de todos os valores para o total
        double total = 0.0;

        for(int c = 0; c < eventos.size(); c++){
            total += eventos.get(c).getValor();
        }

        totalEvento.setText(String.format("%.2f", total));
    }

    protected void onActivityResult(int codigoRequest, int codigoResultado, Intent data) {
        super.onActivityResult(codigoRequest, codigoResultado, data);

        carregaEventosLista();
    }
}
