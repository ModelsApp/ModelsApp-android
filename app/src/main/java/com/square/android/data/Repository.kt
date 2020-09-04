package com.square.android.data

import android.util.SparseArray
import com.square.android.data.network.PhotoId
import com.square.android.data.network.response.AuthResponse
import com.square.android.data.network.response.SendPhoneCodeRespose
import com.square.android.data.network.response.MessageResponse
import com.square.android.data.newPojo.CompleteUserOfferDutyData
import com.square.android.data.newPojo.NewPlace
import com.square.android.data.newPojo.PlacesFiltersData
import com.square.android.data.newPojo.RemoveOfferEventData
import com.square.android.data.pojo.*
import com.square.android.presentation.presenter.auth.LoginData
import com.square.android.presentation.presenter.explore.LatestSearch
import com.square.android.ui.base.tutorial.TutorialService
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.http.*

interface Repository {

    fun saveUserAboutMe(userAboutMeData: UserAboutMeData): Deferred<MessageResponse>

    fun saveUserSkills(userSkillsData: UserSkillsData): Deferred<MessageResponse>

    fun getUserSkills(): Deferred<List<UserSkill>>

    fun saveUserFeatures(userFeaturesData: UserFeaturesData): Deferred<MessageResponse>

    fun getUserFeatures(): Deferred<UserFeatures>

    fun saveUserNetwork(userNetworkData: UserNetworkData): Deferred<MessageResponse>

    fun getUserNetwork(): Deferred<UserNetwork>

    fun deleteUserAgency(agencyId: String): Deferred<MessageResponse>

    fun saveUserInvoiceDetails(userInvoiceData: UserInvoiceData): Deferred<MessageResponse>

    fun getUserInvoiceDetails(): Deferred<UserInvoice>

    fun addUserDeliveryPoints(userDeliveryPointsData: UserDeliveryPointsData): Deferred<MessageResponse>

    fun getUserDeliveryPoints(): Deferred<List<UserDeliveryPoint>>

    fun deleteUserDeliveryPoint(deliveryPointId: String): Deferred<MessageResponse>

    fun updateUserRates(userRatesData: UserRatesData): Deferred<MessageResponse>

    fun getUserRates(): Deferred<List<UserRate>>

    fun addUserSocialChannel(userSocialChannelData: UserSocialChannelData): Deferred<MessageResponse>

    fun getUserSocialChannels(): Deferred<List<UserSocialChannel>>

    fun deleteUserSocialChannel(socialChannelId: String): Deferred<MessageResponse>

    fun getUserCapabilities(): Deferred<List<UserCapability>>

    fun addUserCapabilities(userCapabilitiesData: UserCapabilitiesData): Deferred<MessageResponse>

    fun addUserProfession(professionsData: ProfessionData): Deferred<MessageResponse>

    fun getUserProfessions(): Deferred<ProfessionsResult>

    fun deleteUserProfession1(professionId: Int): Deferred<MessageResponse>
    fun deleteUserProfession2(professionId: String): Deferred<MessageResponse>
    fun deleteUserProfession3(professionDelete1Data: ProfessionDelete1Data): Deferred<MessageResponse>
    fun deleteUserProfession4(professionDelete2Data: ProfessionDelete2Data): Deferred<MessageResponse>


    fun addUserSpecialities(specialitiesData: SpecialitiesData): Deferred<MessageResponse>

    fun getUserSpecialities(): Deferred<SpecialitiesResult>

    fun getRegisterSpecialitiesAndProfessions(): Deferred<RegisterSpecialitiesAndProfessionsData>
    fun getRegisterCapabilities(): Deferred<RegisterCapabilitiesData>

    fun postUserPlan(userPlanData: UserPlanData): Deferred<MessageResponse>

    fun getUserPlans(): Deferred<List<UserPlanData>>


//////// TODO need models

    fun removeOfferEventFromSchedule(removeOfferEventData: RemoveOfferEventData): Deferred<MessageResponse>

    fun completeUserOfferDuty(completeUserOfferDutyData: CompleteUserOfferDutyData): Deferred<MessageResponse>

    fun getUserOfferDuties(offerId: Long): Deferred<List<String>>

