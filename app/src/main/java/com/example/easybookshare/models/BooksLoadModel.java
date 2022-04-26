package com.example.easybookshare.models;

public class BooksLoadModel
{
    String id;
    String imgBook;
    String strBookName;
    String usedId;
    boolean isWished = false;
    String categoryName;

    public BooksLoadModel()
    {}

    public BooksLoadModel(String id, String imgBook, String strBookName,boolean isWished,String usedId)
    {
        this.id = id;
        this.imgBook = imgBook;
        this.strBookName = strBookName;
        this.isWished = isWished;
        this.usedId = usedId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getUsedId() {
        return usedId;
    }

    public void setUsedId(String usedId) {
        this.usedId = usedId;
    }

    public boolean isWished() {
        return isWished;
    }

    public void setWished(boolean wished) {
        isWished = wished;
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
