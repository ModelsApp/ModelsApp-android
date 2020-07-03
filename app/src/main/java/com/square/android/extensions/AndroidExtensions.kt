package com.square.android.extensions

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.location.Location
import android.net.Uri
import android.os.Build
import android.text.*
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.webkit.URLUtil
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.App
import com.square.android.R
import com.square.android.R.color.placeholder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import java.io.ByteArrayOutputStream

private const val PREFIX_METER = "m"
private const val PREFIX_KILOMETER = "km"


fun Int.toHourStr(): String{
    return if(this < 10){
        "0$this"
    } else{ this.toString() }.toString().plus(":00")
}

fun String.toHourInt(): Int{
    return this.replace(":00", "").toInt()
}

fun ImageView.loadImageForIcon(url: String) {
    if (URLUtil.isValidUrl(url)) {
        Picasso.get()
                .load(url)
                .into(this)
    }
}

fun TextView.setHighLightedText(textToHighlight: String, ignoreCase: Boolean = true, @ColorRes textColorRes: Int = android.R.color.black) {

    val tvt = if(ignoreCase){
        this.text.toString().toLowerCase()
    } else{
        this.text.toString()
    }

    var ofe = tvt.indexOf(textToHighlight, 0)
    val wordToSpan = SpannableString(this.text)
    var ofs = 0
    while (ofs < tvt.length && ofe != -1) {
        if(ignoreCase){
            ofe = tvt.toLowerCase().indexOf(textToHighlight.toLowerCase(), ofs)
        } else{
            ofe = tvt.indexOf(textToHighlight, ofs)
        }

        if (ofe == -1){
            break
        }
        else {
            wordToSpan.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, textColorRes)), ofe, ofe + textToHighlight.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            this.setText(wordToSpan, TextView.BufferType.SPANNABLE)
        }
        ofs = ofe + 1
    }
}

fun ImageView.loadImage(url: String?,
                        @ColorRes placeholder: Int = R.color.placeholder,
                        roundedCornersRadiusPx: Int = 0,
                        whichCornersToRound: List<RoundedCornersTransformation.CornerType> = listOf(RoundedCornersTransformation.CornerType.ALL) ) {

    val cornerTransformations: MutableList<Transformation> = mutableListOf()
    for(cornerType in whichCornersToRound){
        cornerTransformations.add(RoundedCornersTransformation(roundedCornersRadiusPx,0, cornerType))
    }

    if (URLUtil.isValidUrl(url)) {
        Picasso.get()
                .load(url)
                .fit()
                .centerCrop()
                .transform(cornerTransformations)
                .placeholder(placeholder)
                .into(this)
    } else{
        Picasso.get()
                .load(R.drawable.placeholder)
                .fit()
                .centerCrop()
                .transform(cornerTransformations)
                .into(this)
    }
}

fun ImageView.loadImage(uri: Uri,
                        @ColorRes placeholder: Int = R.color.placeholder,
                        roundedCornersRadiusPx: Int = 0,
                        withoutCropping: Boolean = false,
                        whichCornersToRound: List<Transformation> = listOf(RoundedCornersTransformation(roundedCornersRadiusPx,0,RoundedCornersTransformation.CornerType.ALL))) {
        val creator = Picasso.get()
                .load(uri)
                .transform(whichCornersToRound)
                .fit()
                .placeholder(placeholder)

    if (!withoutCropping){
        creator.centerCrop()
    } else{
        creator.centerInside()
    }

    creator.into(this)
}

fun ImageView.loadImage(@DrawableRes drawableRes: Int,
                        withoutCropping: Boolean = false,
                        roundedCornersRadiusPx: Int = 0,
                        fitOnly: Boolean = false,
                        whichCornersToRound: RoundedCornersTransformation.CornerType = RoundedCornersTransformation.CornerType.ALL) {
    val creator = Picasso.get()
            .load(drawableRes)
            .transform(RoundedCornersTransformation(roundedCornersRadiusPx, 0, whichCornersToRound))
            .fit()

    if(!fitOnly){
        if (!withoutCropping){
            creator.centerCrop()
        } else{
            creator.centerInside()
        }
    }

    creator.placeholder(R.color.white)
            .into(this)
}

