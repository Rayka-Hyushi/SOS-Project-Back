package rayka.sos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "ordem_servico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long osid;

    @UuidGenerator
    private UUID uuid;

    @ManyToOne
    @JoinColumn(nullable = false, name = "c_id")
    private Cliente cliente;

    @Column(nullable = false, length = 50)
    private String device;

    @Column(nullable = false, length = 100)
    private LocalDateTime opendate;

    @Column(length = 100)
    private LocalDateTime closedate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusOrdemServico status;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal extras;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal discount;

    @Column(nullable = false, precision = 7, scale = 2)
    private BigDecimal total;

    @ManyToMany
    @JoinTable(
        name = "relacao_os",
        joinColumns = @JoinColumn(name = "os_id"),
        inverseJoinColumns = @JoinColumn(name = "s_id")
    )
    private Set<Servico> servicos = new HashSet<>();

    @ManyToOne
    @JoinColumn(nullable = false, name = "u_id")
    private Usuario usuario;
}
