package com.mcxgroup.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
public class PassengerQueryDto {

    /**
     * id
     */

    /**
     * 会员id
     */
    private Long memberId;
}
