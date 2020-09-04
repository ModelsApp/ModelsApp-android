package com.square.android.data.network

import com.square.android.data.network.response.AuthResponse
import com.square.android.data.network.response.SendPhoneCodeRespose
import com.square.android.data.network.response.MessageResponse
import com.square.android.data.newPojo.CompleteUserOfferDutyData
import com.square.android.data.newPojo.NewPlace
import com.square.android.data.newPojo.PlacesFiltersData
import com.square.android.data.newPojo.RemoveOfferEventData
import com.square.android.data.pojo.*
import com.square.android.presentation.presenter.auth.LoginData
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("users/{userId}/aboutMe")
    fun saveUserAboutMe(@Header("Authorization") authorization: String,
                    @Path("userId") userId: Long,
                    @Body userAboutMeData: UserAboutMeData): Call<MessageResponse>

    @POST("users/{userId}/skills")
    fun saveUserSkills(@Header("Authorization") authorization: String,
                       @Path("userId") userId: Long,
                       @Body userSkillsData: UserSkillsData): Call<MessageResponse>

    @GET("users/{userId}/skills")
    fun getUserSkills(@Header("Authorization") authorization: String,
                      @Path("userId") userId: Long): Call<List<UserSkill>>

    @POST("users/{userId}/features")
    fun saveUserFeatures(@Header("Authorization") authorization: String,
                         @Path("userId") userId: Long,
                         @Body userFeaturesData: UserFeaturesData): Call<MessageResponse>

    @GET("users/{userId}/features")
    fun getUserFeatures(@Header("Authorization") authorization: String,
                        @Path("userId") userId: Long): Call<UserFeatures>

    @POST("users/{userId}/network")
    fun saveUserNetwork(@Header("Authorization") authorization: String,
                        @Path("userId") userId: Long,
                        @Body userNetworkData: UserNetworkData): Call<MessageResponse>

    @GET("users/{userId}/network")
    fun getUserNetwork(@Header("Authorization") authorization: String,
                       @Path("userId") userId: Long): Call<UserNetwork>

    @DELETE("users/{userId}/deleteAgency/{agencyId}")
    fun deleteUserAgency(@Header("Authorization") authorization: String,
                         @Path("userId") userId: Long,
                         @Path("agencyId") agencyId: String): Call<MessageResponse>

    @POST("users/{userId}/invoiceDetails")
    fun saveUserInvoiceDetails(@Header("Authorization") authorization: String,
                               @Path("userId") userId: Long,
                               @Body userInvoiceData: UserInvoiceData): Call<MessageResponse>

    @GET("users/{userId}/invoiceDetails")
    fun getUserInvoiceDetails(@Header("Authorization") authorization: String,
                              @Path("userId") userId: Long): Call<UserInvoice>

    @POST("users/{userId}/deliveryPoints")
    fun addUserDeliveryPoints(@Header("Authorization") authorization: String,
                              @Path("userId") userId: Long,
                              @Body userDeliveryPointsData: UserDeliveryPointsData): Call<MessageResponse>

    @GET("users/{userId}/deliveryPoints")
    fun getUserDeliveryPoints(@Header("Authorization") authorization: String,
                              @Path("userId") userId: Long): Call<List<UserDeliveryPoint>>

    @DELETE("users/{userId}/removeDelPoint/{deliveryPointId}")
    fun deleteUserDeliveryPoint(@Header("Authorization") authorization: String,
                                @Path("userId") userId: Long,
                                @Path("deliveryPointId") deliveryPointId: String): Call<MessageResponse>

    @POST("users/{userId}/updateRates")
    fun updateUserRates(@Header("Authorization") authorization: String,
                        @Path("userId") userId: Long,
                        @Body userRatesData: UserRatesData): Call<MessageResponse>

    @GET("users/{userId}/userRates")
    fun getUserRates(@Header("Authorization") authorization: String,
                     @Path("userId") userId: Long): Call<List<UserRate>>

    @POST("users/{userId}/chanels")
    fun addUserSocialChannel(@Header("Authorization") authorization: String,
                             @Path("userId") userId: Long,
                             @Body userSocialChannelData: UserSocialChannelData): Call<MessageResponse>

    @GET("users/{userId}/socialChanels")
    fun getUserSocialChannels(@Header("Authorization") authorization: String,
                              @Path("userId") userId: Long): Call<UserSocialChannelResult>

    @POST("users/removeUserChanel/{socialChannelId}")
    fun deleteUserSocialChannel(@Header("Authorization") authorization: String,
                                @Path("socialChannelId") socialChannelId: String): Call<MessageResponse>

    @POST("users/{userId}/capabilities")
    fun addUserCapabilities(@Header("Authorization") authorization: String,
                             @Path("userId") userId: Long,
                             @Body userCapabilitiesData: UserCapabilitiesData): Call<MessageResponse>

    @GET("users/{userId}/capabilities")
    fun getUserCapabilities(@Header("Authorization") authorization: String,
                              @Path("userId") userId: Long): Call<List<UserCapability>>

    @GET("users/getUserCategoriesAndInterests")
    fun getRegisterSpecialitiesAndProfessions(): Call<RegisterSpecialitiesAndProfessionsData>

    @GET("users/capabilities")
    fun getRegisterCapabilities(): Call<RegisterCapabilitiesData>

    @POST("users/{userId}/saveProfession")
    fun addUserProfession(@Header("Authorization") authorization: String,
                            @Path("userId") userId: Long,
                            @Body professionsData: ProfessionData): Call<MessageResponse>

    @GET("user/{userId}/professions")
    fun getUserProfessions(@Header("Authorization") authorization: String,
                            @Path("userId") userId: Long): Call<ProfessionsResult>

