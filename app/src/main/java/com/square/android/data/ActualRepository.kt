package com.square.android.data

import android.util.Log
import android.util.SparseArray
import com.google.gson.Gson
import com.square.android.SOCIAL
import com.square.android.data.local.LocalDataManager
import com.square.android.data.network.ApiService
import com.square.android.data.network.PhotoId
import com.square.android.data.network.response.*
import com.square.android.data.newPojo.CompleteUserOfferDutyData
import com.square.android.data.newPojo.NewPlace
import com.square.android.data.newPojo.PlacesFiltersData
import com.square.android.data.newPojo.RemoveOfferEventData
import com.square.android.data.pojo.*
import com.square.android.presentation.presenter.auth.LoginData
import com.square.android.presentation.presenter.explore.LIST_ITEMS_SIZE
import com.square.android.presentation.presenter.explore.LatestSearch
import com.square.android.ui.base.tutorial.TutorialService
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.HttpException

private const val TOKEN_PREFIX = "Bearer "

object PlaceThings {
    var placeTypes: List<PlaceType>? = null
    var placeExtras: List<PlaceExtra>? = null
}

class ActualRepository(private val api: ApiService,
                       private val localManager: LocalDataManager) : Repository {

    override fun getTutorialDontShowAgain(tutorialKey: TutorialService.TutorialKey) =
            localManager.getTutorialDontShowAgain(tutorialKey)

    override fun setTutorialDontShowAgain(tutorialKey: TutorialService.TutorialKey, dontShowAgain: Boolean) =
            localManager.setTutorialDontShowAgain(tutorialKey, dontShowAgain)

    override fun saveFcmToken(fcmToken: String?) = localManager.saveFcmToken(fcmToken)
    override fun getFcmToken() = localManager.getFcmToken()

    override fun sendPhoneCode(phone: String): Deferred<SendPhoneCodeRespose> = GlobalScope.async {
        val data = performRequest {api.sendPhoneCode(SendPhoneCodeData(phone))}

        data
    }

    override fun sendFcmToken(uuid: String, newFcmToken: String?, oldToken: String?): Deferred<MessageResponse> =  GlobalScope.async {
        val data = performRequest {api.sendFcmToken(localManager.getUserInfo().id,
                FcmTokenData(uuid, "android", newFcmToken, oldToken))}
        data
    }

    override fun saveUserAboutMe(userAboutMeData: UserAboutMeData): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest {api.saveUserAboutMe(localManager.getAuthToken(), getUserId(), userAboutMeData)}
        data
    }

    override fun saveUserSkills(userSkillsData: UserSkillsData): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest {api.saveUserSkills(localManager.getAuthToken(), getUserId(), userSkillsData)}
        data
    }

    override fun getUserSkills(): Deferred<List<UserSkill>> = GlobalScope.async {
        val data = performRequest {api.getUserSkills(localManager.getAuthToken(), getUserId())}
        data
    }

    override fun saveUserFeatures(userFeaturesData: UserFeaturesData): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest {api.saveUserFeatures(localManager.getAuthToken(), getUserId(), userFeaturesData)}
        data
    }

    override fun getUserFeatures(): Deferred<UserFeatures> = GlobalScope.async {
        val data = performRequest {api.getUserFeatures(localManager.getAuthToken(), getUserId())}
        data
    }

    override fun saveUserNetwork(userNetworkData: UserNetworkData): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest {api.saveUserNetwork(localManager.getAuthToken(), getUserId(), userNetworkData)}
        data
    }

    override fun getUserNetwork(): Deferred<UserNetwork> = GlobalScope.async {
        val data = performRequest {api.getUserNetwork(localManager.getAuthToken(), getUserId())}
        data
    }

    override fun deleteUserAgency(agencyId: String): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest {api.deleteUserAgency(localManager.getAuthToken(), getUserId(), agencyId)}
        data
    }

    override fun saveUserInvoiceDetails(userInvoiceData: UserInvoiceData): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest {api.saveUserInvoiceDetails(localManager.getAuthToken(), getUserId(), userInvoiceData)}
        data
    }

    override fun getUserInvoiceDetails(): Deferred<UserInvoice> = GlobalScope.async {
        val data = performRequest {api.getUserInvoiceDetails(localManager.getAuthToken(), getUserId())}
        data
    }

    override fun addUserDeliveryPoints(userDeliveryPointsData: UserDeliveryPointsData): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest {api.addUserDeliveryPoints(localManager.getAuthToken(), getUserId(), userDeliveryPointsData)}
        data
    }

    override fun getUserDeliveryPoints(): Deferred<List<UserDeliveryPoint>> = GlobalScope.async {
        val data = performRequest {api.getUserDeliveryPoints(localManager.getAuthToken(), getUserId())}
        data
    }

    override fun deleteUserDeliveryPoint(deliveryPointId: String): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest {api.deleteUserDeliveryPoint(localManager.getAuthToken(), getUserId(), deliveryPointId)}
        data
    }

    override fun updateUserRates(userRatesData: UserRatesData): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest {api.updateUserRates(localManager.getAuthToken(), getUserId(), userRatesData)}
        data
    }

    override fun getUserRates(): Deferred<List<UserRate>> = GlobalScope.async {
        val data = performRequest {api.getUserRates(localManager.getAuthToken(), getUserId())}
        data
    }

    override fun addUserSocialChannel(userSocialChannelData: UserSocialChannelData): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest {api.addUserSocialChannel(localManager.getAuthToken(), getUserId(), userSocialChannelData)}
        data
    }

    override fun getUserSocialChannels(): Deferred<List<UserSocialChannel>> = GlobalScope.async {
        val data = performRequest {api.getUserSocialChannels(localManager.getAuthToken(), getUserId())}
        data.list
    }

    override fun deleteUserSocialChannel(socialChannelId: String): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest {api.deleteUserSocialChannel(localManager.getAuthToken(), socialChannelId)}
        data
    }

    override fun getUserCapabilities(): Deferred<List<UserCapability>> = GlobalScope.async {
        val data = performRequest {api.getUserCapabilities(localManager.getAuthToken(), getUserId())}
        data
    }

    override fun addUserCapabilities(userCapabilitiesData: UserCapabilitiesData): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest {api.addUserCapabilities(localManager.getAuthToken(), getUserId(), userCapabilitiesData)}
        data
    }

    override fun addUserProfession(professionData: ProfessionData): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest {api.addUserProfession(localManager.getAuthToken(), getUserId(), professionData)}
        data
    }

    override fun getUserProfessions(): Deferred<ProfessionsResult>  = GlobalScope.async {
        val data = performRequest {api.getUserProfessions(localManager.getAuthToken(), getUserId())}
        data
    }


    override fun deleteUserProfession1(professionId: Int): Deferred<MessageResponse>  = GlobalScope.async {
        val data = performRequest {api.deleteUserProfession1(localManager.getAuthToken(), getUserId(), professionId)}
        data
    }

    override fun deleteUserProfession2(professionId: String): Deferred<MessageResponse>  = GlobalScope.async {
        val data = performRequest {api.deleteUserProfession2(localManager.getAuthToken(), getUserId(), professionId)}
        data
    }

    override fun deleteUserProfession3(professionDelete1Data: ProfessionDelete1Data): Deferred<MessageResponse>  = GlobalScope.async {
        val data = performRequest {api.deleteUserProfession3(localManager.getAuthToken(), getUserId(), professionDelete1Data)}
        data
    }
    override fun deleteUserProfession4(professionDelete2Data: ProfessionDelete2Data): Deferred<MessageResponse>  = GlobalScope.async {
        val data = performRequest {api.deleteUserProfession4(localManager.getAuthToken(), getUserId(), professionDelete2Data)}
        data
    }

    override fun addUserSpecialities(specialitiesData: SpecialitiesData): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest {api.addUserSpecialities(localManager.getAuthToken(), getUserId(), specialitiesData)}
        data
    }

    override fun getUserSpecialities(): Deferred<SpecialitiesResult> = GlobalScope.async {
        val data = performRequest {api.getUserSpecialities(localManager.getAuthToken(), getUserId())}
        data
    }

    override fun getRegisterSpecialitiesAndProfessions(): Deferred<RegisterSpecialitiesAndProfessionsData> = GlobalScope.async {
        val data = performRequest {api.getRegisterSpecialitiesAndProfessions()}
        data
    }

    override fun getRegisterCapabilities(): Deferred<RegisterCapabilitiesData> = GlobalScope.async {
        val data = performRequest {api.getRegisterCapabilities()}
        data
    }

    override fun postUserPlan(userPlanData: UserPlanData): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest {api.postUserPlan(localManager.getAuthToken(), getUserId(), userPlanData)}
        data
    }

    override fun getUserPlans(): Deferred<List<UserPlanData>> = GlobalScope.async {
        val data = performRequest {api.getUserPlans(localManager.getAuthToken(), getUserId())}
        data
    }


