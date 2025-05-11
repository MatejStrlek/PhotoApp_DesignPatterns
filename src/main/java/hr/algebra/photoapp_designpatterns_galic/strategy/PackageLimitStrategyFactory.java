package hr.algebra.photoapp_designpatterns_galic.strategy;

import hr.algebra.photoapp_designpatterns_galic.model.PackageType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class PackageLimitStrategyFactory {
    private final Map<PackageType, PackageLimitStrategy> strategyMap = new EnumMap<>(PackageType.class);

    public PackageLimitStrategyFactory() {
        strategyMap.put(PackageType.FREE, new FreePackageStrategy());
        strategyMap.put(PackageType.PRO, new ProPackageStrategy());
        strategyMap.put(PackageType.GOLD, new GoldPackageStrategy());
    }

    public PackageLimitStrategy getPackageLimitStrategy(PackageType type) {
        return strategyMap.get(type);
    }
}