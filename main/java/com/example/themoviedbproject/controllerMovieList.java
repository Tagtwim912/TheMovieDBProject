package com.example.themoviedbproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;


public class controllerMovieList extends AppCompatActivity {
        ListView film;
        private ArrayList<String> movieIds = new ArrayList<>();
        private ArrayList<String> data = new ArrayList<>();
        private HashMap<String, String> sortBy = new HashMap<String, String>();
        private int nbfilms;
        private String date,genreId,sort,keyWord;

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.layoutmovieslist);

            film = findViewById(R.id.film);
            sortBy.put("Popularité", "popularity.desc");
            sortBy.put("Note moyenne", "vote_average.desc");
            sortBy.put("Date de parution", "release_date.desc");

            ///////RECUPERATION DES CHOIX DE LUTILISATEUR////////
            Bundle extras = getIntent().getExtras();    //RECEPTION DE LA REQUETE
            if (extras == null) {
                return;
            }else{
                genreId = extras.getString("genreId");
                sort = sortBy.get(extras.getString("SortId"));
                date = extras.getString("date");
                keyWord = extras.getString("recherche");
                nbfilms = extras.getInt("nbfilms");
            }
            AsyncHttpClient client = new AsyncHttpClient();

            if(keyWord==null){  //SI RECHERCHE PAR DATE/GENRE/FILTRE
                client.get("https://api.themoviedb.org/3/discover/movie?api_key=873b95f714289a7d1f22322e886b72f0&with_genres="+genreId+"&primary_release_year="+date+"&sort_by="+sort,       //on établie la requete JSON
                        new TextHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                                try {
                                    JSONObject repObj = (JSONObject) new JSONTokener(responseString).nextValue();   //créé un objet json

                                    JSONArray ar = repObj.getJSONArray("results");  //conversion du JSON results en Array

                                    for (int i = 0; i < nbfilms; i++) {    //parcours de l'array du jsonArray
                                        JSONObject val = ar.getJSONObject(i);
                                        data.add(i, val.getString("original_title"));
                                        movieIds.add(i, val.getString("id"));
                                    }
                                } catch (JSONException je) {
                                    Log.e("error", je.getMessage());
                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(controllerMovieList.this, android.R.layout.simple_list_item_1, data);
                                //on créé un adapteur qui contient  les données, au format "simple
                                film.setAdapter(adapter);   //on donne l'adapter à la liste
                                if (data.size()==0){
                                    Toast.makeText(getApplicationContext(), "Aucun film trouvé selon vos critères", Toast.LENGTH_LONG).show();
                                }
                            }
                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                Log.e("failure", throwable.getMessage());
                            }
                        });
            }else { //SI RECHERCHE PAR MOTS CLES
                client.get("https://api.themoviedb.org/3/search/movie?api_key=873b95f714289a7d1f22322e886b72f0&query="+keyWord,//on établie la requete JSON
                new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            JSONObject repObj = (JSONObject) new JSONTokener(responseString).nextValue();   //créé un objet json
                            JSONArray ar = repObj.getJSONArray("results");  //conversion du JSON results en Array
                            for (int i = 0; i < nbfilms; i++) {    //parcours de l'array du jsonArray
                                JSONObject val = ar.getJSONObject(i);
                                data.add(i, val.getString("original_title"));
                                movieIds.add(i, val.getString("id"));   //ajout des id pour transmettre celui du film séléctionné au prochain controlleur
                            }
                        } catch (JSONException je) {
                            Log.e("error", je.getMessage());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(controllerMovieList.this, android.R.layout.simple_list_item_1, data);
                        //on créé un adapteur qui contient  les données, au format "simple
                        film.setAdapter(adapter);   //on donne l'adapter à la liste
                        if (data.size()==0){
                            Toast.makeText(getApplicationContext(), "Aucun résultat à votre recherche", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e("failure", throwable.getMessage());
                    }
                });
            }


        /////////AFFICHAGE DES FILMS DANS LA LISTVIEW////////
        film.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                String movieIdSelected = String.valueOf(Integer.valueOf(movieIds.get(Integer.parseInt(String.valueOf(position)))));    //récupération de la position du film séléectionné dans la liste
                Intent intent = new Intent(controllerMovieList.this, controllersMovieDetails.class);
                intent.putExtra("movieId",movieIdSelected);
                startActivity(intent);
            }
        });

     }


}