//////// TODO need models

    override fun removeOfferEventFromSchedule(removeOfferEventData: RemoveOfferEventData): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest {api.removeOfferEventFromSchedule(localManager.getAuthToken(), removeOfferEventData)}
        data
    }

    override fun completeUserOfferDuty(completeUserOfferDutyData: CompleteUserOfferDutyData): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest {api.completeUserOfferDuty(localManager.getAuthToken(), getUserId(), completeUserOfferDutyData)}
        data
    }

    override fun getUserOfferDuties(offerId: Long): Deferred<List<String>> = GlobalScope.async {
        val data = performRequest {api.getUserOfferDuties(localManager.getAuthToken(), getUserId(), offerId)}
        data
    }

    override fun rejectOfferProposal(userOfferId: String): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest {api.rejectOfferProposal(localManager.getAuthToken(), userOfferId)}
        data
    }

    override fun cancelUserOfferBooking(bookingId: Long): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest {api.cancelUserOfferBooking(localManager.getAuthToken(), bookingId)}
        data
    }

    override fun getNearbyPlaces(lat: Double, lng: Double, radius: Int, date: String, placesFiltersData: PlacesFiltersData): Deferred<List<NewPlace>> = GlobalScope.async {
        val data = performRequest {api.getNearbyPlaces(localManager.getAuthToken(), lat, lng, radius, date, placesFiltersData)}
        data
    }

    override fun getPlaceOffersNew(placeId: Long): Deferred<String> = GlobalScope.async {
        val data = performRequest {api.getPlaceOffersNew(localManager.getAuthToken(), placeId)}
        data
    }

