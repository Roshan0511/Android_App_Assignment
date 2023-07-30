package com.roshan.jha.attendance

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.roshan.jha.attendance.databinding.ActivityMainBinding
import com.roshan.jha.attendance.databinding.ImageChooserDialogBinding
import com.roshan.jha.attendance.mvp.ApiContract
import com.roshan.jha.attendance.mvp.ApiPresenter
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity(), ApiContract.View {

    private lateinit var binding: ActivityMainBinding
    private lateinit var imageUri: Uri
    private lateinit var imageChooserDialog: AlertDialog

    private var presenter: ApiContract.Presenter = ApiPresenter(this)

    private val PERMISSION_REQUEST_CODE = 101
    private var type = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getAddress()

        clickEvents()
    }




    /*
    * Click listeners
    * */
    private fun clickEvents() {
        binding.uploadImage.setOnClickListener {
            openImageChooser()
        }


        binding.markAttendance.setOnClickListener {
            val param = JSONObject()

            val date = getCurrentDateTime()
            val dateInString = date.toString("dd-MM-yyyy HH:mm:ss")

            try {
                param.put("latlng", "${binding.latitude},${binding.longitude}")
                param.put("location", binding.address)
                param.put("remark", binding.remarks.text!!.toString().trim())
                param.put("actDate", dateInString)
            } catch (e: JSONException) {
                Log.e("error", "clickEvents: ${e.localizedMessage}")
            }

            showProgress()
            presenter.markAttendance(param)
        }
    }


    private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }





    // Click image from camera
    private val launcherCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        binding.selectedImage.setImageURI(null)
        binding.selectedImage.setImageURI(imageUri)

        uploadImage(File(getPathOfSavedImage(uriToBitmap(imageUri)!!)!!))

        imageChooserDialog.dismiss()
    }



    // Get image from gallery
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
        binding.selectedImage.setImageURI(null)
        binding.selectedImage.setImageURI(it)

        uploadImage(File(getPathOfSavedImage(uriToBitmap(it!!)!!)!!))

        imageChooserDialog.dismiss()
    }






    /*
    * Validate permission result for android 13 device
    * */
    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        var isGranted = true
        for(items in it) {
            if (!items.value) {
                isGranted = false
            }
        }

        if (isGranted) {
            if (type == "C") {
                openCamera()
            } else {
                getContent.launch("image/*")
            }
        } else {
            Toast.makeText(this@MainActivity, "OOPS, Permission Denied", Toast.LENGTH_SHORT).show()
        }

        imageChooserDialog.dismiss()
    }








    /*
    * Show alert dialog
    * */
    private fun openImageChooser() {
        val imageBinding: ImageChooserDialogBinding = ImageChooserDialogBinding.inflate(layoutInflater)
        imageChooserDialog = AlertDialog.Builder(this@MainActivity).create()
        imageChooserDialog.setView(imageBinding.root)

        imageBinding.cameraLl.setOnClickListener {
            type = "C"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permission = arrayOf(CAMERA)

                if (checkImagePermission(permission[0])) {
                    openCamera()
                } else {
                    permissionLauncher.launch(permission)
                }
            } else {
                if (!checkPermission()) {
                    requestPermission()
                } else {
                    openCamera()
                }
            }
        }

        imageBinding.galleryLl.setOnClickListener {
            type = "G"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permission = arrayOf(READ_MEDIA_IMAGES)

                if (checkImagePermission(permission[0])) {
                    getContent.launch("image/*")
                } else {
                    permissionLauncher.launch(permission)
                }
            } else {
                if (!checkPermission()) {
                    requestPermission()
                } else {
                    getContent.launch("image/*")
                }
            }
        }

        imageChooserDialog = AlertDialog.Builder(this).create()
        imageChooserDialog.setView(imageBinding.root)
        imageChooserDialog.show()
    }







    /*
    * Open camera
    * */
    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "MyPicture")
        values.put(
            MediaStore.Images.Media.DESCRIPTION,
            "Photo taken on " + System.currentTimeMillis()
        )
        imageUri = applicationContext.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )!!
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        launcherCamera.launch(intent)
    }





    /*
    * Check permission for android 13 device
    * */
    private fun checkImagePermission(permission: String) : Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }




    /*
    * Check permission for below android 13 device
    * */
    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this@MainActivity, WRITE_EXTERNAL_STORAGE)
        val result1 = ContextCompat.checkSelfPermission(this@MainActivity, READ_EXTERNAL_STORAGE)
        val result2 =
            ContextCompat.checkSelfPermission(this@MainActivity, CAMERA)
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                WRITE_EXTERNAL_STORAGE,
                READ_EXTERNAL_STORAGE,
                CAMERA
            ),
            PERMISSION_REQUEST_CODE
        )
    }







    /*
    * validate permission result
    * */

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty()) {
                val readAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                val cameraAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED
                if (readAccepted && writeAccepted && cameraAccepted) {
                    Toast.makeText(
                        this@MainActivity,
                        "Thank you, Permission Granted!",
                        Toast.LENGTH_SHORT
                    ).show()
                    imageChooserDialog.dismiss()

                    if (type == "C") {
                        openCamera()
                    } else {
                        getContent.launch("image/*")
                    }
                } else {
                    Toast.makeText(this@MainActivity, "OOPS, Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
            1 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                    getAddress()
                } else {
                    Toast.makeText(this@MainActivity, "OOPS, Location Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }







    /*
    * Upload image
    * */

    private fun uploadImage(file: File) {
        showProgress()
        presenter.uploadImage(file)
    }









    /*
    * Convert uri to bitmap and then get the file path from bitmap
    * */
    private fun uriToBitmap(selectedFileUri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(selectedFileUri, "r")
            val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun getPathOfSavedImage(finalBitmap: Bitmap): String? {
        val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val myDir = File(root.absolutePath + File.separator)
        if (!myDir.exists()) myDir.mkdir()
        var file: File? = null
        try {
            file = File(root.absolutePath + File.separator + System.currentTimeMillis() + ".jpg")
            if (file.exists()) file.delete()
            file.createNewFile()
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 50, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file!!.path
    }








    /*
    * Get the user's location and display them
    * */

    private fun getAddress() {
        val locationPermission = ContextCompat.checkSelfPermission(
            this, ACCESS_FINE_LOCATION
        )
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(ACCESS_FINE_LOCATION), 1)
            return
        }

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Get the current location
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        val geocoder = Geocoder(this)

        // Get the address from the geocoder
        var address = ""
        try {
            val addresses = geocoder.getFromLocation(location!!.latitude, location.longitude, 1)
            address = addresses?.get(0)?.getAddressLine(0) ?: "null"
        } catch (e: Exception) {
            Log.e("Assignment", "Error getting address", e)
        }


        binding.longitude.text = "${location!!.longitude}"
        binding.latitude.text = "${location!!.latitude}"

        binding.address.text = "$address"

        Log.e("Assignment", "address $address")
    }








    /*
    * Show progress bar
    * */
    private fun showProgress() {
        binding.progressBar.visibility = View.VISIBLE
        binding.markAttendance.isEnabled = false
    }


    /*
    * Hide progress bar
    * */
    private fun hideProgress() {
        binding.progressBar.visibility = View.GONE
        binding.markAttendance.isEnabled = true
        if(!binding.remarks.text.isNullOrEmpty()) {
            binding.remarks.text = null
        }
    }






    /*
    * APi response
    * */
    override fun onJsonAPIResponse(jsonObject: JSONObject?, request_code: Int, status_code: Int) {
        hideProgress()
        if (jsonObject != null) {
            if (request_code == 2) {
                val id = jsonObject.getString("id")
                Toast.makeText(this@MainActivity, "Attendance marked successfully", Toast.LENGTH_SHORT).show()
                Toast.makeText(this@MainActivity, "ID: $id", Toast.LENGTH_SHORT).show()
            }

            if (request_code == 1) {
                Log.d("json", "onJsonAPIResponse: ${jsonObject.toString()}")
                Toast.makeText(this@MainActivity, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }


    /*
    * Api failure
    * */
    override fun onAPIFailure(jsonObject: Any?, error_msg: String?) {
        hideProgress()
        Toast.makeText(this@MainActivity, "Something went wrong!! $error_msg", Toast.LENGTH_SHORT).show()
    }
}