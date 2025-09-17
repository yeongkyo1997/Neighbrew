package com.ssafy.backend.domain.region.entity;

import java.io.Serializable;

import lombok.Data;

@Data
public class GugunPK implements Serializable {
    private Integer gugunCode;
    private Integer sidoCode;
}
