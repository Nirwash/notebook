package com.nirwash.test7sqlite

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import coil.load
import com.nirwash.test7sqlite.databinding.ActivityEditBinding
import com.nirwash.test7sqlite.db.MyDbManager
import com.nirwash.test7sqlite.db.MyIntentConstants
import java.text.SimpleDateFormat
import java.util.*

class EditActivity : AppCompatActivity() {

    var tempImageUri = "empty"
    lateinit var binding: ActivityEditBinding
    val myDbManager = MyDbManager(this)
    val imageRequestCode = 10
    var id = 0
    var isEditState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getMyIntents()
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == imageRequestCode) {
            data?.data?.also { documentUri ->
                contentResolver.takePersistableUriPermission(
                    documentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                binding.imgPhoto.setImageURI(data?.data)
                tempImageUri = data?.data.toString()
            }

        }
    }

    fun onClickAddImage(view: android.view.View) {
        binding.apply {
            mainImageLayout.visibility = View.VISIBLE
            bAddImg.visibility = View.GONE
        }
    }

    fun onClickDeleteImage(view: android.view.View) {
        binding.apply {
            mainImageLayout.visibility = View.GONE
            bAddImg.visibility = View.VISIBLE
        }
        tempImageUri = "empty"
    }

    fun onClickChooseImage(view: android.view.View) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        intent.flags = Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        startActivityForResult(intent, imageRequestCode)
    }


    fun onClickSave(view: android.view.View) {
        val myTitle = binding.edTitle.text.toString()
        val myDesc = binding.tvDescription.text.toString()
        if (myTitle != "" && myDesc != "") {
            if (isEditState) {
                myDbManager.editItemDb(myTitle, myDesc, tempImageUri, id, getCurrentTime())
            } else {
                myDbManager.insertToDb(myTitle, myDesc, tempImageUri, getCurrentTime())
            }
            finish()
        }
    }

    fun getMyIntents() {
        val i = intent
        if (i != null) {

            if (i.getStringExtra(MyIntentConstants.I_TITLE_KET) != null) {
                binding.apply {
                    bAddImg.visibility = View.GONE
                    bEditText.visibility = View.VISIBLE
                    bSave.visibility = View.GONE
                    edTitle.setText(i.getStringExtra(MyIntentConstants.I_TITLE_KET))
                    edTitle.isEnabled = false
                    tvDescription.setText(i.getStringExtra(MyIntentConstants.I_DESC_KET))
                    tvDescription.isEnabled = false
                }
                id = i.getIntExtra(MyIntentConstants.I_ID_KET, 0)
                isEditState = true


                if (i.getStringExtra(MyIntentConstants.I_URI_KET) != "empty") {
                    tempImageUri = i.getStringExtra(MyIntentConstants.I_URI_KET)!!
                    binding.apply {
                        mainImageLayout.visibility = View.VISIBLE
                        imgPhoto.setImageURI(Uri.parse(tempImageUri))
                        bEditImage.visibility = View.GONE
                        bDeleteImage.visibility = View.GONE
                    }
                }
            }
        }
    }

    fun onEditEnable(view: android.view.View) {
        binding.apply {
            edTitle.isEnabled = true
            tvDescription.isEnabled = true
            bEditText.visibility = View.GONE
            bSave.visibility = View.VISIBLE
            if (tempImageUri == "empty") bAddImg.visibility = View.VISIBLE
            bEditImage.visibility = View.VISIBLE
            bDeleteImage.visibility = View.VISIBLE

        }
    }

    private fun getCurrentTime(): String{
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yy kk:mm", Locale.getDefault())
        return formatter.format(time)

    }
}