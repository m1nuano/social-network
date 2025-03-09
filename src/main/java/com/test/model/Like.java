package com.test.model;

import com.test.model.enums.LikeObjectType;
import com.test.model.enums.LikeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "object_id")
    private Long objectId;

    @Enumerated(EnumType.STRING)
    @Column(name = "object_type")
    private LikeObjectType objectType;

    @Enumerated(EnumType.STRING)
    @Column(name = "like_type")
    private LikeType likeType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
