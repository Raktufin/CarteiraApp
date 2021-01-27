package com.ocp.carteiraapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import ferramentas.EventosDB;
import modelo.Evento;

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
    static Calendar dataApp;

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

        attValores();
        configurarPermissoes();
    }

    private void configurarPermissoes() {
        if(ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }

    private void cadastroEventos() {
        //Botao de voltar um mes
        btMesAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mudaMes(-1);
            }
        });

        //Botao de avançar um mes
        btMesProximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mudaMes(+1);
            }
        });

        //Botao de nova saida
        btSaida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent trocaAct = new Intent(MainActivity.this, VisualizarEventos.class);

                trocaAct.putExtra("acao", 1);

                startActivityForResult(trocaAct, 1);
            }
        });

        //Botao de nova entrada
        btEntrada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent trocaAct = new Intent(MainActivity.this, VisualizarEventos.class);

                trocaAct.putExtra("acao", 0);

                startActivityForResult(trocaAct, 0);
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

            if(dataApp.get(Calendar.YEAR) == hoje.get(Calendar.YEAR)) {
                if (dataApp.get(Calendar.MONTH) > hoje.get(Calendar.MONTH)) {
                    dataApp.add(Calendar.MONTH, -1);
                }
            } else if(dataApp.get(Calendar.YEAR) > hoje.get(Calendar.YEAR)){
                dataApp.add(Calendar.MONTH, -1);
            }
        } else {
            //Aqui vai uma verificacao do BD se existe registros de meses anteriores
        }

        mostraDataApp();
        attValores();
    }

    private void attValores() {
        //Buscando entradas e saidas cadastradas para este mes no BD
        EventosDB db = new EventosDB(MainActivity.this);

        ArrayList<Evento> saidas = db.buscarEventos(1, dataApp);
        ArrayList<Evento> entradas = db.buscarEventos(0, dataApp);

        //Somando todos os valores dos eventos recuperados
        double saidaTotal = 0.0;
        double entradaTotal = 0.0;

        for (int c = 0; c < saidas.size(); c++){
            saidaTotal += saidas.get(c).getValor();
        }

        for (int c = 0; c < entradas.size(); c++){
            entradaTotal += entradas.get(c).getValor();
        }

        double saldo = entradaTotal - saidaTotal;

        //Mostrando os valores
        valorEntrada.setText(String.format("%.2f", entradaTotal));
        valorSaida.setText(String.format("%.2f", saidaTotal));
        valorSaldo.setText(String.format("%.2f", saldo));
    }

    protected void onActivityResult(int codigoRequest, int codigoResultado, Intent data) {
        super.onActivityResult(codigoRequest, codigoResultado, data);

        attValores();
    }
}