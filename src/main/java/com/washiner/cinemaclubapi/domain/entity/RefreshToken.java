package com.washiner.cinemaclubapi.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String token;

    //@ManyToOne → muitos tokens pertencem a um usuário
    //@JoinColumn → define o nome da coluna FK no banco
    //FetchType.LAZY → só carrega o usuário quando precisar
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @CreationTimestamp
    @Column(name = "expira_em")
    private LocalDateTime expiraEm;

    @Builder.Default
    @Column(nullable = false)
    private boolean revogado = false;
    @CreationTimestamp
    @Column(name = "criado_em")
    private LocalDateTime criadoEm;
}