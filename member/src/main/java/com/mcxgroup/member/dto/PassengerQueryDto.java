package com.mcxgroup.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mcxgroup.common.dto.PageReq;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
public class PassengerQueryDto extends PageReq {
//    不只是查询需要分页，因此可以设置一个父类，来继承
    /**
     * id
     */

    /**
     * 会员id
     */
    private Long memberId;
}
