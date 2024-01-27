package com.biddingSystem.ShopAuction.service.impl;

import com.biddingSystem.ShopAuction.dao.impl.FirestoreShopDAOImpl;
import com.biddingSystem.ShopAuction.dao.impl.SpannerShopDAOImpl;
import com.biddingSystem.ShopAuction.dto.AuctionData;
import com.biddingSystem.ShopAuction.exception.InvalidRequestException;
import com.biddingSystem.ShopAuction.service.ShopService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ShopServiceImpl implements ShopService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShopServiceImpl.class);
    private static final Gson gson = new Gson();
    private static final Type listType = new TypeToken<List<String>>() {}.getType();
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);

    private FirestoreShopDAOImpl firestoreShopDAO;
    private SpannerShopDAOImpl spannerShopDAO;

    private Jedis jedisWrite;
    private Jedis jedisRead;

    @Override
    public List<String> getAllAvailableCollections() {
        LOGGER.info("Getting Available Collections");
        return firestoreShopDAO.getAllCollections();
    }

    @Override
    public List<String> getAllAuctions(String collection) {
        LOGGER.info("Fetching all auctions for collection: {}", collection);
        String cacheResponse = jedisRead.get(collection);
        if (StringUtils.isNotEmpty(cacheResponse)) {
            LOGGER.info("Response from cache");
            return gson.fromJson(cacheResponse, listType);
        }
        List<AuctionData> auctionData = spannerShopDAO.getAllRunningAuctions(collection);
        List<String> auctionDataJson = auctionData
                .stream()
                .map(gson::toJson)
                .toList();
        executorService.submit(()->writeToCache(collection, auctionDataJson));
        return auctionDataJson;
    }

    private void writeToCache(String collection, List<String> auctionData) {
        LOGGER.info("Writing data to cache.");
        String auctionDataJson = gson.toJson(auctionData);
        jedisWrite.set(collection, auctionDataJson, SetParams.setParams().ex(300));
    }

    @Override
    public String getAuctionDetails(String auctionId, String userEmail) throws InvalidRequestException {
        AuctionData auctionDataFromSpanner = spannerShopDAO.getAuctionDetails(auctionId, userEmail);
        if (StringUtils.isNotEmpty(auctionDataFromSpanner.getAuctionId())) {
            AuctionData auctionDataFromFirestore = firestoreShopDAO.getAuctionDetails(auctionDataFromSpanner.getItemCategory(), auctionId);
            if (auctionDataFromFirestore.getUserEmail().equalsIgnoreCase(userEmail)) {
                throw new InvalidRequestException("You can't shop for the auction you created");
            }
            auctionDataFromFirestore.setAuctionId(auctionId);
            auctionDataFromFirestore.setPrevBidPrice(auctionDataFromSpanner.getPrevBidPrice());
            auctionDataFromFirestore.setCurrentMaxBidPrice(auctionDataFromSpanner.getCurrentMaxBidPrice());
            auctionDataFromFirestore.setCreationTime(auctionDataFromSpanner.getCreationTime());
            auctionDataFromFirestore.setExpirationTime(auctionDataFromSpanner.getExpirationTime());
            return gson.toJson(auctionDataFromFirestore);
        } else {
            return "Auction Completed, please try for other auctions.";
        }
    }

    @Autowired
    public void setFirestoreShopDAO(FirestoreShopDAOImpl firestoreShopDAO) {
        this.firestoreShopDAO = firestoreShopDAO;
    }

    @Autowired
    public void setSpannerShopDAO(SpannerShopDAOImpl spannerShopDAO) {
        this.spannerShopDAO = spannerShopDAO;
    }

    @Autowired
    @Qualifier("writeCache")
    public void setJedisWrite(Jedis jedisWrite) {
        this.jedisWrite = jedisWrite;
    }

    @Autowired
    @Qualifier("readCache")
    public void setJedisRead(Jedis jedisRead) {
        this.jedisRead = jedisRead;
    }
}
