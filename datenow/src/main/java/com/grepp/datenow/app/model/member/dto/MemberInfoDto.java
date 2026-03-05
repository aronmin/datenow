package com.grepp.datenow.app.model.member.dto;

import com.grepp.datenow.app.model.member.entity.Member;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberInfoDto {
    private String userId;
    private String email;
    private String name;
    private String nickname;
    private String phone;
    private String birth;

    public static MemberInfoDto from(Member member) {
        MemberInfoDto dto = new MemberInfoDto();
        dto.setUserId(member.getUserId());
        dto.setEmail(member.getEmail());
        dto.setName(member.getName());
        dto.setNickname(member.getNickname());
        dto.setPhone(member.getPhone());
        return dto;
    }
}
