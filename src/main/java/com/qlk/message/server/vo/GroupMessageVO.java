/**
 * 
 */
package com.qlk.message.server.vo;

import java.util.Set;
import java.util.StringJoiner;

/**
 * @author Ldl 
 * @since 1.0.0
 */
public class GroupMessageVO {

    private Set<String> groupResendNoticeSet;

    private Set<String> groupResendPushSet;

    private Integer groupNoticeSize;

    private Integer groupPushSize;



    public Set<String> getGroupResendNoticeSet() {
        return groupResendNoticeSet;
    }

    public void setGroupResendNoticeSet(Set<String> groupResendNoticeSet) {
        this.groupResendNoticeSet = groupResendNoticeSet;
    }

    public Set<String> getGroupResendPushSet() {
        return groupResendPushSet;
    }

    public void setGroupResendPushSet(Set<String> groupResendPushSet) {
        this.groupResendPushSet = groupResendPushSet;
    }

    public Integer getGroupNoticeSize() {
        return groupNoticeSize;
    }

    public void setGroupNoticeSize(Integer groupNoticeSize) {
        this.groupNoticeSize = groupNoticeSize;
    }

    public Integer getGroupPushSize() {
        return groupPushSize;
    }

    public void setGroupPushSize(Integer groupPushSize) {
        this.groupPushSize = groupPushSize;
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", GroupMessageVO.class.getSimpleName() + "[", "]")
                .add("groupResendNoticeSet=" + groupResendNoticeSet)
                .add("groupResendPushSet=" + groupResendPushSet)
                .add("groupNoticeSize=" + groupNoticeSize)
                .add("groupPushSize=" + groupPushSize)
                .toString();
    }
}
