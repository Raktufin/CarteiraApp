package com.ocp.carteiraapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class VisualizarEventos extends AppCompatActivity {
    private TextView tituloEvento;
    private TextView totalEvento;

    private ListView listaEvento;

    private Button btNovoEvento;
    private Button btCancelarEvento;

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
    }

    private void cadastrarEventos() {
        this.btNovoEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (op != -1) {
                    Intent trocaAct = new Intent(VisualizarEventos.this, CadastroEdicaoEventos.class);

                    if (op == 0) {
                        trocaAct.putExtra("acao", 0);
                    } else {
                        trocaAct.putExtra("acao", 1);
                    }

                    startActivity(trocaAct);
                }
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
}