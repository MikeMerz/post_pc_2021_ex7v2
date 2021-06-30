package huji.postpc.y2021.michaelmerzlyak.ex7

import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable
import java.util.*


class Order :Serializable{
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
    private var order =Order()
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
            newOrder()
        }
        else
        {
            sp.edit().putString("id", order.id).apply()
            sp.edit().putString("name", order.customerName).apply()
        }
        db.addSnapshotListener { value, error ->
            if(error != null)
            {
                return@addSnapshotListener
            }
            orderListener()
        }
        newOrder()
    }
    private fun orderListener()
    {
        db.document(order.id).get().addOnSuccessListener { document ->
            if(document!=null)
            {
                val doc = document.get("status")
                if (doc == "ready") {
                    loadReady()
                }
                else if (doc == "in-progress") {
                    loadMakingOrder()
                }
                else if (doc == "done") {
                    loadReady()
                }
                else if(doc == "waiting")
                {
                    setContentView(R.layout.edit_order_screen)
                    findViewById<EditText>(R.id.textView3).setText(document.get("customer_name").toString())
                    findViewById<CheckBox>(R.id.checkBoxCur).isChecked = document.get("hummus") as Boolean
                    findViewById<CheckBox>(R.id.editTextNumberCur2).isChecked = document.get("tahini") as Boolean
                    findViewById<SeekBar>(R.id.editTextNumber10).progress = document.get("pickles").toString().toInt()
                    findViewById<EditText>(R.id.editTextTextPersonNameCur).setText(document.get("comments").toString())
                    loadEditOrder()
                }
            }
        }
            .addOnFailureListener{
                newOrder()
                Toast.makeText(this,"ERROR",Toast.LENGTH_SHORT).show()
            }
    }
    private fun loadEditOrder()
    {
//        setContentView(R.layout.edit_order_screen)
//        findViewById<Button>(R.id.buttonCurDel)
//        findViewById<EditText>(R.id.editTextTextPersonNameCur).setText(order.comment)
//        findViewById<CheckBox>(R.id.editTextNumberCur2).isChecked = order.tahini
//        findViewById<CheckBox>(R.id.checkBoxCur).isChecked = order.hummus
//        findViewById<SeekBar>(R.id.editTextNumber10).progress = order.pickles
//        findViewById<EditText>(R.id.textView3).setText(order.customerName)
        findViewById<Button>(R.id.button).setOnClickListener{
            val finalDataSet = mapOf(
                "hummus" to findViewById<CheckBox>(R.id.checkBoxCur).isChecked,
                "tahini" to findViewById<CheckBox>(R.id.editTextNumberCur2).isChecked,
                "pickles" to findViewById<SeekBar>(R.id.editTextNumber10).progress,
                "comments" to findViewById<EditText>(R.id.editTextTextPersonNameCur).text.toString(),
                "customer_name" to findViewById<EditText>(R.id.textView3).text.toString(),
                "status" to "waiting"
            )
            order.hummus = findViewById<CheckBox>(R.id.checkBoxCur).isChecked
            order.tahini = findViewById<CheckBox>(R.id.editTextNumberCur2).isChecked
            order.comment = findViewById<EditText>(R.id.editTextTextPersonNameCur).text.toString()
            order.customerName = findViewById<EditText>(R.id.textView3).text.toString()
            order.status = "waiting"
            order.pickles = findViewById<SeekBar>(R.id.editTextNumber10).progress
            sp.edit().putString("username",findViewById<EditText>(R.id.textView3).text.toString()).apply()
            db.document(order.id).set(finalDataSet)
        }
        findViewById<Button>(R.id.buttonCurDel).setOnClickListener{
            sp.edit().putString("name",findViewById<EditText>(R.id.textView3).text.toString()).apply()
            db.document(order.id).delete()
            newOrder()

        }
        findViewById<SeekBar>(R.id.editTextNumber10).setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener
        {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                TODO("Not yet implemented")
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                TODO("Not yet implemented")
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                val curProgess = p0!!.progress
                if(curProgess==0)
                {
                    Toast.makeText(applicationContext,"No pickles",Toast.LENGTH_SHORT).show()
                }
                else if(curProgess==10)
                {
                    Toast.makeText(applicationContext,"Max amount of pickles",Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(applicationContext, "You chose $curProgess pickles", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }
    private fun loadMakingOrder()
    {
        setContentView(R.layout.order_proccesing_screen)
    }
    private fun loadReady()
    {
        setContentView(R.layout.order_ready_screen)
        findViewById<FloatingActionButton>(R.id.floatingActionButton).setOnClickListener{
            this.db.document(order.id).update(mapOf("status" to "done"))
            this.db.document(order.id).delete()
            newOrder()
        }
    }
    private fun newOrder()
    {
        setContentView(R.layout.new_order_layout)
        findViewById<EditText>(R.id.editTextTextPersonNameCur)
        findViewById<EditText>(R.id.textView3).setText(sp.getString("name",""))
        findViewById<Button>(R.id.buttonCur).setOnClickListener{view->
            val finalDataSet = mapOf(
                "hummus" to findViewById<CheckBox>(R.id.checkBoxCur).isChecked,
                "tahini" to findViewById<CheckBox>(R.id.editTextNumberCur2).isChecked,
                "pickles" to findViewById<SeekBar>(R.id.editTextNumber10).progress,
                "comments" to findViewById<EditText>(R.id.editTextTextPersonNameCur).text.toString(),
                "customer_name" to findViewById<EditText>(R.id.textView3).text.toString(),
                "status" to "waiting"
            )
            order.hummus = findViewById<CheckBox>(R.id.checkBoxCur).isChecked
            order.tahini = findViewById<CheckBox>(R.id.editTextNumberCur2).isChecked
            order.comment = findViewById<EditText>(R.id.editTextTextPersonNameCur).text.toString()
            order.customerName = findViewById<EditText>(R.id.textView3).text.toString()
            sp.edit().putString("name",findViewById<EditText>(R.id.textView3).text.toString()).apply()
            order.status = "waiting"
            order.pickles = findViewById<SeekBar>(R.id.editTextNumber10).progress
            sp.edit().putString("username",findViewById<EditText>(R.id.textView3).text.toString()).apply()
            db.document(order.id).set(finalDataSet)
//            orderListener()
        }


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(order.status=="waiting")
        {
            val userNameEdit = findViewById<EditText>(R.id.textView3)
            val hummusEdit = findViewById<CheckBox>(R.id.checkBoxCur).isChecked
            val tahiniEdit = findViewById<CheckBox>(R.id.editTextNumberCur2).isChecked
            val pickles2 = findViewById<SeekBar>(R.id.editTextNumber10).progress
            val comments = findViewById<EditText>(R.id.editTextTextPersonNameCur).text.toString()
            if(userNameEdit != null && hummusEdit!= null && tahiniEdit!= null && pickles2!=null && comments!=null )
            {
                order.customerName= userNameEdit.text.toString()
                order.comment  = comments
                order.hummus = hummusEdit
                order.tahini = tahiniEdit
                order.pickles = pickles2
            }
        }
        else
        {
            val userNameNew = findViewById<EditText>(R.id.textView3)
            val commentsNew = findViewById<EditText>(R.id.editTextTextPersonNameCur)
            val hummusNew = findViewById<CheckBox>(R.id.checkBoxCur)
            val tahiniNew = findViewById<CheckBox>(R.id.editTextNumberCur2)
            val pickles = findViewById<SeekBar>(R.id.editTextNumber10).progress
            if(userNameNew != null && commentsNew!= null && hummusNew!= null && tahiniNew!=null && pickles!=null )
            {
                order.customerName= userNameNew.text.toString()
                order.comment  = commentsNew.text.toString()
                order.hummus = hummusNew.isChecked
                order.tahini = tahiniNew.isChecked
                order.pickles = pickles
            }
        }

//        else
//        {
//            val userNameEdit = findViewById<EditText>(R.id.textView3)
//            val hummusEdit = findViewById<CheckBox>(R.id.checkBoxCur).isChecked
//            val tahiniEdit = findViewById<CheckBox>(R.id.editTextNumberCur2).isChecked
//            val pickles2 = findViewById<SeekBar>(R.id.editTextNumber10).progress
//            val comments = findViewById<EditText>(R.id.editTextTextPersonNameCur).text.toString()
//            if(userNameEdit != null && hummusEdit!= null && tahiniEdit!= null && pickles2!=null && comments!=null )
//            {
//                order.customerName= userNameEdit.text.toString()
//                order.comment  = comments
//                order.hummus = hummusEdit
//                order.tahini = tahiniEdit
//                order.pickles = pickles2
//            }
//        }
        outState.putSerializable("Order",order)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        order = savedInstanceState.getSerializable("Order") as Order
    }
    fun getOrderForTesting(): Order {return order}
    override fun onResume() {
        super.onResume()
        orderListener()
    }
}