//////////////////////////////////////////////////////////////
//    @DELETE("users/{userId}/deleteUserProfession")
//    fun deleteUserProfession(@Header("Authorization") authorization: String,
//                             @Path("userId") userId: Long,
//                             @Query("professionId") professionId: String): Call<MessageResponse>

    @DELETE("users/{userId}/deleteUserProfession")
    fun deleteUserProfession1(@Header("Authorization") authorization: String,
                             @Path("userId") userId: Long,
                             @Query("professionId") professionId: Int): Call<MessageResponse>

    @DELETE("users/{userId}/deleteUserProfession")
    fun deleteUserProfession2(@Header("Authorization") authorization: String,
                              @Path("userId") userId: Long,
                              @Query("professionId") professionId: String): Call<MessageResponse>

    @HTTP(method = "DELETE", path = "/users/{userId}/deleteUserProfession", hasBody = true)
    fun deleteUserProfession3(@Header("Authorization") authorization: String,
                              @Path("userId") userId: Long,
                              @Body professionDeleteData: ProfessionDelete1Data): Call<MessageResponse>

    @HTTP(method = "DELETE", path = "/users/{userId}/deleteUserProfession", hasBody = true)
    fun deleteUserProfession4(@Header("Authorization") authorization: String,
                              @Path("userId") userId: Long,
                              @Body professionDeleteData: ProfessionDelete2Data): Call<MessageResponse>
/////////////////////////////////////////////////////////////

    @POST("users/{userId}/specialities")
    fun addUserSpecialities(@Header("Authorization") authorization: String,
                            @Path("userId") userId: Long,
                            @Body specialitiesData: SpecialitiesData): Call<MessageResponse>

    @GET("users/{userId}/specialities")
    fun getUserSpecialities(@Header("Authorization") authorization: String,
                            @Path("userId") userId: Long): Call<SpecialitiesResult>

    @POST("users/{userId}/plan")
    fun postUserPlan(@Header("Authorization") authorization: String,
                     @Path("userId") userId: Long,
                     @Body userPlanData: UserPlanData): Call<MessageResponse>

    @GET("users/{userId}/profilePlans")
    fun getUserPlans(@Header("Authorization") authorization: String,
                     @Path("userId") userId: Long): Call<List<UserPlanData>>


///// Need models - not working right now

//    @POST("userOffers/{userId}/getLocal")
//    fun getOffersByUserLocation(@Header("Authorization") authorization: String,
//                                @Path("userId") userId: Long,
//                                @Body coordinatesData: CoordinatesData
//    ): Call<String>

                           // offerId or userId?
    @GET("userOffers/{offerId}/offerDetails")
    fun getOfferDetails(@Header("Authorization") authorization: String,
                        @Path("offerId") offerId: Long): Call<List<String>>

    @POST("userOffers/{userId}/bookOffer/{couponId}")
    fun bookOffer(@Header("Authorization") authorization: String,
                     @Path("userId") userId: Long,
                     @Path("couponId") couponId: String,
                     @Body userPlanData: UserPlanData): Call<MessageResponse>

