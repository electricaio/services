package io.electrica.webhook.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@ToString
@NoArgsConstructor

@Entity
@Table(name = "webhooks",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "connectionId"})
)
public class Webhook {

    @Id
    private UUID id;

    @NotNull
    @Size(max = 128)
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    private Long connectionId;

    @NotNull
    @Column(nullable = false)
    private Long organizationId;

    @NotNull
    @Column(nullable = false)
    private Long userId;


    @NotNull
    @Column(nullable = false)
    private Long connectorId;

    @NotNull
    @Column(nullable = false, columnDefinition = "int8 default 0")
    private Long invocationsCount = 0L;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @NotNull
    @Size(max = 30)
    @Column(nullable = false)
    private String hash;

}
