package com.ssafy.backend.domain.drink.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ssafy.backend.domain.user.entity.User;
import com.ssafy.backend.domain.user.entity.UserCommentLike;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DrinkReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long drinkReviewId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "drinkId")
    private Drink drink;

    @JsonIgnore
    @OneToMany(mappedBy = "drinkReview", cascade = CascadeType.ALL)
    private List<UserCommentLike> userCommentLikes;

    @JsonIgnore
    @OneToMany(mappedBy = "drinkReview", cascade = CascadeType.ALL)
    private List<SubReview> subReviews;

    @Lob
    @Setter
    private String content;

    @Column(columnDefinition = "varchar(255) default 'no image'")
    private String img;

    @Column(columnDefinition = "int default 0")
    private Long likeCount;

    // createdAt
    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;

    @PrePersist
    public void prePersist() {
        this.likeCount = 0L;
    }

    public void decreaseLikeCount() {
        this.likeCount--;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void updateImg(String uploadedImageUrl) {
        this.img = uploadedImageUrl;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
