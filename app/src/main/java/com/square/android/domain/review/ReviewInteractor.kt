package com.square.android.domain.review

import com.square.android.data.network.response.MessageResponse
import com.square.android.data.pojo.Offer
import com.square.android.data.pojo.ReviewInfo
import kotlinx.coroutines.Deferred

interface ReviewInteractor {
    fun getOffer(id: Long) : Deferred<Offer>

    fun getAction(offerID: Long, bookingId: Long) : Deferred<List<Offer.Action>>

    fun addReview(offerId: Long, bookingId: Long, actionId: String, photo: ByteArray?) : Deferred<MessageResponse>
}