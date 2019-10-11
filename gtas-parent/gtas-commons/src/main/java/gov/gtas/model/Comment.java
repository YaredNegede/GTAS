/*
 *
 *  * All Application code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 *  *
 *  * Please see LICENSE.txt for details.
 *
 */

package gov.gtas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.gtas.enumtype.CommentType;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "comment")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Comment extends BaseEntityAudit {

    @Column(name = "cmt_plain_text", length = 20000, nullable = false)
    private String plainTextComment;

    @Column(name = "cmt_rtf_text", length = 20000, nullable = false)
    private String rtfComment;

    @Column(name = "cmt_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CommentType commentType;

    @JsonIgnore
    @ManyToMany(targetEntity = Attachment.class, fetch = FetchType.LAZY)
    @JoinTable(name = "cmt_attachment", joinColumns = @JoinColumn(name = "cmt_attachment_id"), inverseJoinColumns = @JoinColumn(name = "id"))
    private Set<Attachment> attachmentSet = new HashSet<>();

    public String getPlainTextComment() {
        return plainTextComment;
    }

    public void setPlainTextComment(String plainTextComment) {
        this.plainTextComment = plainTextComment;
    }

    public String getRtfComment() {
        return rtfComment;
    }

    public void setRtfComment(String rtfComment) {
        this.rtfComment = rtfComment;
    }

    public Set<Attachment> getAttachmentSet() {
        return attachmentSet;
    }

    public void setAttachmentSet(Set<Attachment> attachmentSet) {
        this.attachmentSet = attachmentSet;
    }

}
