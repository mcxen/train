package com.mcxgroup.member.req;

import com.mcxgroup.common.req.PageReq;



public class TicketQueryReq extends PageReq {
    private Long memberId;

    @Override
    public String toString() {
        return "TicketQueryReq{" +
                "memberId=" + memberId +
                "} " + super.toString();
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
}