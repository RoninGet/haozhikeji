package com.haozhikeji.bean;

public class QueryVO {

    private String rname;
    private String minprice;
    private String maxprice;
    private String pageNumber;
    private String pageSize;

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

    public String getMinprice() {
        return minprice;
    }

    public void setMinprice(String minprice) {
        this.minprice = minprice;
    }

    public String getMaxprice() {
        return maxprice;
    }

    public void setMaxprice(String maxprice) {
        this.maxprice = maxprice;
    }

    /*public Integer getPageNumber() {
        if (pageNumber != null && !"".equals(pageNumber)) {
            return Integer.parseInt(pageNumber);
        }else {
            return 1;
        }

    }*/
    //第二种。BUG
    public Integer getIntPageNumber(){
        if (pageNumber != null && !"".equals(pageNumber)) {
            return Integer.parseInt(pageNumber);
        }else {
            return 1;
        }
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    /*public Integer getPageSize() {
        if (pageSize != null && !"".equals(pageSize)) {
            return Integer.parseInt(pageSize);
        }else {
            return 8;
        }
    }*/

    //第二种.BUG
    public Integer getIntPageSize(){
        if (pageSize != null && !"".equals(pageSize)) {
            return Integer.parseInt(pageSize);
        }else {
            return 8;
        }
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }
}
