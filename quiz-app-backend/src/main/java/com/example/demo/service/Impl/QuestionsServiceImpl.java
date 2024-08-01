package com.example.demo.service.Impl;

import com.example.demo.dto.QuestionsDto;
import com.example.demo.dto.ResponseDto;
import com.example.demo.entity.*;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.mapper.Mapper;
import com.example.demo.repository.*;
import com.example.demo.service.QuestionsService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class QuestionsServiceImpl implements QuestionsService {
    private AnswersRepository answersRepository;
    private OSQuestionsRepository osQuestionsRepository;
    private DBMSQuestionsRepository dbmsQuestionsRepository;
    private CNSRepository cnsRepository;
    private OOPSRepository oopsRepository;
    private final ConcurrentMap<Long,OSQuestions> map = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long,DBMSQuestions> map1 = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, CNSQuestions> map2 = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, OOPSQuestions> map3 = new ConcurrentHashMap<>();
    @PostConstruct
    public void init() {
        map.clear();
        map1.clear();
        map2.clear();
        map3.clear();
        loadQuestionsIntoOSMap();
        loadQuestionsIntoDBMSMap();
        loadQuestionsIntoCnsMap();
        loadQuestionsIntoOopsMap();
    }

    private void loadQuestionsIntoOSMap() {
        List<OSQuestions> questions = osQuestionsRepository.findAll();
        for (OSQuestions question : questions) {
            map.put(question.getId(), question);
        }
    }
    private void loadQuestionsIntoDBMSMap(){
        List<DBMSQuestions> questions = dbmsQuestionsRepository.findAll();
        for(DBMSQuestions question : questions){
            map1.put(question.getId(),question);
        }
    }
    private void loadQuestionsIntoCnsMap(){
        List<CNSQuestions> questions = cnsRepository.findAll();
        for(CNSQuestions question : questions){
            map2.put(question.getId(),question);
        }
    }
    private void loadQuestionsIntoOopsMap(){
        List<OOPSQuestions> questions = oopsRepository.findAll();
        for(OOPSQuestions question : questions){
            map3.put(question.getId(),question);
        }
    }
    @Override
    public String addOSQuestion(QuestionsDto questionsDto) {
        OSQuestions questions = new OSQuestions();
        questions.setDescription(questionsDto.getDescription());
        questions.setOptionA(questionsDto.getOption_a());
        questions.setOptionB(questionsDto.getOption_b());
        questions.setOptionC(questionsDto.getOption_c());
        questions.setOptionD(questionsDto.getOption_d());
        questions.setTopic(questionsDto.getTopic());
        Answers answers = answersRepository.findByOptions(questionsDto.getOption());
        Set<Answers> answersSet = new HashSet<>();
        answersSet.add(answers);
        questions.setAnswers(answersSet);
        OSQuestions savedQuestion = osQuestionsRepository.save(questions);
        map.put(savedQuestion.getId(), savedQuestion);
        return "OS question successfully added";
    }

    @Override
    public List<QuestionsDto> getOSQuestions() {
        List<QuestionsDto> result = new ArrayList<>();
        for(OSQuestions question : map.values()){
            QuestionsDto questionsDto = new QuestionsDto();
            questionsDto.setDescription(question.getDescription());
            Optional<Answers> answer = question.getAnswers().stream().findFirst();
            answer.ifPresent(answers -> questionsDto.setOption(answers.getOptions()));
            questionsDto.setOption_a(question.getOptionA());
            questionsDto.setOption_b(question.getOptionB());
            questionsDto.setOption_c(question.getOptionC());
            questionsDto.setOption_d(question.getOptionD());
            questionsDto.setTopic(question.getTopic());
            result.add(questionsDto);
        }
        return result;
    }
    private Boolean processResponse(ResponseDto responseDto) {
        long id = responseDto.getId();
        OSQuestions question = map.get(id);
        Answers correctAnswer = question.getAnswers().iterator().next();
        return correctAnswer.getOptions().equals(responseDto.getResponse());
    }
    @Override
    public String processOSResponses(List<ResponseDto> list) {

        int score = list.parallelStream()
                .filter(responseDto -> processResponse(responseDto))
                .mapToInt(e -> 1)
                .sum();

        return "Your score is " + score + "/" + list.size();
    }


    @Override
    public List<QuestionsDto> getOSQuestions(String topic) {
        List<OSQuestions> list = new ArrayList<>(osQuestionsRepository.findByTopic(topic));
        List<QuestionsDto> result = new ArrayList<>();
        for(OSQuestions question : list){
            QuestionsDto questionsDto = new QuestionsDto();
            questionsDto.setDescription(question.getDescription());
            Optional<Answers> answer = question.getAnswers().stream().findFirst();
            answer.ifPresent(answers -> questionsDto.setOption(answers.getOptions()));
            questionsDto.setOption_a(question.getOptionA());
            questionsDto.setOption_b(question.getOptionB());
            questionsDto.setOption_c(question.getOptionC());
            questionsDto.setOption_d(question.getOptionD());
            questionsDto.setTopic(question.getTopic());
            result.add(questionsDto);
        }
        return result;
    }

    @Override
    public String addDBMSQuestion(QuestionsDto questionsDto) {
        DBMSQuestions question = new DBMSQuestions();
        question.setOptionA(questionsDto.getOption_a());
        question.setOptionB(questionsDto.getOption_b());
        question.setOptionC(questionsDto.getOption_c());
        question.setOptionD(questionsDto.getOption_d());
        question.setDescription(questionsDto.getDescription());
        question.setTopic(questionsDto.getTopic());
        Answers answer = answersRepository.findByOptions(questionsDto.getOption());
        Set<Answers> answersSet = new HashSet<>();
        answersSet.add(answer);
        question.setAnswers(answersSet);
        DBMSQuestions savedQuestion = dbmsQuestionsRepository.save(question);
        map1.put(savedQuestion.getId(),savedQuestion);
        return "DBMS question added successfully";
    }

    @Override
    public List<QuestionsDto> getDBMSQuestions() {
        List<QuestionsDto> result = new ArrayList<>();
        for(DBMSQuestions question : map1.values()){
            QuestionsDto questionsDto = Mapper.mapToQuestionsDto(question);
            result.add(questionsDto);
        }
        return result;
    }

    @Override
    public List<QuestionsDto> getDBMSQuestions(String topic) {
        Set<DBMSQuestions> list = dbmsQuestionsRepository.findByTopic(topic);
        List<QuestionsDto> result = new ArrayList<>();
        for(DBMSQuestions question : list){
            QuestionsDto questionsDto = Mapper.mapToQuestionsDto(question);
            result.add(questionsDto);
        }
        return result;
    }

    @Override
    public String processDBMSResponses(List<ResponseDto> responseDtos) {
        int score = responseDtos.parallelStream()
                .filter(this::processDBMSResponse)
                .mapToInt(e -> 1)
                .sum();
        return "Your score is " + score + "/" + responseDtos.size();
    }

    @Override
    public String addCNSQuestion(QuestionsDto questionsDto) {
        CNSQuestions question = new CNSQuestions();
        question.setTopic(questionsDto.getTopic());
        question.setDescription(questionsDto.getDescription());
        question.setOptionA(questionsDto.getOption_a());
        question.setOptionB(questionsDto.getOption_b());
        question.setOptionD(questionsDto.getOption_d());
        question.setOptionC(questionsDto.getOption_c());
        question.setTopic(questionsDto.getTopic());
        Set<Answers> set = new HashSet<>();
        Answers answer = answersRepository.findByOptions(questionsDto.getOption());
        set.add(answer);
        question.setAnswersSet(set);
        CNSQuestions savedQuestion = cnsRepository.save(question);
        map2.put(savedQuestion.getId(),savedQuestion);
        return "CNS Question successfully added";
    }

    @Override
    public List<QuestionsDto> getCNSQuestions() {
        List<QuestionsDto> list = new ArrayList<>();
        for(CNSQuestions question : map2.values()){
            list.add(Mapper.mapCnsToQuestionsDto(question));
        }
        return list;
    }

    @Override
    public List<QuestionsDto> getCNSQuestions(String topic) {
        Set<CNSQuestions> list = cnsRepository.findByTopic(topic);
        List<QuestionsDto> result = new ArrayList<>();
        for(CNSQuestions questions : list){
            result.add(Mapper.mapCnsToQuestionsDto(questions));
        }
        return result;
    }

    @Override
    public String processCNSResponses(List<ResponseDto> responseDtos) {
        int score = responseDtos.parallelStream()
                .filter(this::processCNSResponse)
                .mapToInt(e -> 1)
                .sum();
        return "Your score is " + score + "/" + responseDtos.size();
    }

    private Boolean processCNSResponse(ResponseDto responseDto){
        long id = responseDto.getId();
        CNSQuestions question = map2.get(id);
        String response = question.getAnswersSet().iterator().next().getOptions();
        return response.equals(responseDto.getResponse());
    }
    @Override
    public String addOOPSQuestion(QuestionsDto questionsDto) {
        OOPSQuestions question = new OOPSQuestions();
        question.setTopic(questionsDto.getTopic());
        question.setOptionA(questionsDto.getOption_a());
        question.setDescription(questionsDto.getDescription());
        question.setOptionB(questionsDto.getOption_b());
        question.setOptionD(questionsDto.getOption_d());
        question.setOptionC(questionsDto.getOption_c());
        question.setTopic(questionsDto.getTopic());
        Set<Answers> set = new HashSet<>();
        Answers answer = answersRepository.findByOptions(questionsDto.getOption());
        set.add(answer);
        question.setAnswers(set);
        OOPSQuestions savedQuestion = oopsRepository.save(question);
        map3.put(savedQuestion.getId(),savedQuestion);
        return "OOPS Question successfully added";
    }

    @Override
    public List<QuestionsDto> getOOPSQuestions() {
        List<QuestionsDto> list = new ArrayList<>();
        for(OOPSQuestions questions : map3.values()){
            list.add(Mapper.mapOOPSToQuestionsDto(questions));
        }
        return list;
    }

    @Override
    public List<QuestionsDto> getOOPSQuestions(String topic) {
        Set<OOPSQuestions> list = oopsRepository.findByTopic(topic);
        List<QuestionsDto> result = new ArrayList<>();
        for(OOPSQuestions questions : list){
            result.add(Mapper.mapOOPSToQuestionsDto(questions));
        }
        return result;
    }

    @Override
    public String processOOPSResponses(List<ResponseDto> list) {
        int score = list.parallelStream()
                .filter(this::processOOPSResponse)
                .mapToInt(e -> 1)
                .sum();
        return "Your score is " + score + "/" + list.size();
    }
    private Boolean processOOPSResponse(ResponseDto responseDto){
        Long id = responseDto.getId();
        Answers answers = map3.get(id).getAnswers().iterator().next();
        return answers.getOptions().equals(responseDto.getResponse());
    }
    private Boolean processDBMSResponse(ResponseDto responseDto){
        Long id = responseDto.getId();
        Answers answer = map1.get(id).getAnswers().iterator().next();
        return answer.getOptions().equals(responseDto.getResponse());
    }
}
