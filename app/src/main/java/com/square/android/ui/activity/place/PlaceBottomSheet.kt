package com.square.android.ui.activity.place

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.square.android.R
import com.square.android.data.pojo.Day
import com.square.android.data.pojo.OfferInfo
import com.square.android.data.pojo.Place
import com.square.android.data.pojo.PlaceExtra
import com.square.android.extensions.asDistance
import com.square.android.extensions.loadImage
import com.square.android.extensions.setVisible
import com.square.android.extensions.textIsEmpty
import com.square.android.presentation.presenter.place.PlacePresenter
import com.square.android.presentation.view.place.PlaceView
import com.square.android.ui.fragment.LocationBottomSheetFragment
import com.square.android.ui.fragment.explore.GridItemDecoration
import kotlinx.android.synthetic.main.bottom_sheet_place.*
import java.util.*
import kotlin.math.roundToInt

class PlaceBottomSheetEvent(var calledFromMap: Boolean, var placeId: Long, var daySelected: Int = -1)

class PlaceBottomSheet(var calledFromMap: Boolean, var placeId: Long, var daySelected: Int) : LocationBottomSheetFragment(), PlaceView {
    @InjectPresenter
    lateinit var presenter: PlacePresenter

    var place: Place? = null

    private var isCalculated = false

    private var titleMovePoint: Float = 0F

    private var titleAnimationWeight: Float = 0F

    private var titleMinHeight: Int = 0

    private var isStatusBarLight: Boolean = true

    private var adapter: AboutAdapter? = null

    private var offerAdapter: OfferAdapter? = null

    private var dialog: OfferDialog? = null

    private var daysAdapter: DaysAdapter? = null

    private var decorationAdded = false

    private var intervalsAdapter: IntervalMatchParentAdapter? = null

    private var requirementsAdapter: RequirementsAdapter? = null

    @ProvidePresenter
    fun providePresenter() = PlacePresenter(placeId, daySelected)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_place, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val d = super.onCreateDialog(savedInstanceState)

