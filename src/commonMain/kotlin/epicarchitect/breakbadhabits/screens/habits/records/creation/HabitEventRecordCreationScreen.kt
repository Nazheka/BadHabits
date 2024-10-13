package epicarchitect.breakbadhabits.screens.habits.records.creation

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import epicarchitect.breakbadhabits.Environment
import epicarchitect.breakbadhabits.database.Habit
import epicarchitect.breakbadhabits.habits.validation.HabitEventCountError
import epicarchitect.breakbadhabits.habits.validation.HabitEventRecordTimeRangeError
import epicarchitect.breakbadhabits.habits.validation.checkHabitEventCount
import epicarchitect.breakbadhabits.habits.validation.checkHabitEventRecordTimeRange
import epicarchitect.breakbadhabits.uikit.DateTimeRangeInputCard
import epicarchitect.breakbadhabits.uikit.FlowStateContainer
import epicarchitect.breakbadhabits.uikit.SimpleScrollableScreen
import epicarchitect.breakbadhabits.uikit.button.Button
import epicarchitect.breakbadhabits.uikit.button.ButtonStyles
import epicarchitect.breakbadhabits.uikit.regex.Regexps
import epicarchitect.breakbadhabits.uikit.stateOfOneOrNull
import epicarchitect.breakbadhabits.uikit.text.TextInputCard
import kotlin.time.Duration.Companion.hours

class HabitEventRecordCreationScreen(private val habitId: Int) : Screen {
    @Composable
    override fun Content() {
        HabitEventRecordCreation(habitId)
    }
}

@Composable
fun HabitEventRecordCreation(habitId: Int) {
    val strings = Environment.resources.strings.habitEventRecordCreationStrings
    val habitQueries = Environment.database.habitQueries
    val navigator = LocalNavigator.currentOrThrow

    FlowStateContainer(
        state = stateOfOneOrNull { habitQueries.habitById(habitId) }
    ) { habit ->
        SimpleScrollableScreen(
            title = strings.titleText(habit?.name ?: "..."),
            onBackClick = navigator::pop
        ) {
            if (habit != null) {
                Content(habit)
            }
        }
    }
}

class HabitEventRecordCreationState {
    var timeRange by mutableStateOf(defaultTimeRange())
    var timeRangeError by mutableStateOf<HabitEventRecordTimeRangeError?>(null)
    var eventCount by mutableIntStateOf(1)
    var eventCountError by mutableStateOf<HabitEventCountError?>(null)
    var comment by mutableStateOf("")

    private fun defaultTimeRange() =
        Environment.dateTime.currentInstant().let { (it - 1.hours)..it }
}

// TODO: should be savable
@Composable
fun rememberHabitEventRecordCreationState() = remember {
    HabitEventRecordCreationState()
}

@Composable
private fun ColumnScope.Content(habit: Habit) {
    val state = rememberHabitEventRecordCreationState()
    val strings = Environment.resources.strings.habitEventRecordCreationStrings
    val habitEventRecordQueries = Environment.database.habitEventRecordQueries
    val navigator = LocalNavigator.currentOrThrow

    Spacer(Modifier.height(16.dp))

    TextInputCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        title = strings.eventCountTitle(),
        description = strings.eventCountDescription(),
        value = state.eventCount.toString(),
        onValueChange = {
            state.eventCount = it.toIntOrNull() ?: 0
            state.eventCountError = null
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        regex = Regexps.integersOrEmpty(maxCharCount = 4),
        error = state.eventCountError?.let(strings::eventCountError),
    )

    Spacer(Modifier.height(16.dp))

    DateTimeRangeInputCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        title = strings.timeRangeTitle(),
        description = strings.timeRangeDescription(),
        error = state.timeRangeError?.let(strings::timeRangeError),
        value = state.timeRange,
        onChanged = {
            state.timeRange = it
            state.timeRangeError = null
        },
        startTimeLabel = strings.startDateTimeLabel(),
        endTimeLabel = strings.endDateTimeLabel(),
        timeZone = Environment.dateTime.currentTimeZone()
    )

    Spacer(Modifier.height(16.dp))

    TextInputCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        title = strings.commentTitle(),
        description = strings.commentDescription(),
        value = state.comment,
        onValueChange = {
            state.comment = it
        },
        multiline = true
    )

    Spacer(modifier = Modifier.weight(1.0f))

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .align(Alignment.End),
        text = strings.finishButton(),
        style = ButtonStyles.primary,
        onClick = {
            state.eventCountError = checkHabitEventCount(state.eventCount)
            if (state.eventCountError != null) return@Button

            state.timeRangeError = checkHabitEventRecordTimeRange(
                timeRange = state.timeRange,
                currentTime = Environment.dateTime.currentInstant()
            )
            if (state.timeRangeError != null) return@Button

            habitEventRecordQueries.insert(
                habitId = habit.id,
                startTime = state.timeRange.start,
                endTime = state.timeRange.endInclusive,
//                eventCount = totalHabitEventCountByDaily(
//                    dailyEventCount = dailyEventCount,
//                    timeRange = selectedTimeRange,
//                    timeZone = Environment.dateTime.currentTimeZone()
//                ),
                eventCount = state.eventCount,
                comment = state.comment
            )
            navigator.pop()
        }
    )

    Spacer(modifier = Modifier.height(16.dp))
}