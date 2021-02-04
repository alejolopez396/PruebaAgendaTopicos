package com.example.agenda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.agenda.Adaptadores.ListViewPersonasAdapter;
import com.example.agenda.Models.Persona;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Persona> listPersonas = new ArrayList<Persona>();
    ArrayAdapter<Persona>arrayAdapterPersona;
    ListViewPersonasAdapter listViewPersonasAdapter;
    LinearLayout linearLayoutEditar;
    ListView listViewPersonas;

    EditText inputNombre, inputTelefono;
    Button btnCancelar;

    Persona personaSeleccionada;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputNombre = findViewById(R.id.inputNombre);
        inputTelefono = findViewById(R.id.inputTelefono);
        btnCancelar = findViewById(R.id.btnCancelar);

        listViewPersonas = findViewById(R.id.listViewPersonas);
        linearLayoutEditar = findViewById(R.id.linearLayoutEditar);

        listViewPersonas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                personaSeleccionada = (Persona) parent.getItemAtPosition(position);
                inputNombre.setText(personaSeleccionada.getNombres());
                inputTelefono.setText(personaSeleccionada.getTelefono());
                // visible el linearLayout
                linearLayoutEditar.setVisibility(View.VISIBLE);
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutEditar.setVisibility(View.GONE);
                personaSeleccionada=null;
            }
        });

        inicializarFirebase();
        listarPersonas();
    }
    private void inicializarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void listarPersonas(){
        databaseReference.child("Personas").orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listPersonas.clear();
                for(DataSnapshot objSnaptshot : snapshot.getChildren()){
                    Persona p = objSnaptshot.getValue(Persona.class);
                    listPersonas.add(p);
                }
                //inicar nuestro propio adaptador
                listViewPersonasAdapter= new ListViewPersonasAdapter(MainActivity.this,listPersonas);
                //arrayAdapterPersona = new ArrayAdapter<Persona>(MainActivity.this,android.R.layout.simple_list_item_1,listPersonas);
                listViewPersonas.setAdapter(listViewPersonasAdapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String nombres = inputNombre.getText().toString();
        String telefono = inputTelefono.getText().toString();
        switch (item.getItemId()){
            case R.id.menu_agregar:
                insertar();
                break;
            case R.id.menu_guardar:
                if(personaSeleccionada != null){
                    if(validarInputs()== false){
                        Persona p = new Persona();
                        p.setIdpersona(personaSeleccionada.getIdpersona());
                        p.setNombres(nombres);
                        p.setTelefono(telefono);
                        p.setFecharegistro(personaSeleccionada.getFecharegistro());
                        p.setTimestamp(personaSeleccionada.getTimestamp());
                        databaseReference.child("Personas").child(p.getIdpersona()).setValue(p);
                        Toast.makeText(this,
                                "El contacto se actualizo correctamente",
                                Toast.LENGTH_LONG).show();
                        linearLayoutEditar.setVisibility(View.GONE);
                        personaSeleccionada = null;


                    }
                }else{
                    Toast.makeText(this,
                            "Por favor seleccione un contacto para editarlo",
                            Toast.LENGTH_LONG).show();

                }
                break;
            case R.id.menu_eliminar:
                if(personaSeleccionada != null){
                    Persona p2 = new Persona();
                    p2.setIdpersona(personaSeleccionada.getIdpersona());
                    databaseReference.child("Personas").child(p2.getIdpersona()).removeValue();
                    linearLayoutEditar.setVisibility(View.GONE);
                    personaSeleccionada = null;
                    Toast.makeText(this,
                            "El contacto "+nombres+" ha sido eliminado",
                            Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(this,
                            "Por favor seleccione un contacto para poder eliminarlo",
                            Toast.LENGTH_LONG).show();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    public void insertar(){
        AlertDialog.Builder mBuilder= new AlertDialog.Builder(
                MainActivity.this
        );
        View mView = getLayoutInflater().inflate(R.layout.insertar,null);
        Button btnInsertar = (Button) mView.findViewById(R.id.btnInsertar);
        final EditText mInputNombres = (EditText) mView.findViewById(R.id.inputNombre);
        final EditText mInputTelefono = (EditText) mView.findViewById(R.id.inputTelefono);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        btnInsertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String nombres = mInputNombres.getText().toString();
               String telefono = mInputTelefono.getText().toString();
               if(nombres.isEmpty()||nombres.length()<3){
                   showError(mInputNombres, "Nombre invalido (Tiene que tener min. 3 letras)");
               }else if(telefono.isEmpty() || telefono.length()<10 ){
                   showError(mInputTelefono, "Telefono invalido (Tiene que tener min. 10 números)");

               }else {
                   Persona p = new Persona();
                   p.setIdpersona(UUID.randomUUID().toString());
                   p.setNombres(nombres);
                   p.setTelefono(telefono);
                   p.setFecharegistro(getFechaNormal(getFechaMiliSegundos()));
                   p.setTimestamp(getFechaMiliSegundos() * -1);
                   databaseReference.child("Personas").child(p.getIdpersona()).setValue(p);
                   Toast.makeText(
                           MainActivity.this,
                           "Se ha registrado el contacto " + nombres,
                           Toast.LENGTH_LONG).show();
                   dialog.dismiss();

               }

            }
        });

    }

    public void showError(EditText input, String s){
        input.requestFocus();
        input.setError(s);

    }


    public long getFechaMiliSegundos(){
        Calendar calendar = Calendar.getInstance();
        long tiempounix = calendar.getTimeInMillis();

        return tiempounix;

    }

    public String getFechaNormal(long fechamilisegundos){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-5"));
        String fecha = sdf.format(fechamilisegundos);
        return fecha;
    }

    public boolean validarInputs(){
        String nombre = inputNombre.getText().toString();
        String telefono = inputTelefono.getText().toString();
        if(nombre.isEmpty() || nombre.length()<3){
            showError(inputNombre, "Nombre invalido (Tiene que tener min. 3 letras)");
            return true;
        }else if(telefono.isEmpty() || telefono.length()<10 ){
            showError(inputTelefono, "Telefono invalido (Tiene que tener min. 10 números)");
            return true;
        }else {
            return false;
        }


    }
}