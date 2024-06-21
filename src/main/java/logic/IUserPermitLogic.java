package logic;

import java.util.List;

import entity.Permit;
import entity.User;
import entity.UserPermit;
import exceptions.NotAllowedException;
import exceptions.NotFoundException;

public interface IUserPermitLogic {

	UserPermit allow(String username, Permit permit) throws NotFoundException;

	UserPermit allow(User user, Permit permit) throws NotFoundException;

	UserPermit reject(String username, Permit permit) throws NotFoundException;

	UserPermit reject(User user, Permit permit) throws NotFoundException;

	boolean check(String username, Permit permit);

	boolean check(User user, Permit permit);

	List<UserPermit> list(String username);

	List<UserPermit> list(User user);
	
	void require(String username, Permit permit) throws NotAllowedException;
	
	void require(User user, Permit permit) throws NotAllowedException;

}