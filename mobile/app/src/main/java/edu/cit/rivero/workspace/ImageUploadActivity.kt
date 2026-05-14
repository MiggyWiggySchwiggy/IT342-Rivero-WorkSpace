package edu.cit.rivero.workspace

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import edu.cit.rivero.workspace.api.ApiClient
import edu.cit.rivero.workspace.models.ApiResponse
import edu.cit.rivero.workspace.models.Space
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Standalone image upload screen launched from the admin space list.
 */
class ImageUploadActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SPACE_ID = "UPLOAD_SPACE_ID"
        const val EXTRA_SPACE_NAME = "UPLOAD_SPACE_NAME"
    }

    private lateinit var spaceId: String
    private var selectedUris: List<Uri> = emptyList()

    private lateinit var tvSpaceName: TextView
    private lateinit var tvSelectedCount: TextView
    private lateinit var btnPickImages: Button
    private lateinit var btnUpload: Button
    private lateinit var progressBar: ProgressBar

    private val pickImages = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        selectedUris = uris
        tvSelectedCount.text = if (uris.isEmpty()) "No images selected"
        else "${uris.size} image(s) selected"
        btnUpload.isEnabled = uris.isNotEmpty()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_upload)

        spaceId = intent.getStringExtra(EXTRA_SPACE_ID) ?: run { finish(); return }
        val spaceName = intent.getStringExtra(EXTRA_SPACE_NAME) ?: "Space"

        tvSpaceName = findViewById(R.id.tvUploadSpaceName)
        tvSelectedCount = findViewById(R.id.tvSelectedCount)
        btnPickImages = findViewById(R.id.btnPickImages)
        btnUpload = findViewById(R.id.btnUploadSelected)
        progressBar = findViewById(R.id.progressBarUpload)

        // Back button
        findViewById<android.widget.ImageButton>(R.id.btnBackUpload)?.setOnClickListener { finish() }

        tvSpaceName.text = spaceName

        btnPickImages.setOnClickListener { pickImages.launch("image/*") }
        btnUpload.setOnClickListener { uploadImages() }
    }

    private fun uploadImages() {
        if (selectedUris.isEmpty()) return
        setLoading(true)

        val parts = selectedUris.mapNotNull { uri ->
            try {
                val inputStream = contentResolver.openInputStream(uri) ?: return@mapNotNull null
                val bytes = inputStream.readBytes()
                inputStream.close()
                val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
                val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
                MultipartBody.Part.createFormData("file", "upload_${System.currentTimeMillis()}.jpg", requestBody)
            } catch (e: Exception) {
                null
            }
        }

        if (parts.isEmpty()) {
            setLoading(false)
            Toast.makeText(this, "Could not read selected images", Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.instance.uploadSpaceImages(spaceId, parts)
            .enqueue(object : Callback<ApiResponse<Space>> {
                override fun onResponse(
                    call: Call<ApiResponse<Space>>,
                    response: Response<ApiResponse<Space>>
                ) {
                    setLoading(false)
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(
                            this@ImageUploadActivity,
                            "${parts.size} image(s) uploaded successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(
                            this@ImageUploadActivity,
                            response.body()?.error?.message ?: "Upload failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Space>>, t: Throwable) {
                    setLoading(false)
                    Toast.makeText(this@ImageUploadActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        btnUpload.isEnabled = !loading && selectedUris.isNotEmpty()
        btnPickImages.isEnabled = !loading
    }
}