////////////////////////////










    override fun getIntervalSlots(placeId: Long, date: String): Deferred<List<Place.Interval>> = GlobalScope.async {
        val data = performRequest {api.getIntervalSlots(placeId, date)}
        data
    }

    override fun getActions(offerId: Long, bookingId: Long): Deferred<List<Offer.Action>> = GlobalScope.async {
        val data = performRequest {api.getActions(localManager.getAuthToken(),offerId, bookingId)}
        data
    }

// Event / ride endpoints
    override fun createRide(rideData: RideData): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest {api.createRide(localManager.getAuthToken(), rideData)}
        data
    }

    override fun editRide(ride: Ride): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest {api.editRide(localManager.getAuthToken(), ride)}
        data
    }

    override fun deleteRide(rideId: String): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest {api.deleteRide(localManager.getAuthToken(), rideId)}
        data
    }

    override fun getUserRide(rideId: String): Deferred<Ride?> = GlobalScope.async {
        val data = performRequest {api.getUserRide(localManager.getAuthToken(), rideId, null, null)}

        data.firstOrNull()
    }

    override fun getUserRides(filter: String, pending: Boolean?): Deferred<List<Ride>> = GlobalScope.async {
        val data = performRequest {api.getUserRides(localManager.getAuthToken(), null, pending, filter)}

        data
    }

    override fun rateRide(rideData: RideData): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest {api.rateRide(localManager.getAuthToken(), rideData)}
        data
    }

    override fun getRideTimeframesForPlace(placeId: Long): Deferred<List<DriverRide>> = GlobalScope.async {
        val data = performRequest {api.getRideTimeframesForPlace(localManager.getAuthToken(), placeId)}
        data
    }

