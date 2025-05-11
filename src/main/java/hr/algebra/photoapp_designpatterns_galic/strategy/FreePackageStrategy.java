package hr.algebra.photoapp_designpatterns_galic.strategy;

public class FreePackageStrategy implements PackageLimitStrategy{
    @Override
    public double getMaxUploadSizeMb() {
        return 3;
    }

    @Override
    public int getDailyUploadLimit() {
        return 5;
    }
}