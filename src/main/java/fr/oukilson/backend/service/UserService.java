package fr.oukilson.backend.service;

import fr.oukilson.backend.dto.UserEditDTO;
import fr.oukilson.backend.dto.UserListDTO;
import fr.oukilson.backend.dto.UserProfilDTO;
import fr.oukilson.backend.entity.User;
import fr.oukilson.backend.repository.UserRepository;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService {

    private UserRepository userRepository;
    private ModelMapper mapper;

    public UserService (UserRepository userRepository, ModelMapper mapper){
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    /**
     *  verify if user exist by nickname search
     * @param nickname username of user
     * @return userDTO
     */
    public Optional<UserEditDTO> findUserByNickname(String nickname) {
        Optional<User> optionalUser = userRepository.findUserByNickname(nickname);
        return Optional.of(mapper.map(optionalUser.get(), UserEditDTO.class));
    }

    /**
     * get a list of all users
     * @return
     */
    public List<UserListDTO> findAll(){
        List<UserListDTO> userListDTOList = new ArrayList<>();
        this.userRepository.findAll().forEach(user ->{
            userListDTOList.add(mapper.map(user, UserListDTO.class));
        });
        return userListDTOList;
    }

    /**
     * save or update user method
     * @param userEditDTO
     * @return
     */
    public UserEditDTO save (UserEditDTO userEditDTO){
        User user = mapper.map(userEditDTO, User.class);
        User userToSave = this.userRepository.save(user);
        UserEditDTO userSaved = mapper.map(userToSave, UserEditDTO.class);
        return userSaved;
    }

    /**
     * delete user method
     * @param userProfilDTO
     */
    public void delete(UserProfilDTO userProfilDTO){
        User user = mapper.map(userProfilDTO, User.class);
        this.userRepository.delete(user);
    }
}
