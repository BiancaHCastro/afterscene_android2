package com.afterscene.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.afterscene.data.remote.model.WorkDto
import com.afterscene.viewmodel.MoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodScreen(
    viewModel: MoodViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val moods by viewModel.moods.collectAsState()
    val selectedMoodId by viewModel.selectedMoodId.collectAsState()
    val works by viewModel.works.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showSuggestDialog by remember { mutableStateOf(false) }

    // Dialog state
    var suggestTitle by remember { mutableStateOf("") }
    var suggestType by remember { mutableStateOf("Anime") }
    var suggestDescription by remember { mutableStateOf("") }
    var expandedTypeDropdown by remember { mutableStateOf(false) }
    val types = listOf("Anime", "Dorama", "Série", "Filme")

    // Handle messages via Snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrorMessage()
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccessMessage()
        }
    }

    val currentMood = moods.find { it.id == selectedMoodId }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121212)),
        containerColor = Color(0xFF121212),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Recomendações por Mood",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Sugestões via API FastAPI (Retrofit)",
                            color = Color(0xFFB39DDB),
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar para a Biblioteca",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadMoods() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Recarregar recomendações",
                            tint = Color(0xFFB39DDB)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF121212)
                )
            )
        },
        floatingActionButton = {
            if (selectedMoodId != null) {
                FloatingActionButton(
                    onClick = {
                        suggestTitle = ""
                        suggestDescription = ""
                        suggestType = "Anime"
                        showSuggestDialog = true
                    },
                    containerColor = Color(0xFF7E57C2),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Sugerir obra",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sugerir Obra",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Horizontal scrollable Mood Categories Chips
            if (moods.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    moods.forEach { mood ->
                        val isSelected = mood.id == selectedMoodId
                        val emoji = mood.emoji ?: "✨"
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (isSelected) Color(0xFF7E57C2) else Color(0xFF1E1E1E),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color(0xFFB39DDB) else Color(0xFF333333),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable { viewModel.selectMood(mood.id) }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "$emoji ${mood.name}",
                                color = if (isSelected) Color.White else Color(0xFFB39DDB),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Current Mood Category description banner
            currentMood?.let { mood ->
                if (!mood.description.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFF7E57C2).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = mood.description,
                            color = Color(0xFFCCCCCC),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Content Area: Loading, Error, Empty or List of Works
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color(0xFF7E57C2))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Buscando recomendações na API...",
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
                moods.isEmpty() -> {
                    // API Offline or empty state
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "📡",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Servidor de Recomendações Offline",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Para ver as recomendações, inicie a API FastAPI localmente com:\nuvicorn main:app --reload\n(Acessível no emulador via http://10.0.2.2:8000/)",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.loadMoods() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E57C2)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Tentar Novamente", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                works.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Nenhuma recomendação encontrada para este mood.",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = {
                                    suggestTitle = ""
                                    suggestDescription = ""
                                    suggestType = "Anime"
                                    showSuggestDialog = true
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF7E57C2))
                            ) {
                                Text("Seja o primeiro a sugerir!")
                            }
                        }
                    }
                }
                else -> {
                    // List of recommended works
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                    ) {
                        items(works, key = { it.id }) { work ->
                            RecommendedWorkCard(work = work)
                        }
                        item {
                            Spacer(modifier = Modifier.height(88.dp))
                        }
                    }
                }
            }
        }
    }

    // Dialog for Suggesting a new Work (POST /moods/{mood_id}/works)
    if (showSuggestDialog && selectedMoodId != null) {
        AlertDialog(
            onDismissRequest = { if (!isSubmitting) showSuggestDialog = false },
            title = {
                Text(
                    text = "Sugerir Obra para ${currentMood?.name ?: "esta categoria"}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Sua sugestão será enviada à API (POST /moods/$selectedMoodId/works) e salva localmente no moods.json.",
                        color = Color(0xFFB39DDB),
                        fontSize = 11.sp
                    )

                    // Title input
                    Column {
                        Text(
                            text = "TÍTULO DA OBRA",
                            color = Color(0xFF7E57C2),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = suggestTitle,
                            onValueChange = { suggestTitle = it },
                            placeholder = { Text("Ex: Your Name", color = Color.Gray, fontSize = 13.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF7E57C2),
                                unfocusedBorderColor = Color(0xFF444444)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Type selection dropdown
                    Column {
                        Text(
                            text = "TIPO",
                            color = Color(0xFF7E57C2),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .background(Color.Transparent, RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF444444), RoundedCornerShape(8.dp))
                                .clickable { expandedTypeDropdown = true }
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = suggestType, color = Color.White, fontSize = 13.sp)
                                Text(text = "▼", color = Color(0xFF7E57C2), fontSize = 10.sp)
                            }
                            DropdownMenu(
                                expanded = expandedTypeDropdown,
                                onDismissRequest = { expandedTypeDropdown = false },
                                modifier = Modifier.background(Color(0xFF1E1E1E))
                            ) {
                                types.forEach { t ->
                                    DropdownMenuItem(
                                        text = { Text(text = t, color = Color.White) },
                                        onClick = {
                                            suggestType = t
                                            expandedTypeDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Description input
                    Column {
                        Text(
                            text = "DESCRIÇÃO / MOTIVO DA RECOMENDAÇÃO",
                            color = Color(0xFF7E57C2),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = suggestDescription,
                            onValueChange = { suggestDescription = it },
                            placeholder = { Text("Por que você recomenda esta obra para este mood?", color = Color.Gray, fontSize = 13.sp) },
                            minLines = 2,
                            maxLines = 4,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF7E57C2),
                                unfocusedBorderColor = Color(0xFF444444)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.suggestWork(
                            moodId = selectedMoodId!!,
                            title = suggestTitle,
                            type = suggestType,
                            description = suggestDescription,
                            onSuccess = {
                                showSuggestDialog = false
                            }
                        )
                    },
                    enabled = suggestTitle.isNotBlank() && !isSubmitting,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7E57C2)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Enviar Sugestão", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSuggestDialog = false },
                    enabled = !isSubmitting
                ) {
                    Text("Cancelar", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF1E1E1E),
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun RecommendedWorkCard(
    work: WorkDto,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(Color(0xFF1E1E1E), RoundedCornerShape(14.dp))
            .border(1.dp, Color(0xFF2C2C2C), RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = work.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Type Badge
                Box(
                    modifier = Modifier
                        .background(Color(0xFF7E57C2).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .border(1.dp, Color(0xFF7E57C2).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = work.type,
                        color = Color(0xFFB39DDB),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (work.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = work.description,
                    color = Color(0xFFB0B0B0),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
