package hr.algebra.photoapp_designpatterns_galic.strategy.package_limit;

public class ProPackageStrategy implements PackageLimitStrategy {
    @Override
    public double getMaxUploadSizeMb() {
        return 10;
    }

    @Override
    public int getDailyUploadLimit() {
        return 50;
    }
}