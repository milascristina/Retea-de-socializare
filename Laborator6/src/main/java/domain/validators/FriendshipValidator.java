package domain.validators;

import domain.Friendship;
import domain.User;
import repository.DataBase.UserDBRepository;
import repository.InMemoryRepository;

public class FriendshipValidator implements Validator<Friendship> {
    private UserDBRepository repo;

    public FriendshipValidator(UserDBRepository repo) {
        this.repo = repo;
    }

    @Override
    public void validate(Friendship entity) throws ValidatorException {
        User u1=repo.findOne(entity.getIdUser1());
        User u2=repo.findOne(entity.getIdUser2());

        if(entity.getIdUser1()==null||entity.getIdUser2()==null)
            throw new ValidatorException("The id can't be null");
        if(u1==null||u2==null)
            throw new ValidatorException("The id doesn't null");
    }
}
