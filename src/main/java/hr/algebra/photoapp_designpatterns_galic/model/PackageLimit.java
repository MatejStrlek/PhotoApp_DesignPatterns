package hr.algebra.photoapp_designpatterns_galic.model;

import lombok.Getter;

@Getter
public enum PackageLimit {
    FREE(3, 5),
    PRO(10, 50),
    GOLD(50, 100);

    private final int maxUploadSizeMb;
    private final int dailyUploadLimit;

    PackageLimit(int maxUploadSizeMb, int dailyUploadLimit) {
        this.maxUploadSizeMb = maxUploadSizeMb;
        this.dailyUploadLimit = dailyUploadLimit;
    }

    public static PackageLimit getPackageLimits(PackageType packageType) {
        return PackageLimit.valueOf(packageType.name());
    }
}
