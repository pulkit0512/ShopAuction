package com.biddingSystem.ShopAuction.dao;

import com.biddingSystem.ShopAuction.dto.AuctionData;

import java.util.List;

public interface FirestoreShopDAO {
    List<String> getAllCollections();
    AuctionData getAuctionDetails(String category, String auctionId);
}
