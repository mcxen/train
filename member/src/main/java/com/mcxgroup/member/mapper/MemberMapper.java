package com.mcxgroup.member.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
//    @Select("select count(*) from member")
    int count();
}
