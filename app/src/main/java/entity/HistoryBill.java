package entity;

/**
 * Created by john on 2018/6/9.
 */

public class HistoryBill {

    private int hbid;
    private long btime;
    private String name;
    private double price;

    public int getHbid() {
        return hbid;
    }

    public void setHbid(int hbid) {
        this.hbid = hbid;
    }

    public long getBtime() {
        return btime;
    }

    public void setBtime(long btime) {
        this.btime = btime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
