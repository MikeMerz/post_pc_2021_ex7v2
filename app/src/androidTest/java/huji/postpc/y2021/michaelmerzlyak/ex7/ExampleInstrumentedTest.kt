package huji.postpc.y2021.michaelmerzlyak.ex7

import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.SeekBar
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.firestore.FirebaseFirestore
import junit.framework.TestCase
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @After
    fun clearAfterTest()
    {
        FirebaseFirestore.getInstance().clearPersistence()
    }

    @Test
    fun newOrderSaveButtonIsenabled() {
        val mainActivity = ActivityScenario.launch<MainActivity>(MainActivity::class.java)
        mainActivity.moveToState(Lifecycle.State.CREATED)
        mainActivity.onActivity { it: MainActivity ->
            it.setContentView(R.layout.new_order_layout)
            val pick = it.findViewById<Button>(R.id.buttonCur)
            assertTrue(pick.isEnabled)
        }
    }
    @Test
    fun SeekBarIsEnabled()
    {
        val mainActivity = ActivityScenario.launch<MainActivity>(MainActivity::class.java)
        mainActivity.moveToState(Lifecycle.State.CREATED)
        mainActivity.onActivity { it: MainActivity ->
            it.setContentView(R.layout.new_order_layout)
            val pick = it.findViewById<SeekBar>(R.id.editTextNumber10)
            assertTrue(pick.isEnabled)
        }
    }
    @Test
    fun whenMovingFromNewToEditShouldSaveData()
    {
        val mainActivity = ActivityScenario.launch<MainActivity>(MainActivity::class.java)
        mainActivity.moveToState(Lifecycle.State.CREATED)
        mainActivity.onActivity { it: MainActivity ->
            it.setContentView(R.layout.new_order_layout)
            val name = it.findViewById<EditText>(R.id.textView3)
            val comments = it.findViewById<EditText>(R.id.editTextTextPersonNameCur)
            val pick = it.findViewById<SeekBar>(R.id.editTextNumber10)
            val tahi = it.findViewById<CheckBox>(R.id.editTextNumberCur2)
            val hummi = it.findViewById<CheckBox>(R.id.checkBoxCur)
            name.setText("DA")
            comments.setText("No onions")
            pick.progress = 4
            tahi.isChecked = false
            hummi.isChecked = true
            it.findViewById<Button>(R.id.buttonCur).performClick()
            assertEquals(it.findViewById<EditText>(R.id.textView3).text.toString(),name.text.toString())
            assertEquals(it.findViewById<EditText>(R.id.editTextTextPersonNameCur).text.toString(),comments.text.toString())
            assertEquals(it.findViewById<SeekBar>(R.id.editTextNumber10).progress,pick.progress)
            assertEquals(it.findViewById<CheckBox>(R.id.editTextNumberCur2).isChecked,tahi.isChecked)
            assertEquals(it.findViewById<CheckBox>(R.id.checkBoxCur).isChecked,hummi.isChecked)
        }
    }
}