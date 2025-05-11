package hr.algebra.photoapp_designpatterns_galic.strategy;

public interface PackageLimitStrategy {
    int getMaxUploadSizeMb();
    int getDailyUploadLimit();
}