//////

    // skipped:
    // (save offer) http://localhost:8000/api/offer/create, (Confirm offer booking) http://localhost:8000/api/userOffer/confirm/5ee60f84230bcc2
    // (Cancel offer prebooking) http://localhost:8000/api/userOffer/prebooking/5ee38ed2702b011b9029d3a2/cancel, (Cancel Offer) http://localhost:8000/api/userOffer/5ee3bde44798b31b3dde7023/cancel,

    @POST("agenda/removeEvent")
    fun removeOfferEventFromSchedule(@Header("Authorization") authorization: String,
                                @Body removeOfferEventData: RemoveOfferEventData
    ): Call<MessageResponse>

    @POST("agenda/{userId}/completeOfferDuty")
    fun completeUserOfferDuty(@Header("Authorization") authorization: String,
                              @Path("userId") userId: Long,
                              @Body completeUserOfferDutyData: CompleteUserOfferDutyData
    ): Call<MessageResponse>

    @GET("agenda/{userId}/dutiesList/{offerId}")
    fun getUserOfferDuties(@Header("Authorization") authorization: String,
                           @Path("userId") userId: Long,
                           @Path("offerId") offerId: Long): Call<List<String>>


    @POST("userOffer/{userOfferId}")
    fun rejectOfferProposal(@Header("Authorization") authorization: String,
                            @Path("userOfferId") userOfferId: String
    ): Call<MessageResponse>


    @POST("userOffer/{bookingId}/cancel")
    fun cancelUserOfferBooking(@Header("Authorization") authorization: String,
                               @Path("bookingId") bookingId: Long
    ): Call<MessageResponse>


    @POST("place/near")
    fun getNearbyPlaces(@Header("Authorization") authorization: String,
                        @Query("lat") lat: Double,
                        @Query("long") lng: Double,
                        @Query("radius") radius: Int,
                        @Query("date") date: String,
                        @Body placesFiltersData: PlacesFiltersData
    ): Call<List<NewPlace>>


    @GET("place/{placeId}/offers")
    fun getPlaceOffersNew(@Header("Authorization") authorization: String,
                       @Path("placeId") placeId: Long
    ): Call<String>


////////////////////////// End of new endpoints

    //TODO doesn't work, API returns: "\"driverRideId\" is required" even when driverRideId is defined
    //TODO or maybe all id's are checked and they must match
    @POST("ride")
    fun createRide(@Header("Authorization") authorization: String,
                   @Body rideData: RideData): Call<MessageResponse>

    @PUT("ride")
    fun editRide(@Header("Authorization") authorization: String,
                 @Body ride: Ride): Call<MessageResponse>

    @DELETE("ride/{id}")
    fun deleteRide(@Header("Authorization") authorization: String,
                   @Path("id") rideId: String): Call<MessageResponse>

    // get ride by id
    @GET("ride")
    fun getUserRide(@Header("Authorization") authorization: String,
                     @Query("id") rideId: String,
                     @Query("pending") pending: Boolean?,
                     @Query("filter") filter: String?): Call<List<Ride>>

    // filters: oneWay, return
    // filter by filter and pending if needed
    @GET("ride")
    fun getUserRides(@Header("Authorization") authorization: String,
                      @Query("id") rideId: String?,
                      @Query("pending") pending: Boolean?,
                      @Query("filter") filter: String): Call<List<Ride>>

    @POST("ride/rate")
    fun rateRide(@Header("Authorization") authorization: String,
                 @Body rideData: RideData): Call<MessageResponse>

    @GET("driver-ride/place")
    fun getRideTimeframesForPlace(@Header("Authorization") authorization: String,
                               @Query("placeId") placeId: Long): Call<List<DriverRide>>

