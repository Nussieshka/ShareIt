package com.nussia.request;

import lombok.Data;

import java.time.Period;
import java.util.Date;

@Data
public class ItemRequest {
    private long requestId;
    private long requestingUserId;
    private String name;
    private Date requestDate;
    private Period period;
    private int price;
}
