package edu.cit.rivero.workspace

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import edu.cit.rivero.workspace.api.ApiClient
import edu.cit.rivero.workspace.models.ApiResponse
import edu.cit.rivero.workspace.models.Space
import edu.cit.rivero.workspace.models.SpaceRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Handles both Create and Edit operations for a Space.
 * Pass EXTRA_SPACE_ID to indicate edit mode (the existing space is loaded via intent extras).
 */
class SpaceFormActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SPACE_ID = "FORM_SPACE_ID"
        const val EXTRA_SPACE_NAME = "FORM_SPACE_NAME"
        const val EXTRA_SPACE_LOCATION = "FORM_SPACE_LOCATION"
        const val EXTRA_SPACE_TYPE = "FORM_SPACE_TYPE"
        const val EXTRA_SPACE_CAPACITY = "FORM_SPACE_CAPACITY"
        const val EXTRA_SPACE_RATE = "FORM_SPACE_RATE"
        const val EXTRA_SPACE_AVAILABLE = "FORM_SPACE_AVAILABLE"
        const val EXTRA_SPACE_DESCRIPTION = "FORM_SPACE_DESCRIPTION"
        const val EXTRA_SPACE_AMENITIES = "FORM_SPACE_AMENITIES"
        const val EXTRA_SPACE_UTILITIES = "FORM_SPACE_UTILITIES"
        const val EXTRA_SPACE_CHECK_IN = "FORM_SPACE_CHECK_IN"
        const val EXTRA_SPACE_POLICY = "FORM_SPACE_POLICY"

        private val SPACE_TYPES = listOf("Pod", "Meeting Room", "Studio", "Boardroom", "Hot Desk")
    }

    private var editingSpaceId: String? = null

    private lateinit var tvFormTitle: TextView
    private lateinit var etName: EditText
    private lateinit var etLocation: EditText
    private lateinit var spinnerType: Spinner
    private lateinit var etCapacity: EditText
    private lateinit var etRate: EditText
    private lateinit var switchAvailable: SwitchMaterial
    private lateinit var etDescription: EditText
    private lateinit var etAmenities: EditText
    private lateinit var etUtilities: EditText
    private lateinit var etCheckIn: EditText
    private lateinit var etPolicy: EditText
    private lateinit var btnSubmit: Button
    private lateinit var progressBar: ProgressBar

    // Photo picker — picks multiple images
    private val pickImages = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) uploadImages(uris)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_space_form)

        tvFormTitle = findViewById(R.id.tvFormTitle)

        // Back button
        findViewById<ImageButton>(R.id.btnBackSpaceForm)?.setOnClickListener { finish() }

        etName = findViewById(R.id.etSpaceName)
        etLocation = findViewById(R.id.etSpaceLocation)
        spinnerType = findViewById(R.id.spinnerSpaceType)
        etCapacity = findViewById(R.id.etSpaceCapacity)
        etRate = findViewById(R.id.etSpaceRate)
        switchAvailable = findViewById(R.id.switchAvailable)
        etDescription = findViewById(R.id.etSpaceDescription)
        etAmenities = findViewById(R.id.etSpaceAmenities)
        etUtilities = findViewById(R.id.etSpaceUtilities)
        etCheckIn = findViewById(R.id.etCheckInWindow)
        etPolicy = findViewById(R.id.etCancellationPolicy)
        btnSubmit = findViewById(R.id.btnSubmitSpace)
        progressBar = findViewById(R.id.progressBarSpaceForm)

        // Populate the type spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, SPACE_TYPES)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = adapter

        // Check if we're in edit mode
        editingSpaceId = intent.getStringExtra(EXTRA_SPACE_ID)
        if (editingSpaceId != null) {
            tvFormTitle.text = "Edit Workspace"
            btnSubmit.text = "Update Workspace"
            populateFormFromIntent()
        }

        btnSubmit.setOnClickListener { submitForm() }
    }

    private fun populateFormFromIntent() {
        etName.setText(intent.getStringExtra(EXTRA_SPACE_NAME) ?: "")
        etLocation.setText(intent.getStringExtra(EXTRA_SPACE_LOCATION) ?: "")
        etCapacity.setText(intent.getIntExtra(EXTRA_SPACE_CAPACITY, 1).toString())
        etRate.setText(intent.getDoubleExtra(EXTRA_SPACE_RATE, 0.0).toString())
        switchAvailable.isChecked = intent.getBooleanExtra(EXTRA_SPACE_AVAILABLE, true)
        etDescription.setText(intent.getStringExtra(EXTRA_SPACE_DESCRIPTION) ?: "")
        etAmenities.setText(intent.getStringExtra(EXTRA_SPACE_AMENITIES) ?: "")
        etUtilities.setText(intent.getStringExtra(EXTRA_SPACE_UTILITIES) ?: "")
        etCheckIn.setText(intent.getStringExtra(EXTRA_SPACE_CHECK_IN) ?: "")
        etPolicy.setText(intent.getStringExtra(EXTRA_SPACE_POLICY) ?: "")

        // Pre-select the spinner type
        val type = intent.getStringExtra(EXTRA_SPACE_TYPE) ?: "Pod"
        val typeIdx = SPACE_TYPES.indexOf(type).takeIf { it >= 0 } ?: 0
        spinnerType.setSelection(typeIdx)
    }

    private fun submitForm() {
        val name = etName.text.toString().trim()
        val location = etLocation.text.toString().trim()
        val capacityStr = etCapacity.text.toString().trim()
        val rateStr = etRate.text.toString().trim()

        if (name.isEmpty() || location.isEmpty() || capacityStr.isEmpty() || rateStr.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields (*)", Toast.LENGTH_SHORT).show()
            return
        }

        val request = SpaceRequest(
            name = name,
            location = location,
            type = spinnerType.selectedItem.toString(),
            capacity = capacityStr.toIntOrNull() ?: 1,
            hourlyRate = rateStr.toDoubleOrNull() ?: 0.0,
            available = switchAvailable.isChecked,
            description = etDescription.text.toString().trim().ifEmpty { null },
            amenities = etAmenities.text.toString().trim().ifEmpty { null },
            utilities = etUtilities.text.toString().trim().ifEmpty { null },
            checkInWindow = etCheckIn.text.toString().trim().ifEmpty { null },
            cancellationPolicy = etPolicy.text.toString().trim().ifEmpty { null }
        )

        setLoading(true)
        val spaceId = editingSpaceId
        if (spaceId != null) {
            ApiClient.instance.updateSpace(spaceId, request).enqueue(spaceCallback("updated"))
        } else {
            ApiClient.instance.createSpace(request).enqueue(spaceCallback("created"))
        }
    }

    private fun spaceCallback(action: String): Callback<ApiResponse<Space>> {
        return object : Callback<ApiResponse<Space>> {
            override fun onResponse(call: Call<ApiResponse<Space>>, response: Response<ApiResponse<Space>>) {
                setLoading(false)
                if (response.isSuccessful && response.body()?.success == true) {
                    val savedSpace = response.body()?.data
                    Toast.makeText(this@SpaceFormActivity, "Workspace $action!", Toast.LENGTH_SHORT).show()

                    // If this was a create AND we have images, offer to upload them now
                    if (editingSpaceId == null && savedSpace != null) {
                        // Prompt to upload images for the newly created space
                        editingSpaceId = savedSpace.id
                        Toast.makeText(this@SpaceFormActivity, "Space created! You can now upload images via the Spaces list.", Toast.LENGTH_LONG).show()
                    }

                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(
                        this@SpaceFormActivity,
                        response.body()?.error?.message ?: "Operation failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<Space>>, t: Throwable) {
                setLoading(false)
                Toast.makeText(this@SpaceFormActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Called by AdminDashboardActivity when the "Images" button is tapped.
     * Opens the system image picker and uploads selected files to the backend.
     */
    fun launchImagePicker() {
        pickImages.launch("image/*")
    }

    private fun uploadImages(uris: List<Uri>) {
        val id = editingSpaceId ?: return
        setLoading(true)

        val parts = uris.mapNotNull { uri ->
            try {
                val inputStream = contentResolver.openInputStream(uri) ?: return@mapNotNull null
                val bytes = inputStream.readBytes()
                inputStream.close()
                val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
                val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
                MultipartBody.Part.createFormData("file", "image_${System.currentTimeMillis()}.jpg", requestBody)
            } catch (e: Exception) {
                null
            }
        }

        if (parts.isEmpty()) {
            setLoading(false)
            Toast.makeText(this, "Could not read images", Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.instance.uploadSpaceImages(id, parts).enqueue(object : Callback<ApiResponse<Space>> {
            override fun onResponse(call: Call<ApiResponse<Space>>, response: Response<ApiResponse<Space>>) {
                setLoading(false)
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@SpaceFormActivity, "${parts.size} image(s) uploaded!", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                } else {
                    Toast.makeText(this@SpaceFormActivity, "Upload failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<Space>>, t: Throwable) {
                setLoading(false)
                Toast.makeText(this@SpaceFormActivity, "Upload error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        btnSubmit.isEnabled = !loading
    }
}
