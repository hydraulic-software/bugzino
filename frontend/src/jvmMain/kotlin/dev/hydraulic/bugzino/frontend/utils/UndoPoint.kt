package dev.hydraulic.bugzino.frontend.utils

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.hydraulic.bugzino.frontend.ui.IOButton

/**
 * Use this to implement sets of [MutableState] objects that can be rolled back atomically e.g. for undo. Can only be used once.
 */
class UndoPoint(vararg states: MutableState<*>) : AutoCloseable {
    private val states: Set<MutableState<*>> = states.toSet().also { require(it.isNotEmpty()) }
    private val initialSnapshot = Snapshot.takeSnapshot()

    /** Whether any of the monitored properties were written to. */
    val modified = derivedStateOf {
        whatChanged.isNotEmpty()
    }

    /** Which states have changed since the construction of the object. */
    val whatChanged: List<MutableState<*>> get() = states.filter { s -> s.value != readInitial(s) }

    /** Disposes of the underlying observer. After this point no further changes will be recorded. */
    override fun close() {
        initialSnapshot.dispose()
    }

    /** Replaces the values of the monitored states with the values they had when this object was constructed. */
    @Suppress("UNCHECKED_CAST")
    fun undo() {
        for (state in states)
            (state as MutableState<in Any>).value = readInitial(state)
    }

    @Suppress("UNCHECKED_CAST")
    private fun readInitial(state: MutableState<*>) =
        initialSnapshot.enter { Snapshot.withoutReadObservation { (state as MutableState<out Any>).value } }

    /**
     * Renders buttons that triggers [undo] or runs [onSave] on a background thread. Should be placed in a row.
     */
    @Composable
    fun saveAndDiscardButtons(onSave: suspend () -> Unit) {
        val saving = remember { mutableStateOf(false) }

        Button(
            onClick = { undo() },
            modifier = Modifier.padding(10.dp),
            enabled = modified.value && !saving.value
        ) {
            Text(text = "Discard")
        }

        IOButton(
            "Save",
            action = { onSave() },
            enabled = modified.value,
            modifier = Modifier.padding(10.dp),
            isWorking = saving
        )
    }
}
