package com.account_catalogue.accounting.infraestructure.adapters.output.messageBroker.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO<T> {
    private String type;
    private T data;
}
