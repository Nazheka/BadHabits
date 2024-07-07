package epicarchitect.breakbadhabits.data.resources.strings

import epicarchitect.breakbadhabits.operation.habits.validation.DailyHabitEventCountError
import epicarchitect.breakbadhabits.operation.habits.validation.HabitNewNameError
import epicarchitect.breakbadhabits.ui.screen.habits.creation.HabitDuration

interface HabitCreationStrings {
    fun titleText(): String
    fun habitNameDescription(): String
    fun habitNameTitle(): String
    fun habitIconTitle(): String
    fun habitEventCountTitle(): String
    fun habitDurationTitle(): String
    fun habitIconDescription(): String
    fun finishButtonText(): String
    fun habitNameError(error: HabitNewNameError): String
    fun habitDuration(duration: HabitDuration): String // move to duration formatting?
    fun habitDurationDescription(): String
    fun trackEventCountError(reason: DailyHabitEventCountError): String
    fun trackEventCountDescription(): String
}

class RussianHabitCreationStrings : HabitCreationStrings {
    override fun titleText() = "Новая привычка"
    override fun habitNameDescription() = "Введите название привычки, например курение."
    override fun habitNameTitle() = "Название"
    override fun habitIconTitle() = "Иконка"
    override fun habitEventCountTitle() = "Частота"
    override fun habitDurationTitle() = "Продолжительность"
    override fun habitIconDescription() = "Выберите подходящую иконку для привычки."
    override fun finishButtonText() = "Готово"
    override fun habitNameError(error: HabitNewNameError) = when (error) {
        HabitNewNameError.AlreadyUsed -> "Это название уже используется."
        HabitNewNameError.Empty       -> "Название не может быть пустым."
        is HabitNewNameError.TooLong  -> {
            "Название не может быть длиннее чем ${error.maxLength} символов."
        }
    }

    override fun habitDuration(duration: HabitDuration) = when (duration) {
        HabitDuration.MONTH_1 -> "1 месяц"
        HabitDuration.MONTH_3 -> "3 месяца"
        HabitDuration.MONTH_6 -> "6 месяцев"
        HabitDuration.YEAR_1  -> "1 год"
        HabitDuration.YEAR_2  -> "2 года"
        HabitDuration.YEAR_3  -> "3 года"
        HabitDuration.YEAR_4  -> "4 года"
        HabitDuration.YEAR_5  -> "5 лет"
        HabitDuration.YEAR_6  -> "6 лет"
        HabitDuration.YEAR_7  -> "7 лет"
        HabitDuration.YEAR_8  -> "8 лет"
        HabitDuration.YEAR_9  -> "9 лет"
        HabitDuration.YEAR_10 -> "10 лет"
    }
    override fun habitDurationDescription() = "Укажите примерно как давно у вас эта привычка."
    override fun trackEventCountError(reason: DailyHabitEventCountError) = when (reason) {
        DailyHabitEventCountError.Empty -> {
            "Поле не может быть пустым"
        }
    }
    override fun trackEventCountDescription() = "Укажите сколько примерно было событий привычки в день."
}

class EnglishHabitCreationStrings : HabitCreationStrings {
    override fun titleText() = "New habit"
    override fun habitNameDescription() = "Enter a name for the habit, such as smoking."
    override fun habitNameTitle() = "Name"
    override fun habitIconTitle() = "Icon"
    override fun habitEventCountTitle() = "Frequency"
    override fun habitDurationTitle() = "Duration"
    override fun habitIconDescription() = "Choose the appropriate icon for the habit."
    override fun finishButtonText() = "Done"
    override fun habitNameError(error: HabitNewNameError) = when (error) {
        HabitNewNameError.AlreadyUsed -> "This name has already been used."
        HabitNewNameError.Empty       -> "The title cannot be empty."
        is HabitNewNameError.TooLong  -> {
            "The name cannot be longer than ${error.maxLength} characters."
        }
    }

    override fun habitDuration(duration: HabitDuration) = when (duration) {
        HabitDuration.MONTH_1 -> "1 month"
        HabitDuration.MONTH_3 -> "3 months"
        HabitDuration.MONTH_6 -> "6 month"
        HabitDuration.YEAR_1  -> "1 year"
        HabitDuration.YEAR_2  -> "2 years"
        HabitDuration.YEAR_3  -> "3 years"
        HabitDuration.YEAR_4  -> "4 years"
        HabitDuration.YEAR_5  -> "5 years"
        HabitDuration.YEAR_6  -> "6 years"
        HabitDuration.YEAR_7  -> "7 years"
        HabitDuration.YEAR_8  -> "8 years"
        HabitDuration.YEAR_9  -> "9 years"
        HabitDuration.YEAR_10 -> "10 years"
    }

    override fun habitDurationDescription() = "Please indicate approximately how long you have had this habit."

    override fun trackEventCountError(reason: DailyHabitEventCountError) = when (reason) {
        DailyHabitEventCountError.Empty -> {
            "Cant be empty"
        }
    }

    override fun trackEventCountDescription() = "Please indicate approximately how many habit events occurred per day."
}