//    override fun bookEvent(bookEventData: BookEventData): Deferred<MessageResponse> = GlobalScope.async {
//        val data = performRequest {api.bookEvent(localManager.getAuthToken(), bookEventData)}
//        data
//    }

    override fun getUserEventBookings(eventBookingId: String?): Deferred<List<BookEventData.EventBooking>> = GlobalScope.async {
        val data = performRequest {api.getUserEventBookings(localManager.getAuthToken(), eventBookingId)}
        data
    }

    override fun editEventBookings(rideId: String, eventBooking: BookEventData): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest {api.editEventBookings(localManager.getAuthToken(), rideId, eventBooking)}
        data
    }

    override fun getEvents(): Deferred<List<Event>> = GlobalScope.async {
        val data = performRequest {api.getEvents(localManager.getAuthToken())}
        data
    }

    override fun getEvent(eventId: String): Deferred<Event?> = GlobalScope.async {
        val data = performRequest {api.getEvent(localManager.getAuthToken(), eventId)}
        data.firstOrNull()
    }
//////////////////////


    override fun getIntervals(placeId: Long, date: String) : Deferred<IntervalsWrapper> = GlobalScope.async {
        val data = performRequest {api.getIntervals(placeId, date)}
        data
    }

    override fun getOffersForBooking(placeId: Long, bookingId: Long): Deferred<List<OfferInfo>> = GlobalScope.async {
        val data = performRequest { api.getOffersForBooking(placeId, bookingId) }
        data
    }

    override fun addOfferToBook(bookId: Long, offerId: Long): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest { api.addOfferToBook(bookId, OfferToBook(offerId)) }
        data
    }

    override fun getCities(): Deferred<List<City>> = GlobalScope.async {
        val data = performRequest { api.getCities(localManager.getAuthToken()) }
        data
    }

    override fun getTimeFrames(): Deferred<List<FilterTimeframe>> = GlobalScope.async {
        val data = performRequest { api.getTimeFrames(localManager.getAuthToken()) }
        data
    }
    override fun getPlacesByFilters(placeData: PlaceData): Deferred<List<Place>> = GlobalScope.async {
        val data = performRequest { api.getPlacesByFilters(localManager.getAuthToken(), placeData.timeFrame, placeData.typology, placeData.date, placeData.city) }
        data
    }

    override fun getBadgeCount(): Deferred<BadgeInfo> = GlobalScope.async {
        val id = getUserInfo().id
        val data = performRequest { api.getBadgeCount(id) }
        data
    }

    override fun getFeedbackContent(placeId: Long): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest { api.getFeedbackBody(placeId) }
        data
    }

    override fun getPlaceOffers(placeId: Long): Deferred<List<OfferInfo>> = GlobalScope.async {
        val data = performRequest { api.getPlaceOffers(placeId) }
        data
    }

