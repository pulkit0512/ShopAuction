package com.biddingSystem.ShopAuction.controller;

import com.biddingSystem.ShopAuction.authentication.AuthenticationService;
import com.biddingSystem.ShopAuction.exception.InvalidRequestException;
import com.biddingSystem.ShopAuction.service.impl.ShopServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
public class ShopController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShopController.class);
    private static final String TOKEN_NOT_VALID = "Token Not Valid";

    private AuthenticationService authenticationService;

    private ShopServiceImpl shopService;

    @GetMapping("/")
    public String hello() {
        return "Service for shopping auction items.";
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        String modifiedUserName = username.replace('@','_').replace('.','-');
        try {
            String jwtToken = authenticationService.login(modifiedUserName, password);
            return new ResponseEntity<>(jwtToken, HttpStatus.OK);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/getAllCollections")
    public ResponseEntity<List<String>> getAllCollections(@RequestHeader("Authorization") String token) {
        LOGGER.info("Getting Available collections for Auction.");
        try {
            String userEmail = authenticationService.getUserNameFromValidToken(token);
            if (userEmail == null) {
                LOGGER.warn(TOKEN_NOT_VALID);
                return new ResponseEntity<>(Collections.singletonList(TOKEN_NOT_VALID), HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(shopService.getAllAvailableCollections(), HttpStatus.OK);
        } catch (Exception ex) {
            LOGGER.error("Error while getting all collections available for auction with message {}", ex.getMessage());
            return new ResponseEntity<>(Collections.singletonList(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getRunningAuctions")
    public ResponseEntity<List<String>> getAllRunningAuctions(@RequestParam String collection, @RequestHeader("Authorization") String token) {
        LOGGER.info("Getting Available collections for Auction.");
        try {
            String userEmail = authenticationService.getUserNameFromValidToken(token);
            if (userEmail == null) {
                LOGGER.warn(TOKEN_NOT_VALID);
                return new ResponseEntity<>(Collections.singletonList(TOKEN_NOT_VALID), HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(shopService.getAllAuctions(collection), HttpStatus.OK);
        } catch (Exception ex) {
            LOGGER.error("Error while getting all running auctions for {} with message {}", collection, ex.getMessage());
            return new ResponseEntity<>(Collections.singletonList(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getAuctionDetails")
    public ResponseEntity<String> getAuctionDetails(@RequestParam String auctionId, @RequestHeader("Authorization") String token) {
        LOGGER.info("Getting details for the given auctionId: {}", auctionId);
        try {
            String userEmail = authenticationService.getUserNameFromValidToken(token);
            if (userEmail == null) {
                LOGGER.warn(TOKEN_NOT_VALID);
                return new ResponseEntity<>(TOKEN_NOT_VALID, HttpStatus.UNAUTHORIZED);
            }
            userEmail = userEmail.replace('_', '@').replace('-','.');
            return new ResponseEntity<>(shopService.getAuctionDetails(auctionId, userEmail), HttpStatus.OK);
        } catch (InvalidRequestException ex) {
            LOGGER.error(ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            LOGGER.error("Error while getting auction details for {} with message {}", auctionId, ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    @Autowired
    public void setShopService(ShopServiceImpl shopService) {
        this.shopService = shopService;
    }
}
