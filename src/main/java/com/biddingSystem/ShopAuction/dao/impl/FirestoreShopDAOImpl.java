package com.biddingSystem.ShopAuction.dao.impl;

import com.biddingSystem.ShopAuction.dao.FirestoreShopDAO;
import com.biddingSystem.ShopAuction.dto.AuctionData;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Repository
public class FirestoreShopDAOImpl implements FirestoreShopDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(FirestoreShopDAOImpl.class);

    private Firestore firestore;

    @Override
    public List<String> getAllCollections() {
        LOGGER.info("Inside Firestore DAO to get all collections.");
        List<String> collections = new ArrayList<>();
        firestore.listCollections().forEach(collectionReference -> collections.add(collectionReference.getId()));
        LOGGER.info("Fetched all collections available from firestore.");
        return collections;
    }

    @Override
    public AuctionData getAuctionDetails(String category, String auctionId) {
        LOGGER.info("Inside Firestore DAO to read auction details for category: {}, auctionId: {}", category, auctionId);
        DocumentReference docRef = firestore.collection(category).document(auctionId);
        // asynchronously retrieve the document
        ApiFuture<DocumentSnapshot> future = docRef.get();

        // block on response
        DocumentSnapshot document;
        try {
            document = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        AuctionData auctionData;
        if (document.exists()) {
            auctionData = document.toObject(AuctionData.class);
        } else {
            auctionData = new AuctionData();
        }
        return auctionData;
    }

    @Autowired
    public void setFirestore(Firestore firestore) {
        this.firestore = firestore;
    }
}
