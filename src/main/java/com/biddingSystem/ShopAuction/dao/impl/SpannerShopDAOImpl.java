package com.biddingSystem.ShopAuction.dao.impl;

import com.biddingSystem.ShopAuction.dao.SpannerShopDAO;
import com.biddingSystem.ShopAuction.dto.AuctionData;
import com.google.cloud.spanner.DatabaseClient;
import com.google.cloud.spanner.ResultSet;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.Value;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SpannerShopDAOImpl implements SpannerShopDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpannerShopDAOImpl.class);
    private static final String READ_RUNNING_AUCTIONS_SQL = "SELECT AUCTION_ID, BASE_PRICE, MAX_BID_PRICE," +
            "AUCTION_CREATION_TIME, AUCTION_EXPIRY_TIME FROM AUCTION WHERE CATEGORY = @category";

    private static final String READ_AUCTION_DETAILS_SQL = "SELECT CATEGORY, MAX_BID_PRICE, AUCTION_CREATION_TIME, AUCTION_EXPIRY_TIME " +
            "FROM AUCTION WHERE AUCTION_ID = @auctionId AND AUCTION_EXPIRY_TIME > CURRENT_TIMESTAMP";
    private static final String READ_BID_DETAILS_SQL = "SELECT MAX_BID_PRICE FROM BID WHERE AUCTION_ID = @auctionId " +
            "AND C_USER_ID = (SELECT C_USER_ID FROM C_USER WHERE EMAIL = @email)";
    private DatabaseClient databaseClient;

    @Override
    public List<AuctionData> getAllRunningAuctions(String collection) {
        LOGGER.info("Inside Spanner DAO to read all auctions for a collection.");
        List<AuctionData> auctionDataList = new ArrayList<>();
        Statement statement = Statement.newBuilder(READ_RUNNING_AUCTIONS_SQL)
                .bind("category")
                .to(collection)
                .build();

        try (ResultSet queryResultSet = databaseClient.singleUse().executeQuery(statement)) {
            while (queryResultSet.next()) {
                AuctionData auctionData = new AuctionData();
                auctionData.setAuctionId(queryResultSet.getString("AUCTION_ID"));
                auctionData.setBasePrice(queryResultSet.getDouble("BASE_PRICE"));
                Value maxBidPriceValue = queryResultSet.getValue("MAX_BID_PRICE");
                if (!maxBidPriceValue.isNull()) {
                    auctionData.setCurrentMaxBidPrice(maxBidPriceValue.getFloat64());
                }
                auctionData.setCreationTime(queryResultSet.getTimestamp("AUCTION_CREATION_TIME").toString());
                auctionData.setExpirationTime(queryResultSet.getTimestamp("AUCTION_EXPIRY_TIME").toString());

                auctionDataList.add(auctionData);
            }
        }

        return auctionDataList;
    }

    @Override
    public AuctionData getAuctionDetails(String auctionId, String userEmail) {
        LOGGER.info("Inside Spanner DAO to read auction details for auctionId: {}", auctionId);
        Statement statement = Statement.newBuilder(READ_AUCTION_DETAILS_SQL)
                .bind("auctionId")
                .to(auctionId)
                .build();

        AuctionData auctionData = new AuctionData();
        try (ResultSet resultSet = databaseClient.singleUse().executeQuery(statement)) {
            if (resultSet.next()) {
                auctionData.setAuctionId(auctionId);
                auctionData.setItemCategory(resultSet.getString("CATEGORY"));
                Value maxBidPriceValue = resultSet.getValue("MAX_BID_PRICE");
                if (!maxBidPriceValue.isNull()) {
                    auctionData.setCurrentMaxBidPrice(maxBidPriceValue.getFloat64());
                }
                auctionData.setCreationTime(resultSet.getTimestamp("AUCTION_CREATION_TIME").toString());
                auctionData.setExpirationTime(resultSet.getTimestamp("AUCTION_EXPIRY_TIME").toString());
            }
        }
        if (StringUtils.isNotEmpty(auctionData.getAuctionId())) {
            Statement bidStatement = Statement.newBuilder(READ_BID_DETAILS_SQL)
                    .bind("auctionId")
                    .to(auctionId)
                    .bind("email")
                    .to(userEmail)
                    .build();

            try (ResultSet resultSet = databaseClient.singleUse().executeQuery(bidStatement)) {
                if (resultSet.next()) {
                    Value prevBidPrice = resultSet.getValue("MAX_BID_PRICE");
                    if (!prevBidPrice.isNull()) {
                        auctionData.setPrevBidPrice(prevBidPrice.getFloat64());
                    }
                }
            }
        }
        return auctionData;
    }

    @Autowired
    public void setDatabaseClient(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }
}
