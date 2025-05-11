package hr.algebra.photoapp_designpatterns_galic.strategy;

public class GoldPackageStrategy implements PackageLimitStrategy{
    @Override
    public int getMaxUploadSizeMb() {
        return 50;
    }

    @Override
    public int getDailyUploadLimit() {
        return 100;
    }
}