//    override fun addReview(offerId: Long, bookingId: Long, link: String, actionId: String, imageBytes: ByteArray) = performRequest {
//        val requestFile = RequestBody.create(
//                MediaType.parse("image/*"),
//                imageBytes
//        )
//        val body = MultipartBody.Part.createFormData("images", "", requestFile)
//
//        api.addReview(localManager.getAuthToken(), offerId, offerId, link, actionId, body)
//    }
    override fun addReview(offerId: Long, bookingId: Long, link: String, actionType: String, imageBytes: ByteArray?) = performRequest {
        var body: MultipartBody.Part? = null

        imageBytes?.let {
            val requestFile = RequestBody.create(
                    MediaType.parse("image/*"),
                    it
            )
            body = MultipartBody.Part.createFormData("images", "", requestFile)
        }
        Log.e("LOL", actionType + " OFFER ID:" + offerId + " BOOKING ID:" + bookingId + " " + body.toString())
        api.addReview(localManager.getAuthToken(), offerId, bookingId, link, actionType, body)
    }

    override fun claimOffer(offerId: Long): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest { api.claimRedemption(offerId) }
        data
    }

    override fun setSocialLink(username: String) {
        val link = SOCIAL.SOCIAL_LINK_FORMAT.format(username)
        localManager.setSocialLink(link)
    }

    override fun getOffer(offerId: Long) = GlobalScope.async {
        val userId = getUserInfo().id
        val data = performRequest { api.getOffer(offerId, userId) }

        data
    }

    override fun setUserName(name: String, surname: String) {
        localManager.setUserName("$name $surname")
    }

    override fun setUserPaymentRequired(paymentRequired: Boolean) {
        localManager.setUserPaymentRequired(paymentRequired)
    }

    override fun getUserInfo() = localManager.getUserInfo()

    override fun setAvatarUrl(url: String?) {
        localManager.setAvatarUrl(url)
    }

    override fun getRedemption(redemptionId: Long): Deferred<RedemptionFull> = GlobalScope.async {
        val data = performRequest { api.getRedemption(redemptionId) }
        data
    }

    override fun clearUserData() {
        localManager.clearUserData()
    }

    override fun deleteRedemption(id: Long): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest { api.deleteRedemption(id) }
        data
    }

    override fun getRedemptions(): Deferred<List<RedemptionInfo>> = GlobalScope.async {
        val data = performRequest { api.getRedemptions(getUserInfo().id) }
        data
    }

    override fun setUserId(id: Long) {
        localManager.setId(id)
    }

    override fun getUserId(): Long  {
        return localManager.getId()
    }

    override fun getProfileInfo(): String {
       return localManager.getProfileInfo()
    }

    override fun saveProfileInfo(profileInfo: String, fragmentNumber: Int) {
        localManager.setProfileInfo(profileInfo, fragmentNumber)
    }

    override fun getLatestSearches(): SparseArray<LatestSearch> {
        val latestSearches: SparseArray<LatestSearch> = SparseArray(LIST_ITEMS_SIZE)

        val gson = Gson()

        val latestSearchesJson: List<String?> = localManager.getLatestSearchesJson()

        var index = 0
        for (latestSearchJson in latestSearchesJson) {
            if (latestSearchJson == null) {
                latestSearches.append(index, LatestSearch(type = index))
            } else {
                try {
                    latestSearches.append(index, gson.fromJson(latestSearchJson, LatestSearch::class.java).apply { type = index })
                } catch (e: Exception) {
                    latestSearches.append(index, LatestSearch(type = index))
                }
            }

            index++
        }

        return latestSearches
    }

    override fun saveLatestSearches(latestSearches: SparseArray<LatestSearch>) {
        val gson = Gson()

        for(x in 0 until latestSearches.size()){
            localManager.setLatestSearchAtIndex(gson.toJson(latestSearches[x]), x)
        }
    }

    override fun getFragmentNumber(): Int {
        return localManager.getFragmentNumber()
    }

    override fun book(placeId: Long, bookInfo: BookInfo): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequestCheckingMessage { api.book(placeId, bookInfo) }
        data
    }

    override fun getPlace(id: Long): Deferred<Place> = GlobalScope.async {
        val data = performRequest { api.getPlace(id) }
        data
    }

    override fun getPlaces(): Deferred<List<Place>> = GlobalScope.async {
        val places = performRequest { api.getPlaces() }

        places.forEach { place ->
            val prices = place.offers.map { it.price }

            place.award = prices.min() ?: 0
        }

        places
    }

    override fun getPlaceTypes(): Deferred<List<PlaceType>> = GlobalScope.async {
        PlaceThings.placeTypes?.let {
            it
        } ?: run{
            var data = performRequest { api.getPlaceTypes(localManager.getAuthToken()) }
            PlaceThings.placeTypes = data

            data
        }
    }

    override fun getPlaceExtras(): Deferred<List<PlaceExtra>> = GlobalScope.async {
        PlaceThings.placeExtras?.let {
            it
        } ?: run{
            var data = performRequest {api.getPlaceExtras(localManager.getAuthToken())}
            PlaceThings.placeExtras = data

            data
        }
    }

    override fun setLoggedIn(isLogged: Boolean) {
        localManager.setLoggedIn(isLogged)
    }

    override fun isLoggedInFacebook(): Boolean = localManager.isLoggedInFacebook()

    override fun setLoggedInFacebook(isLoggedFb: Boolean){
        localManager.setLoggedInFacebook(isLoggedFb)
    }

    override fun getCurrentUser(): Deferred<Profile.User> = GlobalScope.async {
        val data = performRequest { api.getCurrentProfile() }
        setUserPaymentRequired(data.isPaymentRequired)
        data
    }

    override fun isProfileFilled() = localManager.isProfileFilled()

    override fun setProfileFilled(isFilled: Boolean) {
        localManager.setProfileFilled(isFilled)
    }

    override fun setUserToken(token: String) {
        localManager.setAuthToken(TOKEN_PREFIX + token)
    }

    override fun fillProfile(info: ProfileInfo): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequestCheckingMessage { api.editProfile(info.apply { gender = gender.toLowerCase() }) }

        data
    }

    override fun registerUser(signUpData: SignUpData): Deferred<AuthResponse> = GlobalScope.async {
        val data = performRequest {
            api.registerUser(signUpData)
        }

        if (data.token.isNullOrEmpty()) {
            throw Exception(data.message)
        }

        data
    }

    override fun loginUser(loginData: LoginData): Deferred<AuthResponse> = GlobalScope.async {
        val data = performRequest {
            api.loginUser(loginData)
        }

        if (data.token.isNullOrEmpty()) {
            throw Exception(data.message)
        }

        data
    }

    override fun resetPassword(authData: AuthData): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest { api.resetPassword(authData) }
        data
    }

    override fun introDisplayed() {
        localManager.setShouldDisplayIntro(false)
    }

    override fun shouldDisplayIntro(): Boolean = localManager.shouldDisplayIntro()

    override fun isLoggedIn(): Boolean = localManager.isLoggedIn()

    private inline fun <T> performRequest(block: () -> Call<T>): T {
        val response = block().execute()

        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw HttpException(response) as Throwable
        }
    }

    private inline fun performRequestCheckingMessage(block: () -> Call<MessageResponse>): MessageResponse {
        val result = performRequest(block)

        if (result.message in ERRORS) {
            throw Exception(result.message)
        }

        return result
    }

    override fun removePhoto(userId: Long, photoId: PhotoId): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest { api.removePhoto(userId, photoId.imageId) }
        data
    }

    override fun addPhoto(userId: Long, imageBytes: ByteArray): Deferred<Images> = GlobalScope.async {
        // create RequestBody instance from file
        val requestFile = RequestBody.create(
                MediaType.parse("image/*"),
                imageBytes
        )

        // MultipartBody.Part is used to send also the actual file name
        val body = MultipartBody.Part.createFormData("images", "", requestFile)

        val data = performRequest { api.addPhoto(userId, body) }
        data
    }

    override fun setPhotoAsMain(userId: Long, photoId: String): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest { api.setPhotoAsMain(userId, photoId) }
        data
    }

    override fun getPaymentTokens(): Deferred<List<BillingTokenInfo>> = GlobalScope.async {
        val data = performRequest {api.getPaymentTokens(localManager.getAuthToken())}
        data
    }

    override fun sendPaymentToken(billingTokenInfo: BillingTokenInfo): Deferred<List<BillingTokenInfo>> = GlobalScope.async {
        val data = performRequest {api.sendPaymentToken(localManager.getAuthToken(), billingTokenInfo)}
        data
    }

    override fun setUserEntitlement(entitlementId: String, active: Boolean) {
        localManager.setUserEntitlement(entitlementId, active)
    }

    override fun getUserEntitlement(entitlementId: String): Boolean = localManager.getUserEntitlement(entitlementId)

    override fun clearUserEntitlements() = localManager.clearUserEntitlements()

    override fun grantAllUserEntitlements() = localManager.grantAllUserEntitlements()



    override fun getGeolocationAllowed(): Boolean = localManager.getGeolocationAllowed()

    override fun getLocationDontAsk(): Boolean = localManager.getLocationDontAsk()

    override fun setLocationDontAsk(dontAsk: Boolean) {
        localManager.setLocationDontAsk(dontAsk)
    }

    override fun getLocationPermissionsOneTimeChecked(): Boolean = localManager.getLocationPermissionsOneTimeChecked()

    override fun setLocationPermissionsOneTimeChecked(checkedAlready: Boolean) {
        localManager.setLocationPermissionsOneTimeChecked(checkedAlready)
    }

