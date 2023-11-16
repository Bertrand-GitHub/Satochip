package fr.toporin.satochip.util

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import fr.toporin.satochip.viewmodel.ScanViewModel

/**
 * A Drawable that handles displaying a QR Code's data and a bounding box around the QR code.
 */
class QrCodeDrawable(scanViewModel: ScanViewModel) : Drawable() {
    private val boundingRectPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.YELLOW
        strokeWidth = 5F
        alpha = 200
    }

    private val contentRectPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.YELLOW
        alpha = 255
    }

    private val contentTextPaint = Paint().apply {
        color = Color.DKGRAY
        alpha = 255
        textSize = 36F
    }

    private val scanViewModel= scanViewModel
    private val contentPadding = 25

    override fun draw(canvas: Canvas) {
        val qrContent = scanViewModel.qrContent.value ?: ""
        val textWidth = contentTextPaint.measureText(qrContent).toInt()
        scanViewModel.boundingRect?.let { rect ->
            canvas.drawRect(rect, boundingRectPaint)

            val contentRect = Rect(
                rect.left,
                rect.bottom + contentPadding / 2,
                rect.left + textWidth + contentPadding * 2,
                rect.bottom + contentTextPaint.textSize.toInt() + contentPadding
            )

            canvas.drawRect(contentRect, contentRectPaint)
            canvas.drawText(
                qrContent,
                (rect.left + contentPadding).toFloat(),
                (rect.bottom + contentPadding * 2).toFloat(),
                contentTextPaint
            )
        }
    }

    override fun setAlpha(alpha: Int) {
        boundingRectPaint.alpha = alpha
        contentRectPaint.alpha = alpha
        contentTextPaint.alpha = alpha
    }

    override fun setColorFilter(colorFiter: ColorFilter?) {
        boundingRectPaint.colorFilter = colorFilter
        contentRectPaint.colorFilter = colorFilter
        contentTextPaint.colorFilter = colorFilter
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("PixelFormat.TRANSLUCENT", "android.graphics.PixelFormat")
    )
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}