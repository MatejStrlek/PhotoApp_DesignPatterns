package hr.algebra.photoapp_designpatterns_galic.strategy.package_limit;

public class GoldPackageStrategy implements PackageLimitStrategy {
    @Override
    public double getMaxUploadSizeMb() {
        return 50;
    }

    @Override
    public int getDailyUploadLimit() {
        return 100;
    }
}