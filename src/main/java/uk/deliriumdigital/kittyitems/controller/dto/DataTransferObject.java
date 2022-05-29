package uk.deliriumdigital.kittyitems.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataTransferObject {

    private String account;
    private Long kittyItemId;
    private Double price;

}
