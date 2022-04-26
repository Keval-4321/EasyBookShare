package com.example.easybookshare.models;

public class SellBookModel
{
    String imgUrl1,bookName,bookId;

    public SellBookModel()
    {}

    public SellBookModel(String imgUrl1, String bookName,String bookId)
    {
        this.imgUrl1 = imgUrl1;
        this.bookName = bookName;
        this.bookId = bookId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getImgUrl1()
    {
        return imgUrl1;
    }

    public void setImgUrl1(String imgUrl1) {
        this.imgUrl1 = imgUrl1;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }
}