/////////// push notifications
    override fun setSpotsCloseAllowed(allowed: Boolean) {
        localManager.setSpotsCloseAllowed(allowed)
    }
    override fun getSpotsCloseAllowed(): Boolean = localManager.getSpotsCloseAllowed()

    override fun setNewLocationsAllowed(allowed: Boolean) {
        localManager.setNewLocationsAllowed(allowed)
    }
    override fun getNewLocationsAllowed(): Boolean = localManager.getNewLocationsAllowed()

    override fun setNewJobMatchingAllowed(allowed: Boolean) {
        localManager.setNewJobMatchingAllowed(allowed)
    }
    override fun getNewJobMatchingAllowed(): Boolean = localManager.getNewJobMatchingAllowed()

    override fun setEventsInCityAllowed(allowed: Boolean) {
        localManager.setEventsInCityAllowed(allowed)
    }
    override fun getEventsInCityAllowed(): Boolean = localManager.getEventsInCityAllowed()

    override fun setCreditsAddedAllowed(allowed: Boolean) {
        localManager.setCreditsAddedAllowed(allowed)
    }
    override fun getCreditsAddedAllowed(): Boolean = localManager.getCreditsAddedAllowed()

    override fun setFriendMessagesAllowed(allowed: Boolean) {
        localManager.setFriendMessagesAllowed(allowed)
    }
    override fun getFriendMessagesAllowed(): Boolean = localManager.getFriendMessagesAllowed()

    override fun setBusinessMessagesAllowed(allowed: Boolean) {
        localManager.setBusinessMessagesAllowed(allowed)
    }
    override fun getBusinessMessagesAllowed(): Boolean = localManager.getBusinessMessagesAllowed()
