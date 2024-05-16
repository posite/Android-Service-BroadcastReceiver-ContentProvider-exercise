package com.posite.broadcastreceiverex.gallery

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.posite.broadcastreceiverex.R
import com.posite.broadcastreceiverex.databinding.ActivityGalleryBinding

class GalleryActivity : AppCompatActivity() {
    private val binding by lazy { ActivityGalleryBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val galleryResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            try {
                val size = calcualteSampleSize(
                    it.data!!.data!!,
                    resources.getDimensionPixelSize(R.dimen.imgSize),
                    resources.getDimensionPixelSize(R.dimen.imgSize)
                )
                Log.d("img", "size: $size")
                val option = BitmapFactory.Options()
                option.inSampleSize = size
                var inputStream = contentResolver.openInputStream(it.data!!.data!!)
                val bitmap = BitmapFactory.decodeStream(inputStream, null, option)
                inputStream!!.close()
                inputStream = null
                bitmap?.let { img ->
                    binding.profileImg.setImageBitmap(img)
                } ?: let {
                    Log.d("img", "img 없음")
                }
            } catch (e: Exception) {
                Log.e("갤러리", "갤러리에서 이미지를 가져오는데 실패했습니다.")
            }
        }
        binding.galleryBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            galleryResult.launch(intent)
        }
    }

    private fun calcualteSampleSize(uri: Uri, width: Int, height: Int): Int {
        val option = BitmapFactory.Options()
        option.inJustDecodeBounds = true
        BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, option)
        val imageWidth = option.outWidth
        val imageHeight = option.outHeight
        var inSampleSize = 1
        if (imageWidth > width || imageHeight > height) {
            val halfWidth = imageWidth / 2
            val halfHeight = imageHeight / 2
            while (halfWidth / inSampleSize >= width && halfHeight / inSampleSize >= height) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}