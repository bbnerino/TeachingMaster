package com.ssafy.api.service;

import com.ssafy.api.request.UserUpdateReq;
import com.ssafy.db.entity.Quiz;
import com.ssafy.db.entity.Room;
import com.ssafy.db.entity.User;
import com.ssafy.db.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ssafy.api.request.UserRegisterPostReq;

import javax.transaction.Transactional;
import java.util.List;


/**
 *	유저 관련 비즈니스 로직 처리를 위한 서비스 구현 정의.
 */
@Service("userService")
public class UserServiceImpl implements UserService {
	@Autowired
	UserRepository userRepository;

	@Autowired
	RoomRepository roomRepository;

	@Autowired
	QuizRepositorySupport quizRepositorySupport;

	@Autowired
	QuizRepository quizRepository;

	@Autowired
	UserRepositorySupport userRepositorySupport;
	
	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	RoomService roomService;

	@Override
	public User createUser(UserRegisterPostReq userRegisterInfo) {

		if(getUserByUserId(userRegisterInfo.getUserId())!=null)
			return null;

		User user = new User();
		user.setUserId(userRegisterInfo.getUserId());
		// 보안을 위해서 유저 패스워드 암호화 하여 디비에 저장.
		user.setPassword(passwordEncoder.encode(userRegisterInfo.getPassword()));
		user.setUserName(userRegisterInfo.getUserName());
		user.setUserHomeroom(userRegisterInfo.getUserHomeroom());
		user.setUserProfile(userRegisterInfo.getUserProfile());
		user.setMaster(userRegisterInfo.getMaster());

		Room room = roomService.getRoomByRoomGradeAndRoomNum(userRegisterInfo.getRoomGrade(),
				userRegisterInfo.getRoomNum());

		if(room == null) {
			room = new Room();
			room.setRoomNum(userRegisterInfo.getRoomNum());
			room.setRoomGrade(userRegisterInfo.getRoomGrade());
			room = roomService.createRoom(room);
		}

		user.setRoom(room);

		return userRepository.save(user);
	}

	@Override
	public User getUserByUserId(String userId) {
		// 디비에 유저 정보 조회 (userId 를 통한 조회).
		User user = userRepositorySupport.findUserByUserId(userId).orElse(null);

		return user;
	}

	@Override
	public User updateUser(UserUpdateReq userUpdateInfo) {
		// 유저 정보 수정
		User user = getUserByUserId(userUpdateInfo.getUserId());

		user.setUserId(userUpdateInfo.getUserId());
		user.setUserName(userUpdateInfo.getUserName());
//		user.setPassword(passwordEncoder.encode(userUpdateInfo.getPassword()));
		user.setUserHomeroom(userUpdateInfo.getUserHomeroom());
		user.setUserProfile(userUpdateInfo.getUserProfile());
		user.setMaster(userUpdateInfo.getMaster());

		Room room = user.getRoom();

		room.setRoomNum(userUpdateInfo.getRoomNum());
		room.setRoomGrade(userUpdateInfo.getRoomGrade());
		roomRepository.save(room);

		user.setRoom(room);

		return userRepository.save(user);
	}

	@Override
	@Transactional
	public void deleteUserByUserId(String userId) {
		//유저 정보 삭제(userId로)
		//master가 true인 유저 검색
		User user = userRepositorySupport.findUserByMaster().get();
		//User user = userRepositorySupport.findUserByUserId("master").get();

		//유저아이디로 연관된 퀴즈 찾기
		List<Quiz> quizList = userRepositorySupport.findQuizByUserId(userId);

		//찾은 퀴즈들 User 변경
		for (Quiz q:quizList) {
			q.setUser(user);
		}
		//User 삭제
		userRepository.delete(userRepositorySupport.findUserByUserId(userId).get());

	}


}
