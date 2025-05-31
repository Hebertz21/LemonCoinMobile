package com.example.lemoncoin

import ConfirmExitDialogFragment
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.text.intl.Locale
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.lemoncoin.classeObjetos.Movimentacao
import com.example.lemoncoin.databinding.ActivityHomeBinding
import com.example.lemoncoin.fragments.CategoriasFragment
import com.example.lemoncoin.fragments.ContasFragment
import com.example.lemoncoin.fragments.HomeFragment
import com.example.lemoncoin.fragments.MovimentacoesFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.io.File
import java.io.FileOutputStream
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.IOException
import java.util.Date


//import com.example.lemoncoin.fragments.

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding //binding criado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)  //inflando o layout da tela
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.navView) { v, insets ->  //NavView colocado para impedir que a Toolbar fique bugada
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (savedInstanceState == null){ //Verifica se o layout está vazio
            val fragment = HomeFragment() //Define o fragment incial como home fragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit()
        }

        // Configuração do botão para abrir/fechar o menu lateral
        binding.btnMenu.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) { //Se estiver aberta, o bindinf fecha
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {                                                      //Se estiver fechada, o binding abre
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        // Configuração do clique para abrir o fragmento Contas
        binding.txtContas.setOnClickListener { //Função do clique Contas
            val fragmentAtual = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            if (fragmentAtual !is ContasFragment) {
                if (fragmentAtual is HomeFragment) {
                    openFragment(ContasFragment(), true)
                } else {
                    openFragment(ContasFragment())
                }
            }
        }

        //Relatorios
        binding.txtMovimentacoes.setOnClickListener {
            val fragmentAtual = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            if (fragmentAtual !is MovimentacoesFragment) {
                if (fragmentAtual is HomeFragment) {
                    openFragment(MovimentacoesFragment(), true)
                } else {
                    openFragment(MovimentacoesFragment())
                }
            }
        }

        //Categorias
        binding.txtCategorias.setOnClickListener {
            val fragmentAtual = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            if (fragmentAtual !is CategoriasFragment) {
                if (fragmentAtual is HomeFragment) {
                    openFragment(CategoriasFragment(), true)
                } else {
                    openFragment(CategoriasFragment())
                }
            }
        }

        binding.txtExportarExcel.setOnClickListener {
            var listaMovimentacoes: MutableList<Movimentacao> = mutableListOf()

            val db = Firebase.firestore.collection("usuarios")
                .document(Firebase.auth.currentUser!!.uid)
                .collection("movimentações")

            db.orderBy("data").get().addOnCompleteListener {
                if (it.isSuccessful) {
                    for (document in it.result) {
                        Log.i(null, "entrou no ID: ${document.id}")
                        val categoriaId = document.get("categoriaId")?.toString()
                        val contaId = document.getString("contaId")
                        val data = document.getDate("data")
                        val nome = document.getString("nome")
                        val tipo = document.getString("tipo")
                        val valor = document.getDouble("valor")

                        val movimentacao = Movimentacao (
                            nome = nome ?: "",
                            valor = valor ?: 0.0,
                            data = data ?: Date(),
                            categoria = categoriaId ?: "",
                            conta = contaId ?: "",
                            id = document.id
                        )
                        listaMovimentacoes.add(movimentacao)
                    }
                }
                exportarExcel(listaMovimentacoes, this)
            }
        }

        binding.include.imgLogo.setOnClickListener {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            val fragmentAtual = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            if (fragmentAtual !is HomeFragment) {
                openFragment(HomeFragment())
            }

        }

        binding.includeButtonLogout.btnLogout.setOnClickListener() {
            ConfirmExitDialogFragment().show(supportFragmentManager, "confirmExit")
        }

        supportFragmentManager.setFragmentResultListener("exitRequestKey", this) { _, bundle ->
            val confirmedExit = bundle.getBoolean("confirmedExit")
            if (confirmedExit) {
                Firebase.auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }

    }


    //função para abrir fragment
    private fun openFragment(fragment: Fragment, retornavel: Boolean = false) {
        if (retornavel) {
            supportFragmentManager.beginTransaction() //
                .replace(R.id.fragmentContainer, fragment)
                /*Replace substitui, ele pega o R.id e coloca
                 o id recebido atraves do argumento que foi passado */
                .addToBackStack(null) // Permite voltar ao fragmento anterior depois de apertar
                //o voltar do celular
                .commit()//Finaliza a aplicação
        } else {
            supportFragmentManager.beginTransaction() //realiza a mudança sem salvar o fragment
                .replace(R.id.fragmentContainer, fragment)
                .commit()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    fun exportarExcel(movimentacoes: List<Movimentacao>, context: Context) {
        Log.i("HomeActivity_Excel", "Iniciando exportação para Excel com ${movimentacoes.size} movimentações.")
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Movimentações")

        // Cabeçalhos
        val header = sheet.createRow(0)
        val campos = listOf("Nome", "Valor", "Conta", "Categoria", "Data", "Tipo") // Adicionei "Nome" e "Tipo" se forem relevantes
        campos.forEachIndexed { i, nomeCampo -> header.createCell(i).setCellValue(nomeCampo) }

        // Dados
        movimentacoes.forEachIndexed { index, mov ->
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(mov.nome) // Supondo que 'nome' seja o nome da movimentação
            row.createCell(1).setCellValue(mov.valor) // POI trata números como double por padrão
            row.createCell(2).setCellValue(mov.conta) // ID da conta ou nome da conta?
            row.createCell(3).setCellValue(mov.categoria) // ID da categoria ou nome da categoria?
            // Formatar a data para uma string legível é uma boa prática
            val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.getDefault())
            row.createCell(4).setCellValue(dateFormat.format(mov.data))
            row.createCell(5).setCellValue(mov.tipo)
        }

        val nomeArquivo = "Movimentacoes_${System.currentTimeMillis()}.xlsx" // Nome de arquivo único
        val file = File(context.getExternalFilesDir(null), nomeArquivo)

        try {
            FileOutputStream(file).use { outputStream ->
                workbook.write(outputStream)
            } // .use garante que o outputStream é fechado
            workbook.close()
            Log.i("HomeActivity_Excel", "Excel exportado com sucesso para: ${file.absolutePath}")
            Toast.makeText(context, "Excel exportado para: ${file.name}", Toast.LENGTH_LONG).show()

            abrirOuCompartilharArquivo(file, context)

        } catch (e: IOException) {
            Log.e("HomeActivity_Excel", "IOException ao escrever o arquivo Excel", e)
            Toast.makeText(context, "Erro de E/S ao exportar Excel.", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e("HomeActivity_Excel", "Erro geral ao exportar Excel", e)
            Toast.makeText(context, "Erro ao exportar Excel.", Toast.LENGTH_LONG).show()
        }
    }

    // Função auxiliar para abrir ou compartilhar (exemplo)
    private fun abrirOuCompartilharArquivo(file: File, context: Context) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply { // Ou ACTION_SEND para compartilhar
            setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        }
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Nenhum aplicativo encontrado para abrir arquivos Excel.", Toast.LENGTH_LONG).show()
            Log.e("HomeActivity_Excel", "Nenhum app para abrir Excel.", e)
        }
    }
}