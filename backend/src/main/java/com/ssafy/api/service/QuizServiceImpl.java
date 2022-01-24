package com.ssafy.api.service;


import com.ssafy.api.request.QuizRegisterReq;
import com.ssafy.api.response.QuizLogRes;
import com.ssafy.api.response.QuizRes;
import com.ssafy.db.entity.*;
import com.ssafy.db.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.List;

@Service("QuizService")
public class QuizServiceImpl implements QuizService{

    @Autowired
    QuizRepository quizRepository;

    @Autowired
    QuizRepositorySupport quizRepositorySupport;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    FolderQuizRepository folderQuizRepository;

    @Autowired
    BookMarkRepository bookMarkRepository;

    @Autowired
    QuizLogRepository quizLogRepository;

    @Autowired
    StudentRepository studentRepository;

    @Override
    public Quiz createQuiz(QuizRegisterReq quizRegisterReq, Long folderId) {
        Quiz quiz = new Quiz();
        quiz.setSubject(quizRegisterReq.getSubject());
        quiz.setQuizPhoto(quizRegisterReq.getQuizPhoto());
        quiz.setQuizTitle(quizRegisterReq.getQuizTitle());
        quiz.setQuizContents(quizRegisterReq.getQuizContents());
        quiz.setQuizAnswer(quizRegisterReq.getQuizAnswer());
        quiz.setOpenStatus(quizRegisterReq.getOpenStatus());
        quiz.setQuizTimeout(quizRegisterReq.getQuizTimeout());
        quiz.setQuizGrade(quizRegisterReq.getQuizGrade());
        quiz.setOptions(quizRegisterReq.getOptions());

        //퀴즈 본문 저장
        User user = new User();
        user.setUserId(quizRegisterReq.getUserId());
        quiz.setUser(user);
        Quiz quizRes = quizRepository.save(quiz);

        //퀴즈와 폴더 매핑 후 FolderQuiz에 저장
        FolderQuiz folderQuiz = new FolderQuiz();
        folderQuiz.setFolder(folderRepository.findById(folderId).get());
        folderQuiz.setQuiz(quizRes);
        folderQuizRepository.save(folderQuiz);

        return quizRes;
    }

    @Override
    public Quiz updateQuiz(QuizRegisterReq quizRegisterReq) {
        Quiz quiz = new Quiz();
        quiz.setQuizId(quizRegisterReq.getQuizId());
        quiz.setSubject(quizRegisterReq.getSubject());
        quiz.setQuizPhoto(quizRegisterReq.getQuizPhoto());
        quiz.setQuizTitle(quizRegisterReq.getQuizTitle());
        quiz.setQuizContents(quizRegisterReq.getQuizContents());
        quiz.setQuizAnswer(quizRegisterReq.getQuizAnswer());
        quiz.setOpenStatus(quizRegisterReq.getOpenStatus());
        quiz.setQuizTimeout(quizRegisterReq.getQuizTimeout());
        quiz.setQuizGrade(quizRegisterReq.getQuizGrade());
        quiz.setOptions(quizRegisterReq.getOptions());

        User user = new User();
        user.setUserId(quizRegisterReq.getUserId());
        quiz.setUser(user);

        Quiz quizRes = quizRepository.save(quiz);

        return quizRes;
    }

    @Override
    public QuizRes selectQuiz(Long quizId) {
        QuizRes quizRes = new QuizRes();
        //quiz 본문 세팅
        Quiz quiz = quizRepository.findById(quizId).get();

        quizRes.setQuizId(quizId);
        quizRes.setSubject(quiz.getSubject());
        quizRes.setQuizPhoto(quiz.getQuizPhoto());
        quizRes.setQuizTitle(quiz.getQuizTitle());
        quizRes.setQuizContents(quiz.getQuizContents());
        quizRes.setQuizAnswer(quiz.getQuizAnswer());
        quizRes.setOpenStatus(quiz.getOpenStatus());
        quizRes.setQuizTimeout(quiz.getQuizTimeout());
        quizRes.setQuizGrade(quiz.getQuizGrade());
        quizRes.setQuizGrade(quiz.getQuizGrade());
        quizRes.setUserId(quiz.getUser().getUserId());
        quizRes.setOptions(quiz.getOptions());

        return quizRes;
    }