//    @POST("event-booking")
//    fun bookEvent(@Header("Authorization") authorization: String,
//                  @Body bookEventData: BookEventData): Call<MessageResponse>


    @GET("event-booking")
    fun getUserEventBookings(@Header("Authorization") authorization: String,
    @Query("id") eventBookingId: String?): Call<List<BookEventData.EventBooking>>


    @PUT("event-booking")
    fun editEventBookings(@Header("Authorization") authorization: String,
                 @Path("id") rideId: String,
                 @Body eventBooking: BookEventData): Call<MessageResponse>

    //TODO not working? What it returns?
    // GET /api/event-booking/summary?id=xxx
    // fun getEventBookingSummary()

    @GET("event")
    fun getEvent(@Header("Authorization") authorization: String,
                 @Query("id") eventId: String): Call<List<Event>>

    @GET("event")
    fun getEvents(@Header("Authorization") authorization: String): Call<List<Event>>

    @GET("city")
    fun getCities(@Header("Authorization") authorization: String): Call<List<City>>

    @GET("place-time-frame")
    fun getTimeFrames(@Header("Authorization") authorization: String): Call<List<FilterTimeframe>>

    @GET("v2/place")
    fun getPlacesByFilters(@Header("Authorization") authorization: String,
                           @Query("tf") timeframe: String,
                           @Query("typology") type: String,
                           @Query("date") date: String,
                           @Query("city") city: String): Call<List<Place>>

    @POST("auth/user/signin")
    fun registerUser(@Body signUpData: SignUpData): Call<AuthResponse>

    @POST("auth/user/login")
    fun loginUser(@Body loginData: LoginData): Call<AuthResponse>

    @POST("auth/user/confirm-phone")
    fun sendPhoneCode(@Body sendPhoneCodeData: SendPhoneCodeData): Call<SendPhoneCodeRespose>

    @POST("user/forgotPassword")
    fun resetPassword(@Body authData: AuthData): Call<MessageResponse>

    @GET("user/current")
    fun getCurrentProfile(): Call<Profile.User>

    @PUT("user/current")
    fun editProfile(@Body body: ProfileInfo): Call<MessageResponse>

    @GET("place")
    fun getPlaces(): Call<List<Place>>

    @GET("place/{id}")
    fun getPlace(@Path("id") id: Long): Call<Place>

    @GET("place-type")
    fun getPlaceTypes(@Header("Authorization") authorization: String): Call<List<PlaceType>>

    @GET("place-extra")
    fun getPlaceExtras(@Header("Authorization") authorization: String) : Call<List<PlaceExtra>>

    @POST("v2/place/{id}/book")
    fun book(@Path("id") id: Long,
             @Body body: BookInfo): Call<MessageResponse>

    @GET("place/{placeId}/booking/{bookingId}/offers")
    fun getOffersForBooking(@Path("placeId") placeId: Long,
                            @Path("bookingId") bookingId: Long): Call<List<OfferInfo>>

    @GET("place/{placeId}/booking/{bookingId}/offers")
    fun getOffersFor(@Path("placeId") placeId: Long,
                            @Path("bookingId") bookingId: Long,
                            @Query("start") start: String,
                            @Query("end") end: String): Call<List<OfferInfo>>

    @GET("user/{id}/bookings")
    fun getRedemptions(@Path("id") userId: Long) : Call<List<RedemptionInfo>>

    @GET("place/book/{id}")
    fun getRedemption(@Path("id") id: Long) : Call<RedemptionFull>

    @GET("place/{id}/offer")
    fun getPlaceOffers(@Path("id") id: Long): Call<List<OfferInfo>>

    @DELETE("place/book/{id}")
    fun deleteRedemption(@Path("id") id: Long) : Call<MessageResponse>

    @PUT("place/book/{id}/claim")
    fun claimRedemption(@Path("id") id: Long) : Call<MessageResponse>

    @GET("place/offer/{offerId}")
    fun getOffer(@Path("offerId") offerId: Long,
                 @Query("userID") userId: Long) : Call<Offer>

