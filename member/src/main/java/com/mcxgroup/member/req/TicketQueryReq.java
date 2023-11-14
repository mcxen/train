package com.mcxgroup.member.req;

import com.mcxgroup.common.req.PageReq;
import lombok.Data;

@Data
public class TicketQueryReq extends PageReq {
    private Long memberId;
}