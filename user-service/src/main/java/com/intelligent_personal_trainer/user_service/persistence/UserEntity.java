package com.intelligent_personal_trainer.user_service.persistence;

import com.intelligent_personal_trainer.user_common.Lifestyle;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "user_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private Lifestyle lifestyle;

    private String dataSourceId;

    private String externalSourceUserId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_diseases", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "disease")
    private List<String> diseases;
}
