package hr.algebra.photoapp_designpatterns_galic.strategy;

public class ProPackageStrategy implements PackageLimitStrategy{
    @Override
    public int getMaxUploadSizeMb() {
        return 10;
    }

    @Override
    public int getDailyUploadLimit() {
        return 50;
    }
}