///////////

    override fun setGeolocationAllowed(allowed: Boolean) {
       localManager.setGeolocationAllowed(allowed)
    }

// Campaign
    override fun getCampaigns(): Deferred<List<CampaignInfo>> = GlobalScope.async {
        val data = performRequest { api.getCampaigns(localManager.getAuthToken()) }
        data
    }

    override fun getCampaign(campaignId: Long): Deferred<Campaign> = GlobalScope.async {
        val data = performRequest { api.getCampaign(localManager.getAuthToken(), campaignId) }
        data
    }

    override fun joinCampaign(campaignId: Long): Deferred<Campaign> = GlobalScope.async {
        val data = performRequest { api.joinCampaign(localManager.getAuthToken(), campaignId) }
        data
    }

    override fun requestReview(campaignId: Long): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest { api.requestReview(localManager.getAuthToken(), campaignId) }
        data
    }

    override fun addCampaignImage(campaignId: Long, imageBytes: ByteArray): Deferred<Images> = GlobalScope.async {
        val requestFile = RequestBody.create(
                MediaType.parse("image/*"),
                imageBytes
        )

        val body = MultipartBody.Part.createFormData("images", "", requestFile)

        val data = performRequest { api.addCampaignImage(localManager.getAuthToken(), campaignId, body) }
        data
    }

    override fun removeCampaignImage(campaignId: Long, imageId: String): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest { api.removeCampaignImage(localManager.getAuthToken(), campaignId, imageId) }
        data
    }

    override fun getCampaignPhotos(campaignId: Long): Deferred<List<String>> = GlobalScope.async {
        val data = performRequest { api.getCampaignPhotos(localManager.getAuthToken(), campaignId) }
        data
    }

    override fun getCampaignLocations(campaignId: Long): Deferred<List<CampaignInterval.Location>> = GlobalScope.async {
        val data = performRequest { api.getCampaignLocations(localManager.getAuthToken(), campaignId) }
        data
    }

    override fun getCampaignSlots(campaignId: Long, intervalId: Long, date: String): Deferred<List<CampaignInterval.Slot>> = GlobalScope.async {
        val data = performRequest { api.getCampaignSlots(localManager.getAuthToken(), campaignId, intervalId, date) }
        data
    }

    override fun campaignBook(campaignId: Long, intervalId: Long, campaignBookInfo: CampaignBookInfo): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest { api.campaignBook(localManager.getAuthToken(), campaignId, intervalId, campaignBookInfo) }
        data
    }

    override fun sendQr(qrInfo: QrInfo): Deferred<Campaign> = GlobalScope.async {
        val data = performRequest { api.sendQr(localManager.getAuthToken(), qrInfo) }
        data
    }

    override fun getCampaignBookings(): Deferred<List<CampaignBooking>> = GlobalScope.async {
        val data = performRequest { api.getCampaignBookings(localManager.getAuthToken()) }
        data
    }

    override fun sendCampaignForReview(campaignId: Long): Deferred<MessageResponse> = GlobalScope.async {
        val data = performRequest { api.sendCampaignForReview(localManager.getAuthToken(), campaignId) }
        data
    }


}