package com.example.easybookshare.models;

public class BookModel
{
    String bookId;
    String userId;
    String imgUrl1,imgUrl2,imgUrl3;
    String volume,quantity;
    String bookName,noOfPages,categoryName,subCategoryName,oPrice,sPrice;
    boolean isWished = false;
    String authorName;

    public BookModel()
    {

    }

    public BookModel(BookModel bookModel)
    {
        this.bookId = bookModel.bookId;
        this.userId = bookModel.userId;
        this.imgUrl1 = bookModel.imgUrl1;
        this.imgUrl2 = bookModel.imgUrl2;
        this.imgUrl3 = bookModel.imgUrl3;
        this.bookName = bookModel.bookName;
        this.noOfPages = bookModel.noOfPages;
        this.categoryName = bookModel.categoryName;
        this.subCategoryName = bookModel.subCategoryName;
        this.oPrice = bookModel.oPrice;
        this.sPrice = bookModel.sPrice;
        this.authorName = bookModel.authorName;
    }

    public BookModel(String bookId,String userId ,String imgUrl1, String imgUrl2, String imgUrl3,
                     String bookName, String noOfPages, String categoryName, String
                             subCategoryName,String volume,String quantity, String oPrice, String sPrice,boolean isWished
                    ,String authorName)
    {

        this.bookId = bookId;
        this.userId = userId;
        this.imgUrl1 = imgUrl1;
        this.imgUrl2 = imgUrl2;
        this.imgUrl3 = imgUrl3;
        this.bookName = bookName;
        this.noOfPages = noOfPages;
        this.categoryName = categoryName;
        this.subCategoryName = subCategoryName;
        this.quantity = quantity;
        this.volume = volume;
        this.oPrice = oPrice;
        this.sPrice = sPrice;
        this.isWished = isWished;
        this.authorName = authorName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public boolean isWished() {
        return isWished;
    }

    public void setWished(boolean wished) {
        isWished = wished;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getImgUrl1() {
        return imgUrl1;
    }
    public void setImgUrl1(String imgUrl1) {
        this.imgUrl1 = imgUrl1;
    }

    public String getImgUrl2() {
        return imgUrl2;
    }

    public void setImgUrl2(String imgUrl2) {
        this.imgUrl2 = imgUrl2;
    }

    public String getImgUrl3() {
        return imgUrl3;
    }

    public void setImgUrl3(String imgUrl3) {
        this.imgUrl3 = imgUrl3;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getNoOfPages() {
        return noOfPages;
    }

    public void setNoOfPages(String noOfPages) {
        this.noOfPages = noOfPages;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getoPrice() {
        return oPrice;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setoPrice(String oPrice) {
        this.oPrice = oPrice;
    }

    public String getsPrice() {
        return sPrice;
    }

    public void setsPrice(String sPrice) {
        this.sPrice = sPrice;
    }
}
