package com.ocp.carteiraapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

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

    private Spinner spinnerCEEvento;

    //Operacao 0 == cadastro de entrada; 1 == cadastro de saida; 2 == edicao de entrada; 3 == edicao de saida
    private int op = -1;
    private String nomeFoto;

    private Evento eventoSelecionado;

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

        spinnerCEEvento = (Spinner) findViewById(R.id.spinnerCEEvento);

        Intent intencao = getIntent();
        op = intencao.getIntExtra("acao", -1);

        cadastrarEventos();
        ajustaPorOperacao();
    }

    private void confSpinner() {
        List<String> meses = new ArrayList<>();

        for(int c = 1; c <= 24; c++){
            meses.add(c + "");
        }

        ArrayAdapter<String> listaAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, meses);

        this.spinnerCEEvento.setAdapter(listaAdapter);
        this.spinnerCEEvento.setEnabled(false);
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
                if(op < 2) {
                    cadastrarNovoEvento();
                } else {
                    updateEvento();
                }
                finish();
            }
        });

        //Tratando a repeticao do evento
        this.checkCEEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkCEEvento.isChecked()) {
                    spinnerCEEvento.setEnabled(true);
                } else {
                    spinnerCEEvento.setEnabled(false);
                }
            }
        });

        this.btnCancelarCEEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(op >= 2) {
                    excluirEvento();
                }
                finish();
            }
        });

        this.btnFotoCEEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraActivity = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(cameraActivity, 10);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            Bitmap imagemUser = (Bitmap) data.getExtras().get("data");
            this.imgCEEvento.setImageBitmap(imagemUser);
            this.imgCEEvento.setBackground(null);

            salvarImg(imagemUser);
        }
    }

    private void salvarImg(Bitmap img) {
        Random gerador = new Random();
        Date instante = new Date();

        String nome = gerador.nextInt() + "" + instante.getTime() + ".png";

        this.nomeFoto = nome;

        File sd = Environment.getExternalStorageDirectory();
        File fotoArquivo = new File(sd, nome);

        try {
            FileOutputStream gravador = new FileOutputStream(fotoArquivo);
            img.compress(Bitmap.CompressFormat.PNG, 100, gravador);
            gravador.flush();
            gravador.close();

        } catch (Exception ex) {
            System.err.println("Erro ao armazenar a foto");
            ex.printStackTrace();
        }
    }

    private void carregarImg() {
        if(this.nomeFoto != null) {
            File sd = Environment.getExternalStorageDirectory();
            File arquivoLeitura = new File(sd, this.nomeFoto);

            try{
                FileInputStream leitor = new FileInputStream(arquivoLeitura);
                Bitmap img = BitmapFactory.decodeStream(leitor);

                this.imgCEEvento.setImageBitmap(img);
                this.imgCEEvento.setBackground(null);

            } catch (Exception ex) {
                System.err.println("Erro na leitura da foto");
                ex.printStackTrace();
            }
        }
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
                ajusteEdicao();
                break;
            case 3:
                this.tituloCEEvento.setText("Edição de Saída");
                ajusteEdicao();
                break;
            default:
                this.tituloCEEvento.setText("[ERRO]");
        }
    }

    private void ajusteEdicao() {
        this.btnCancelarCEEvento.setText("excluir");
        this.btnSalvarCEEvento.setText("atualizar");

        //Carregando a info do BD
        int id = Integer.parseInt(getIntent().getStringExtra("id"));

        if(id != 0) {
            EventosDB db = new EventosDB(CadastroEdicaoEventos.this);
            eventoSelecionado = db.buscaEventoId(id);

            SimpleDateFormat formatar = new SimpleDateFormat("dd/MM/yyyy");

            this.nomeCEEvento.setText(eventoSelecionado.getNome());
            this.valorCEEvento.setText(eventoSelecionado.getValor() + "");
            this.dataCEEvento.setText(formatar.format(eventoSelecionado.getOcorreu()));

            this.nomeFoto = eventoSelecionado.getCaminhoFt();
            carregarImg();

            Calendar d1 = Calendar.getInstance();
            d1.setTime(eventoSelecionado.getOcorreu());

            Calendar d2 = Calendar.getInstance();
            d2.setTime(eventoSelecionado.getValida());

            this.checkCEEvento.setChecked((d1.get(Calendar.MONTH) != d2.get(Calendar.MONTH)) ? true : false);

            if (this.checkCEEvento.isChecked()) {
                this.spinnerCEEvento.setEnabled(true);

                this.spinnerCEEvento.setSelection(d2.get(Calendar.MONTH) - d1.get(Calendar.MONTH) - 1);
            }
        }
    }

    /*Partindo da suposicao de que todos os dados possuem um valor associado, nao ocorrera uma
        exclusao real. O ID, portanto, tera o seu valor original em modulo, porem o valor no BD
        estara registrado em negativo.
     */
    private void excluirEvento() {
        this.eventoSelecionado.setNome(this.nomeCEEvento.getText().toString());
        this.eventoSelecionado.setValor(Double.parseDouble(this.valorCEEvento.getText().toString()));

        if(this.op == 3){
            this.eventoSelecionado.setValor(this.eventoSelecionado.getValor() * -1);
        }

        this.eventoSelecionado.setOcorreu(calendarioTemp.getTime());

        //Um novo calendario para calcular a data limite
        Calendar dataLimite = Calendar.getInstance();
        dataLimite.setTime(calendarioTemp.getTime());

        //Verificando se o evento ira se repetir
        if (this.checkCEEvento.isChecked()){
            String mesStr = (String) spinnerCEEvento.getSelectedItem();

            dataLimite.add(Calendar.MONTH, Integer.parseInt(mesStr));
        }

        //Setando para o ultimo dia do mes
        dataLimite.set(Calendar.DAY_OF_MONTH, dataLimite.getActualMaximum(Calendar.DAY_OF_MONTH));

        this.eventoSelecionado.setValida(dataLimite.getTime());

        this.eventoSelecionado.setCaminhoFt(this.nomeFoto);

        EventosDB db = new EventosDB(CadastroEdicaoEventos.this);
        db.excluirEvento(this.eventoSelecionado);
    }

    private void updateEvento() {
        this.eventoSelecionado.setNome(this.nomeCEEvento.getText().toString());
        this.eventoSelecionado.setValor(Double.parseDouble(this.valorCEEvento.getText().toString()));

        if(this.op == 3){
            this.eventoSelecionado.setValor(this.eventoSelecionado.getValor() * -1);
        }

        this.eventoSelecionado.setOcorreu(calendarioTemp.getTime());

        //Um novo calendario para calcular a data limite
        Calendar dataLimite = Calendar.getInstance();
        dataLimite.setTime(calendarioTemp.getTime());

        //Verificando se o evento ira se repetir
        if (this.checkCEEvento.isChecked()){
            String mesStr = (String) spinnerCEEvento.getSelectedItem();

            dataLimite.add(Calendar.MONTH, Integer.parseInt(mesStr));
        }

        //Setando para o ultimo dia do mes
        dataLimite.set(Calendar.DAY_OF_MONTH, dataLimite.getActualMaximum(Calendar.DAY_OF_MONTH));

        this.eventoSelecionado.setValida(dataLimite.getTime());

        this.eventoSelecionado.setCaminhoFt(this.nomeFoto);

        EventosDB db = new EventosDB(CadastroEdicaoEventos.this);
        db.updateEvento(this.eventoSelecionado);
    }

    private void cadastrarNovoEvento() {
        String nome = this.nomeCEEvento.getText().toString();
        Double valor = Double.parseDouble(this.valorCEEvento.getText().toString());

        if(op == 1 || op == 3) {
            valor *= -1;
        }

        //SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
        //String dataStr = this.dataCEEvento.getText().toString();

        //try {
            Date diaEvento = calendarioTemp.getTime();

            //Um novo calendario para calcular a data limite
            Calendar dataLimite = Calendar.getInstance();
            dataLimite.setTime(calendarioTemp.getTime());

            //Verificando se o evento ira se repetir
            if (this.checkCEEvento.isChecked()){
                String mesStr = (String) spinnerCEEvento.getSelectedItem();

                dataLimite.add(Calendar.MONTH, Integer.parseInt(mesStr));
            }

            //Setando para o ultimo dia do mes
            dataLimite.set(Calendar.DAY_OF_MONTH, dataLimite.getActualMaximum(Calendar.DAY_OF_MONTH));

            Evento novoEvento = new Evento(nome, this.nomeFoto, valor, new Date(), dataLimite.getTime(), diaEvento);

            //Inserir esse evento no BD
            EventosDB bd = new EventosDB(CadastroEdicaoEventos.this);
            bd.insereEvento(novoEvento);
        /*} catch (ParseException ex){
            System.err.println("erro no formato da data...");
        }*/
    }
}