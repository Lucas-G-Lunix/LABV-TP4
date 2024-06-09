package generator;


import com.github.javafaker.Faker;

import entity.User;
import logicImpl.UserLogicImpl;

public class UserGenerator implements IEntityGenerator<User> {


    private UserLogicImpl users;
    private Faker faker;
    
    public UserGenerator() {
    	faker = new Faker();
    	users = new UserLogicImpl();
    }
    
	@Override
	public User generate() {
        User user = new User();
        user.setName(faker.name().fullName());
        user.setUsername(faker.name().username());
        user.setPassword("12345678"); // Todos los usuarios generados automáticamente usarán esta contraseña.
        user.setActive(true);
        return user;
	}

	@Override
	public User save() {
		User random = generate();
        users.signup(random);
        return random;
	}
	
	
}
