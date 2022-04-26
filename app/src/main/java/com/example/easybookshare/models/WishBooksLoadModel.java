package com.example.easybookshare.models;

public class WishBooksLoadModel
{
    String id;
    String imgBook;
    String strBookName;
    boolean isWished=false;

    public WishBooksLoadModel()
    {}

    public WishBooksLoadModel(String id, String imgBook, String strBookName,boolean isWished)
    {
        this.id = id;
        this.imgBook = imgBook;
        this.strBookName = strBookName;
        this.isWished = isWished;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgBook() {
        return imgBook;
    }

    public void setImgBook(String imgBook) {
        this.imgBook = imgBook;
    }

    public String getStrBookName() {
        return strBookName;
    }

    public void setStrBookName(String strBookName) {
        this.strBookName = strBookName;
    }
}