    fun rejectOfferProposal(userOfferId: String): Deferred<MessageResponse>

    fun cancelUserOfferBooking(bookingId: Long): Deferred<MessageResponse>

    fun getNearbyPlaces(lat: Double, lng: Double, radius: Int, date: String, placesFiltersData: PlacesFiltersData): Deferred<List<NewPlace>>

    fun getPlaceOffersNew(placeId: Long): Deferred<String>

////////////////////////////


    fun createRide(rideData: RideData): Deferred<MessageResponse>

    fun editRide(ride: Ride): Deferred<MessageResponse>

    fun deleteRide(rideId: String): Deferred<MessageResponse>

    fun getUserRide(rideId: String): Deferred<Ride?>

    fun getUserRides(filter: String, pending: Boolean?): Deferred<List<Ride>>

    fun rateRide(rideData: RideData): Deferred<MessageResponse>

    fun getRideTimeframesForPlace(placeId: Long): Deferred<List<DriverRide>>

//    fun bookEvent(bookEventData: BookEventData): Deferred<MessageResponse>

    fun sendPhoneCode(phone: String): Deferred<SendPhoneCodeRespose>

    fun getUserEventBookings(eventBookingId: String?): Deferred<List<BookEventData.EventBooking>>

    fun editEventBookings(rideId: String, eventBooking: BookEventData): Deferred<MessageResponse>

    fun getEvents(): Deferred<List<Event>>

    fun getEvent(eventId: String): Deferred<Event?>


    fun getCities(): Deferred<List<City>>

    fun getTimeFrames(): Deferred<List<FilterTimeframe>>

    fun getPlacesByFilters(placeData: PlaceData): Deferred<List<Place>>

    fun shouldDisplayIntro(): Boolean
    fun introDisplayed()

    fun isLoggedIn(): Boolean
    fun isLoggedInFacebook(): Boolean
    fun setLoggedInFacebook(isLoggedFb: Boolean)
    fun setLoggedIn(isLogged: Boolean)

    fun setUserToken(token: String)

    fun isProfileFilled(): Boolean
    fun setProfileFilled(isFilled: Boolean)

    fun registerUser(signUpData: SignUpData): Deferred<AuthResponse>
    fun loginUser(loginData: LoginData): Deferred<AuthResponse>

    fun resetPassword(authData: AuthData): Deferred<MessageResponse>

    fun fillProfile(info: ProfileInfo): Deferred<MessageResponse>

    fun getCurrentUser(): Deferred<Profile.User>

    fun getPlaces(): Deferred<List<Place>>

    fun getPlaceTypes(): Deferred<List<PlaceType>>

    fun getPlaceExtras(): Deferred<List<PlaceExtra>>

    fun setUserId(id: Long)

    fun getUserId(): Long

    fun setAvatarUrl(url: String?)
    fun setUserName(name: String, surname: String)
    fun setSocialLink(username: String)

    fun setUserPaymentRequired(paymentRequired: Boolean)

    fun getUserInfo() : UserInfo

    fun clearUserData()

    fun book(placeId: Long, bookInfo: BookInfo): Deferred<MessageResponse>

    fun getPlace(id: Long): Deferred<Place>

    fun getRedemptions(): Deferred<List<RedemptionInfo>>

    fun getRedemption(redemptionId: Long): Deferred<RedemptionFull>

    fun deleteRedemption(id: Long): Deferred<MessageResponse>

    fun getOffer(offerId: Long): Deferred<Offer>

    fun claimOffer(offerId: Long) : Deferred<MessageResponse>

//    fun addReview(offerId: Long, bookingId: Long, link: String, actionId: String, imageBytes: ByteArray) : MessageResponse
    fun addReview(offerId: Long, bookingId: Long, link: String, actionType: String, imageBytes: ByteArray?) : MessageResponse


    fun getPlaceOffers(placeId: Long) : Deferred<List<OfferInfo>>

    fun getFeedbackContent(placeId: Long) : Deferred<MessageResponse>

    fun getBadgeCount() : Deferred<BadgeInfo>

    fun addOfferToBook(bookId: Long,
                       offerId: Long) : Deferred<MessageResponse>

