package com.biddingSystem.ShopAuction.service;

import com.biddingSystem.ShopAuction.exception.InvalidRequestException;

import java.util.List;

public interface ShopService {
    List<String> getAllAvailableCollections();
    List<String> getAllAuctions(String collection);
    String getAuctionDetails(String auctionId, String userEmail) throws InvalidRequestException;
}
