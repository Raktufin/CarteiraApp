package ferramentas;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

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

    public void atualizaEvento() {

    }

    public void buscarEvento() {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Ficara parado ate a att da activity de update (funcionalidade)
    }
}