    fun getIntervals(placeId: Long, date: String): Deferred<IntervalsWrapper>
    fun getIntervalSlots(placeId: Long, date: String): Deferred<List<Place.Interval>>

    fun getActions(offerId: Long, bookingId: Long): Deferred<List<Offer.Action>>

    fun removePhoto(userId: Long, photoId: PhotoId): Deferred<MessageResponse>
    fun addPhoto(userId: Long, imageBytes: ByteArray): Deferred<Images>
    fun setPhotoAsMain(userId: Long, photoId: String): Deferred<MessageResponse>

    fun saveFcmToken(fcmToken: String?)
    fun getFcmToken(): String?

    fun saveProfileInfo(profileInfo: String, fragmentNumber: Int)

    fun getProfileInfo(): String

    fun getFragmentNumber(): Int

    fun getLatestSearches(): SparseArray<LatestSearch>
    fun saveLatestSearches(latestSearches: SparseArray<LatestSearch>)

    fun sendFcmToken(uuid: String, newFcmToken: String?, oldToken: String?): Deferred<MessageResponse>
    fun getOffersForBooking(placeId: Long, bookingId: Long): Deferred<List<OfferInfo>>

    fun setTutorialDontShowAgain(tutorialKey: TutorialService.TutorialKey, dontShowAgain: Boolean)
    fun getTutorialDontShowAgain(tutorialKey: TutorialService.TutorialKey): Boolean

    fun getPaymentTokens(): Deferred<List<BillingTokenInfo>>

    fun sendPaymentToken(billingTokenInfo: BillingTokenInfo): Deferred<List<BillingTokenInfo>>

    fun setUserEntitlement(entitlementId: String, active: Boolean)

    fun getUserEntitlement(entitlementId: String): Boolean

    fun clearUserEntitlements()

    fun grantAllUserEntitlements()

    fun getGeolocationAllowed(): Boolean

    fun getLocationDontAsk(): Boolean

    fun setLocationDontAsk(dontAsk: Boolean)

    fun getLocationPermissionsOneTimeChecked(): Boolean

    fun setLocationPermissionsOneTimeChecked(checkedAlready: Boolean)

/////////// push notifications
    fun setSpotsCloseAllowed(allowed: Boolean)
    fun getSpotsCloseAllowed(): Boolean
    fun setNewLocationsAllowed(allowed: Boolean)
    fun getNewLocationsAllowed(): Boolean
    fun setNewJobMatchingAllowed(allowed: Boolean)
    fun getNewJobMatchingAllowed(): Boolean
    fun setEventsInCityAllowed(allowed: Boolean)
    fun getEventsInCityAllowed(): Boolean
    fun setCreditsAddedAllowed(allowed: Boolean)
    fun getCreditsAddedAllowed(): Boolean
    fun setFriendMessagesAllowed(allowed: Boolean)
    fun getFriendMessagesAllowed(): Boolean
    fun setBusinessMessagesAllowed(allowed: Boolean)
    fun getBusinessMessagesAllowed(): Boolean
///////////

    fun setGeolocationAllowed(allowed: Boolean)

// Campaign
    fun getCampaigns(): Deferred<List<CampaignInfo>>

    fun getCampaign(campaignId: Long): Deferred<Campaign>

    fun joinCampaign(campaignId: Long): Deferred<Campaign>

    fun requestReview(campaignId: Long): Deferred<MessageResponse>

    fun addCampaignImage(campaignId: Long, imageBytes: ByteArray): Deferred<Images>

    fun removeCampaignImage(campaignId: Long, imageId: String): Deferred<MessageResponse>

    fun getCampaignPhotos(campaignId: Long): Deferred<List<String>>

    fun getCampaignLocations(campaignId: Long): Deferred<List<CampaignInterval.Location>>

    fun getCampaignSlots(campaignId: Long, intervalId: Long, date: String): Deferred<List<CampaignInterval.Slot>>

    fun campaignBook(campaignId: Long, intervalId: Long, campaignBookInfo: CampaignBookInfo): Deferred<MessageResponse>

    fun sendQr( qrInfo: QrInfo): Deferred<Campaign>

    fun getCampaignBookings(): Deferred<List<CampaignBooking>>
    fun sendCampaignForReview(campaignId: Long): Deferred<MessageResponse>
}