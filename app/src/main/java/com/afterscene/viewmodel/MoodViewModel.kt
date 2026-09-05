package com.afterscene.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.afterscene.data.remote.model.MoodCategoryDto
import com.afterscene.data.remote.model.WorkDto
import com.afterscene.data.repository.MoodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MoodViewModel(
    private val repository: MoodRepository = MoodRepository()
) : ViewModel() {

    private val _moods = MutableStateFlow<List<MoodCategoryDto>>(emptyList())
    val moods: StateFlow<List<MoodCategoryDto>> = _moods.asStateFlow()

    private val _selectedMoodId = MutableStateFlow<Int?>(null)
    val selectedMoodId: StateFlow<Int?> = _selectedMoodId.asStateFlow()

    private val _works = MutableStateFlow<List<WorkDto>>(emptyList())
    val works: StateFlow<List<WorkDto>> = _works.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        loadMoods()
    }

    /**
     * Carrega todas as categorias de mood da API (GET /moods).
     */
    fun loadMoods() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            repository.getMoods().fold(
                onSuccess = { moodList ->
                    _moods.value = moodList
                    _isLoading.value = false
                    
                    // Se nenhum mood estiver selecionado, seleciona o primeiro por padrão
                    if (_selectedMoodId.value == null && moodList.isNotEmpty()) {
                        selectMood(moodList.first().id)
                    } else if (_selectedMoodId.value != null) {
                        loadWorks(_selectedMoodId.value!!)
                    }
                },
                onFailure = { error ->
                    _isLoading.value = false
                    _errorMessage.value = "Não foi possível conectar à API de Moods (${error.localizedMessage ?: "Erro de conexão"}). Certifique-se de que a API FastAPI local está rodando."
                }
            )
        }
    }

    /**
     * Seleciona um mood e busca suas obras recomendadas.
     */
    fun selectMood(moodId: Int) {
        _selectedMoodId.value = moodId
        loadWorks(moodId)
    }

    /**
     * Carrega as obras de um mood específico (GET /moods/{mood_id}/works).
     */
    fun loadWorks(moodId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.getWorksByMood(moodId).fold(
                onSuccess = { workList ->
                    _works.value = workList
                    _isLoading.value = false
                },
                onFailure = { error ->
                    _isLoading.value = false
                    _errorMessage.value = "Erro ao buscar obras para esta categoria: ${error.localizedMessage}"
                }
            )
        }
    }

    /**
     * Envia a sugestão de uma nova obra para a categoria de mood (POST /moods/{mood_id}/works).
     */
    fun suggestWork(
        moodId: Int,
        title: String,
        type: String,
        description: String,
        onSuccess: () -> Unit = {}
    ) {
        if (title.isBlank()) {
            _errorMessage.value = "O título da obra não pode estar vazio."
            return
        }

        viewModelScope.launch {
            _isSubmitting.value = true
            _errorMessage.value = null

            repository.suggestWork(moodId, title, type, description).fold(
                onSuccess = { newWork ->
                    _isSubmitting.value = false
                    _successMessage.value = "Sugestão \"${newWork.title}\" cadastrada com sucesso!"
                    // Atualiza a lista local de obras para refletir a nova sugestão
                    loadWorks(moodId)
                    onSuccess()
                },
                onFailure = { error ->
                    _isSubmitting.value = false
                    _errorMessage.value = "Erro ao enviar sugestão: ${error.localizedMessage}"
                }
            )
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    class Factory(private val repository: MoodRepository = MoodRepository()) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MoodViewModel::class.java)) {
                return MoodViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
