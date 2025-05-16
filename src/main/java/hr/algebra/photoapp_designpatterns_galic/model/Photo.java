package hr.algebra.photoapp_designpatterns_galic.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "photo")
@NoArgsConstructor
@AllArgsConstructor
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileType;
    private String filePath;

    private int width;
    private int height;
    @Column(name = "file_size_mb")
    private double fileSizeMB;

    private String description;

    @ElementCollection
    private List<String> hashtags = new ArrayList<>();

    private LocalDateTime uploadTime;

    @ManyToOne(fetch = FetchType.LAZY)
    private User author;
}