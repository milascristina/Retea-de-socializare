package domain.validators;

import domain.Friendship;
import domain.Message;
import domain.User;
import repository.DataBase.UserDBRepository;

public class MessageValidator implements Validator<Message> {
    private UserDBRepository repo;
    public MessageValidator(UserDBRepository repo) {
        this.repo = repo;
    }
    @Override
    public void validate(Message entity) throws ValidatorException {
        User fr=repo.findOne(entity.getFrom().getId());
        if(fr==null) {
            throw new ValidatorException("Invalid User from");
        }
        for(User u:entity.getTo()){
            User to=repo.findOne(u.getId());
            if(to==null) {
                throw new ValidatorException("Invalid User to");
            }
        }
        if(entity.getMessage()==""||entity.getMessage()==null){
            throw new ValidatorException("Invalid Message");
        }
    }
}
