package com.example.crud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.crud.model.Pessoa;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    EditText  editNome, editEmail;
    ListView listViewDados;

    // objetos de conexão com o bd
    FirebaseDatabase firebaseDatabase;
    DatabaseReference  databaseReference;

    private List<Pessoa> listPessoa = new ArrayList<Pessoa>();
    private ArrayAdapter<Pessoa> arrayAdapterPessoa;

    Pessoa pessoaSelecionada;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editNome = findViewById(R.id.editNome);
        editEmail = findViewById(R.id.editEmail);
        listViewDados = findViewById(R.id.listViewDados);

        inicializarFirebase();
        eventoDatabase();

        listViewDados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pessoaSelecionada = (Pessoa) parent.getItemAtPosition(position);
                editNome.setText(pessoaSelecionada.getNome());
                editEmail.setText(pessoaSelecionada.getEmail());
            }
        });

    }

    private void eventoDatabase() {
        databaseReference.child("Pessoa").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPessoa.clear();
                // tras cada objeto na ordem que estao no banco
                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    Pessoa p = objSnapshot.getValue(Pessoa.class);
                    listPessoa.add(p);
                }
                arrayAdapterPessoa = new ArrayAdapter<Pessoa>(MainActivity.this,
                        android.R.layout.simple_list_item_1,listPessoa);
                listViewDados.setAdapter(arrayAdapterPessoa);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(MainActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    // inserindo os dados no bd
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        // Cadastrar novo registro
        if (id == R.id.menuNovo){
            Pessoa p = new Pessoa();
            p.setId(UUID.randomUUID().toString());
            p.setNome(editNome.getText().toString());
            p.setEmail(editEmail.getText().toString());
            databaseReference.child("Pessoa").child(p.getId()).setValue(p);
            limparCampos();
        // Atualizar um registro
        }else if (id == R.id.menuUpdate){
            Pessoa p = new Pessoa();
            p.setId(pessoaSelecionada.getId());
            p.setNome(editNome.getText().toString().trim());
            p.setEmail(editEmail.getText().toString().trim());
            databaseReference.child("Pessoa").child(p.getId()).setValue(p);
            limparCampos();
        // Deletar um registro
        }else if (id == R.id.menuDelete){
            Pessoa p = new Pessoa();
            p.setId(pessoaSelecionada.getId());
            databaseReference.child("Pessoa").child(p.getId()).removeValue();
            limparCampos();
        }
        return true;
    }

    private void limparCampos() {
        editEmail.setText("");
        editNome.setText("");
    }
}
