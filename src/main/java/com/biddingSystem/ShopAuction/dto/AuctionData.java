package com.biddingSystem.ShopAuction.dto;

import java.util.Map;

public class AuctionData {
    private String auctionId;
    private String userEmail;
    private String itemCategory;
    private String itemName;
    private Double basePrice;
    private String currencyCode;
    private Double convertedBasePrice;
    private String expirationTime;
    private String creationTime;
    private Long expirationInSeconds;
    private Map<String, String> itemAttributes;
    private Double currentMaxBidPrice;
    private Double prevBidPrice;


    public String getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Double getConvertedBasePrice() {
        return convertedBasePrice;
    }

    public void setConvertedBasePrice(Double convertedBasePrice) {
        this.convertedBasePrice = convertedBasePrice;
    }

    public String getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(String expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public Long getExpirationInSeconds() {
        return expirationInSeconds;
    }

    public void setExpirationInSeconds(Long expirationInSeconds) {
        this.expirationInSeconds = expirationInSeconds;
    }

    public Map<String, String> getItemAttributes() {
        return itemAttributes;
    }

    public void setItemAttributes(Map<String, String> itemAttributes) {
        this.itemAttributes = itemAttributes;
    }

    public Double getCurrentMaxBidPrice() {
        return currentMaxBidPrice;
    }

    public void setCurrentMaxBidPrice(Double currentMaxBidPrice) {
        this.currentMaxBidPrice = currentMaxBidPrice;
    }

    public Double getPrevBidPrice() {
        return prevBidPrice;
    }

    public void setPrevBidPrice(Double prevBidPrice) {
        this.prevBidPrice = prevBidPrice;
    }
}
