/**
 * clé d'utilisation de l'api: 873b95f714289a7d1f22322e886b72f0
 * */


package com.example.themoviedbproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    Button bouttonLancement;
    EditText date,recherche;
    TextView nbFilmsTxt;
    Spinner spinner, spinnerSort;
    SeekBar nbFilms;
    Switch switchRecherche;
    private ArrayList<String> data = new ArrayList<>();
    private ArrayList<String> dataId = new ArrayList<>();
    private ArrayList<String> sortList = new ArrayList<>();
    private String SortId, genreId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recherche = findViewById(R.id.recherche);
        nbFilms = findViewById(R.id.nbFilms);
        date = findViewById(R.id.date);
        bouttonLancement = findViewById(R.id.btRecherche);
        nbFilmsTxt = findViewById(R.id.nbFilmsTxt);
        spinner = findViewById(R.id.spinner);
        spinnerSort = findViewById(R.id.spinnerSort);
        switchRecherche = findViewById(R.id.switchRecherche);
        date.setEnabled(false);
        spinner.setEnabled(false);
        spinnerSort.setEnabled(false);
        sortList.add("Popularité");
        sortList.add("Note moyenne");
        sortList.add("Date de parution");

        nbFilms.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {  //affichage nombre de films
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                nbFilmsTxt.setText(String.valueOf(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //récupération genres disponibles
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://api.themoviedb.org/3/genre/movie/list?api_key=873b95f714289a7d1f22322e886b72f0&language=fr-FR",       //on établie la requete JSON
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            JSONObject repObj = (JSONObject) new JSONTokener(responseString).nextValue();   //créé un objet json
                            JSONArray ar = repObj.getJSONArray("genres");  //conversion du JSON results en Array
                            data.add(0,"Tous");
                            dataId.add(0,"");
                            for (int i = 1; i <= ar.length()+1; i++) {    //parcours de l'array du jsonArray

                                JSONObject val = ar.getJSONObject(i);
                                data.add(i, val.getString("name"));
                                dataId.add(i, String.valueOf(val.getInt("id")));
                            }
                        } catch (JSONException je) {
                            Log.e("error", je.getMessage());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, data);
                        spinner.setAdapter(adapter);

                        ArrayAdapter<String> adapterSort = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, sortList);
                        spinnerSort.setAdapter(adapterSort);    //ajout adapteur de tri de la liste
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e("failure", throwable.getMessage());
                    }
                });




        //à chaque changement de mode de recherche
        switchRecherche.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked){
                    recherche.setEnabled(true);
                    date.setEnabled(false);
                    spinner.setEnabled(false);
                    spinnerSort.setEnabled(false);
                    switchRecherche.setText("Recherche par mots clés");
                }else{
                    recherche.setEnabled(false);
                    date.setEnabled(true);
                    spinner.setEnabled(true);
                    spinnerSort.setEnabled(true);
                    switchRecherche.setText("Recherche autre");
                }
            }
        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {        //récupération genre séléctionné
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                genreId = dataId.get(spinner.getSelectedItemPosition());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {     //récupération tri séléctionné
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                SortId = sortList.get(spinnerSort.getSelectedItemPosition());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });


        bouttonLancement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {           //chargement seconde page
                Intent intent = new Intent(MainActivity.this, controllerMovieList.class);

                if (switchRecherche.isChecked()==true || recherche.getText().toString().equals("")){
                    intent.putExtra("date", date.getText().toString());
                    intent.putExtra("genreId",genreId);
                    intent.putExtra("SortId",SortId);
                }else{
                    intent.putExtra("recherche",recherche.getText().toString());
                }
                intent.putExtra("nbfilms",nbFilms.getProgress());
                startActivity(intent);
            }
        });
    }
}