    @Override
    public void deleteQuiz(Long quizId) {

        Quiz quiz = quizRepository.findById(quizId).get();

        //folder_quiz 삭제
        List<FolderQuiz> folderQuizList = folderQuizRepository.findByQuiz(quiz);
        for (FolderQuiz folderQuiz : folderQuizList) {
            folderQuizRepository.delete(folderQuiz);
        }

        quizRepository.delete(quiz);
    }

    @Override
    public List<Folder> selectFolders(String userId) {
        User user = userRepository.findById(userId).get();
        System.out.println("Service1 : " + user.getUserId());
        List<Folder> list = folderRepository.findByUser(user);

        return list;
    }

    @Override
    public List<Quiz> selectsFolderQuiz(Long folderId) {
        Folder folder = folderRepository.findById(folderId).get();
        System.out.println("folderIndex : "+folder.getFolderId());
        List<FolderQuiz> folderQuizList = folderQuizRepository.findByFolder(folder);

        List<Quiz> quizList = new ArrayList<>();
        for (FolderQuiz folderquiz : folderQuizList) {
            System.out.println("quizTitle : " + folderquiz.getQuiz().getQuizTitle());
            quizList.add(folderquiz.getQuiz());
        }

        return quizList;
    }

    @Override
    public List<Quiz> selectQuizAll() {
        List<Quiz> quizList = quizRepository.findAll();
        return quizList;
    }

    @Override
    public Folder createFolder(String userId, String folderName) {
        Folder folder = new Folder();
        folder.setUser(userRepository.findById(userId).get());
        folder.setFolderName(folderName);

        Folder folderRes = folderRepository.save(folder);

        return folderRes;
    }

    @Override
    public Bookmark createFavor(String userId, Long quizId) {
        Bookmark bookmark = new Bookmark();
        bookmark.setQuiz(quizRepository.findById(quizId).get());
        bookmark.setUser(userRepository.findById(userId).get());

        Bookmark bookmarkRes = bookMarkRepository.save(bookmark);

        return bookmarkRes;
    }

    @Override
    public List<Quiz> selectFavor(String userId) {
        List<Bookmark> bookmarkList = bookMarkRepository.findByUser(userRepository.findById(userId).get());
        List<Quiz> quizList = new ArrayList<>();
        for (Bookmark bookmark:bookmarkList) {
            quizList.add(quizRepository.findById(bookmark.getQuiz().getQuizId()).get());
        }

        return quizList;
    }

    @Override
    public FolderQuiz insertQuiz(Long folderId, Long quizId) {
        FolderQuiz folderQuiz = new FolderQuiz();
        folderQuiz.setQuiz(quizRepository.findById(quizId).get());
        folderQuiz.setFolder(folderRepository.findById(folderId).get());

        FolderQuiz folderQuizRes = folderQuizRepository.save(folderQuiz);

        return folderQuizRes;
    }

    @Override
    public List<QuizLogRes> selectQuizLog(String studentId) {
        List<QuizLog> quizLogList = quizLogRepository.findByStudent(studentRepository.findByStudentId(studentId));

        List<QuizLogRes> quizLogRes = new ArrayList<>();

        for (QuizLog quizLog : quizLogList) {
            QuizLogRes quizLogTemp = new QuizLogRes();
            Quiz quiz = quizLog.getQuiz();

            quizLogTemp.setStudentId(studentId);
            quizLogTemp.setQuizId(quiz.getQuizId());
            quizLogTemp.setQuizResult(quizLog.getQuizResult());
            quizLogTemp.setQuizDate(quizLog.getQuizDate());
            quizLogTemp.setSelectAnswer(quizLog.getSelectAnswer());
            quizLogTemp.setSubject(quiz.getSubject());
            quizLogTemp.setQuizPhoto(quiz.getQuizPhoto());
            quizLogTemp.setQuizTitle(quiz.getQuizTitle());
            quizLogTemp.setQuizContents(quiz.getQuizContents());
            quizLogTemp.setQuizAnswer(quiz.getQuizAnswer());
            quizLogTemp.setOpenStatus(quiz.getOpenStatus());
            quizLogTemp.setQuizTimeout(quiz.getQuizTimeout());
            quizLogTemp.setQuizGrade(quiz.getQuizGrade());
            quizLogTemp.setOptions(quiz.getOptions());

            quizLogRes.add(quizLogTemp);
        }

        return quizLogRes;
    }

}