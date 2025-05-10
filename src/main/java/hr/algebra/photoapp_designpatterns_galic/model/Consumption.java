package hr.algebra.photoapp_designpatterns_galic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "consumption")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Consumption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private LocalDate date;
    private int uploadSizeMb;
    private int dailyUploadCount;

    public Consumption(User user, LocalDate date, int uploadSizeMb, int dailyUploadCount) {
        this.user = user;
        this.date = date;
        this.uploadSizeMb = uploadSizeMb;
        this.dailyUploadCount = dailyUploadCount;
    }
}