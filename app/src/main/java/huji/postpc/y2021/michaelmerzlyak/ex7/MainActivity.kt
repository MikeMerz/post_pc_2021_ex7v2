package huji.postpc.y2021.michaelmerzlyak.ex7

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


internal class Order {
    var id = UUID.randomUUID().toString()
    var customerName: String? = null
    var pickles = 0
    var hummus = false
    var tahini = false
    var comment: String? = null
    var status: String? = null
}
class MainActivity : AppCompatActivity() {
    private lateinit var db: CollectionReference;
    private var firestore = FirebaseFirestore.getInstance();
    private var order =Order();
    private lateinit var sp :SharedPreferences;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        sp = getSharedPreferences("orderDB", MODE_PRIVATE)
        db = firestore.collection("orders")
        val id = sp.getString("id", "failed").toString()
        if(id != "failed")
        {
            order.id = id;
            order.customerName = sp.getString("name", "").toString()
        }
        else
        {
            sp.edit().putString("id", id)
            sp.edit().putString("name", order.customerName)
        }
        db.addSnapshotListener { value, error ->
            if(error != null)
            {
                return@addSnapshotListener
            }

        }
    }
    private fun orderListener()
    {
        db.document(order.id).get().addOnSuccessListener { document ->
            if(document!=null)
            {
                val doc = document.get("status")
                if (doc == "ready") {
                    loadEditOrder()
                }
                else if (doc == "in_progress") {
                    loadMakingOrder()
                }
                else if (doc == "done") {
                    loadReady()
                }
                else
                {

                }
            }
        }
            .addOnFailureListener{ Toast.makeText(this,"ERROR",Toast.LENGTH_SHORT).show()
            }
    }
    private fun loadEditOrder()
    {
        setContentView(R.layout.edit_order_screen)
        findViewById<Button>(R.id.buttonCur)
        findViewById<EditText>(R.id.editTextTextPersonNameCur).setText(order.comment)
        findViewById<CheckBox>(R.id.editTextNumberCur2).isChecked = order.tahini
        findViewById<CheckBox>(R.id.checkBoxCur).isChecked = order.hummus
        findViewById<SeekBar>(R.id.editTextNumber10).progress = order.pickles
        findViewById<SeekBar>(R.id.editTextNumber10).setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener
        {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                TODO("Not yet implemented")
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                TODO("Not yet implemented")
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun loadMakingOrder()
    {

    }
    private fun loadReady()
    {

    }
    private fun newOrder()
    {
        setContentView(R.layout.new_order_layout)
        findViewById<EditText>(R.id.editTextTextPersonNameCur)
        findViewById<CheckBox>(R.id.checkBoxCur).isChecked = order.hummus
        findViewById<CheckBox>(R.id.editTextNumberCur2).isChecked = order.tahini

    }
}