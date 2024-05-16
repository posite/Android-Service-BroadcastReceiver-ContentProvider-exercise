package com.posite.broadcastreceiverex.contact

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.posite.broadcastreceiverex.databinding.ActivityContactsBinding

class ContactsActivity : AppCompatActivity() {
    private val binding by lazy { ActivityContactsBinding.inflate(layoutInflater) }
    private val adapter by lazy { ContactsAdapter() }
    private val contacts = ArrayList<Contacts>()
    private lateinit var requestLauncher: ActivityResultLauncher<Intent>
    private val permissions: ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("권한", "권한 없음")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("권한", "권한 없음")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                permissions.toTypedArray(),
                1001
            )
        }
        binding.contactsRv.adapter = adapter
        binding.contactsRv.layoutManager = LinearLayoutManager(this)
        requestLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    Log.d("ContractsActivity", "URI: ${it.data!!.data!!}")
                    val cursor = contentResolver.query(
                        it.data!!.data!!,
                        arrayOf(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                        ),
                        null,
                        null,
                        null
                    )
                    if (cursor!!.moveToFirst()) {
                        val firstName = cursor.getString(0)
                        val firstNumber = cursor.getString(1)
                        contacts.add(Contacts(firstName, firstNumber))

                    }
                    adapter.submitList(contacts.toList())
                    Log.d("ContactsArrayList", contacts.toString())
                }
            }

        binding.contractsBtn.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            requestLauncher.launch(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            binding.contractsBtn.isEnabled = true
        }
    }
}