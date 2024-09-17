package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Service
public class UserService {
    public User addFriend (User user, User friend) {
        user.getFriends().add(friend.getId());
        friend.getFriends().add(user.getId());
        return user;
    }

    public User deleteFriend (User user, User friend) {
        user.getFriends().remove(friend.getId());
        friend.getFriends().remove(user.getId());
        return user;
    }

    public List<Long> getAMutualFriends (User user, User friend) {
      List<Long> mutualFriends = new ArrayList<>();
      Set<Long> usersFriend = new HashSet<>(user.getFriends());

      for (Long getFriend : usersFriend) {
          for (Long man : friend.getFriends()) {
              if (Objects.equals(getFriend, man)) {
                  mutualFriends.add(getFriend);
              }
          }
      }

      return mutualFriends;
    }
}