        d.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                    bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            parentLayout?.let { parent ->
                val behaviour = BottomSheetBehavior.from(parent)

                setupFullHeight(parent)

                behaviour.state = if(calledFromMap) BottomSheetBehavior.STATE_HALF_EXPANDED else BottomSheetBehavior.STATE_EXPANDED

                behaviour.setBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback(){
                    override fun onSlide(p0: View, p1: Float) {}

                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if(newState == BottomSheetBehavior.STATE_EXPANDED){
                            placeNested.isScrollable = true
                        } else{
                            placeNested.isScrollable = false

                            if(newState == BottomSheetBehavior.STATE_HIDDEN){
                                dismiss()
                            }
                        }
                    }
                })

                //TODO bottom sheet height is wrong when opened for the first time - its just half of the screen(STATE_HALF_EXPANDED) instead of peek height
                behaviour.peekHeight = placeCollapsing.measuredHeight + placeAddressCl.measuredHeight

                if(calledFromMap){
                    //TODO make something to enable clicks outside bottom sheet - is it possible?
                }
            }
        }

        if(calledFromMap){
            isCancelable = false
            d.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        } else{
            isCancelable = true
        }

        return d
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placeArrowCollapse.setOnClickListener {
            dismiss()
        }

        placeAppBar.addOnOffsetChangedListener(
                AppBarLayout.OnOffsetChangedListener { appBarLayout, i ->
                    if(!isCalculated){
                        titleMovePoint = 1 - 0.9f
                        titleAnimationWeight =  1 / (1 - titleMovePoint)
                        isCalculated = true
                    }
                    updateViews(Math.abs(i / appBarLayout.totalScrollRange.toFloat()))
                })

        aboutMore.setOnClickListener {
            aboutMore.visibility = View.GONE
            placeAbout.maxLines = Integer.MAX_VALUE

            checkAndShowAboutRv()
        }

        placeAddressCl.setOnClickListener {
            if(presenter.latitude != null && presenter.longitude != null){
                presenter.data?.let {
                    val uri = "https://www.google.com/maps/dir/?api=1&origin=${presenter.latitude},${presenter.longitude}&destination=${it.address}&travelmode=walking"
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(uri)
                    val chooser = Intent.createChooser(intent, getString(R.string.select_an_app))
                    startActivity(chooser)
                }
            }
        }

        placeBookingBtn.setOnClickListener { presenter.bookClicked() }

        placeNested.isScrollable = false
    }

    private fun updateViews(offset: Float){
        when (offset) {
            in 0.555F..1F -> {

                if(!isStatusBarLight){
                    isStatusBarLight = true
                    setLightStatusBar(requireActivity())
                }

                placeArrowCollapse.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), android.R.color.black))
            }
            in 0F..0.555F -> {

                if(isStatusBarLight){
                    isStatusBarLight = false
                    clearLightStatusBar(requireActivity())
                }

                placeArrowCollapse.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(), android.R.color.white))
            }
        }

        placeName.apply {
            when {
                offset > titleMovePoint -> {

                    ratingLl.visibility = View.GONE

                    val titleAnimationOffset = (offset - titleMovePoint) * titleAnimationWeight

                    val measuredMargin = Math.round(resources.getDimension(R.dimen.ac_place_default_margin) + ((resources.getDimension(R.dimen.backArrowSize) + resources.getDimension(R.dimen.backArrowMarginStart)) * titleAnimationOffset))
                    this.layoutParams.also {
                        (it as LinearLayout.LayoutParams).setMargins(Math.round(resources.getDimension(R.dimen.ac_place_default_margin)),0,measuredMargin,0)
                        this.requestLayout()
                    }
                    this.translationX = (resources.getDimension(R.dimen.backArrowSize) + resources.getDimension(R.dimen.backArrowMarginStart)) * titleAnimationOffset

                    this.height = Math.round(titleMinHeight + (resources.getDimension(R.dimen.toolbar_extra_space)* titleAnimationOffset))
                }
                else ->{
                    ratingLl.visibility = View.VISIBLE

                    this.layoutParams.also {
                        translationX = 0f
                        (it as LinearLayout.LayoutParams).setMargins(Math.round(resources.getDimension(R.dimen.ac_place_default_margin)),0,Math.round(resources.getDimension(R.dimen.ac_place_default_margin)),0)
                    }
                }
            }
        }

        roundedView.apply {
            val mHeight = Math.round(resources.getDimension(R.dimen.v_20dp) + placeName.height)

            when {
                offset > titleMovePoint -> {
                    val titleAnimationOffset = (offset - titleMovePoint) * titleAnimationWeight

                    val roundedMeasuredHeight = Math.round(mHeight - (mHeight * (titleAnimationOffset * 1.5f)))

                    if(offset == 1f){
                        this.layoutParams.also {
                            it.height = 0
                        }
                    } else{
                        if(roundedMeasuredHeight >= 0) {
                            this.layoutParams.also {
                                it.height = roundedMeasuredHeight
                            }
                        }
                    }
                }

                else -> {
                    this.layoutParams.also {
                        it.height = mHeight
                    }
                }
            }
        }
    }

    override fun showProgress() {
        placeIntervalsRv.visibility = View.GONE
        placeIntervalsEmpty.visibility = View.GONE
        placeProgress.visibility = View.VISIBLE

        placeBookingText.text = ""
    }

    override fun hideProgress() {
        placeProgress.visibility = View.GONE
        placeIntervalsRv.visibility = View.VISIBLE

        placeBookingText.text = ""
    }

    override fun setSelectedIntervalItem(position: Int) {
        intervalsAdapter?.setSelectedItem(position)
    }

    override fun showIntervals(data: List<Place.Interval>) {
        intervalsAdapter = IntervalMatchParentAdapter(data, intervalHandler)

        placeIntervalsRv.layoutManager = GridLayoutManager(requireActivity(), 2)
        placeIntervalsRv.adapter = intervalsAdapter

        if(!decorationAdded){
            decorationAdded = true
            placeIntervalsRv.addItemDecoration(GridItemDecoration(2,placeIntervalsRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), false))
        }

        placeIntervalsEmpty.visibility = if(data.isNullOrEmpty()) View.VISIBLE else View.GONE
    }

    var intervalHandler = object : IntervalMatchParentAdapter.Handler{
        override fun itemClicked(position: Int, text: String, offers: List<Long>) {
            presenter.intervalItemClicked(position)
            placeBookingBtn.isEnabled = true
            placeBookingText.text = text

            offerAdapter?.updateAlpha(offers)
        }
    }

    override fun updateMonthName(calendar: Calendar) {
        val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()).capitalize()
        placeBookingMonth.text = getString(R.string.calendar_format, month, calendar.get(Calendar.YEAR))

        val d = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()).capitalize()
        val m = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()).capitalize()

        placeBookingDate.text = d + ", " + m + " " + dayToString(calendar.get(Calendar.DAY_OF_MONTH))
    }

    override fun setSelectedDayItem(position: Int) {
        daysAdapter?.setSelectedItem(position)
    }

    var dayHandler = object : DaysAdapter.Handler{
        override fun dayItemClicked(position: Int) {
            presenter.dayItemClicked(position)
            placeBookingBtn.isEnabled = false
            offerAdapter?.updateAlpha(null)
        }
    }

    override fun locationGotten(lastLocation: Location?) {
        presenter.locationGotten(lastLocation)
    }

    override fun showDistance(distance: Int?) {
        if (distance != null)  {
            val distanceFormatted = distance.asDistance()

            placeDistance.visibility = View.VISIBLE
            placeDistance.text = distanceFormatted
        } else {
            placeDistance.visibility = View.GONE
        }
    }

    override fun showOfferDialog(offer: OfferInfo, place: Place?) {
        dialog = OfferDialog(requireActivity())
        dialog!!.show(offer, place)
    }

    override fun showData(place: Place, offers: List<OfferInfo>, calendar: Calendar, typeImage: String?, extras: List<PlaceExtra>) {
        this.place = place

//        typeImage?.let { placeAboutImage.loadImageForIcon(it) }

        placeMainImage.loadImage(place.mainImage ?: (place.photos?.firstOrNull() ?: ""))

        if(place.description.textIsEmpty()){
            placeAboutCl.setVisible(false)
        } else{
            placeAbout.text = place.description
            placeAboutCl.setVisible(true)
        }

        //TODO change later - waiting for API
//        val aboutItems = listOf("www", "insta")
//        placeAboutSize = aboutItems.size
//        adapter = AboutAdapter(aboutItems)
//        placeAboutRv.adapter = adapter
//        placeAboutRv.layoutManager = LinearLayoutManager(placeAboutRv.context, RecyclerView.HORIZONTAL, false)
//        placeAboutRv.addItemDecoration(MarginItemDecorator(placeAboutRv.context.resources.getDimension(R.dimen.rv_item_decorator_4).toInt(), vertical = false))
//
//        if(!extras.isEmpty()){
//            placeRequirementsCl.visibility = View.VISIBLE
//
//            requirementsAdapter = RequirementsAdapter(extras, null)
//            placeRequirementsRv.layoutManager = GridLayoutManager(this, 2)
//            placeRequirementsRv.adapter = requirementsAdapter
//            placeRequirementsRv.addItemDecoration(GridItemDecoration(2,placeRequirementsRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), false))
//        }

        if(!offers.isNullOrEmpty()){
            placeOffersCl.visibility = View.VISIBLE

            offerAdapter = OfferAdapter(offers, object: OfferAdapter.Handler {
                override fun itemClicked(position: Int) {
                    presenter.offersItemClicked(position, place)
                }
            })

            placeOffersRv.layoutManager = GridLayoutManager(requireActivity(), 3)
            placeOffersRv.adapter = offerAdapter
            placeOffersRv.addItemDecoration(GridItemDecoration(3,placeOffersRv.context.resources.getDimension(R.dimen.rv_item_decorator_12).toInt(), false))
        }

        val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()).capitalize()
        placeBookingMonth.text = getString(R.string.calendar_format, month, calendar.get(Calendar.YEAR))

        val d = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).capitalize()
        val m =  calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()).capitalize()

        placeBookingDate.text = d +", " + m +" " + dayToString(calendar.get(Calendar.DAY_OF_MONTH))

        val days = mutableListOf<Day>()
        val calendar2 = Calendar.getInstance().apply { timeInMillis = calendar.timeInMillis }

        for (x in 0 until 7) {
            val day = Day()

            day.monthNumber = calendar2.get(Calendar.MONTH) + 1
            day.dayValue = calendar2.get(Calendar.DAY_OF_MONTH)
            day.dayName = calendar2.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).substring(0, 1)

            days.add(day)

            calendar2.add(Calendar.DAY_OF_YEAR, 1)
        }

        val margins = 2 * resources.getDimension(R.dimen.ac_place_default_margin)
        val itemSize = ((Resources.getSystem().displayMetrics.widthPixels - margins)/7).roundToInt()

        daysAdapter = DaysAdapter(days.toList(), dayHandler, itemSize)
