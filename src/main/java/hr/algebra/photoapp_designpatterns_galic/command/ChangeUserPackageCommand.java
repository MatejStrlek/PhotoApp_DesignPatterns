package hr.algebra.photoapp_designpatterns_galic.command;

import hr.algebra.photoapp_designpatterns_galic.model.PackageType;
import hr.algebra.photoapp_designpatterns_galic.model.User;
import hr.algebra.photoapp_designpatterns_galic.repository.UserRepository;

public class ChangeUserPackageCommand implements ICommand {
    private final User user;
    private final PackageType newPackageType;
    private final UserRepository userRepository;

    public ChangeUserPackageCommand(User user, PackageType newPackageType, UserRepository userRepository) {
        this.user = user;
        this.newPackageType = newPackageType;
        this.userRepository = userRepository;
    }

    @Override
    public void execute() {
        user.setPackageType(newPackageType);
        userRepository.save(user);
    }
}