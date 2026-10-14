package com.javaforge.rediscachedemo.model;

import java.math.BigDecimal;

public record Product(long id, String name, String description, BigDecimal price) {
}
