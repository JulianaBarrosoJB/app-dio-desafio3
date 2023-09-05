package br.com.igorbag.githubsearch.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.domain.Repository
import br.com.igorbag.githubsearch.ui.adapter.RepositoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    lateinit var nomeUsuario: EditText
    lateinit var btnConfirmar: Button
    lateinit var listaRepositories: RecyclerView
    lateinit var githubApi: GitHubService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        showUserName()
        setupRetrofit()
        setupListeners()
        getAllReposByUserName()
    }

    // Metodo responsavel por realizar o setup da view e recuperar os Ids do layout
    fun setupView() {
        btnConfirmar = findViewById(R.id.btn_confirmar)
        nomeUsuario = findViewById(R.id.et_nome_usuario)
        listaRepositories = findViewById(R.id.rv_lista_repositories)
    }

    //metodo responsavel por configurar os listeners click da tela
    private fun setupListeners() {
        btnConfirmar.setOnClickListener {
            saveUserLocal()
            getAllReposByUserName()
        }
    }

    private fun saveUserLocal() {
        val sharedPreferences = getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
        val usuario = nomeUsuario.text.toString()
        val shaEdit = sharedPreferences.edit()
        shaEdit.putString("usuario", usuario)
        shaEdit.apply()
    }

    private fun showUserName() {
        val sharedPreferences = getSharedPreferences("Preferencias", Context.MODE_PRIVATE)
        val prefUser = sharedPreferences.getString("usuario", "")
        nomeUsuario.setText(prefUser)
    }

    //Metodo responsavel por fazer a configuracao base do Retrofit
    fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        githubApi = retrofit.create(GitHubService::class.java)
    }

    //Metodo responsavel por buscar todos os repositorios do usuario fornecido
    fun getAllReposByUserName() {
        val usuario = nomeUsuario.text.toString()
        val buscar = githubApi.getAllRepositoriesByUser(usuario)
        buscar.enqueue(object : Callback<List<Repository>> {
            override fun onResponse(
                call: Call<List<Repository>>,
                response: Response<List<Repository>>
            ) {
                if (response.isSuccessful) {
                    val repositorios = response.body() ?: emptyList()
                    setupAdapter(repositorios)
                }
            }
            override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    "Ocorreu um problema ao buscar os dados: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    // Metodo responsavel por realizar a configuracao do adapter
    fun setupAdapter(list: List<Repository>) {
        val adapter = RepositoryAdapter(
            list,
            itemClickListener = { repository -> openBrowser(repository.htmlUrl) },
            btnShareLister = { repository -> shareRepositoryLink(repository.htmlUrl) }
        )
        listaRepositories.adapter = adapter
    }


    // Metodo responsavel por compartilhar o link do repositorio selecionado
    fun shareRepositoryLink(urlRepository: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlRepository)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    // Metodo responsavel por abrir o browser com o link informado do repositorio
    fun openBrowser(urlRepository: String) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlRepository)
            )
        )

    }

}