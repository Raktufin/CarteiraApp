package ferramentas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import modelo.Evento;

public class EventosDB extends SQLiteOpenHelper {

    private Context contexto;

    public EventosDB(Context cont) {
        super(cont, "carteiraapp", null, 1);
        contexto = cont;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String criaTabela = "CREATE TABLE IF NOT EXISTS evento(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                    "nome TEXT, valor REAL, imagem TEXT, dataocorreu DATE, datacadastro DATE," +
                                    "datavalida DATE)";

        db.execSQL(criaTabela);
    }

    public void insereEvento(Evento novoEvento){
        try(SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues valores = new ContentValues();

            valores.put("nome", novoEvento.getNome());
            valores.put("valor", novoEvento.getValor());
            valores.put("imagem", novoEvento.getCaminhoFt());
            valores.put("dataocorreu", novoEvento.getOcorreu().getTime());
            valores.put("datacadastro", new Date().getTime());
            valores.put("datavalida", novoEvento.getValida().getTime());

            db.insert("evento", null, valores);

        } catch (SQLiteException ex) {
            ex.printStackTrace();
        }
    }

    public void excluirEvento(Evento eventoAlvo) {
        try(SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues valores = new ContentValues();
            valores.put("id", eventoAlvo.getId() * -1);
            valores.put("nome", eventoAlvo.getNome());
            valores.put("valor", eventoAlvo.getValor());
            valores.put("imagem", eventoAlvo.getCaminhoFt());
            valores.put("dataocorreu", eventoAlvo.getOcorreu().getTime());
            valores.put("datavalida", eventoAlvo.getValida().getTime());

            db.update("evento", valores, "id = ?", new String[]{eventoAlvo.getId() + ""});
        } catch (SQLiteException ex) {
            System.err.println("Erro na atualizacao do evento");
            ex.printStackTrace();
        }
    }

    public void updateEvento(Evento eventoAtt) {
        try(SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues valores = new ContentValues();
            valores.put("nome", eventoAtt.getNome());
            valores.put("valor", eventoAtt.getValor());
            valores.put("imagem", eventoAtt.getCaminhoFt());
            valores.put("dataocorreu", eventoAtt.getOcorreu().getTime());
            valores.put("datavalida", eventoAtt.getValida().getTime());

            db.update("evento", valores, "id = ?", new String[]{eventoAtt.getId() + ""});
        } catch (SQLiteException ex) {
            System.err.println("Erro na atualizacao do evento");
            ex.printStackTrace();
        }
    }

    public Evento buscaEventoId(int idEvento) {
        String sql = "SELECT * FROM evento WHERE id = " + idEvento;

        Evento resultado = null;

        try(SQLiteDatabase db = this.getWritableDatabase()) {
            Cursor tupla = db.rawQuery(sql, null);

            if(tupla.moveToFirst()) {
                String nome = tupla.getString(1);
                double valor = tupla.getDouble(2);
                if(valor < 0) {
                    valor *= -1;
                }
                String urlFoto = tupla.getString(3);
                Date dataOcorreu = new Date(tupla.getLong(4));
                Date dataCadastro = new Date(tupla.getLong(5));
                Date dataValida = new Date(tupla.getLong(6));

                resultado = new Evento(idEvento, nome, urlFoto, valor, dataCadastro, dataValida, dataOcorreu);

            }

        } catch (SQLiteException ex) {
            System.err.println("Erro na consulta SQL da busca de eventos por id");
            ex.printStackTrace();
        }

        return resultado;
    }

    public ArrayList<Evento> buscarEventos(int op, Calendar data) {
        ArrayList<Evento> resultado = new ArrayList<>();

        Calendar dia1 = Calendar.getInstance();
        dia1.setTime(data.getTime());
        dia1.set(Calendar.DAY_OF_MONTH, 1);
        dia1.set(Calendar.HOUR, -12);
        dia1.set(Calendar.MINUTE, 0);
        dia1.set(Calendar.SECOND, 0);

        Calendar dia2 = Calendar.getInstance();
        dia2.setTime(data.getTime());
        dia2.set(Calendar.DAY_OF_MONTH, dia2.getActualMaximum(Calendar.DAY_OF_MONTH));
        dia2.set(Calendar.HOUR, 11);
        dia2.set(Calendar.MINUTE, 59);
        dia2.set(Calendar.SECOND, 59);

        String sql = "SELECT * FROM evento WHERE id >= 0 AND ((datavalida <= " + dia2.getTime().getTime() +
                " AND datavalida >= " + dia1.getTime().getTime() + ") OR (dataocorreu <= " + dia2.getTime().getTime() +
                " AND datavalida >= " + dia1.getTime().getTime() + "))";
        sql += " AND valor ";

        if(op == 0){
            //Entradas
            sql += ">= 0";
        } else {
            //Saidas
            sql += "< 0";
        }

        try(SQLiteDatabase db = this.getWritableDatabase()) {
            Cursor tuplas = db.rawQuery(sql, null);

            //Efetuar a leitura das tuplas
            if(tuplas.moveToFirst()) {
                do {
                    int id = tuplas.getInt(0);
                    String nome = tuplas.getString(1);
                    double valor = tuplas.getDouble(2);
                    if(valor < 0) {
                        valor *= -1;
                    }
                    String urlFoto = tuplas.getString(3);
                    Date dataOcorreu = new Date(tuplas.getLong(4));
                    Date dataCadastro = new Date(tuplas.getLong(5));
                    Date dataValida = new Date(tuplas.getLong(6));

                    Evento temporario = new Evento(id, nome, urlFoto, valor, dataCadastro, dataValida, dataOcorreu);

                    resultado.add(temporario);
                } while(tuplas.moveToNext());
            }

        } catch (SQLiteException ex){
            System.err.println("Ocorreu um bug na consulta do banco");
            ex.printStackTrace();
        }

        return resultado;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Ficara parado ate a att da activity de update (funcionalidade)
    }
}