fun ImageView.loadImageCenterInside(url: String,
                                    @ColorRes placeholder: Int = R.color.placeholder,
                                    roundedCornersRadiusPx: Int = 0,
                                    whichCornersToRound: RoundedCornersTransformation.CornerType = RoundedCornersTransformation.CornerType.ALL) {
    Picasso.get()
            .load(url)
            .fit()
            .centerInside()
            .transform(RoundedCornersTransformation(roundedCornersRadiusPx, 0, whichCornersToRound))
            .placeholder(placeholder)
            .into(this)
}


inline fun View.doOnPreDraw(crossinline listener: (View) -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            listener.invoke(this@doOnPreDraw)
        }
    })
}

fun ImageView.loadFirstOrPlaceholder(photos: List<String>?) {
    if (photos?.isEmpty() == true) {
        setImageResource(R.color.placeholder)
    } else {
        photos?.run {
            loadImage(first())
        }
    }
}

fun ImageView.loadImageInside(@DrawableRes drawableRes: Int, whitePlaceholder: Boolean = false) {
    Picasso.get()
            .load(drawableRes)
            .fit()
            .centerInside()
            .placeholder(if(whitePlaceholder) R.color.white else placeholder)
            .into(this)
}

fun TextView.setStartDrawable(@DrawableRes drawableRes: Int?) {
    val drawable = if (drawableRes != null) context.getDrawable(drawableRes) else null

    setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
}

fun TextView.clearText() {
    text = ""
}

fun ImageView.makeBlackWhite() {
    this.removeFilters()

    val matrix = ColorMatrix()
    matrix.setSaturation(0f)

    val filter = ColorMatrixColorFilter(matrix)
    colorFilter = filter
}

fun ImageView.makeReddish() {
    this.removeFilters()

    val matrix = ColorMatrix()
    matrix.setRotate(0, 180f)
    val filter = ColorMatrixColorFilter(matrix)
    colorFilter = filter
}

fun ImageView.removeFilters() {
    colorFilter = null
}

fun Bitmap.convertToByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    return stream.toByteArray()
}

val TextView.content
    get() = this.text.toString()

fun Intent.withClearingStack(): Intent {
    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    return this
}

var EditText.content: String
    get() = this.text.toString()
    set(value) = setText(value, TextView.BufferType.EDITABLE)

fun Activity.hideKeyboard() {
    val view = currentFocus
    view?.hideKeyboard()
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun TextView.setTextCarryingEmpty(content: String?) {
    text = content
    visibility = if (content.isNullOrEmpty()) View.GONE else View.VISIBLE
}

fun Context.copyToClipboard(text: String, showToast: Boolean = false) {
    val clipManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText(null, text)

    clipManager.primaryClip = clipData

    if(showToast){
        Toast.makeText(this, getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
    }
}

fun TextView.onTextChanged(block: (CharSequence) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            block.invoke(s)
        }
    })
}

fun Context.getBitmap(drawableRes: Int): Bitmap {
    val drawable = ContextCompat.getDrawable(this, drawableRes)!!
    val canvas = Canvas()
    val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    canvas.setBitmap(bitmap)
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    drawable.draw(canvas)

    return bitmap
}

@Suppress("ConstantConditionIf", "DEPRECATION")
fun TextView.setHtml(string: String) {
    val html = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(string, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(string)
    }

    text = html
}

fun TextView.setTextColorRes(@ColorRes colorRes: Int) {
    val color = App.getColor(colorRes)

    setTextColor(color)
}

fun Int?.asDistance(): String {
    if (this == null) return ""

    val truncated = toInt()

    return when (truncated) {
        in 1..999 -> truncated.toString() + PREFIX_METER
        else -> (truncated / 1000).toString() + PREFIX_KILOMETER
    }
}

fun Location.distanceTo(latLng: LatLng): Float {
    val temp = Location("Temp")

    temp.latitude = latLng.latitude
    temp.longitude = latLng.longitude

    return distanceTo(temp)
}

fun Uri.toBytes(context: Context): ByteArray? {
    return context.contentResolver.openInputStream(this)?.let {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)

        var len = it.read(buffer)
        while (len != -1) {
            byteBuffer.write(buffer, 0, len)
            len = it.read(buffer)
        }
        return@let byteBuffer.toByteArray()
    }
}