//        daysAdapter!!.selectedMonth = calendar.get(Calendar.MONTH) + 1
        placeBookingCalendar.adapter = daysAdapter
        daysAdapter!!.selectedItemPosition = 0
        daysAdapter!!.notifyItemChanged(0, DaysAdapter.SelectedPayload)

        //TODO get from api
        rating.text = "4.5"

        placeName.text = place.name

        placeName.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                placeToolbar.apply {
                    this.layoutParams.also {
                        titleMinHeight = placeName.measuredHeight
                        placeName.height = titleMinHeight

                        it.height = Math.round(titleMinHeight + resources.getDimension(R.dimen.toolbar_extra_space))

                        roundedView.layoutParams.apply {
                            this.height = Math.round(resources.getDimension(R.dimen.v_20dp) + placeName.height)
                        }

                        placeCollapsing.layoutParams.apply {
                            this.height = Math.round(resources.getDimension(R.dimen.toolbar_image_height))
                        }
                    }
                }
                placeName.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        placeAbout.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                onAboutLoaded()
                placeAbout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                placeAbout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        placeAddress.text = place.address
    }

    private fun dayToString(day: Int): String{
       val s = when(day){
           1, 21, 31 -> "st"
           2, 22 -> "nd"
           3, 23 -> "rd"
           else -> "th"
       }

       return day.toString() + s
   }

    private fun onAboutLoaded(){
        var startOffset: Int
        var endOffset: Int
        var lineToEnd = 3
        val maxLines = 3
        var isLineSelected = false
        var notEmptyLinesToShowMore = 0

        if (!TextUtils.isEmpty(placeAbout.text)) {
            if (placeAbout.layout != null) {
                var shouldShowReadMore = true

                if (placeAbout.layout.lineCount <= maxLines) {
                    shouldShowReadMore = false
                } else {

                    for(i in 2 until placeAbout.layout.lineCount){
                        startOffset = placeAbout.layout.getLineStart(i)
                        endOffset = placeAbout.layout.getLineEnd(i)
                        if (!TextUtils.isEmpty((placeAbout.layout.text.subSequence(startOffset, endOffset)).toString().trim())) {
                            if (!isLineSelected) {
                                lineToEnd = i + 1
                                isLineSelected = true
                            } else {
                                notEmptyLinesToShowMore++
                            }
                        }
                    }

                    if (notEmptyLinesToShowMore < 2) {
                        shouldShowReadMore = false
                    }
                }

                if(shouldShowReadMore){
                    placeAbout.maxLines = lineToEnd
                    aboutMore.visibility = View.VISIBLE
                } else{
                    checkAndShowAboutRv()
                }
            }
         }
    }

    private fun checkAndShowAboutRv(){
//        if(placeAboutSize > 0){
//            placeAboutRv.visibility = View.VISIBLE
//        }
    }

    override fun onStart() {
        super.onStart()

        if(isStatusBarLight){
            setLightStatusBar(requireActivity())
        } else{
            clearLightStatusBar(requireActivity())
        }
    }

    override fun onStop() {
        super.onStop()

        if(!isStatusBarLight){
            setLightStatusBar(requireActivity())
        }
    }

    private fun setLightStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = activity.window.decorView.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            activity.window.decorView.systemUiVisibility = flags
        }
    }

    private fun clearLightStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = activity.window.decorView.systemUiVisibility
            flags = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            activity.window.decorView.systemUiVisibility = flags
        }
    }

    companion object {
        const val TAG = "PlaceBottomSheet"
    }
}