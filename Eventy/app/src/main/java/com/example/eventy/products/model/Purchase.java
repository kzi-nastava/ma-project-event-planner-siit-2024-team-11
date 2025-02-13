package com.example.eventy.products.model;

public class Purchase {
    private Long productId;
    private Long eventId;

    public Purchase() {}
    public Purchase(Long productId, Long eventId) {
        this.productId = productId;
        this.eventId = eventId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
