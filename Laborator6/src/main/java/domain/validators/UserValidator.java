package domain.validators;

import domain.User;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User user) throws ValidatorException{
        String ermessage="";
        if(user.getFirstName().equals("")){
            ermessage+="First name is required";
        }
        if(user.getLastName().equals("")){
            ermessage+="Last name is required";
        }
        System.out.println(ermessage);
        if(!ermessage.equals("")){
            throw new ValidatorException(ermessage);
        }
    }

}