//    @POST("v2/offer/{id}/booking/{bookingId}/post")
//    @Multipart
//    fun addReview(@Header("Authorization") authorization: String,
//                  @Path("id") offerId: Long,
//                  @Path("bookingId") bookingId: Long,
//                  @Part("link") link: String,
//                  @Part("actionId") actionId: String,
//                  @Part image: MultipartBody.Part) : Call<MessageResponse>

    @POST("v2/offer/booking")
    @Multipart
    fun addReview(@Header("Authorization") authorization: String,
                  @Part("offerId") offerId: Long,
                  @Part("bookingId") bookingId: Long,
                  @Part("link") link: String,
                  @Part("actionType") actionId: String,
                  @Part image: MultipartBody.Part?) : Call<MessageResponse>

    @GET("place/{id}/sample")
    fun getFeedbackBody(@Path("id") id: Long) : Call<MessageResponse>

    @GET("user/{id}/bookNum")
    fun getBadgeCount(@Path("id") id: Long) : Call<BadgeInfo>

    @PUT("place/book/{id}/offer")
    fun addOfferToBook(@Path("id") bookId: Long,
                       @Body body: OfferToBook) : Call<MessageResponse>

    @GET("place/{id}/intervals")
    fun getIntervals(@Path("id") placeId: Long,
                     @Query("date") date: String) : Call<IntervalsWrapper>

    @GET("place/{id}/book/slots")
    fun getIntervalSlots(@Path("id") placeId: Long,
                         @Query("date") date: String) : Call<List<Place.Interval>>

    @GET("v2/offer/booking/actions")
    fun getActions(@Header("Authorization") authorization: String,
                   @Query("offerId") offerId: Long,
                   @Query("bookingId") bookingId: Long) : Call<List<Offer.Action>>

    @DELETE("user/{id}/images")
    fun removePhoto(@Path("id") userId: Long,
                    @Query("imageId") photoId: String) : Call<MessageResponse>

    @Multipart
    @POST("user/{id}/images")
    fun addPhoto(@Path("id") userId: Long,
                 @Part photo: MultipartBody.Part) : Call<Images>

    @PUT("user/{id}/images/{imageId}/main")
    fun setPhotoAsMain(@Path("id") userId: Long,
                       @Path("imageId") imageId: String) : Call<MessageResponse>

    @PUT("user/{id}/device")
    fun sendFcmToken(@Path("id") userId: Long,
                     @Body fcmTokenData: FcmTokenData) : Call<MessageResponse>


    @GET("user/paymentToken")
    fun getPaymentTokens(@Header("Authorization") authorization: String): Call<List<BillingTokenInfo>>

    @POST("user/paymentToken")
    fun sendPaymentToken(@Header("Authorization") authorization: String,
                         @Body body: BillingTokenInfo): Call<List<BillingTokenInfo>>

// Campaign
    @GET("campaign")
    fun getCampaigns(@Header("Authorization") authorization: String): Call<List<CampaignInfo>>

    @GET("campaign/{id}")
    fun getCampaign(@Header("Authorization") authorization: String,
                    @Path("id") campaignId: Long): Call<Campaign>

    @POST("campaign/{id}/join")
    fun joinCampaign(@Header("Authorization") authorization: String,
                     @Path("id") campaignId: Long): Call<Campaign>

    @POST("campaign/{id}/review")
    fun requestReview(@Header("Authorization") authorization: String,
                      @Path("id") campaignId: Long): Call<MessageResponse>

    @Multipart
    @POST("campaign/{id}/images")
    fun addCampaignImage(@Header("Authorization") authorization: String,
                         @Path("id") campaignId: Long,
                         @Part image: MultipartBody.Part) : Call<Images>

    @DELETE("campaign/{id}/images")
    fun removeCampaignImage(@Header("Authorization") authorization: String,
                            @Path("id") campaignId: Long,
                            @Query("imageId") imageId: String) : Call<MessageResponse>

    @GET("campaign/{id}/photos")
    fun getCampaignPhotos(@Header("Authorization") authorization: String,
                          @Path("id") campaignId: Long): Call<List<String>>


    //TODO change when API done - probably wrong -------------

    @GET("campaign/{id}/interval")
    fun getCampaignLocations(@Header("Authorization") authorization: String,
                             @Path("id") campaignId: Long) : Call<List<CampaignInterval.Location>>


    @GET("campaign/{id}/interval/{intervalId}/slots")
    fun getCampaignSlots(@Header("Authorization") authorization: String,
                         @Path("id") campaignId: Long,
                         @Path("intervalId") intervalId: Long,
                         @Query("date") date: String) : Call<List<CampaignInterval.Slot>>


    @POST("campaign/{id}/interval/{intervalId}/book")
    fun campaignBook(@Header("Authorization") authorization: String,
                     @Path("id") campaignId: Long,
                     @Path("intervalId") intervalId: Long,
                     @Body body: CampaignBookInfo): Call<MessageResponse>

    //TODO ---------------------------------------------------

    @POST("campaign")
    fun sendQr(@Header("Authorization") authorization: String,
               @Body body: QrInfo
    ): Call<Campaign>

    @GET("campaign/bookings")
    fun getCampaignBookings(@Header("Authorization") authorization: String): Call<List<CampaignBooking>>

    @POST("campaign/{id}/review")
    fun sendCampaignForReview(@Header("Authorization") authorization: String,
               @Path("id") campaignId: Long
    ): Call<MessageResponse>
}