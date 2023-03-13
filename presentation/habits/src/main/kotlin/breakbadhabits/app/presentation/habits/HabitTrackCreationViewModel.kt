package breakbadhabits.app.presentation.habits

import androidx.lifecycle.viewModelScope
import breakbadhabits.app.entity.Habit
import breakbadhabits.app.entity.HabitTrack
import breakbadhabits.app.logic.habits.creator.HabitTrackCreator
import breakbadhabits.app.logic.habits.provider.HabitProvider
import breakbadhabits.app.logic.habits.validator.CorrectHabitTrackEventCount
import breakbadhabits.app.logic.habits.validator.CorrectHabitTrackRange
import breakbadhabits.app.logic.habits.validator.HabitTrackEventCountValidator
import breakbadhabits.app.logic.habits.validator.HabitTrackRangeValidator
import breakbadhabits.foundation.controller.LoadingController
import breakbadhabits.foundation.controller.RequestController
import breakbadhabits.foundation.controller.ValidatedInputController
import breakbadhabits.foundation.viewmodel.ViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class HabitTrackCreationViewModel(
    habitProvider: HabitProvider,
    private val habitTrackCreator: HabitTrackCreator,
    private val trackRangeValidator: HabitTrackRangeValidator,
    private val trackEventCountValidator: HabitTrackEventCountValidator,
    private val habitId: Habit.Id
) : ViewModel() {

    val habitController = LoadingController(
        coroutineScope = viewModelScope,
        flow = habitProvider.habitFlow(habitId)
    )

    val eventCountInputController = ValidatedInputController(
        coroutineScope = viewModelScope,
        initialInput = HabitTrack.EventCount(dailyCount = 1),
        validation = trackEventCountValidator::validate
    )

    val rangeInputController = ValidatedInputController(
        coroutineScope = viewModelScope,
        initialInput = HabitTrack.Range(Clock.System.now()..Clock.System.now()),
        validation = trackRangeValidator::validate
    )

    val commentInputController = ValidatedInputController<HabitTrack.Comment?, Nothing>(
        coroutineScope = viewModelScope,
        initialInput = null,
        validation = { null }
    )

    val creationController = RequestController(
        coroutineScope = viewModelScope,
        request = {
            val eventCount = eventCountInputController.validateAndAwait()
            require(eventCount is CorrectHabitTrackEventCount)
            val trackRange = rangeInputController.validateAndAwait()
            require(trackRange is CorrectHabitTrackRange)

            val trackComment = commentInputController.state.value.input

            habitTrackCreator.createHabitTrack(
                habitId = habitId,
                range = trackRange,
                eventCount = eventCount,
                comment = trackComment
            )
        },
        isAllowedFlow = combine(
            eventCountInputController.state,
            rangeInputController.state,
        ) { trackValue, trackRange ->
            trackRange.validationResult.let {
                it == null || it is CorrectHabitTrackRange
            } && trackValue.validationResult.let {
                it == null || it is CorrectHabitTrackEventCount
            }
        }
    )
}