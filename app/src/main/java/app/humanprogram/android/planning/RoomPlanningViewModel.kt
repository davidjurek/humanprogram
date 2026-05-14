package app.humanprogram.android.planning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.humanprogram.android.planning.model.BacklogItem
import app.humanprogram.android.planning.model.DailyTask
import app.humanprogram.android.planning.repository.BacklogRepository
import app.humanprogram.android.planning.repository.DailyPageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class RoomPlanningUiState(
    val todayTasks: List<DailyTask> = emptyList(),
    val activeBacklog: List<BacklogItem> = emptyList()
)

class RoomPlanningViewModel(
    private val dailyPageRepository: DailyPageRepository,
    private val backlogRepository: BacklogRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RoomPlanningUiState())
    val uiState: StateFlow<RoomPlanningUiState> = _uiState.asStateFlow()

    fun start(today: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            dailyPageRepository.observeTasks(today).collect { tasks ->
                _uiState.value = _uiState.value.copy(todayTasks = tasks)
            }
        }
        viewModelScope.launch {
            backlogRepository.observeActiveBacklog().collect { backlog ->
                _uiState.value = _uiState.value.copy(activeBacklog = backlog)
            }
        }
    }

    fun addBacklogItem(title: String) {
        val cleanTitle = title.trim()
        if (cleanTitle.isEmpty()) return

        viewModelScope.launch {
            backlogRepository.save(BacklogItem(title = cleanTitle))
        }
    }

    fun setTaskCompleted(
        taskId: String,
        completed: Boolean
    ) {
        viewModelScope.launch {
            dailyPageRepository.updateTaskCompletion(taskId, completed)
        }
    }
}
