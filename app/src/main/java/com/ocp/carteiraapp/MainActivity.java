package com.ocp.carteiraapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private TextView titulo;
    private TextView valorEntrada;
    private TextView valorSaida;
    private TextView valorSaldo;

    private ImageButton btSaida;
    private ImageButton btEntrada;

    private Button btMesAnterior;
    private Button btMesProximo;

    private Calendar hoje;
    private Calendar dataApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Link com componentes do layout
        titulo = (TextView) findViewById(R.id.tituloMain);
        valorEntrada = (TextView) findViewById(R.id.valorEntrada);
        valorSaida = (TextView) findViewById(R.id.valorSaida);
        valorSaldo = (TextView) findViewById(R.id.valorSaldo);

        btSaida = (ImageButton) findViewById(R.id.btnNovaSaida);
        btEntrada = (ImageButton) findViewById(R.id.btnNovaEntrada);

        btMesAnterior = (Button) findViewById(R.id.btnMesAnterior);
        btMesProximo = (Button) findViewById(R.id.btnMesProximo);

        //Recupera data atual
        hoje = Calendar.getInstance();
        dataApp = Calendar.getInstance();

        //Mostra a data no app
        mostraDataApp();

        //Configuracao de eventos
        cadastroEventos();
    }

    private void cadastroEventos() {
        //Botao de voltar um mes
        btMesAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mudaMes(-1);
                mostraDataApp();
            }
        });

        //Botao de avançar um mes
        btMesProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mudaMes(+1);
                mostraDataApp();
            }
        });

        //Botao de nova saida
        btSaida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent trocaAct = new Intent(MainActivity.this, VisualizarEventos.class);

                trocaAct.putExtra("acao", 1);

                startActivity(trocaAct);
            }
        });

        //Botao de nova entrada
        btEntrada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent trocaAct = new Intent(MainActivity.this, VisualizarEventos.class);

                trocaAct.putExtra("acao", 0);

                startActivity(trocaAct);
            }
        });
    }

    private void mostraDataApp() {
        String nomeMes[] = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho",
                "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};

        int mes = dataApp.get(Calendar.MONTH);
        int ano = dataApp.get(Calendar.YEAR);

        titulo.setText(nomeMes[mes] + "/" + ano);
    }

    private void mudaMes(int ajuste) {
        dataApp.add(Calendar.MONTH, ajuste);

        if(ajuste > 0) {
            //Volta ao ultimo mes caso a data avance o mes atual
            if (dataApp.after(hoje)) {
                dataApp.add(Calendar.MONTH, -1);
            }
        } else {
            //Aqui vai uma verificacao do BD se existe registros de meses anteriores
        }
    }
}