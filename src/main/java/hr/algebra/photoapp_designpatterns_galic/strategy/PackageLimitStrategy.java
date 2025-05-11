package hr.algebra.photoapp_designpatterns_galic.strategy;

public interface PackageLimitStrategy {
    double getMaxUploadSizeMb();
    int getDailyUploadLimit();
}