package com.ocp.carteiraapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ferramentas.EventosDB;
import modelo.Evento;

public class CadastroEdicaoEventos extends AppCompatActivity {
    private DatePickerDialog calendarioUsuario;
    private Calendar calendarioTemp;

    private TextView tituloCEEvento;
    private TextView dataCEEvento;

    private EditText nomeCEEvento;
    private EditText valorCEEvento;

    private CheckBox checkCEEvento;

    private ImageView imgCEEvento;

    private Button btnFotoCEEvento;
    private Button btnSalvarCEEvento;
    private Button btnCancelarCEEvento;

    //Operacao 0 == cadastro de entrada; 1 == cadastro de saida; 2 == edicao de entrada; 3 == edicao de saida
    private int op = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_edicao_eventos);

        //Instanciamento de todos os atributos da classe
        tituloCEEvento = (TextView) findViewById(R.id.tituloCEEvento);
        dataCEEvento = (TextView) findViewById(R.id.dataCEEvento);

        nomeCEEvento = (EditText) findViewById(R.id.nomeCEEvento);
        valorCEEvento = (EditText) findViewById(R.id.valorCEEvento);

        checkCEEvento = (CheckBox) findViewById(R.id.checkCEEvento);

        imgCEEvento = (ImageView) findViewById(R.id.imgCEEvento);

        btnFotoCEEvento = (Button) findViewById(R.id.btnFotoCEEvento);
        btnSalvarCEEvento = (Button) findViewById(R.id.btnSalvarCEEvento);
        btnCancelarCEEvento = (Button) findViewById(R.id.btnCancelarCEEvento);

        Intent intencao = getIntent();
        op = intencao.getIntExtra("acao", -1);

        cadastrarEventos();
        ajustaPorOperacao();
    }

    private void cadastrarEventos() {
        //Instanciando o DatePicker
        calendarioTemp = Calendar.getInstance();

        calendarioUsuario = new DatePickerDialog(CadastroEdicaoEventos.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int ano, int mes, int dia) {
                calendarioTemp.set(ano, mes, dia);
                dataCEEvento.setText(dia + "/" + (mes + 1) + "/" + ano);
            }
        }, calendarioTemp.get(Calendar.YEAR), calendarioTemp.get(Calendar.MONTH), calendarioTemp.get(Calendar.DAY_OF_MONTH));

        this.dataCEEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarioUsuario.show();
            }
        });

        this.btnSalvarCEEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastrarNovoEvento();
                finish();
            }
        });
    }

    private void ajustaPorOperacao(){
        Calendar hoje = Calendar.getInstance();

        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
        this.dataCEEvento.setText(formatador.format(hoje.getTime()));

        switch (op){
            case 0:
                this.tituloCEEvento.setText("Cadastro de Entrada");
                break;
            case 1:
                this.tituloCEEvento.setText("Cadastro de Saída");
                break;
            case 2:
                this.tituloCEEvento.setText("Edição de Entrada");
                break;
            case 3:
                this.tituloCEEvento.setText("Edição de Saída");
                break;
            default:
                this.tituloCEEvento.setText("[ERRO]");
        }
        if(op == 0) {
            this.tituloCEEvento.setText("Cadastro de Entrada");
        } else if(op == 1) {
            this.tituloCEEvento.setText("Cadastro de Saída");
        }
    }

    private void cadastrarNovoEvento() {
        String nome = this.nomeCEEvento.getText().toString();
        Double valor = Double.parseDouble(this.valorCEEvento.getText().toString());

        if(op == 1 || op == 3) {
            valor *= -1;
        }

        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
        String dataStr = this.dataCEEvento.getText().toString();

        try {
            Date diaEvento = formatador.parse(dataStr);

            //Um novo calendario para calcular a data limite
            Calendar dataLimite = Calendar.getInstance();
            dataLimite.setTime(calendarioTemp.getTime());

            //Verificando se o evento ira se repetir
            if (this.checkCEEvento.isChecked()){
                //Por enquanto so com um mes
            }

            //Setando para o ultimo dia do mes
            dataLimite.set(Calendar.DAY_OF_MONTH, dataLimite.getActualMaximum(Calendar.DAY_OF_MONTH));

            Evento novoEvento = new Evento(nome, null, valor, new Date(), dataLimite.getTime(), diaEvento);

            //Inserir esse evento no BD
            EventosDB bd = new EventosDB(CadastroEdicaoEventos.this);
            bd.insereEvento(novoEvento);
        } catch (ParseException ex){
            System.err.println("erro no formato da data...");
        }
    }
}