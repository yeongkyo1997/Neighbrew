package com.ssafy.backend.domain.evaluation.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

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
