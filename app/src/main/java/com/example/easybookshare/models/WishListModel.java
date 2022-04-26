package com.example.easybookshare.models;

public class WishListModel
{
    String wishListId;
    String bookId;

    public WishListModel()
    {}

    public WishListModel(String wishListId, String bookId) {
        this.wishListId = wishListId;
        this.bookId = bookId;
    }

    public String getWishListId() {
        return wishListId;
    }

    public void setWishListId(String userId) {
        this.wishListId = userId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}
