package epicarchitect.breakbadhabits.features.habits.widgets.editing

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import epicarchitect.breakbadhabits.foundation.uikit.Card
import epicarchitect.breakbadhabits.foundation.uikit.Checkbox
import epicarchitect.breakbadhabits.foundation.uikit.Dialog
import epicarchitect.breakbadhabits.foundation.uikit.ProgressIndicator
import epicarchitect.breakbadhabits.foundation.uikit.button.Button
import epicarchitect.breakbadhabits.foundation.uikit.effect.ClearFocusWhenKeyboardHiddenEffect
import epicarchitect.breakbadhabits.foundation.uikit.text.Text
import epicarchitect.breakbadhabits.foundation.uikit.text.TextField
import epicarchitect.breakbadhabits.sqldelight.main.Habit
import kotlinx.coroutines.Dispatchers

@Composable
fun HabitWidgetEditing(dependencies: HabitWidgetEditingDependencies) {
    val habits by remember(dependencies) {
        dependencies.mainDatabase.habitQueries
            .selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
    }.collectAsState(emptyList())

    val initialWidgetState = remember(dependencies) {
        dependencies.mainDatabase.habitWidgetQueries
            .selectById(dependencies.widgetId)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
    }.collectAsState(null)

    val initialWidget = initialWidgetState.value
    val selectedHabitIds = rememberSaveable(initialWidget) {
        initialWidget?.habitIds?.toMutableStateList() ?: mutableStateListOf()
    }
    var widgetTitle by rememberSaveable(initialWidget) {
        mutableStateOf(initialWidget?.title ?: "")
    }

    if (initialWidget == null) {
        ProgressIndicator()
    } else {
        ClearFocusWhenKeyboardHiddenEffect()

        var deletionShow by remember { mutableStateOf(false) }
        if (deletionShow) {
            Dialog(onDismiss = { deletionShow = false }) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = dependencies.resources.deleteConfirmation(),
                        type = Text.Type.Description,
                        priority = Text.Priority.High
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Button(
                            text = dependencies.resources.cancel(),
                            onClick = {
                                deletionShow = false
                            }
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Button(
                            text = dependencies.resources.yes(),
                            type = Button.Type.Main,
                            onClick = {
                                dependencies.mainDatabase.habitWidgetQueries.deleteById(dependencies.widgetId)
                            }
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = dependencies.resources.title(),
                type = Text.Type.Title,
                priority = Text.Priority.High
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = dependencies.resources.nameDescription()
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                label = dependencies.resources.title(),
                value = widgetTitle,
                onValueChange = {
                    widgetTitle = it
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = dependencies.resources.habitsDescription()
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(habits, key = { it.id }) { habit ->
                    HabitItem(
                        habit = habit,
                        checked = selectedHabitIds.contains(habit.id),
                        onClick = {
                            if (selectedHabitIds.contains(habit.id)) {
                                selectedHabitIds.remove(habit.id)
                            } else {
                                selectedHabitIds.add(habit.id)
                            }
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = dependencies.resources.deleteDescription()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                text = dependencies.resources.deleteButtonText(),
                type = Button.Type.Dangerous,
                onClick = {
                    deletionShow = true
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                modifier = Modifier.align(Alignment.End),
                text = dependencies.resources.finishButton(),
                type = Button.Type.Main,
                onClick = {
                    dependencies.mainDatabase.habitWidgetQueries.update(
                        id = dependencies.widgetId,
                        title = widgetTitle,
                        habitIds = selectedHabitIds,
                    )
                    dependencies.navigation.back()
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LazyItemScope.HabitItem(
    habit: Habit,
    checked: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateItemPlacement()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked,
                    onCheckedChange = { onClick() }
                )

                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = habit.name
                )
            }
        }
    }
}