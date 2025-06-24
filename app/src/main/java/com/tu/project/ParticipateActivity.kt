package com.tu.project

import com.tu.project.ContestActivity
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ParticipateActivity : AppCompatActivity() {

    // 카메라용 URI
    private lateinit var photoUri: Uri
    // 사용자가 찍은 사진 또는 선택한 사진을 업로드할 URI
    private var selectedImageUri: Uri? = null

    // ActivityResult 런처들
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private lateinit var requestCameraPermission: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // 엣지-투-엣지
        setContentView(R.layout.activity_participate)

        // 시스템 바 인셋 처리
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val s = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(s.left, s.top, s.right, s.bottom)
            insets
        }

        // 닫기
        findViewById<ImageButton>(R.id.btnClose).setOnClickListener { finish() }

        findViewById<TextView>(R.id.btnConfirm).setOnClickListener {
            // TODO: 제목, 내용 등 유효성 검사 필요시 추가

            val intent = Intent(this, ContestActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 갤러리에서 이미지 선택 콜백
        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                showPreview(uri)
            }
        }

        // 카메라 권한 요청 콜백
        requestCameraPermission = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) launchCamera()
            else Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }

        // 카메라 촬영 콜백
        takePictureLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success ->
            if (success) {
                selectedImageUri = photoUri
                showPreview(photoUri)
            }
        }

        // “사진 선택” 영역 클릭 → 갤러리 열기
        findViewById<FrameLayout>(R.id.flSelectPhoto).setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // “사진 찍기” 버튼 클릭
        findViewById<MaterialButton>(R.id.btnCapture).setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED -> launchCamera()
                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ->
                    Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                else -> requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }

        // “업로드” 버튼 클릭
        findViewById<MaterialButton>(R.id.btnUpload).setOnClickListener {
            selectedImageUri?.let { uri ->
                val title = findViewById<EditText>(R.id.etTitle).text.toString()
                val content = findViewById<EditText>(R.id.etContent).text.toString()

                if (title.isBlank() || content.isBlank()) {
                    Toast.makeText(this, "제목과 내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r", null) ?: return@setOnClickListener
                val file = File(cacheDir, "upload_image.jpg")
                val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
                val outputStream = FileOutputStream(file)
                inputStream.copyTo(outputStream)

                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
                val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
                val contentPart = content.toRequestBody("text/plain".toMediaTypeOrNull())

                val sharedPref = getSharedPreferences("auth", MODE_PRIVATE)
                val accessToken = sharedPref.getString("accessToken", null)

                RetrofitClient.instance.uploadPost(
                    token = "Bearer $accessToken",
                    title = titlePart,
                    content = contentPart,
                    image = body
                ).enqueue(object : Callback<UploadResponse> {
                    override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(this@ParticipateActivity, "업로드 성공!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@ParticipateActivity, "업로드 실패: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                        Toast.makeText(this@ParticipateActivity, "에러: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })

            } ?: Toast.makeText(this, "먼저 이미지를 선택하거나 촬영하세요.", Toast.LENGTH_SHORT).show()
        }

        // “삭제” 버튼 클릭
        findViewById<MaterialButton>(R.id.btnDelete).setOnClickListener {
            findViewById<ImageView>(R.id.ivPreview).visibility = android.view.View.GONE
            findViewById<TextView>(R.id.tvSelectPrompt).visibility = android.view.View.VISIBLE
            selectedImageUri = null
        }
    }

    /** 미리보기 ImageView 에 Glide 로 띄워주고 프롬프트 숨기기 */
    private fun showPreview(uri: Uri) {
        val iv = findViewById<ImageView>(R.id.ivPreview)
        Glide.with(this)
            .load(uri)
            .centerCrop()
            .into(iv)

        iv.visibility = android.view.View.VISIBLE
        findViewById<TextView>(R.id.tvSelectPrompt).visibility = android.view.View.GONE
    }

    /** 기본 카메라 앱 실행 */
    private fun launchCamera() {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val photoFile = File.createTempFile("IMG_$timestamp", ".jpg", storageDir)

        photoUri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            photoFile
        )
        takePictureLauncher.launch(photoUri)
    }
}
