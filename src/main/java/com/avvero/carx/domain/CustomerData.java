package com.avvero.carx.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;



/**
 * @author Avvero
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerData {

    @Id
    public String uuid;
    public Long money;
    public String country;

}
