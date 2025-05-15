package hr.algebra.photoapp_designpatterns_galic.strategy.package_limit;

public interface PackageLimitStrategy {
    double getMaxUploadSizeMb();
    int getDailyUploadLimit();
}