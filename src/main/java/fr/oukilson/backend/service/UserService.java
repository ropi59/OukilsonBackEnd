package fr.oukilson.backend.service;

import fr.oukilson.backend.dto.user.UserCreationDTO;
import fr.oukilson.backend.dto.user.UserDTO;
import fr.oukilson.backend.model.RegexCollection;
import fr.oukilson.backend.entity.User;
import fr.oukilson.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import java.util.*;

@RequiredArgsConstructor
public class UserService {
    private UserRepository userRepository;
    private ModelMapper modelMapper;
    private RegexCollection regexCollection;

    public UserService(UserRepository userRepository, ModelMapper modelMapper, RegexCollection regexCollection) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.regexCollection = regexCollection;
    }

    /**
     * Method to save a user entity to the database
     * @param userCreationDTO User's data
     * @return UserDTO
     */
    public UserDTO createUser(UserCreationDTO userCreationDTO) {
        UserDTO result = null;
        if (userCreationDTO!=null
                && userCreationDTO.isValid(regexCollection.getNicknamePattern(), regexCollection.getEmailPattern())) {
            User user = this.userRepository.save(this.modelMapper.map(userCreationDTO, User.class));
            result = this.modelMapper.map(user, UserDTO.class);
        }
        return result;
    }

    /**
     * Add a user to the main user's friend list
     * @param mainUser User (nickname) to alter the friend list
     * @param secondUser User (nickname) to add
     * @return True if the adding has been done
     */
    public boolean addUserToFriendList(String mainUser, String secondUser) {
        boolean result;
        Optional<User> myOptionalUser = this.userRepository.findByNickname(mainUser);
        if (myOptionalUser.isPresent()) {
            Optional<User> myOptionalFriend = this.userRepository.findByNickname(secondUser);
            if (myOptionalFriend.isPresent()) {
                User myUser = myOptionalUser.get();
                List<User> friends = myUser.getFriendList();
                User myFriend = myOptionalFriend.get();
                if (!friends.contains(myFriend)) {
                    friends.add(myFriend);
                    this.userRepository.save(myUser);
                    result = true;
                }
                else
                    result = false;
            }
            else
                result = false;
        }
        else
            result = false;
        return result;
    }

    /**
     * Remove a user from a friend list
     * @param mainUser The user (nickname) to alter his friend list
     * @param secondUser The user's nickname to remove
     * @return True if the removing has been done
     */
    public boolean removeUserFromFriendList(String mainUser, String secondUser) {
        boolean result;
        Optional<User> myOptionalUser = this.userRepository.findByNickname(mainUser);
        if (myOptionalUser.isPresent()) {
            Optional<User> myOptionalFriend = this.userRepository.findByNickname(secondUser);
            if (myOptionalFriend.isPresent()) {
                User myUser = myOptionalUser.get();
                User myFriend = myOptionalFriend.get();
                myUser.getFriendList().remove(myFriend);
                this.userRepository.save(myUser);
                result = true;
            }
            else
                result = false;
        }
        else
            result = false;
        return result;
    }

    /**
     * Empty a friend list
     * @param nickname The user (nickname) to empty his friend list
     * @return True if successfully emptied
     */
    public boolean emptyFriendList(String nickname) {
        boolean result;
        Optional<User> myOptionalUser = this.userRepository.findByNickname(nickname);
        if (myOptionalUser.isPresent()) {
            User myUser = myOptionalUser.get();
            myUser.getFriendList().clear();
            this.userRepository.save(myUser);
            result = true;
        }
        else
            result = false;
        return result;
    }
}
