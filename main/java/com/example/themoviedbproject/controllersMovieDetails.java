package com.example.themoviedbproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import cz.msebera.android.httpclient.Header;

public class controllersMovieDetails extends AppCompatActivity {
        ImageView Movieimg;
        TextView moviedescription, anneeParution, movieTitle,moviedScore;
        FloatingActionButton fab;
        private String movieImgLink;
        private String linkMovieWebsite;

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.specificationsfilm);

            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                return;
            }
            final String movieId = extras.getString("movieId");
            if (movieId != null) {
                moviedescription = findViewById(R.id.moviedescription);
                anneeParution = findViewById(R.id.anneeParution);
                movieTitle = findViewById(R.id.movieTitle);
                moviedScore = findViewById(R.id.moviedScore);
                Movieimg = findViewById(R.id.Movieimg);
                fab = findViewById(R.id.fab);
                fab.hide();

                AsyncHttpClient client = new AsyncHttpClient();
                client.get("https://api.themoviedb.org/3/movie/"+movieId+"?api_key=873b95f714289a7d1f22322e886b72f0&language=fr-FR",       //on établie la requete JSON
                        new TextHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                                try {
                                    JSONObject repObj = (JSONObject) new JSONTokener(responseString).nextValue();   //créé un objet json
                                    movieTitle.setText((CharSequence) repObj.get("original_title"));

                                    anneeParution.setText("Date de sortie : "+(CharSequence) repObj.get("release_date"));
                                    moviedescription.setText((CharSequence) repObj.get("overview"));
                                    try {
                                        movieImgLink = (String) repObj.get("backdrop_path");
                                        movieImgLink = "https://image.tmdb.org/t/p/w1280"+movieImgLink;
                                    }catch (Exception e){
                                    }
                                    try{        //gestion site
                                        linkMovieWebsite = (String) repObj.get("homepage");
                                        if(linkMovieWebsite.equals("")){
                                            fab.hide();
                                        }else{
                                            fab.show();
                                        }
                                    }catch (Exception e){
                                        fab.hide();
                                    }
                                    Picasso.get()
                                            .load(movieImgLink)
                                            .into(Movieimg);
                                    moviedScore.setText("Note: "+repObj.get("vote_average")+"/10 - "+(Integer) repObj.get("vote_count")+" vote(s)");
                                } catch (JSONException je) {
                                    Log.e("error", je.getMessage());
                                }
                            }
                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                Log.e("failure", throwable.getMessage());
                            }
                        });
            }


            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = linkMovieWebsite;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });
        }
    }
