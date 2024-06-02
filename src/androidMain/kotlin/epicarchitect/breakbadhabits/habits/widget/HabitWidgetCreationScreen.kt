package epicarchitect.breakbadhabits.habits.widget

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import epicarchitect.breakbadhabits.data.AppData
import epicarchitect.breakbadhabits.data.Habit
import epicarchitect.breakbadhabits.ui.component.Card
import epicarchitect.breakbadhabits.ui.component.Checkbox
import epicarchitect.breakbadhabits.ui.component.FlowStateContainer
import epicarchitect.breakbadhabits.ui.component.button.Button
import epicarchitect.breakbadhabits.ui.component.stateOfList
import epicarchitect.breakbadhabits.ui.component.text.Text
import epicarchitect.breakbadhabits.ui.component.text.TextField

@Composable
fun HabitWidgetCreation(systemWidgetId: Int, onDone: () -> Unit) {
    val resources = LocalHabitWidgetCreationResources.current

    FlowStateContainer(
        state = stateOfList { AppData.database.habitQueries.habits() }
    ) { habits ->
        val selectedHabitIds = rememberSaveable { mutableStateListOf<Int>() }
        var widgetTitle by rememberSaveable { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = resources.title(),
                type = Text.Type.Title,
                priority = Text.Priority.High
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = resources.nameDescription()
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                label = resources.title(),
                value = widgetTitle,
                onValueChange = {
                    widgetTitle = it.toString()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = resources.habitsDescription()
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

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                modifier = Modifier.align(Alignment.End),
                text = resources.finishButton(),
                type = Button.Type.Main,
                onClick = {
                    AppData.database.habitWidgetQueries.insert(
                        title = widgetTitle,
                        habitIds = selectedHabitIds,
                        systemId = systemWidgetId
                    )
                    onDone()
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