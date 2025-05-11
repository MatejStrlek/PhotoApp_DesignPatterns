package hr.algebra.photoapp_designpatterns_galic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "package_change_requests")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PackageChangeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "requested_package_type")
    private PackageType requestedPackageType;

    @Column(name = "request_date")
    private LocalDate requestDate;

    public boolean hasRequestedToday(LocalDate today) {
        return requestDate != null && requestDate.isEqual(today);
    }
}