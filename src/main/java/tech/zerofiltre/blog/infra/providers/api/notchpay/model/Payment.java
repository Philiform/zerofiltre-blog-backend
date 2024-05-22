package tech.zerofiltre.blog.infra.providers.api.notchpay.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Payment {
    private String email;
    private int amount;
    private String currency;
    private String description;
    private String reference;
    private Map<String, Object> customerMeta = new HashMap<>();
}
