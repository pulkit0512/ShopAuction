package com.biddingSystem.ShopAuction.dao;

import com.biddingSystem.ShopAuction.dto.AuctionData;

import java.util.List;

public interface SpannerShopDAO {
    List<AuctionData> getAllRunningAuctions(String collection);
    AuctionData getAuctionDetails(String auctionId, String userEmail);
}
