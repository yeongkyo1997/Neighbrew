package com.ssafy.backend.domain.evaluation.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.ssafy.backend.common.enums.EvaluationType;
import com.ssafy.backend.domain.meet.entity.Meet;
import com.ssafy.backend.domain.user.entity.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long evaluationId;

    @ManyToOne
    @JoinColumn(name = "rated_user_id", referencedColumnName = "userId") // 수정된 부분
    private User ratedUser;

    @ManyToOne
    @JoinColumn(name = "reviewer_id", referencedColumnName = "userId") // 수정된 부분
    private User reviewer;

    @ManyToOne
    @JoinColumn(name = "meetId")
    private Meet meet;

    @Enumerated(EnumType.STRING)
    private EvaluationType evaluationType;

    @Lob
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;

    @PrePersist
    public void createdAt() {
        this.createdAt = new Date();
    }
}
