package com.example.lemoncoin

import ConfirmExitDialogFragment
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.text.intl.Locale
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.lemoncoin.classeObjetos.Movimentacao
import com.example.lemoncoin.databinding.ActivityHomeBinding
import com.example.lemoncoin.fragments.CategoriasFragment
import com.example.lemoncoin.fragments.ConfiguracoesFragment
import com.example.lemoncoin.fragments.ContasFragment
import com.example.lemoncoin.fragments.HomeFragment
import com.example.lemoncoin.fragments.MovimentacoesFragment
import com.example.lemoncoin.fragments.PopupFiltroPlanilha
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.IOException
import java.text.NumberFormat
import java.util.Date
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.DataFormat
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import java.text.SimpleDateFormat
import java.util.Calendar


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

        //CONTAS
        fun abrirConats(){
            val fragmentAtual = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            if (fragmentAtual !is ContasFragment) {
                if (fragmentAtual is HomeFragment) {
                    openFragment(ContasFragment(), true)
                } else {
                    openFragment(ContasFragment())
                }
            }
        }
        binding.txtContas.setOnClickListener {
            abrirConats()
        }

        binding.iconContas.setOnClickListener(){
            abrirConats()
        }

        //MOVIMENTAÇÕES
        fun abrirMovimentacoes(){
            val fragmentAtual = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            if (fragmentAtual !is MovimentacoesFragment) {
                if (fragmentAtual is HomeFragment) {
                    openFragment(MovimentacoesFragment(), true)
                } else {
                    openFragment(MovimentacoesFragment())
                }
            }
        }
        binding.txtMovimentacoes.setOnClickListener {
            abrirMovimentacoes()
        }

        binding.iconMovimentacoes.setOnClickListener(){
            abrirMovimentacoes()
        }

        //Categorias

        fun abrirCategorias(){
            val fragmentAtual = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            if (fragmentAtual !is CategoriasFragment) {
                if (fragmentAtual is HomeFragment) {
                    openFragment(CategoriasFragment(), true)
                } else {
                    openFragment(CategoriasFragment())
                }
            }
        }

        binding.iconCategoria.setOnClickListener{
            abrirCategorias()
        }
        binding.txtCategorias.setOnClickListener {
            abrirCategorias()
        }

        fun abrirConfiguracoes(){
            val fragmentAtual = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            if (fragmentAtual !is ConfiguracoesFragment) {
                openFragment(ConfiguracoesFragment(), true)
            } else {
                openFragment(ConfiguracoesFragment())
            }
        }
        //Configurações
        binding.txtConfiguracoes.setOnClickListener {
            abrirConfiguracoes()
        }

        binding.iconConfiguracoes.setOnClickListener(){
            abrirConfiguracoes()
        }

        //EXCEL
        binding.iconExportarExcel.setOnClickListener(){
            PopupFiltroPlanilha().show(supportFragmentManager, "popupFiltroPlanilha")
        }
        binding.txtExportarExcel.setOnClickListener {
            PopupFiltroPlanilha().show(supportFragmentManager, "popupFiltroPlanilha")
        }

        binding.include.imgLogo.setOnClickListener {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            val fragmentAtual = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
            if (fragmentAtual !is HomeFragment) {
                openFragment(HomeFragment())
            }

        }

        binding.includeButtonLogout.txtLogout.setOnClickListener() {
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

        supportFragmentManager.setFragmentResultListener("filtroPlanilhaRequestKey", this) { _, bundle ->
            val usarDespesas = bundle.getBoolean("usarDespesas")
            val usarReceitas = bundle.getBoolean("usarReceitas")
            val todasDatas = bundle.getBoolean("todasDatas")
            val txtDataInicio = bundle.getString("dataInicio")
            val txtDataFim = bundle.getString("dataFim")
            var dataInicio: Date? = null
            var dataFim: Date? = null

            if (!todasDatas) {
                dataInicio = SimpleDateFormat("dd/MM/yyyy",
                    java.util.Locale.getDefault()).parse(txtDataInicio!!)

                dataFim = SimpleDateFormat("dd/MM/yyyy",
                    java.util.Locale.getDefault()).parse(txtDataFim!!)
            }

            binding.btnMenu.performClick()
            binding.loadingOverlay.visibility = View.VISIBLE

            val listaMovimentacoes = mutableListOf<Movimentacao>()
            val db = Firebase.firestore
                .collection("usuarios")
                .document(Firebase.auth.currentUser!!.uid)
                .collection("movimentações")

            db.orderBy("data").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val categoriaId = document.getString("categoriaId") ?: ""
                        val contaId = document.getString("contaId") ?: ""
                        val data = document.getDate("data") ?: Date()
                        val nome = document.getString("nome") ?: ""
                        val tipo = document.getString("tipo") ?: ""
                        val valor = document.getDouble("valor") ?: 0.0

                        if(!todasDatas){
                            if(data.before(dataInicio!!) || data.after(dataFim!!)) continue
                        }

                        if(tipo == "Despesa" && !usarDespesas) continue
                        if(tipo == "Receita" && !usarReceitas) continue

                        listaMovimentacoes.add(
                            Movimentacao(
                                nome = nome,
                                valor = valor,
                                data = data,
                                categoria = categoriaId,
                                conta = contaId,
                                tipo = tipo,
                                id = document.id
                            )
                        )
                    }
                }
                // Volta pra UI e exporta
                CoroutineScope(Dispatchers.Main).launch {
                    exportarExcel(listaMovimentacoes, this@HomeActivity)
                    binding.loadingOverlay.visibility = View.GONE
                }
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

    suspend fun exportarExcel(movimentacoes: List<Movimentacao>, context: Context) {
        Log.i("HomeActivity_Excel", "Iniciando exportação para Excel com ${movimentacoes.size} movimentações.")
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Movimentações")

        val currencyCellStyle: CellStyle = workbook.createCellStyle()
        val dataFormat: DataFormat = workbook.createDataFormat()
        currencyCellStyle.dataFormat = dataFormat.getFormat("R$ #,##0.00")

        val contasMap = carregarNomesExcel("contas")
        val categoriasMap = carregarNomesExcel("categorias")

        // Cabeçalhos
        val header = sheet.createRow(0)
        val campos = listOf("Nome", "Valor", "Conta", "Categoria", "Data", "Tipo") // Adicionei "Nome" e "Tipo" se forem relevantes
        campos.forEachIndexed { i, nomeCampo -> header.createCell(i).setCellValue(nomeCampo) }

        // Dados
        movimentacoes.forEachIndexed { index, mov ->

            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(mov.nome)

            val cellValor = row.createCell(1)
            cellValor.setCellValue(mov.valor)
            cellValor.cellStyle = currencyCellStyle as XSSFCellStyle?

            row.createCell(2).setCellValue(contasMap[mov.conta] ?: "Conta desconhecida")
            row.createCell(3).setCellValue(categoriasMap[mov.categoria] ?: "Categoria desconhecida")

            val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
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

    suspend fun carregarNomesExcel(colecao: String): Map<String, String> {
        val db = Firebase.firestore
        val userId = Firebase.auth.currentUser?.uid ?: return emptyMap()
        val snapshot = db.collection("usuarios").document(userId).collection(colecao).get().await()
        return snapshot.documents.associate { it.id to (it.getString("nome") ?: "Sem nome") }
    }
}