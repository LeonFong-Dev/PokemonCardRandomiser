package com.example.pokemonpacksimulator

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class MainActivity : AppCompatActivity() {

    data class PokemonData(
        val name: String,
        val abilities: List<String>,
        val imageUrl: String?
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Log.d("Debug Start", "Program Started")
        setupButton(findViewById<Button>(R.id.Button))
        getPokemon()

    }

    private fun setupButton(button: Button) {
        button.setOnClickListener{
            getPokemon()
        }
    }

    fun getPokemon(){
        val client = AsyncHttpClient()
        val params = RequestParams()
        params["limit"] = "1"
        params["page"] = "0"
        val randomId = (1..1025).random()
        client["https://pokeapi.co/api/v2/pokemon/$randomId", params, object:
            JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                // called when response HTTP status is "200 OK"
//                Log.d("DEBUG ARRAY", json.jsonArray.toString())
//                Log.d("DebugPokemon", "response successful")
                //Get the Pokemon name
                Log.d("DEBUG OBJECT", json.jsonObject.toString())
                val name = json.jsonObject.getString("name")
                //Get the Pokemon abilities
                Log.d("POKEMON_Debug", "Name:" + name)
                val abilities = json.jsonObject.getJSONArray("abilities")
                val abilityArray = Array(abilities.length()){""}
                for(i in 0 until abilities.length()){
                    Log.d("POKEMON_Debug",abilities.getJSONObject(i).toString())
                    abilityArray[i] = abilities.getJSONObject(i).getJSONObject("ability").getString("name")
                }
                Log.d("POKEMON_Debug", abilities.toString())
                //Get the Pokemon image
                val image = json.jsonObject.getJSONObject("sprites").getString("front_shiny")
                Log.d("POKEMON_Debug",  "Image URL: " + image)

                //returning the Pokemon data
                var curPokemon = PokemonData(name,abilityArray.toList(),image)
                var nameText = findViewById<TextView>(R.id.nameDescription)
                nameText.text = curPokemon.name
                var abilityText = findViewById<TextView>(R.id.AbilityDescription)
                abilityText.text = curPokemon.abilities.toString().substring(1,curPokemon.abilities.toString().length-1)
                var imageView = findViewById<ImageView>(R.id.imageView2)
                Glide.with(this@MainActivity)
                    .load(curPokemon.imageUrl)
                    .fitCenter()
                    .into(imageView)

            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                t: Throwable?
            ) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }
        }]
    }

}